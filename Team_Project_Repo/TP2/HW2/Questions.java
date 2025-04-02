package HW2;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Questions {
    private List<Question> questions;

    public Questions() {
        this.questions = new ArrayList<>();
    }

    // Create
    public void addQuestion(Question question) {
        if (question == null) {
            throw new IllegalArgumentException("Question cannot be null");
        }
        questions.add(question);
    }

    // Read
    public List<Question> getAllQuestions() {
        return new ArrayList<>(questions);
    }

    public Question getQuestionById(String id) {
        return questions.stream()
            .filter(q -> q.getId().equals(id))
            .findFirst()
            .orElse(null);
    }

    // Update is handled by Question class setters

    // Delete
    public void deleteQuestion(String id) {
        questions.removeIf(q -> q.getId().equals(id));
    }

    // Search functionality
    public List<Question> searchQuestions(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllQuestions();
        }

        String searchTerm = keyword.toLowerCase().trim();
        return questions.stream()
            .filter(q -> 
                q.getTitle().toLowerCase().contains(searchTerm) || 
                q.getDescription().toLowerCase().contains(searchTerm))
            .collect(Collectors.toList());
    }
}