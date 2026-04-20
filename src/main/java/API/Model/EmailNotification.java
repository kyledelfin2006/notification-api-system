package API.Model;

import API.Logger.Logger;

public class EmailNotification extends Notification {

    private final String senderEmail;
    private final String receiverEmail;

    public EmailNotification(Logger logger, String sender, String senderEmail,
                             String receiverEmail, String message) {
        super(sender, message, logger);

        // Validate and store trimmed emails
        this.senderEmail = validateAndTrimEmail(senderEmail, "Sender Email");
        this.receiverEmail = validateAndTrimEmail(receiverEmail, "Receiver Email");
    }

    private String validateAndTrimEmail(String email, String fieldName) {
        // Check for null
        if (email == null) {
            String errorMsg = fieldName + " cannot be null";
            logger.error(errorMsg);
            throw new IllegalArgumentException(errorMsg);
        }

        // Trim whitespace
        String trimmed = email.trim();

        // Check for empty after trimming
        if (trimmed.isEmpty()) {
            String errorMsg = fieldName + " cannot be empty";
            logger.error(errorMsg);
            throw new IllegalArgumentException(errorMsg);
        }

        // Validate email format (basic but comprehensive)
        if (!isValidEmail(trimmed)) {
            String errorMsg = fieldName + " must be a valid email address: " + trimmed;
            logger.error(errorMsg);
            throw new IllegalArgumentException(errorMsg);
        }

        return trimmed;
    }

    private boolean isValidEmail(String email) {
        // Basic email regex that handles most valid cases
        // Allows: local-part@domain.tld
        // Prevents: consecutive dots, leading/trailing dots, invalid characters
        String emailRegex = "^[\\p{L}\\p{N}+_.-]+@[\\p{L}\\p{N}.-]+\\.[\\p{L}]{2,}$";

        if (!email.matches(emailRegex)) {
            return false;
        }

        // Additional checks for consecutive dots
        String localPart = email.substring(0, email.indexOf('@'));
        if (localPart.contains("..") || localPart.startsWith(".") || localPart.endsWith(".")) {
            return false;
        }

        String domain = email.substring(email.indexOf('@') + 1);
        if (domain.contains("..") || domain.startsWith(".") || domain.endsWith(".")) {
            return false;
        }

        return true;
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