package HW2;

import java.util.Date;
import java.util.UUID;

public class Answer {
    private String id;
    private String questionId;
    private String answerText;
    private Date createdAt;
    private Date updatedAt;

    public Answer(String questionId, String answerText) {
        if (answerText == null || answerText.trim().isEmpty()) {
            throw new IllegalArgumentException("Answer text cannot be empty");
        }
        this.id = UUID.randomUUID().toString();
        this.questionId = questionId;
        this.answerText = answerText.trim();
        this.createdAt = new Date();
        this.updatedAt = new Date();
    }

    // Getters
    public String getId() { return id; }
    public String getQuestionId() { return questionId; }
    public String getAnswerText() { return answerText; }
    public Date getCreatedAt() { return createdAt; }
    public Date getUpdatedAt() { return updatedAt; }

    // Setter with validation
    public void setAnswerText(String answerText) {
        if (answerText == null || answerText.trim().isEmpty()) {
            throw new IllegalArgumentException("Answer text cannot be empty");
        }
        this.answerText = answerText.trim();
        this.updatedAt = new Date();
    }

    @Override
    public String toString() {
        return answerText;
    }
}