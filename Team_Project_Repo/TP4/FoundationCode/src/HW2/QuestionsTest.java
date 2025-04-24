package HW2;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for {@link Questions}.
 * <p>
 * This class contains unit tests for verifying the functionality of the Questions class,
 * including methods for adding, updating, deleting, and searching questions.
 * </p>
 * 
 * @author CSE360 Team
 * @version 1.0
 * @see Questions
 * @see Question
 */
public class QuestionsTest {
    /** The question manager instance used for testing. */
    private Questions questionManager;

    /**
     * Sets up the test environment before each test.
     * <p>
     * Creates a new Questions instance to ensure each test starts with a clean state.
     * </p>
     */
    @Before
    public void setUp() {
        questionManager = new Questions();
    }

    /**
     * Tests adding a new question to the collection.
     * <p>
     * Verifies that when a question is added, the size of the collection increases.
     * </p>
     */
    @Test
    public void testAddQuestion() {
        Question newQuestion = new Question("What is OOP?", "Please explain Object Oriented Programming", "TestUser");
        questionManager.addQuestion(newQuestion);
        assertEquals("question added!", 1, questionManager.getAllQuestions().size());
    }

    /**
     * Tests deleting a question from the collection.
     * <p>
     * Verifies that when a question is deleted, it is correctly removed from the collection.
     * </p>
     */
    @Test
    public void testDeleteQuestion() {
        Question question = new Question("Delete me", "This is a test question", "TestUser");
        questionManager.addQuestion(question);
        questionManager.deleteQuestion(question.getId());
        assertEquals("question should be deleted", 0, questionManager.getAllQuestions().size());
    }

    /**
     * Tests updating a question in the collection.
     * <p>
     * Verifies that when a question's description is updated, the changes are reflected
     * in the stored question.
     * </p>
     */
    @Test
    public void testUpdateQuestion() {
        Question question = new Question("Old title", "Old description", "TestUser");
        questionManager.addQuestion(question);
        question.setDescription("updated description");
        assertEquals("Question should be updated", "updated description", questionManager.getAllQuestions().get(0).getDescription());
    }

    /**
     * Tests searching for questions by keyword.
     * <p>
     * Verifies that the search functionality correctly finds questions that match
     * the provided keyword.
     * </p>
     */
    @Test
    public void testSearchQuestion() {
        questionManager.addQuestion(new Question("Java Question", "What is Java?", "TestUser"));
        boolean found = questionManager.searchQuestions("java").size() > 0;
        assertTrue("search should find at least one matching question", found);
    }
}