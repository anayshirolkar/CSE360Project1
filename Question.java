package HW2;

import java.util.Date;

public class Question {

    private int id;
    private String text;
    private Date createdAt;
    private Date updatedAt;

    public Question(int id, String text) {
        this.id = id;
        this.text = text;
        this.createdAt = new Date();
        this.updatedAt = new Date();
    }

    public int getId() {
        return id;
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
        // Delete the question from the system, placeholder for actual logic
    }

    public static Question createQuestion(int id, String text) {
        return new Question(id, text);
    }

    public Question getQuestion() {
        return this;
    }

    public void updateQuestion(String newText) {
        updateText(newText);
    }

    public void deleteQuestion() {
        delete();
    }
    
    public void setText(String newText) {
        this.text = newText;
    }
}
