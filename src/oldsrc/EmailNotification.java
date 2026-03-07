public class EmailNotification extends Notification {

    private final String senderEmail;
    private final String receiverEmail;

    public String getReceiverEmail() {
        return receiverEmail;
    }
    public String getSenderEmail(){
        return senderEmail;
    }

    // Constructor
    public EmailNotification(Logger logger, String sender, String senderEmail, String receiverEmail, String message) {
        super(sender, message, logger); // calls abstract class constructor
        validateField(receiverEmail, "Receiver Email ");
        validateField(senderEmail,"Sender Email ");

        this.senderEmail = senderEmail;
        this.receiverEmail = receiverEmail;

    }

    @Override
    public void displayNotification() {
        logger.info("Email from " + getSenderEmail() + " to " + getReceiverEmail() + " Message: " + getMessage());
    }

    @Override
    public void sendMessage() {
        logger.info("Sending Email Notification: " + " to " + getReceiverEmail() + ":" + getMessage() + "\n");
    }
}
