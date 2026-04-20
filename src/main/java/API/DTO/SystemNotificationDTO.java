package API.DTO;

public class SystemNotificationDTO extends NotificationDTO {

    private String deviceOS;
    private String deviceToken;

    public SystemNotificationDTO(){}


    public String getDeviceOS() {
        return deviceOS;
    }

    public void setDeviceOS(String deviceOS) {
        this.deviceOS = deviceOS;
    }

    public String getDeviceToken() {
        return deviceToken;
    }

    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }
}
