package HW2;

import java.util.Date;

public class Message {
    private String id;
    private String sender;
    private String recipient;
    private String content;
    private Date sentAt;
    private String relatedId;

    public Message(String sender, String recipient, String content) {
        this.id = java.util.UUID.randomUUID().toString();
        this.sender = sender;
        this.recipient = recipient;
        this.content = content;
        this.sentAt = new Date();
    }

    /**
     * Creates a message with a related ID (for feedback on reviews, questions, etc.)
     * 
     * @param sender The username of the sender
     * @param recipient The username of the recipient
     * @param content The message content
     * @param relatedId The ID of the related item (review, question, etc.)
     */
    public Message(String sender, String recipient, String content, String relatedId) {
        this(sender, recipient, content); // Call the existing constructor
        this.relatedId = relatedId;
    }

    // Getters
    public String getId() { return id; }
    public String getSender() { return sender; }
    public String getRecipient() { return recipient; }
    public String getContent() { return content; }
    public Date getSentAt() { return sentAt; }

    /**
     * Gets the ID of the related item for this message
     * @return The related item ID
     */
    public String getRelatedId() {
        return relatedId;
    }

    /**
     * Sets the ID of this message
     * @param id The ID to set for this message
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Sets the sent timestamp for this message
     * @param sentAt The date when the message was sent
     * @throws IllegalArgumentException if sentAt is null
     */
    public void setSentAt(Date sentAt) {
        if (sentAt == null) {
            throw new IllegalArgumentException("Sent date cannot be null");
        }
        this.sentAt = sentAt;
    }

    /**
     * Sets the ID of the related item for this message
     * @param relatedId The ID of the related item (review, question, etc.)
     */
    public void setRelatedId(String relatedId) {
        this.relatedId = relatedId;
    }

    @Override
    public String toString() {
        return String.format("From: %s - %s", sender, content);
    }
}