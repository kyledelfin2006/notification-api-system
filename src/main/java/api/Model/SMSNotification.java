package api.Model;
import api.Logger.Logger;

public class SMSNotification extends Notification {

    private final String receiverPhoneNumber;
    private final String senderPhoneNumber;


    public SMSNotification(Logger logger, String sender, String senderPhoneNumber,
                           String receiverPhoneNumber, String message) {
        super(sender, message, logger);

        // Validate and store trimmed phone numbers
        this.senderPhoneNumber = validateAndTrimPhoneNumber(senderPhoneNumber, "Sender phone number");
        this.receiverPhoneNumber = validateAndTrimPhoneNumber(receiverPhoneNumber, "Receiver phone number");
    }

    private String validateAndTrimPhoneNumber(String phoneNumber, String fieldName) {
        if (phoneNumber == null) {
            logger.error(fieldName + " cannot be null");
            throw new IllegalArgumentException(fieldName + " cannot be null");
        }

        String trimmed = phoneNumber.trim();

        if (trimmed.isEmpty()) {
            logger.error(fieldName + " cannot be empty");
            throw new IllegalArgumentException(fieldName + " cannot be empty");
        }

        if (!trimmed.matches("\\d{11}")) {
            logger.error(fieldName + " must be exactly 11 digits, got: " + trimmed);
            throw new IllegalArgumentException(fieldName + " must be exactly 11 digits");
        }

        return trimmed;
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