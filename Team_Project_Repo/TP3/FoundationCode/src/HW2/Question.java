
package HW2;

import java.util.Date;
import java.util.UUID;

public class Question {
    private String id;
    private String title;
    private String description;
    private Date createdAt;
    private Date updatedAt;
    private String author; // New field

    public Question(String title, String description, String author) {
        this.id = UUID.randomUUID().toString();
        this.title = title;
        this.description = description;
        this.author = author;
        this.createdAt = new Date();
        this.updatedAt = new Date();
    }

    // Getters
    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public Date getCreatedAt() { return createdAt; }
    public Date getUpdatedAt() { return updatedAt; }
    public String getAuthor() { return author; }

    // Setters with validation
    public void setTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Title cannot be empty");
        }
        this.title = title.trim();
        this.updatedAt = new Date();
    }

    public void setDescription(String description) {
        if (description == null || description.trim().isEmpty()) {
            throw new IllegalArgumentException("Description cannot be empty");
        }
        this.description = description.trim();
        this.updatedAt = new Date();
    }
    public void setId(String id) {
        this.id = id;
    }
    public void setAuthor(String author) {
        this.author = author;
    }

    @Override
    public String toString() {
        return title + "\n" + description;
    }
}
