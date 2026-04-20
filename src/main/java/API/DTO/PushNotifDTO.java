package API.DTO;

public class PushNotifDTO extends NotificationDTO {

    private String deviceToken;

    public PushNotifDTO(){}

    public String getDeviceToken() {
        return deviceToken;
    }

    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }
}
