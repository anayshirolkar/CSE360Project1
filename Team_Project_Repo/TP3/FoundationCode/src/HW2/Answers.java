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

    /**
     * Gets an answer by ID.
     * @param id The answer ID.
     * @return The answer, or null if not found.
     */
    public Answer getAnswerById(String id) {
        for (Answer answer : answers) {
            if (answer.getId().equals(id)) {
                return answer;
            }
        }
        return null;
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

    /**
     * Gets all answers.
     * @return A list of all answers.
     */
    public List<Answer> getAllAnswers() {
        return new ArrayList<>(answers); // Return a copy to prevent external modification
    }
    public void clearAll() {
        answers.clear();
    }
}