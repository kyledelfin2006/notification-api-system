package api.DTO;

public class SystemNotifDTO extends NotificationDTO {

    private String deviceOS;
    private String deviceToken;

    public SystemNotifDTO(){}

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
