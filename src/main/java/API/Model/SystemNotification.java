package API.Model;

import API.Logger.Logger;
import API.Model.Notification;

public class SystemNotification extends Notification {

    private final String deviceOS;
    private final String deviceToken;


    public SystemNotification(Logger logger, String sender, String deviceOS, String deviceToken, String message) {
        super(sender, message,logger); // calls abstract class constructor
        validateField(deviceToken, "Device Token");
        validateField(deviceOS, "DeviceOS");

        this.deviceToken = deviceToken;
        this.deviceOS = deviceOS;

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
