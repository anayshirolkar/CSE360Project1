package application;

import java.util.ArrayList;
import java.util.List;

public class Answers {
    private List<Answer> answersList = new ArrayList<>();

    public void addAnswer(Answer answer) {
        answersList.add(answer);
    }

    public void removeAnswer(Answer answer) {
        answersList.remove(answer);
    }

    public void updateAnswer(Answer answer) {
        // Placeholder for updating answer (e.g., edit text)
    }

    public List<Answer> getAnswers() {
        return answersList;
    }

    public void deleteAnswer(Answer answer) {
        answersList.remove(answer);
    }
}
