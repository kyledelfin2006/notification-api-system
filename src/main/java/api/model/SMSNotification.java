package api.model;
import api.loggers.Logger;
import api.util.Validator;

public class SMSNotification extends Notification {

    private final String receiverPhoneNumber;
    private final String senderPhoneNumber;

    public SMSNotification(Logger logger, String sender, String senderPhoneNumber,
                           String receiverPhoneNumber, String message) {
        super(sender, message, logger);

        // Validate and store trimmed phone numbers
        this.senderPhoneNumber = Validator.requireMatches(senderPhoneNumber,"Sender phone number", Validator.PHONE_PATTERN);
        this.receiverPhoneNumber =  Validator.requireMatches(receiverPhoneNumber, "Receiver phone number", Validator.PHONE_PATTERN);
    }

    @Override
    public void displayMessage() {
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