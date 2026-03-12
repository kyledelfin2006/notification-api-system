package kyle.com;

import java.util.ArrayList;
import java.util.List;

public class NotificationService {
    private final NotificationRepository repository;


    // Class Function : Filtering service
    public NotificationService(NotificationRepository repository) {
        this.repository = repository;
    }

    public List<Notification> returnFailedNotifications(){

        List<Notification> failedNotifications = new ArrayList<>();

        for (Notification notification : repository.getAll()){
            if (notification.getStatus() == Notification.NotificationStatus.FAILED){
                failedNotifications.add(notification);
            }
        }
        return failedNotifications;
    }


    public List<Notification> getPendingNotifications() {
        List<Notification> pendingNotifications = new ArrayList<>();

        for (Notification notification : repository.getAll()){
            if (notification.getStatus() == Notification.NotificationStatus.PENDING){
                pendingNotifications.add(notification);
            }
        }
        return pendingNotifications;
    }

    public long countSentNotifications() {
        long sentNotifications = 0;

        for (Notification notification : repository.getAll()){
            if (notification.getStatus() == Notification.NotificationStatus.SENT){
                sentNotifications++;
            }
        }
        return sentNotifications;
    }

    public boolean hasFailedNotifications() {
        return repository.getAll().stream().
                anyMatch(notification -> notification.getStatus() == Notification.NotificationStatus.FAILED);
    }


}
