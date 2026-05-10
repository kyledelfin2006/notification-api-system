package api.storage;

import api.loggers.Logger;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import api.model.Notification;

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

        logger.info("Saving: " + notifications.size() + " notifications");
        for (Notification n : notifications) {
          logger.info("  - " + n.getClass().getSimpleName() + ": " + n.getMessage());
        }

        mapper.writeValue(new File(filename), notifications);
        logger.info("Saved to: " + new File(filename).getAbsolutePath());
}

    // Load all notifications from file // Should be caught externally
    @Override
    public List<Notification> load() throws IOException {
        File filePath = new File(filename);

        if (!filePath.exists()) {
            return new ArrayList<>();
        }

        return mapper.readValue(
                // Jackson reads the JSON, looks at the "type" field, and creates the right subclass
                filePath,                                                   //sms,push,email,system
                mapper.getTypeFactory().constructCollectionType(List.class, Notification.class)

        ); // Returns the loaded list read by mapper
    }

    public Logger getLogger() {
        return logger;
    }
}