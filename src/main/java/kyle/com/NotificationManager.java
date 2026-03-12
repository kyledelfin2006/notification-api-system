package kyle.com;
import java.util.List;
import java.io.IOException;

public class NotificationManager {
    private final NotificationRepository repository; // In-Memory Repository Object
    private final NotificationService service;
    private final Storage<Notification> storage; // JSON file handler
    private final Logger logger; // Logging output (console/file)
    private int successfulDeliveries = 0; // Stats Counter
    private int failedDeliveries = 0; // Stats Counter

    // Manages in-memory repository and coordinates file storage, printing, and sending
    public NotificationManager(NotificationRepository repository, NotificationService service, Logger logger, Storage<Notification> storage) {
        this.repository = repository;
        this.service = service;
        this.logger = logger;
        this.storage = storage;
        loadFromStorage();
    }

    // Load from storage -> repository
    private void loadFromStorage() {
        try {
            List<Notification> loaded = storage.load(); // Loads storage into loadedList

            repository.addAll(loaded); // Add to the repo
            logger.info("Loaded " + loaded.size() + " notifications from storage");
        } catch (IOException e) {
            logger.warn("Could not load notifications: " + e.getMessage());
            // Start with empty list == not fatal
        }
    }

    // Save from repository -> storage
    private void saveToStorage() {
        try {
            List<Notification> loaded = repository.getAll();
            storage.save(loaded); // Calls save method of storage
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
         saveToStorage();  // Only save if deleted.

        logger.info("Deleted Notification: " + notification);

    }

    // Sends specific Notification
    public void sendMessage(Notification notification){
       try{
           notification.processNotification();
       } catch (Exception e) {
           throw new RuntimeException(e);
       }
    }

    // Sends all Notifications
    public void sendAllMessages(){
       // Reset every call to avoid doubling.
        successfulDeliveries = 0;
        failedDeliveries = 0;

        for (Notification notificationObject : repository.getAll()){
            try {
                notificationObject.processNotification();
                switch (notificationObject.getStatus()) {


                    case SENT -> {
                        successfulDeliveries++;
                        logger.info("Notification: " + notificationObject.getID() + " successfully sent. ");
                    }

                    case FAILED -> {
                        failedDeliveries++;
                        logger.error("Notification: " + notificationObject.getID() + " failed to be sent. ");
                    }


                    case PENDING -> {
                        failedDeliveries++;
                        logger.warn("Notification: " + notificationObject.getID() + " stuck in pending. ");
                    }

                }

                saveToStorage();

            } catch (Exception e) {
                logger.error("Failed to process " +
                        notificationObject.getClass().getSimpleName() + ": " + e.getMessage());
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

    public void printSummary() {
        logger.info("Total notifications: " + repository.getAll().size());
        for (Notification n : repository.getAll()) {
            System.out.println("  " + n);
        }
    }

    public void printStats(){
        logger.info("Successful messages : " + successfulDeliveries);
        logger.info("Failed: " + failedDeliveries);
        logger.info("Total: " + repository.getAll().size());
        int total = successfulDeliveries + failedDeliveries;
        if (total > 0) {
            double rate = (successfulDeliveries * 100.0) / total;
            logger.info(String.format("Success Rate: %.1f%%", rate));
        } else {
            logger.info("Success Rate: N/A (no deliveries attempted)");
        }
    }

}
