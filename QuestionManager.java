package HW2;

import java.util.ArrayList;
import java.util.List;

public class QuestionManager {

    private List<Question> questionList;

    public QuestionManager() {
        this.questionList = new ArrayList<>();
    }

    // Method to add a question
    public Question addQuestion(Question q) {
        questionList.add(q);
        return q;
    }

    // Method to get all questions
    public List<Question> getQuestions() {
        return new ArrayList<>(questionList);  // Return a copy to prevent modification
    }

    // Method to update a question
    public Question updateQuestion(Question q) {
        for (int i = 0; i < questionList.size(); i++) {
            if (questionList.get(i).getId() == q.getId()) {
                questionList.set(i, q);
                return q;
            }
        }
        return null;  // Return null if the question was not found
    }

    // Method to delete a question
    public void deleteQuestion(Question q) {
        questionList.removeIf(question -> question.getId() == q.getId());
    }

    // Optionally, you could implement a method to remove all questions
    public void removeAllQuestions() {
        questionList.clear();
    }
}
