package api.repository;
import api.model.Notification;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NotificationRepository implements Repository<Notification> {
    private final List<Notification> notificationList;

    public NotificationRepository(List<Notification> notificationList) {
        this.notificationList = new ArrayList<>(notificationList); // create new list for defensive copying
    }

    @Override
    public synchronized void add(Notification notification){
        notificationList.add(notification);
    }

    @Override
    public synchronized void remove(Notification notification){
        notificationList.remove(notification);
    }

    @Override
    public synchronized void addAll(List<Notification> notification) {
        notificationList.addAll(notification);
    }

    @Override
    public synchronized List<Notification> getAll() {
        return List.copyOf(notificationList);
    }

    @Override
    public synchronized void clear(){
        notificationList.clear();
    }


}
