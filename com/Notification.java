package kyle.com;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = EmailNotification.class, name = "email"),
        @JsonSubTypes.Type(value = SMSNotification.class, name = "sms"),
        @JsonSubTypes.Type(value = PushNotification.class, name = "push"),
        @JsonSubTypes.Type(value = SystemNotification.class, name = "system")
})


public abstract class Notification implements Sendable{

    private NotificationStatus status;
    private final String sender;
    private final String message;
    private static int nextId = 0;
    private final int id;
    protected final Logger logger;
    private static final int MAX_RETRY_ATTEMPTS = 3;
    private static final int RETRY_DELAY_MS = 1000;
    protected enum NotificationStatus {
        PENDING,
        SENT,
        FAILED
    }


    protected Notification(String sender, String message, Logger logger) {
        validateField(sender, "Sender");
        validateField(message, "Message");

        this.logger = logger;
        this.id = nextId++;
        this.sender = sender;
        this.message = message;
        this.status = NotificationStatus.PENDING;

    }

    public NotificationStatus getStatus() {
        return status;
    }

    public abstract void displayNotification();

    // All subtypes can use validateField
    protected void validateField(String value, String fieldName) {
        if (value == null || value.isEmpty()) {
            throw new IllegalArgumentException(fieldName + " cannot be empty");
        }
    }

    public final void processNotification() { // Cannot change logic

        // Processes until out of attempts (Retry Feature)
        for (int attempt = 1; attempt <= getMaxRetryAttempts(); attempt++){
            try {

                logger.info("========== Starting Notification Process ==========");
                logger.info("Notification # " + getID() + " [" + status + "] ");
                sendMessage();      // Abstract method - each subclass implements differently
                displayNotification(); // Abstract method - each subclass implements differently
                status = NotificationStatus.SENT;
                logger.info("Notification status: " + getID() + ": " + status);
                logger.info("==========  Notification Process Complete ==========");
                break;

              } catch (IllegalArgumentException e){
                logger.error("Validation error (won't retry): " + e.getMessage());
                status = NotificationStatus.FAILED;
                break;

            }  catch (Exception e) {
               int attemptsLeft = getMaxRetryAttempts() - attempt;
                status = NotificationStatus.FAILED;
             logger.warn("Processing of message failed. " + attemptsLeft + " attempt(s) left. ");
            }
        }

    }



    public String toString() {
        return "Notification#" + id + " [" + status + "]";
    }
    public String getSender(){
        return sender;
    }
    public String getMessage(){
        return message;
    }
    public int getID(){ return id;}
    public static int getMaxRetryAttempts() {
        return MAX_RETRY_ATTEMPTS;
    }


}


