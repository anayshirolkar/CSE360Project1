package HW2;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

public class AnswersTest {
    private Answers answerManager;
    private String testQuestionId = "test-question-id";

    @Before
    public void setUp() {
        answerManager = new Answers();
    }

    @Test
    public void testAddAnswer() {
        Answer newAnswer = new Answer(testQuestionId, "test answer 1", "TestUser");
        answerManager.addAnswer(newAnswer);
        assertEquals("answer list should contain one answer", 1, answerManager.getAnswersForQuestion(testQuestionId).size());
    }

    @Test
    public void testDeleteAnswer() {
        Answer answer = new Answer(testQuestionId, "answer to delete", "TestUser");
        answerManager.addAnswer(answer);
        answerManager.deleteAnswer(answer.getId());
        assertEquals("answer list empty after deletion", 0, answerManager.getAnswersForQuestion(testQuestionId).size());
    }

    @Test
    public void testUpdateAnswer() {
        Answer answer = new Answer(testQuestionId, "old answer", "TestUser");
        answerManager.addAnswer(answer);
        answer.setAnswerText("updated answer");
        assertEquals("Answer should be updated", "updated answer", answerManager.getAnswerById(answer.getId()).getAnswerText());
    }
}