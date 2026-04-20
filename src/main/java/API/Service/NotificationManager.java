package API.Service;

import API.Logger.Logger;
import API.Model.Notification;
import API.Repository.Repository;
import API.Storage.Storage;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NotificationManager {

    private final Logger logger;
    private final Repository<Notification> repository; // In-Memory Repository Object
    private final Storage<Notification> storage;
    private final NotificationService service;

    private static int successfulDeliveries = 0; // Stats Counter
    private static int failedDeliveries = 0; // Stats Counter

    // Manages in-memory repository and coordinates file storage, printing, and sending
    public NotificationManager(Logger logger, Repository<Notification> repository, Storage<Notification> storage, NotificationService service) {
        this.repository = repository;
        this.service = service;
        this.logger = logger;
        this.storage = storage;
        loadFromStorage();
    }

    // Load from storage -> repository
    private void loadFromStorage() {
        try {
            List<Notification> loaded = storage.load();  // Load once, store in variable
            repository.addAll(loaded);
            logger.info("Loaded " + loaded.size() + " notifications from storage");
        } catch (IOException e) {
            logger.warn("Could not load notifications: " + e.getMessage());
        }
    }

    // Save from repository -> storage
    private void saveToStorage() {
        try {
            storage.save(repository.getAll()); // Calls save method of storage
        } catch (IOException e) {
            logger.error("Failed to save notifications: " + e.getMessage());
        }
    }

    // Adds Notification in Repository, Then Saves to Storage
    public void addNotification(Notification notification) {

        if (notification == null) {
            logger.error("Notification can't be null.");
            throw new IllegalArgumentException("Notification cannot be null");
        }

        repository.add(notification);

        saveToStorage();

        logger.info("Adding of " + notification + " complete.");

    }

    // Deletes Notification in Repository, Then Saves to Storage
    public void deleteNotification(Notification notification) {

        if (notification == null) {
            logger.error("Notification can't be null.");
            throw new IllegalArgumentException("Notification cannot be null");
        }

        repository.remove(notification);

        saveToStorage();

        logger.info("Removing of " + notification + " complete.");

    }


    // GET - /api/notifications/
    public List<Notification> getNotifications() {
        return repository.getAll();
    }

    // GET - /api/notifications/
    public Notification getNotificationById(int id) {
        return repository.getAll().stream().filter(n -> n.getID() == id).findFirst().orElse(null);
    }


    // Sends specific Notification
    public void sendMessage(Notification notification){

            try {
                notification.processNotification();
            } catch (Exception e){
                throw new RuntimeException(e);
            }
    }

    // Sends all Notifications
    public void sendAllMessages(){
       // Reset every call to avoid doubling.
        successfulDeliveries = 0;
        failedDeliveries = 0;

        for (Notification notification : repository.getAll()) {
            try {
                notification.processNotification();
                switch (notification.getStatus()) {


                    case SENT -> {
                        successfulDeliveries++;
                        logger.info("Notification: " + notification.getID() + " successfully sent. ");
                    }

                    case FAILED -> {
                        failedDeliveries++;
                        logger.error("Notification: " + notification.getID() + " failed to be sent. ");
                    }


                    case PENDING -> {
                        failedDeliveries++;
                        logger.warn("Notification: " + notification.getID() + " stuck in pending. ");
                    }

                }

                saveToStorage();

            } catch (Exception e) {
                logger.error("Failed to process " +
                        notification.getClass().getSimpleName() + ": " + e.getMessage());
                saveToStorage(); // Save failed state even on exception.
            }
        }

        if (service.hasFailedNotifications()){
            logger.warn("There were failures in this Notification batch ");
        }

    }

    public void clearAllNotifications(){

        logger.info("Clearing Notifications. ");
        repository.clear();
        logger.info("Notifications cleared. ");
        saveToStorage();

    }

    public Map<String, Object> getDeliveryStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("total", repository.getAll().size());
        stats.put("successful", successfulDeliveries);
        stats.put("failed", failedDeliveries);

        int total = successfulDeliveries + failedDeliveries;
        if (total > 0) {
            stats.put("successRate", (successfulDeliveries * 100.0) / total);
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

    public static int getSuccessfulDeliveries() {
        return successfulDeliveries;
    }

    public static void setSuccessfulDeliveries(int successfulDeliveries) {
        NotificationManager.successfulDeliveries = successfulDeliveries;
    }

    public static int getFailedDeliveries() {
        return failedDeliveries;
    }

    public static void setFailedDeliveries(int failedDeliveries) {
        NotificationManager.failedDeliveries = failedDeliveries;
    }

}
