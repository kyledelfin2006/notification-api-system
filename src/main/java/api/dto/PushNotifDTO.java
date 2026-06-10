package api.dto;

import api.util.Validator;

public class PushNotifDTO extends NotificationDTO {

    private String deviceToken;
    private String backupDeviceToken; // optional

    public PushNotifDTO(){}

    public String getDeviceToken() {
        return deviceToken;
    }
    public String getBackupDeviceToken() {
        return backupDeviceToken;
    }

    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }
    // When setting backup token, validate only if present
    public void setBackupDeviceToken(String token) {
        this.backupDeviceToken = Validator.tryValidate(token, Validator.ALPHANUMERIC_PATTERN)
                .orElse(null); // invalid or blank becomes null
    }
}
