import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

//edit to fit the code
class QuestionServiceTest {

    @Test
    void testAddQuestion() {
        QuestionService questionService = new QuestionService();
        boolean result = questionService.addQuestion("what is oop?");
        assertTrue(result, "question added successfully");
    }
}

class SearchServiceTest {

    @Test
    void testSearchExistingQuestion() {
        SearchService searchService = new SearchService();
        searchService.addQuestion("What is java?");
        assertTrue(searchService.search("java").size() > 0, "Search should return at least one result");
    }
}

class AnswerServiceTest {

    @Test
    void testAddAnswer() {
        AnswerService answerService = new AnswerService();
        answerService.addQuestion(1, "what is the question?");
        boolean result = answerService.addAnswer(1, "here is the answer to the question");
        assertTrue(result, "Answer added successfully");
    }
}

//only if there is login feature in the code
class AuthServiceTest {

    @Test
    void testValidLogin() {
        AuthService authService = new AuthService();
        authService.registerUser("user1", "password123");
        boolean result = authService.login("user1", "password123");
        assertTrue(result, "User should log in successfully");
    }
}

