
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

    public NotificationStatus getStatus() {
        return status;
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

    public static int getRetryDelayMs() {
        return RETRY_DELAY_MS;
    }

    public abstract void displayNotification();

    protected void validateField(String value, String fieldName) {
        if (value == null || value.isEmpty()) {
            throw new IllegalArgumentException(fieldName + " cannot be empty");
        }
    }

    public final void process() {

        for (int attempt = 1; attempt <= getMaxRetryAttempts(); attempt++){
            try {

                logger.info("=== Starting notification process ===\n");
                logger.info("Notification # " + id + " [" + status + "] ");
                validateNotification();
                sendMessage();      // Abstract method - each subclass implements differently
                displayNotification(); // Abstract method - each subclass implements differently
                logDelivery();
                status = NotificationStatus.SENT;
                logger.info("Notification status: " + status);
                logger.info("=== Notification process complete ===");


                break;

              } catch (IllegalArgumentException e){
                logger.error("Validation error (won't retry): " + e.getMessage());
                status = NotificationStatus.FAILED;
                break;
            }  catch (Exception e) {
               int attemptsLeft = getMaxRetryAttempts() - attempt;
                status = NotificationStatus.FAILED;
             logger.warn("Processing of message failed. " + attemptsLeft + " attempt(s) left. ");
                if (attemptsLeft > 0) {
                    try { Thread.sleep(getRetryDelayMs()); } catch (InterruptedException ie) { Thread.currentThread().interrupt(); }
                }
            }
        }

    }

    private void validateNotification() {
       logger.info("✓ Validating " + this.getClass().getSimpleName() + "...\n");
    }

    private void logDelivery() {
      logger.info("✓ Logging delivery of notification\n");
    }

    public String toString() {
        return "Notification#" + id + " [" + status + "]";
    }



}


