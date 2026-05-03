package API.Service;

import API.Logger.Logger;
import API.Model.Notification;
import API.Repository.Repository;
import API.Storage.Storage;
import API.Util.NotificationIDGenerator;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class NotificationManager {

    private final Logger logger;
    private final Repository<Notification> repository; // In-Memory Repository Object
    private final Storage<Notification> storage; // JSON File Storage
    private final NotificationService service; // Service Logic
    private final Map<Integer, Notification> idIndex; // ID-Indexing

    private final AtomicInteger successfulDeliveries = new AtomicInteger(0); // Stats Counter
    private final AtomicInteger failedDeliveries = new AtomicInteger(0); // Stats Counter

    // Manages in-memory repository and coordinates file storage, printing, and sending
    public NotificationManager(Logger logger, Repository<Notification> repository, Storage<Notification> storage, NotificationService service) {
        this.repository = repository;
        this.service = service;
        this.logger = logger;
        this.storage = storage;
        this.idIndex = new HashMap<>();
        loadFromStorage();
    }

    // Load from storage -> repository
    private void loadFromStorage() {
        try {
            List<Notification> loaded = storage.load();  // Load once, store in variable

            idIndex.clear();
            repository.clear();

            // Creates a stream for loaded, maps notifications into their id, finds the max
            int maxId = loaded.stream().mapToInt(Notification::getID).max().orElse(-1);

            // if -1, NextId converts to 0 (-1 + 1)

            NotificationIDGenerator.setNextId(maxId + 1);
            repository.addAll(loaded);
            logger.info("Loaded " + loaded.size() + " notifications from storage");
        } catch (IOException e) {
            logger.warn("Could not load notifications: " + e.getMessage());
            idIndex.clear();
            repository.clear();
        }
        resetIndex(); // Successful or Not, ResetIndex still occurs
    }

    // Save from repository -> storage
    private void saveToStorage() {
        try {
            storage.save(repository.getAll()); // Calls save method of storage
        } catch (IOException e) {
            logger.error("Failed to save notifications: " + e.getMessage());
        }
    }

    private void resetIndex(){
        idIndex.clear();
        for (Notification notification : repository.getAll()){
            idIndex.put(notification.getID(),notification); // Add ID, Object
        }
    }

    // Adds Notification in Repository, Then Saves to Storage
    public void addNotification(Notification notification) {

        if (notification == null) {
            logger.error("Notification can't be null.");
            throw new IllegalArgumentException("Notification cannot be null");
        }

        repository.add(notification);
        idIndex.put(notification.getID(), notification);

        saveToStorage();

        logger.info("Adding of " + notification + " complete.");

    }

    // Deletes Notification in Repository, Then Saves to Storage
    public void deleteNotification(Notification notification) {

        if (notification == null) {
            logger.error("Notification can't be null.");
            throw new IllegalArgumentException("Notification can't be null");
        }

        repository.remove(notification);
        idIndex.remove(notification.getID());
        saveToStorage();

        logger.info("Removing of " + notification + " complete.");

    }


    // GET - /api/notifications/
    public List<Notification> getNotifications() {
        return repository.getAll();
    }

    // GET - /api/notifications/
    public Notification getNotificationById(int id) {
        return idIndex.get(id);
    }


    // Sends specific Notification
    public void sendMessage(Notification notification){
        notification.processNotification();
        saveToStorage();
    }

    // Sends all Notifications
    public void sendAllMessages() {

        // Set Batch Counters
        int batchSuccess = 0;
        int batchFailed = 0;

        for (Notification notification : repository.getAll()) {
            try {
                notification.processNotification(); // same process for all polymorphic notifications
                switch (notification.getStatus()) {
                    case SENT -> {
                        successfulDeliveries.incrementAndGet();
                        batchSuccess++;
                        logger.info("Notification: " + notification.getID() + " successfully sent.");
                    }
                    case FAILED -> {
                        failedDeliveries.incrementAndGet();
                        batchFailed++;
                        logger.error("Notification: " + notification.getID() + " failed to be sent.");
                    }
                }
                saveToStorage();
            } catch (Exception e) {
                failedDeliveries.incrementAndGet();
                batchFailed++;
                logger.error("Failed to process " +
                        notification.getClass().getSimpleName() + ": " + e.getMessage());
                saveToStorage();
            }
        }

        logger.info(String.format("| Batch completed - Success=%d, Failed=%d |  ",batchSuccess,batchFailed));
    }


    public void clearAllNotifications(){

        logger.info("Clearing Notifications. ");
        idIndex.clear();
        repository.clear();
        logger.info("Notifications cleared. ");
        saveToStorage();

    }

    public Map<String, Object> getDeliveryStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("total", repository.getAll().size());
        stats.put("successful", successfulDeliveries.get());  // Use .get()
        stats.put("failed", failedDeliveries.get());          // Use .get()

        int total = successfulDeliveries.get() + failedDeliveries.get();
        if (total > 0) {
            stats.put("successRate", (successfulDeliveries.get() * 100.0) / total);
        } else {
            stats.put("successRate", null);
        }

        return stats;
    }

    public NotificationService getService() {
        return service;
    }

    public Storage<Notification> getStorage() {
        return storage;
    }

    public Logger getLogger() {
        return logger;
    }

    public int getSuccessfulDeliveries() {
        return successfulDeliveries.get();
    }

    public void setSuccessfulDeliveries(int value) {
        successfulDeliveries.set(value);
    }

    public int getFailedDeliveries() {
        return failedDeliveries.get();
    }

    public void setFailedDeliveries(int value) {
        failedDeliveries.set(value);
    }

}
