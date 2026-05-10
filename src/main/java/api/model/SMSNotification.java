package api.model;
import api.loggers.Logger;

public class SMSNotification extends Notification {

    private final String receiverPhoneNumber;
    private final String senderPhoneNumber;

    private static final String SMS_REGEX = "\\d{11}";


    public SMSNotification(Logger logger, String sender, String senderPhoneNumber,
                           String receiverPhoneNumber, String message) {
        super(sender, message, logger);

        // Validate and store trimmed phone numbers
        this.senderPhoneNumber = validateAndTrim(senderPhoneNumber, "Sender phone number", SMS_REGEX);
        this.receiverPhoneNumber =  validateAndTrim(receiverPhoneNumber, "Receiver phone number", SMS_REGEX);
    }

    @Override
    public void displayNotification() {
        logger.info("SMS from " + getSender() + " (" + getSenderPhoneNumber() +
                ") to " + getReceiverPhoneNumber() + ": " + getMessage());
    }

    @Override
    public void sendMessage() {
        logger.info("Sending SMS to " + getReceiverPhoneNumber() + ": " + getMessage());
    }

    public String getReceiverPhoneNumber() {
        return receiverPhoneNumber;
    }

    public String getSenderPhoneNumber() {
        return senderPhoneNumber;
    }
}