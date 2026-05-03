package api.Repository;
import api.Model.Notification;

import java.util.Collections;
import java.util.List;

public class NotificationRepository implements Repository<Notification> {
    private final List<Notification> notificationList;

    public NotificationRepository(List<Notification> notificationList) {
        this.notificationList = notificationList;
    }

    @Override
    public void add(Notification notification){
        notificationList.add(notification);
    }

    @Override
    public void remove(Notification notification){
        notificationList.remove(notification);
    }

    @Override
    public void addAll(List<Notification> notification) {
        notificationList.addAll(notification);
    }

    @Override
    public List<Notification> getAll() {
        return Collections.unmodifiableList(notificationList);
    }

    @Override
    public void clear(){
        notificationList.clear();
    }


}
