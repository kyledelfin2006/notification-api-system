package api.model;

import api.loggers.Logger;

public class SystemNotification extends Notification {

    private final String deviceOS;
    private final String deviceToken;


    public SystemNotification(Logger logger, String sender, String deviceOS, String deviceToken, String message) {
        super(sender, message,logger);
        this.deviceToken = validateAndTrim(deviceToken, "Device Token");
        this.deviceOS = validateAndTrim(deviceOS, "Device OS");

    }

    @Override
    public void displayNotification() {
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
