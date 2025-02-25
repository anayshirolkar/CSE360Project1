package HW2;

import java.util.Date;

public class Answer {

    private int id;
    private int questionId;
    private String text;
    private Date createdAt;
    private Date updatedAt;

    public Answer(int id, int questionId, String text) {
        this.id = id;
        this.questionId = questionId;
        this.text = text;
        this.createdAt = new Date();
        this.updatedAt = new Date();
    }

    public int getId() {
        return id;
    }

    public int getQuestionId() {
        return questionId;
    }

    public String getText() {
        return text;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void updateText(String newText) {
        this.text = newText;
        this.updatedAt = new Date();
    }

    public void delete() {
        // Delete the answer from the system, placeholder for actual logic
    }

    public static Answer createAnswer(int id, int questionId, String text) {
        return new Answer(id, questionId, text);
    }

    public Answer getAnswer() {
        return this;
    }

    public void updateAnswer(String newText) {
        updateText(newText);
    }

    public void deleteAnswer() {
        delete();
    }
    public void setText(String newText) {
        this.text = newText;
    }
}
