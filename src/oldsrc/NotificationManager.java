import java.util.ArrayList;
import java.util.List;

public class NotificationManager{

    private final List<Notification> notificationList = new ArrayList<>();
    private final Logger logger;
    private int successfulDeliveries = 0;
    private int failedDeliveries = 0;

    NotificationManager(Logger logger){
        this.logger = logger;
    }

   public void addNotification(Notification notification){

        if (notification == null){
           logger.error("Notification can't be null.");
            throw new IllegalArgumentException();
        }

       logger.info("Processing: " + notification);
       notificationList.add(notification);
       logger.info("Adding of " + notification + " complete. ");
    }


    public void deleteNotification(Notification notification){
        if (notification == null){
            logger.error("Notification can't be null.");
            throw new IllegalArgumentException();
        }

        logger.info("Deleting Notification:  " + notification );
        boolean deleted = notificationList.remove(notification);

        if (deleted){
            logger.info("Deleted notification successfully. ");
        } else {
            logger.warn("Notification not found.");
        }
    }

    public void sendAllMessages(){
       for (Notification notification : notificationList){
          try {
              notification.process();
              if (notification.getStatus() == Notification.NotificationStatus.SENT){
                  successfulDeliveries++;
                  logger.info(" ✓ Notification delivery successful: " + notification);
              } else if (notification.getStatus() == Notification.NotificationStatus.FAILED){
                  failedDeliveries++;
                  logger.info("✗ Delivery failed : " + notification );
              } else if (notification.getStatus() == Notification.NotificationStatus.PENDING){
                  failedDeliveries++;
                  logger.info("⚠ Notification#" + notification.getID() + " stuck in PENDING state");
              }
          } catch (Exception e) {
             logger.error("Failed to process " +
                      notification.getClass().getSimpleName() + ": " + e.getMessage());
          }



       }
    }


    public void clearAllMessages(){

        logger.info("Clearing Notifications. ");
        notificationList.clear();
        logger.info("Notifications cleared. ");
    }

    public void printSummary() {
      logger.info("Total notifications: " + notificationList.size());
        for (Notification n : notificationList) {
            System.out.println("  " + n);
        }
    }

    public void printStats(){

        logger.info("Successful messages : " + successfulDeliveries);
        logger.info("Failed: " + failedDeliveries);
        logger.info("Total: " + notificationList.size());
        int total = successfulDeliveries + failedDeliveries;
        if (total > 0) {
            double rate = (successfulDeliveries * 100.0) / total;
            logger.info(String.format("Success Rate: %.1f%%", rate));
        } else {
            logger.info("Success Rate: N/A (no deliveries attempted)");
        }

    }


    public int getSuccessfulDeliveries() {
        return successfulDeliveries;
    }
    public int getFailedDeliveries() {
        return failedDeliveries;
    }

}
