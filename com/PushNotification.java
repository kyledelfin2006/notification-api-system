package kyle.com;
public class PushNotification extends Notification {

    private final String deviceToken;
    

    public String getDeviceToken(){
        return deviceToken;
    }

    public PushNotification(Logger logger, String sender, String deviceToken, String message) {
        super(sender, message,logger); // calls abstract class constructor
        validateField(deviceToken, "Device Token");
        this.deviceToken = deviceToken;
    }

    @Override
    public void displayNotification() {
       logger.info("Push Notification from " + getSender() +
                " to device " + getDeviceToken() + ": " + getMessage() + "\n");    }

    @Override
    public void sendMessage() {
        logger.info("Sending Push Notification to " + getDeviceToken() + ": " + getMessage() +"\n");
    }
}
