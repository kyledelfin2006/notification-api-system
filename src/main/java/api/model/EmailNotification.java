package api.model;

import api.loggers.Logger;
import api.util.Validator;

public class EmailNotification extends Notification {

    private final String senderEmail;
    private final String receiverEmail;

    public EmailNotification(Logger logger, String sender, String senderEmail,
                             String receiverEmail, String message) {
        super(sender, message, logger);

        // Validate and store trimmed emails
        this.senderEmail = Validator.requireMatches(senderEmail, "Sender Email", Validator.EMAIL_PATTERN);
        this.receiverEmail = Validator.requireMatches(receiverEmail, "Receiver Email", Validator.EMAIL_PATTERN);
    }

    @Override
    public void displayNotification() {
        logger.info("Email from " + getSenderEmail() + " to " + getReceiverEmail() +
                ": " + getMessage());
    }

    @Override
    public void sendMessage() {
        logger.info("Sending Email Notification to " + getReceiverEmail() + ": " + getMessage());
    }

    public String getSenderEmail() {
        return senderEmail;
    }
    public String getReceiverEmail() {
        return receiverEmail;
    }
}