package API.Model;
import API.Logger.Logger;
import API.Util.NotificationIDGenerator;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = EmailNotification.class, name = "email"),
        @JsonSubTypes.Type(value = SMSNotification.class, name = "sms"),
        @JsonSubTypes.Type(value = PushNotification.class, name = "push"),
        @JsonSubTypes.Type(value = SystemNotification.class, name = "system")
})

public abstract class Notification implements Sendable {

    private NotificationStatus status;
    private final String sender;
    private final String message;
    private final int id;
    protected final Logger logger;
    private static final int MAX_RETRY_ATTEMPTS = 3;

    public enum NotificationStatus {
        PENDING,
        SENT,
        FAILED
    }

    protected Notification(String sender, String message, Logger logger) {
        // Validate and format the fields
        validateField(sender, "Sender");
        validateField(message, "Message");

        this.logger = logger;
        this.id = NotificationIDGenerator.generateNextID();
        this.sender = formatField(sender);  // Store trimmed version
        this.message = formatField(message); // Store trimmed version
        this.status = NotificationStatus.PENDING;
    }

    public abstract void displayNotification();

    // Validate that field is not null or empty after trimming
    protected void validateField(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            String errorMsg = fieldName + " cannot be null or empty";
            if (logger != null) {
                logger.error(errorMsg);
            }
            throw new IllegalArgumentException(errorMsg);
        }
    }

    // Format the field by trimming whitespace
    private String formatField(String field) {
        // Field is already validated, so safe to trim
        return field.trim();
    }

    public final void processNotification() {
        for (int attempt = 1; attempt <= getMaxRetryAttempts(); attempt++){
            try {
                logger.info("========== Starting Notification Process ==========");
                logger.info("Notification # " + getID() + " [" + status + "] ");
                sendMessage();
                displayNotification();
                status = NotificationStatus.SENT;
                logger.info("Notification status: " + getID() + ": " + status);
                logger.info("==========  Notification Process Complete ==========");
                break;
            } catch (IllegalArgumentException e){
                logger.error("Validation error (won't retry): " + e.getMessage());
                status = NotificationStatus.FAILED;
                break;
            } catch (Exception e) {
                int attemptsLeft = getMaxRetryAttempts() - attempt;
                status = NotificationStatus.PENDING;
                logger.warn("Processing of message failed. " + attemptsLeft + " attempt(s) left. ");
            }
        }

        if (status == NotificationStatus.PENDING) {
            status = NotificationStatus.FAILED;
            logger.error("Notification #" + getID() + " failed after " + getMaxRetryAttempts() + " attempts");
        }
    }

    public String toString() {
        return "Notification#" + id + " [" + status + "]";
    }

    public NotificationStatus getStatus() {
        return status;
    }

    public String getSender() {
        return sender;
    }

    public String getMessage() {
        return message;
    }

    public int getID() {
        return id;
    }

    public static int getMaxRetryAttempts() {
        return MAX_RETRY_ATTEMPTS;
    }
}