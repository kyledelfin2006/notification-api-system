package api.model;
import api.loggers.Logger;
import api.util.NotificationIDGenerator;
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
        this.logger = logger;
        this.id = NotificationIDGenerator.generateNextID();
        this.sender =  validateAndTrim(sender,"Sender");
        this.message = validateAndTrim(message,"Message");
        this.status = NotificationStatus.PENDING;
    }

    // Polymorphic Implementation
    public abstract void displayNotification();

    public final void processNotification() {
        for (int attempt = 1; attempt <= getMaxRetryAttempts(); attempt++){
            try {
                logger.info(" | Starting Notification Process | ");
                logger.info("Notification # " + getID() + " [" + status + "] ");
                sendMessage(); // Polymorphic Call
                displayNotification(); // Polymorphic Call
                status = NotificationStatus.SENT;
                logger.info("Notification status: " + getID() + ": " + status);
                logger.info(" | Notification Process Complete | ");
                break;
            } catch (IllegalArgumentException e){
                logger.error("Validation error (won't retry): " + e.getMessage());
                status = NotificationStatus.FAILED;
                break;
            } catch (Exception e) {
                int attemptsLeft = getMaxRetryAttempts() - attempt;
                status = NotificationStatus.PENDING;
                logger.warn("Processing of message faile d. " + attemptsLeft + " attempt(s) left. ");
            }
        }

        if (status == NotificationStatus.PENDING) {
            status = NotificationStatus.FAILED;
            logger.error("Notification #" + getID() + " failed after " + getMaxRetryAttempts() + " attempts");
        }
    }

    protected String validateAndTrim(String value, String fieldName, String regex) {
        if (value == null || value.trim().isEmpty()) {
            String msg = fieldName + " cannot be null or empty";
            logger.error(msg);
            throw new IllegalArgumentException(msg);
        }
        String trimmed = value.trim();
        if (!trimmed.matches(regex)) {
            String msg = fieldName + " is invalid: " + trimmed;
            logger.error(msg);
            throw new IllegalArgumentException(msg);
        }
        return trimmed;
    }

    protected String validateAndTrim(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            String msg = fieldName + " Cannot be null or empty";
            logger.error(msg);
            throw new IllegalArgumentException(msg);
        }
        return value.trim();
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