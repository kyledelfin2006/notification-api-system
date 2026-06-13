package api.model;

import api.loggers.Logger;
import api.util.Validator;

public class SystemNotification extends Notification {

    private final String deviceOS;
    private final String deviceToken;

    public SystemNotification(Logger logger, String sender, String deviceOS, String deviceToken, String message) {
        super(sender, message,logger);
        this.deviceToken = Validator.requireNonBlank(deviceToken, "Device Token");
        this.deviceOS = Validator.requireNonBlank(deviceOS, "Device OS");
    }

    @Override
    public void displayMessage() {
        logger.info("Notification from: " + deviceOS + " to: " + deviceToken + " Message: " + getMessage());
    }

    @Override
    public void sendMessage() {
        logger.info("Sending System Notification to: " + getDeviceOS() + " Message: " + getMessage());
    }


    public String getDeviceOS() {
        return deviceOS;
    }
    public String getDeviceToken() {
        return deviceToken;
    }
}
