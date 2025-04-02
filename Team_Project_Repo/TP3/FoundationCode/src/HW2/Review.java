package HW2;

import java.util.Date;

public class Review {
    private String id;
    private String reviewer;
    private String content;
    private String associatedId; // ID of the question or answer being reviewed
    private Date createdAt;
    private Date updatedAt;
    private int rating; // Rating given by the reviewer (1-5)
    private String type; // Type of the review (Question, Answer, Comment)

    public Review(String reviewer, String content, String associatedId) {
        this(reviewer, content, associatedId, 0);
    }
    
    public Review(String reviewer, String content, String associatedId, int rating) {
        this.id = java.util.UUID.randomUUID().toString();
        this.reviewer = reviewer;
        this.content = content;
        this.associatedId = associatedId;
        this.rating = rating;
        this.createdAt = new Date();
        this.updatedAt = new Date();
        this.type = "Question"; // Default type
    }
    
    /**
     * Constructor with type parameter
     * @param reviewer The username of the reviewer
     * @param content The content of the review
     * @param associatedId The ID of the associated item
     * @param type The type of the review (Question, Answer, Comment)
     */
    public Review(String reviewer, String content, String associatedId, String type) {
        this.id = java.util.UUID.randomUUID().toString();
        this.reviewer = reviewer;
        this.content = content;
        this.associatedId = associatedId;
        this.rating = 0; // Default rating
        this.type = type;
        this.createdAt = new Date();
        this.updatedAt = new Date();
    }
    
    /**
     * Complete constructor with all parameters
     * @param reviewer The username of the reviewer
     * @param content The content of the review
     * @param associatedId The ID of the associated item
     * @param rating The rating (0-5)
     * @param type The type of the review
     */
    public Review(String reviewer, String content, String associatedId, int rating, String type) {
        this.id = java.util.UUID.randomUUID().toString();
        this.reviewer = reviewer;
        this.content = content;
        this.associatedId = associatedId;
        this.rating = rating;
        this.type = type;
        this.createdAt = new Date();
        this.updatedAt = new Date();
    }

    // Getters and setters
    public String getId() { return id; }
    public String getReviewer() { return reviewer; }
    public String getContent() { return content; }
    public String getAssociatedId() { return associatedId; }
    public Date getCreatedAt() { return createdAt; }
    public Date getUpdatedAt() { return updatedAt; }
    public int getRating() { return rating; }
    public String getType() { return type; }

    public void setContent(String content) {
        this.content = content;
        this.updatedAt = new Date();
    }
    
    public void setRating(int rating) {
        if (rating < 0 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 0 and 5");
        }
        this.rating = rating;
        this.updatedAt = new Date();
    }
    
    public void setType(String type) {
        this.type = type;
        this.updatedAt = new Date();
    }
}