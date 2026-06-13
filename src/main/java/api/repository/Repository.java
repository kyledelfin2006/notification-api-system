package api.repository;


import java.util.List;
import java.util.Map;

public interface Repository<T> {
    public void add(T type);
    public void remove(T type);
    public void addAll(List<T> type);
    List<T> getAll();
    public void clear();
}
