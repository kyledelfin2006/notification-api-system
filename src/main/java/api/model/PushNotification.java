package api.model;

import api.loggers.Logger;
import api.util.Validator;

public class PushNotification extends Notification {

    private final String deviceToken;


    public PushNotification(Logger logger, String sender, String deviceToken, String message) {
        super(sender, message,logger); // calls abstract class constructor

        this.deviceToken = Validator.requireNonBlank(deviceToken, "Device Token");
    }

    @Override
    public void displayMessage() {
       logger.info("Push Notification from " + getSender() +
                " to device " + getDeviceToken() + ": " + getMessage() + "\n");
    }

    @Override
    public void sendMessage() {
        logger.info("Sending Push Notification to " + getDeviceToken() + ": " + getMessage() +"\n");
    }

    public String getDeviceToken(){
        return deviceToken;
    }
}
