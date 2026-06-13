package api.repository;
import api.model.Notification;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class NotificationRepository implements Repository<Notification> {
    private final ConcurrentHashMap<Integer, Notification> storage;

    public NotificationRepository() {
        this.storage = new ConcurrentHashMap<>(); // create new list for defensive copying
    }

    @Override
    public void add(Notification notification){
        storage.put(notification.getID(),notification);
    }

    @Override
    public void remove(Notification notification){
        storage.remove(notification.getID());
    }

    @Override
    public void addAll(List<Notification> notifications) {
        for (Notification n : notifications) {
            storage.put(n.getID(), n);
        }
    }

    @Override
    public List<Notification> getAll() {
        return List.copyOf(storage.values());
    }

    @Override
    public void clear(){
        storage.clear();
    }

    public Notification findById(int id) {
        return storage.get(id);  // O(1)
    }


}
