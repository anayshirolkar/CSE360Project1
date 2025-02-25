package HW2;

import java.util.ArrayList;
import java.util.List;

public class AnswerManager {

    private List<Answer> answerList;

    public AnswerManager() {
        this.answerList = new ArrayList<>();
    }

    public Answer addAnswer(Answer a) {
        answerList.add(a);
        return a;
    }

    public List<Answer> getAnswers() {
        return new ArrayList<>(answerList);  // Return a copy to prevent modification
    }

    public Answer updateAnswer(Answer a) {
        for (int i = 0; i < answerList.size(); i++) {
            if (answerList.get(i).getId() == a.getId()) {
                answerList.set(i, a);
                return a;
            }
        }
        return null;  // Return null if the answer was not found
    }

    public void removeAnswer(Answer a) {
        answerList.removeIf(answer -> answer.getId() == a.getId());
    }
}
