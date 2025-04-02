package HW2;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Answers {
    private List<Answer> answers;

    public Answers() {
        this.answers = new ArrayList<>();
    }

    // Create
    public void addAnswer(Answer answer) {
        if (answer == null) {
            throw new IllegalArgumentException("Answer cannot be null");
        }
        answers.add(answer);
    }

    // Read
    public List<Answer> getAnswersForQuestion(String questionId) {
        return answers.stream()
            .filter(a -> a.getQuestionId().equals(questionId))
            .collect(Collectors.toList());
    }

    public Answer getAnswerById(String id) {
        return answers.stream()
            .filter(a -> a.getId().equals(id))
            .findFirst()
            .orElse(null);
    }

    // Update is handled by Answer class setters

    // Delete
    public void deleteAnswer(String id) {
        answers.removeIf(a -> a.getId().equals(id));
    }

    // Delete all answers for a question
    public void deleteAnswersForQuestion(String questionId) {
        answers.removeIf(a -> a.getQuestionId().equals(questionId));
    }
}