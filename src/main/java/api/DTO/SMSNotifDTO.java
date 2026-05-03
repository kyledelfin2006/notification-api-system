package api.DTO;

public class SMSNotifDTO extends NotificationDTO{

    private String receiverPhoneNumber;
    private String senderPhoneNumber;

    public SMSNotifDTO(){}

    public String getReceiverPhoneNumber() {
        return receiverPhoneNumber;
    }
    public void setReceiverPhoneNumber(String receiverPhoneNumber) {
        this.receiverPhoneNumber = receiverPhoneNumber;
    }
    public String getSenderPhoneNumber() {
        return senderPhoneNumber;
    }
    public void setSenderPhoneNumber(String senderPhoneNumber) {
        this.senderPhoneNumber = senderPhoneNumber;
    }
}
