package kyle.com;
public class SMSNotification extends Notification {

    private final String receiverPhoneNumber;
    private final String senderPhoneNumber;

    // Constructor
    public SMSNotification(kyle.com.Logger logger, String sender, String senderPhoneNumber, String receiverPhoneNumber, String message) {
        super(sender, message,logger); // calls abstract class constructor
        validatePhoneNumber(receiverPhoneNumber);
        validatePhoneNumber(senderPhoneNumber);

        this.senderPhoneNumber = senderPhoneNumber;
        this.receiverPhoneNumber = receiverPhoneNumber;

    }

    @Override
    public void displayNotification() {
        logger.info("SMS from " + getSender() + " ( "
                + getSenderPhoneNumber() + ") to " + getReceiverPhoneNumber() + ": " + getMessage() + "\n");
    }

    @Override
    public void sendMessage() {
        logger.info("Sending SMS to " + getReceiverPhoneNumber() + ": " + getMessage() + "\n");
    }

    protected void validatePhoneNumber(String value) {
        if (value == null) {
            logger.error("Phone number cannot be null");
            throw new IllegalArgumentException();
        }

        value = value.trim(); // remove leading/trailing spaces

        if (!value.matches("\\d{11}")) { // regex: exactly 11 digits
            logger.error("Phone number must be exactly 11 digits");
            throw new IllegalArgumentException();
        }
    }




    public String getReceiverPhoneNumber() {
        return receiverPhoneNumber;
    }
    public String  getSenderPhoneNumber() {
        return senderPhoneNumber;
    }

}
