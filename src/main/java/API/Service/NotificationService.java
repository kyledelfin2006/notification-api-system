package API.Service;

import API.Model.Notification;
import API.Model.Notification.*;
import API.Repository.Repository;


import java.util.ArrayList;
import java.util.List;

public class NotificationService {
    private final Repository<Notification> repository;


    // Class Function : Filtering service
    public NotificationService(Repository<Notification> repository) {
        this.repository = repository;
    }

    public List<Notification> getFailedNotifications(){

        List<Notification> failedNotifications = new ArrayList<>();

        for (Notification notification : repository.getAll()){
            if (notification.getStatus() == NotificationStatus.FAILED){
                failedNotifications.add(notification);
            }
        }
        return failedNotifications;
    }


    public List<Notification> getPendingNotifications() {
        List<Notification> pendingNotifications = new ArrayList<>();

        for (Notification notification : repository.getAll()){
            if (notification.getStatus() == NotificationStatus.PENDING){
                pendingNotifications.add(notification);
            }
        }
        return pendingNotifications;
    }

    public long getSentNotificationCount() {
        return repository.getAll().stream()
                .filter(n -> n.getStatus() == NotificationStatus.SENT)
                .count();
    }

    public boolean hasFailedNotifications() {
        return repository.getAll().stream().
                anyMatch(notification -> notification.getStatus() == NotificationStatus.FAILED);
    }


}
