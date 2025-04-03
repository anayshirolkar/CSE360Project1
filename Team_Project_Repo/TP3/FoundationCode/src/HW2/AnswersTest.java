package HW2;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for {@link Answers}.
 * <p>
 * This class contains unit tests for verifying the functionality of the Answers class,
 * including methods for adding, updating, and deleting answers.
 * </p>
 * 
 * @author CSE360 Team
 * @version 1.0
 * @see Answers
 * @see Answer
 */
public class AnswersTest {
    /** The answer manager instance used for testing. */
    private Answers answerManager;
    
    /** A test question ID used for creating answers. */
    private String testQuestionId = "test-question-id";

    /**
     * Sets up the test environment before each test.
     * <p>
     * Creates a new Answers instance to ensure each test starts with a clean state.
     * </p>
     */
    @Before
    public void setUp() {
        answerManager = new Answers();
    }

    /**
     * Tests adding a new answer to the collection.
     * <p>
     * Verifies that when an answer is added, the size of the collection increases
     * and the answer can be retrieved by its question ID.
     * </p>
     */
    @Test
    public void testAddAnswer() {
        Answer newAnswer = new Answer(testQuestionId, "test answer 1", "TestUser");
        answerManager.addAnswer(newAnswer);
        assertEquals("answer list should contain one answer", 1, answerManager.getAnswersForQuestion(testQuestionId).size());
    }

    /**
     * Tests deleting an answer from the collection.
     * <p>
     * Verifies that when an answer is deleted, it is correctly removed from the collection.
     * </p>
     */
    @Test
    public void testDeleteAnswer() {
        Answer answer = new Answer(testQuestionId, "answer to delete", "TestUser");
        answerManager.addAnswer(answer);
        answerManager.deleteAnswer(answer.getId());
        assertEquals("answer list empty after deletion", 0, answerManager.getAnswersForQuestion(testQuestionId).size());
    }

    /**
     * Tests updating an answer in the collection.
     * <p>
     * Verifies that when an answer's text is updated, the changes are reflected
     * in the stored answer.
     * </p>
     */
    @Test
    public void testUpdateAnswer() {
        Answer answer = new Answer(testQuestionId, "old answer", "TestUser");
        answerManager.addAnswer(answer);
        answer.setAnswerText("updated answer");
        assertEquals("Answer should be updated", "updated answer", answerManager.getAnswerById(answer.getId()).getAnswerText());
    }
}