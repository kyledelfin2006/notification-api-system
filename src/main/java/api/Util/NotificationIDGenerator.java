package api.Util;


import java.util.concurrent.atomic.AtomicInteger;

public class NotificationIDGenerator{
    private static final AtomicInteger nextId = new AtomicInteger(0);

    public static int generateNextID() {
        // READ + WRITE as ONE operation
        return nextId.getAndIncrement();
    }

    // Setter for NextId
    public static void setNextId(int id) {
        NotificationIDGenerator.nextId.set(id);
    }
}
