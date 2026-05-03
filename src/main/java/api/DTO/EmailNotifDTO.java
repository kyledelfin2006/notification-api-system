package api.DTO;

public class EmailNotifDTO extends NotificationDTO{

    private String senderEmail;
    private String receiverEmail;

    public EmailNotifDTO() {}

    public String getSenderEmail() {
        return senderEmail;
    }
    public void setSenderEmail(String senderEmail) {
        this.senderEmail = senderEmail;
    }
    public String getReceiverEmail() {
        return receiverEmail;
    }
    public void setReceiverEmail(String receiverEmail) {
        this.receiverEmail = receiverEmail;
    }

}
