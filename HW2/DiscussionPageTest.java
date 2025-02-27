// import static org.junit.jupiter.api.Assertions.*;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import javafx.embed.swing.JFXPanel;
// import javafx.stage.Stage;
// import javafx.application.Platform;

package HW2;
import static org.junit.Assert.*;
class DiscussionPageTest {
    private DiscussionPage discussionPage;
    private Stage stage;

    @BeforeEach
    void setUp() {
        // Initialize JavaFX environment
        new JFXPanel();
        Platform.runLater(() -> {
            stage = new Stage();
            discussionPage = new DiscussionPage();
            discussionPage.show(stage);
        });
    }

    @Test
    void testAddQuestion() {
        Platform.runLater(() -> {
            // Simulate adding a question
            discussionPage.getQuestions().addQuestion(new Question("Test Title", "Test Description", "Test Author"));
            assertEquals(1, discussionPage.getQuestions().getAllQuestions().size(), "Question should be added");
        });
    }

    @Test
    void testDeleteQuestion() {
        Platform.runLater(() -> {
            // Simulate adding and then deleting a question
            Question question = new Question("Test Title", "Test Description", "Test Author");
            discussionPage.getQuestions().addQuestion(question);
            discussionPage.getQuestions().deleteQuestion(question.getId());
            assertEquals(0, discussionPage.getQuestions().getAllQuestions().size(), "Question should be deleted");
        });
    }

    @Test
    void testAddAnswer() {
        Platform.runLater(() -> {
            // Simulate adding an answer
            Question question = new Question("Test Title", "Test Description", "Test Author");
            discussionPage.getQuestions().addQuestion(question);
            discussionPage.getAnswers().addAnswer(new Answer(question.getId(), "Test Answer", "Test Author"));
            assertEquals(1, discussionPage.getAnswers().getAnswersForQuestion(question.getId()).size(), "Answer should be added");
        });
    }

    @Test
    void testDeleteAnswer() {
        Platform.runLater(() -> {
            // Simulate adding and then deleting an answer
            Question question = new Question("Test Title", "Test Description", "Test Author");
            discussionPage.getQuestions().addQuestion(question);
            Answer answer = new Answer(question.getId(), "Test Answer", "Test Author");
            discussionPage.getAnswers().addAnswer(answer);
            discussionPage.getAnswers().deleteAnswer(answer.getId());
            assertEquals(0, discussionPage.getAnswers().getAnswersForQuestion(question.getId()).size(), "Answer should be deleted");
        });
    }
}