package HW2;

import java.util.Date;
import java.util.UUID;

/**
 * Represents a review written by a reviewer for a question, answer, or other content
 */
public class Review {
    private String id;
    private String reviewer; // Username of the reviewer
    private String content;  // Review content text
    private String associatedId; // ID of the item being reviewed (question, answer, etc.)
    private int rating;      // Rating (0-5)
    private String type;     // Type of review (Question, Answer, etc.)
    private Date createdAt;  // When the review was created
    private Date updatedAt;  // When the review was last updated
    
    private static final int MIN_RATING = 0;
    private static final int MAX_RATING = 5;
    private static final int DEFAULT_RATING = 0;
    private static final String DEFAULT_TYPE = "Question";
    
    /**
     * Creates a new review with default rating and type
     * @param reviewer Username of the reviewer
     * @param content Content of the review
     * @param associatedId ID of the item being reviewed
     */
    public Review(String reviewer, String content, String associatedId) {
        this.id = UUID.randomUUID().toString();
        this.reviewer = reviewer;
        this.content = content;
        this.associatedId = associatedId;
        this.rating = DEFAULT_RATING;
        this.type = DEFAULT_TYPE;
        this.createdAt = new Date();
        this.updatedAt = new Date();
    }
    
    /**
     * Creates a new review with specified rating
     * @param reviewer Username of the reviewer
     * @param content Content of the review
     * @param associatedId ID of the item being reviewed
     * @param rating Rating (0-5)
     */
    public Review(String reviewer, String content, String associatedId, int rating) {
        this(reviewer, content, associatedId);
        setRating(rating); // Use setter to apply validation
    }
    
    /**
     * Creates a new review with specified type
     * @param reviewer Username of the reviewer
     * @param content Content of the review
     * @param associatedId ID of the item being reviewed
     * @param type Type of review (Question, Answer, etc.)
     */
    public Review(String reviewer, String content, String associatedId, String type) {
        this(reviewer, content, associatedId);
        this.type = type;
    }
    
    /**
     * Creates a new review with specified rating and type
     * @param reviewer Username of the reviewer
     * @param content Content of the review
     * @param associatedId ID of the item being reviewed
     * @param rating Rating (0-5)
     * @param type Type of review (Question, Answer, etc.)
     */
    public Review(String reviewer, String content, String associatedId, int rating, String type) {
        this(reviewer, content, associatedId);
        setRating(rating); // Use setter to apply validation
        this.type = type;
    }

    // Getters
    public String getId() {
        return id;
    }
    
    public String getReviewer() {
        return reviewer;
    }
    
    public String getContent() {
        return content;
    }
    
    public String getAssociatedId() {
        return associatedId;
    }
    
    public int getRating() {
        return rating;
    }
    
    public String getType() {
        return type;
    }
    
    public Date getCreatedAt() {
        return createdAt;
    }
    
    public Date getUpdatedAt() {
        return updatedAt;
    }
    
    // Setters
    public void setContent(String content) {
        this.content = content;
        this.updatedAt = new Date(); // Update the timestamp
    }
    
    /**
     * Sets the rating, ensuring it's between 0-5
     * @param rating Rating value
     */
    public void setRating(int rating) {
        // Apply validation rules
        if (rating < MIN_RATING) {
            this.rating = MIN_RATING;
        } else if (rating > MAX_RATING) {
            this.rating = MAX_RATING;
        } else {
            this.rating = rating;
        }
        this.updatedAt = new Date(); // Update the timestamp
    }
    
    public void setType(String type) {
        this.type = type;
        this.updatedAt = new Date(); // Update the timestamp
    }
    
    // For tests and debugging
    public void setId(String id) {
        this.id = id;
    }
    public void setCreatedDate(Date createdAt) {
        if (createdAt == null) {
            throw new IllegalArgumentException("Created date cannot be null");
        }
        this.createdAt = createdAt;
        
        // If the update timestamp is before the new creation date, update it as well
        if (this.updatedAt.before(createdAt)) {
            this.updatedAt = createdAt;
        }
    }
    public void setUpdatedDate(Date updatedAt) {
        if (updatedAt == null) {
            throw new IllegalArgumentException("Updated date cannot be null");
        }
        
        // Ensure updated date is not before creation date
        if (updatedAt.before(this.createdAt)) {
            throw new IllegalArgumentException("Updated date cannot be before creation date");
        }
        
        this.updatedAt = updatedAt;
    }
}