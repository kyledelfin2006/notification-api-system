package API.Repository;

import API.Model.Notification;

import java.util.List;

public interface Repository<T> {
    public void add(T type);
    public void remove(T type);
    public void addAll(List<T> type);
    List<T> getAll();
    public void clear();
}
