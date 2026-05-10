package api.model;

import api.loggers.Logger;

public class EmailNotification extends Notification {

    private final String senderEmail;
    private final String receiverEmail;

    private static final String EMAIL_REGEX = "^[\\p{L}\\p{N}+_.-]+@[\\p{L}\\p{N}.-]+\\.[\\p{L}]{2,}$";

    public EmailNotification(Logger logger, String sender, String senderEmail,
                             String receiverEmail, String message) {
        super(sender, message, logger);

        // Validate and store trimmed emails
        this.senderEmail = validateAndTrim(senderEmail, "Sender Email", EMAIL_REGEX);
        this.receiverEmail = validateAndTrim(receiverEmail, "Receiver Email", EMAIL_REGEX);
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