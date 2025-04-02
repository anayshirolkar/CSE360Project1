package HW2;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

public class QuestionsTest {
    private Questions questionManager;

    @Before
    public void setUp() {
        questionManager = new Questions();
    }

    @Test
    public void testAddQuestion() {
        Question newQuestion = new Question("What is OOP?", "Please explain Object Oriented Programming", "TestUser");
        questionManager.addQuestion(newQuestion);
        assertEquals("question added!", 1, questionManager.getAllQuestions().size());
    }

    @Test
    public void testDeleteQuestion() {
        Question question = new Question("Delete me", "This is a test question", "TestUser");
        questionManager.addQuestion(question);
        questionManager.deleteQuestion(question.getId());
        assertEquals("question should be deleted", 0, questionManager.getAllQuestions().size());
    }

    @Test
    public void testUpdateQuestion() {
        Question question = new Question("Old title", "Old description", "TestUser");
        questionManager.addQuestion(question);
        question.setDescription("updated description");
        assertEquals("Question should be updated", "updated description", questionManager.getAllQuestions().get(0).getDescription());
    }

    @Test
    public void testSearchQuestion() {
        questionManager.addQuestion(new Question("Java Question", "What is Java?", "TestUser"));
        boolean found = questionManager.searchQuestions("java").size() > 0;
        assertTrue("search should find at least one matching question", found);
    }
}