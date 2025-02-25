package application;

import java.util.ArrayList;
import java.util.List;

public class Questions {
    private List<Question> questionsList = new ArrayList<>();

    public void addQuestion(Question question) {
        questionsList.add(question);
    }

    public void removeQuestion(Question question) {
        questionsList.remove(question);
    }

    public void updateQuestion(Question question) {
        // Placeholder for updating question (e.g., edit text)
    }

    public List<Question> getQuestions() {
        return questionsList;
    }

    public void deleteQuestion(Question question) {
        questionsList.remove(question);
    }
}
