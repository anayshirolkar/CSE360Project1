package HW2;

import java.util.Date;

public class Message {
    private String id;
    private String sender;
    private String recipient;
    private String content;
    private Date sentAt;

    public Message(String sender, String recipient, String content) {
        this.id = java.util.UUID.randomUUID().toString();
        this.sender = sender;
        this.recipient = recipient;
        this.content = content;
        this.sentAt = new Date();
    }

    // Getters
    public String getId() { return id; }
    public String getSender() { return sender; }
    public String getRecipient() { return recipient; }
    public String getContent() { return content; }
    public Date getSentAt() { return sentAt; }
}