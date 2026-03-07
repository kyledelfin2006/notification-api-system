package kyle.com;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

// Is a type of storage
public class NotificationStorage implements Storage<Notification> {
    private final ObjectMapper mapper;
    private final String filename;
    private final Logger logger;

    public NotificationStorage(Logger logger, String filename) {
        this.filename = filename; // User decides this
        this.logger = logger;
        this.mapper = new ObjectMapper();

        // Make JSON output pretty (easier to read)
        this.mapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    // Save all notifications to file // Should be caught externally
    @Override
    public void save(List<Notification> notifications) throws IOException {
        mapper.writeValue(new File(filename), notifications);
    }

    // Load all notifications from file // Should be caught externally
    @Override
    public List<Notification> load() throws IOException {
        File filePath = new File(filename);

        // If file doesn't exist yet, return empty list
        if (!filePath.exists()) {
            return new ArrayList<>();
        }

        // Jackson reads the JSON, looks at the "type" field, and creates the right subclass
        List<Notification> notifications = mapper.readValue(
                filePath,                                                        //sms,push,email,system
                mapper.getTypeFactory().constructCollectionType(List.class, Notification.class)
        );

        return notifications; // Returns the loaded list read by mapper
    }
}