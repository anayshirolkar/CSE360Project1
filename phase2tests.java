import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class QuestionsTest {
    private Questions questionManager;

    @BeforeEach
    void setUp() {
        questionManager = new Questions();
    }

    @Test
    void testAddQuestion() {
        Question newQuestion = new Question(1, "what is oop?");
        questionManager.addQuestion(newQuestion);
        assertEquals(1, questionManager.getQuestions().size(), "question added!");
    }

    @Test
    void testDeleteQuestion() {
        questionManager.addQuestion(new Question(1, "question to delete?"));
        questionManager.deleteQuestion(questionManager.getQuestions().get(0));
        assertEquals(0, questionManager.getQuestions().size(), "question should be deleted");
    }

    @Test
    void testUpdateQuestion() {
        questionManager.addQuestion(new Question(1, "old question?"));
        Question question = questionManager.getQuestions().get(0);
        question.setText("updated question?");
        questionManager.updateQuestion(question);
        assertEquals("updated question?", questionManager.getQuestions().get(0).getText(), "Question should be updated");
    }

    @Test
    void testSearchQuestion() {
        questionManager.addQuestion(new Question(1, "what is java?"));
        boolean found = questionManager.getQuestions().stream()
                            .anyMatch(q -> q.getText().contains("java"));
        assertTrue(found, "search should find at least one matching question");
    }
}

class AnswersTest {
    private Answers answerManager;

    @BeforeEach
    void setUp() {
        answerManager = new Answers();
    }

    @Test
    void testAddAnswer() {
        Answer newAnswer = new Answer(1, 1, "test answer 1");
        answerManager.addAnswer(newAnswer);
        assertEquals(1, answerManager.getAnswers().size(), "answer list should contain one answer");
    }

    @Test
    void testDeleteAnswer() {
        answerManager.addAnswer(new Answer(1, 1, "answer to delete"));
        Answer answer = answerManager.getAnswers().get(0);
        answerManager.removeAnswer(answer);
        assertEquals(0, answerManager.getAnswers().size(), "answer list empty after deletion");
    }

    @Test
    void testUpdateAnswer() {
        answerManager.addAnswer(new Answer(1, 1, "old answer"));
        Answer answer = answerManager.getAnswers().get(0);
        answer.setText("updated answer");
        answerManager.updateAnswer(answer);
        assertEquals("updated answer", answerManager.getAnswers().get(0).getText(), "Answer should be updated");
    }
}




