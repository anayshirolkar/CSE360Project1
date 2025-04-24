package HW2;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import databasePart1.DatabaseHelper;
import java.sql.SQLException;
import java.util.List;
import java.util.Date;
import HW2.Review;
import HW2.Message;
import HW2.Question;
import HW2.Answer;
import java.util.UUID;

/**
 * Model class representing private feedback for a review.
 */
class PrivateFeedback {
    private String id;
    private String reviewId;
    private String studentId;
    private String content;
    private Date createdAt;

    public PrivateFeedback(String id, String reviewId, String studentId, String content, Date createdAt) {
        this.id = id;
        this.reviewId = reviewId;
        this.studentId = studentId;
        this.content = content;
        this.createdAt = createdAt;
    }

    public String getId() { return id; }
    public String getReviewId() { return reviewId; }
    public String getStudentId() { return studentId; }
    public String getContent() { return content; }
    public Date getCreatedAt() { return createdAt; }
}

/**
 * Model class representing the history of a review.
 */
class ReviewHistory {
    private String id;
    private String reviewId;
    private String content;
    private int rating;
    private Date timestamp;

    public ReviewHistory(String id, String reviewId, String content, int rating, Date timestamp) {
        this.id = id;
        this.reviewId = reviewId;
        this.content = content;
        this.rating = rating;
        this.timestamp = timestamp;
    }

    public String getId() { return id; }
    public String getReviewId() { return reviewId; }
    public String getContent() { return content; }
    public int getRating() { return rating; }
    public Date getTimestamp() { return timestamp; }
}

/**
 * Model class representing a notification for a review update.
 */
class Notification {
    private String id;
    private String reviewId;
    private String recipientId;
    private boolean isRead;
    private Date createdAt;

    public Notification(String id, String reviewId, String recipientId, boolean isRead, Date createdAt) {
        this.id = id;
        this.reviewId = reviewId;
        this.recipientId = recipientId;
        this.isRead = isRead;
        this.createdAt = createdAt;
    }

    public String getId() { return id; }
    public String getReviewId() { return reviewId; }
    public String getRecipientId() { return recipientId; }
    public boolean isRead() { return isRead; }
    public Date getCreatedAt() { return createdAt; }
}

/**
 * Model class representing a reviewer's profile information.
 */
class ReviewerProfile {
    private String reviewerId;
    private String bio;
    private String email;
    private String skills;

    public ReviewerProfile(String reviewerId, String bio, String email, String skills) {
        if (reviewerId == null || reviewerId.isEmpty()) {
            throw new IllegalArgumentException("Reviewer ID cannot be empty");
        }
        if (email != null && !email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new IllegalArgumentException("Invalid email format");
        }
        this.reviewerId = reviewerId;
        this.bio = bio;
        this.email = email;
        this.skills = skills;
    }

    public String getReviewerId() { return reviewerId; }
    public String getBio() { return bio; }
    public String getEmail() { return email; }
    public String getSkills() { return skills; }
}

/**
 * Test class for reviewer-related features in the application.
 * This class contains test cases for various reviewer functionalities including:
 * - Adding, updating, and deleting reviews
 * - Managing trusted reviewers
 * - Handling questions and answers
 * - Sending and receiving messages
 * - Managing reviewer profiles and notifications
 * 
 * The tests are ordered using @Order annotations to ensure proper test execution sequence.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ReviewerFeaturesTest {
    private static DatabaseHelper dbHelper;
    private static final String TEST_REVIEWER = "testReviewer";
    private static final String TEST_STUDENT = "testStudent";
    private static final String TEST_QUESTION_ID = "testQ1";
    private static final String TEST_ANSWER_ID = "testA1";

    /**
     * Sets up the test environment before running any tests.
     * Creates necessary database tables and test data.
     * 
     * @throws SQLException if there is an error connecting to the database
     */
    @BeforeAll
    public static void setup() throws SQLException {
        dbHelper = new DatabaseHelper();
        // Connect to the database first
        dbHelper.connectToDatabase();
        
        // Create test users
        dbHelper.registerUser(TEST_REVIEWER, "password", "reviewer");
        dbHelper.registerUser(TEST_STUDENT, "password", "student");
        
        // Create test content
        Question question = new Question("Test Question", "Test Description", TEST_STUDENT);
        question.setId(TEST_QUESTION_ID);
        dbHelper.saveQuestion(question);
        
        Answer answer = new Answer(TEST_QUESTION_ID, "Test Answer", TEST_STUDENT);
        answer.setId(TEST_ANSWER_ID);
        dbHelper.saveAnswer(answer);
    }

    /**
     * Cleans up the test environment after all tests are completed.
     * Removes test data and closes database connections.
     * 
     * @throws SQLException if there is an error cleaning up the database
     */
    @AfterAll
    public static void cleanup() throws SQLException {
        try {
            // Clean up test data
            dbHelper.deleteQuestion(TEST_QUESTION_ID);
            dbHelper.deleteAnswer(TEST_ANSWER_ID);
        } finally {
            // Always close the database connection
            if (dbHelper != null) {
                dbHelper.closeConnection();
            }
        }
    }

    /**
     * Tests the functionality of adding a new review.
     * Verifies that a review can be successfully added and retrieved.
     * 
     * @throws SQLException if there is an error accessing the database
     */
    @Test
    @Order(1)
    public void testAddReview() throws SQLException {
        Review review = new Review(TEST_REVIEWER, "Test review content", TEST_QUESTION_ID);
        review.setId(UUID.randomUUID().toString());
        review.setRating(4);
        review.setType("Question");
        review.setCreatedDate(new Date());
        review.setUpdatedDate(new Date());
        dbHelper.addReview(review);
        
        List<Review> reviews = dbHelper.getReviewsForAssociatedId(TEST_QUESTION_ID);
        assertFalse(reviews.isEmpty(), "Should retrieve added review");
        assertEquals("Test review content", reviews.get(0).getContent(), "Review content should match");
    }

    /**
     * Tests the functionality of updating an existing review.
     * Verifies that a review's content can be modified and the changes are persisted.
     * 
     * @throws SQLException if there is an error accessing the database
     */
    @Test
    @Order(2)
    public void testUpdateReview() throws SQLException {
        Review review = new Review(TEST_REVIEWER, "Initial review", TEST_QUESTION_ID);
        review.setId(UUID.randomUUID().toString());
        review.setRating(3);
        review.setType("Question");
        review.setCreatedDate(new Date());
        review.setUpdatedDate(new Date());
        dbHelper.addReview(review);
        
        String reviewId = review.getId();
        dbHelper.updateReview(reviewId, "Updated review content");
        
        List<Review> reviews = dbHelper.getReviewsForAssociatedId(TEST_QUESTION_ID);
        boolean found = false;
        for (Review r : reviews) {
            if (r.getId().equals(reviewId)) {
                assertEquals("Updated review content", r.getContent(), "Review should be updated");
                found = true;
                break;
            }
        }
        assertTrue(found, "Updated review should be found");
    }

    /**
     * Tests the functionality of deleting a review.
     * Verifies that a review can be successfully removed from the system.
     * 
     * @throws SQLException if there is an error accessing the database
     */
    @Test
    @Order(3)
    public void testDeleteReview() throws SQLException {
        Review review = new Review(TEST_REVIEWER, "Test review to delete", TEST_QUESTION_ID);
        review.setId(UUID.randomUUID().toString());
        review.setRating(4);
        review.setType("Question");
        review.setCreatedDate(new Date());
        review.setUpdatedDate(new Date());
        dbHelper.addReview(review);
        
        String reviewId = review.getId();
        dbHelper.deleteReview(reviewId);
        
        List<Review> reviews = dbHelper.getReviewsForAssociatedId(TEST_QUESTION_ID);
        boolean found = false;
        for (Review r : reviews) {
            if (r.getId().equals(reviewId)) {
                found = true;
                break;
            }
        }
        assertFalse(found, "Review should be deleted");
    }

    /**
     * Tests adding a trusted reviewer to a student's list.
     * Verifies that a reviewer can be added as trusted and retrieved.
     * 
     * @throws SQLException if there is an error accessing the database
     */
    @Test
    @Order(5)
    public void testAddTrustedReviewer() throws SQLException {
        dbHelper.addTrustedReviewer(TEST_STUDENT, TEST_REVIEWER, 5);
        
        List<TrustedReviewer> reviewers = dbHelper.getTrustedReviewers(TEST_STUDENT);
        assertFalse(reviewers.isEmpty(), "Should retrieve trusted reviewer");
        assertEquals(TEST_REVIEWER, reviewers.get(0).getReviewerUsername(), "Reviewer username should match");
    }

    /**
     * Tests removing a trusted reviewer from a student's list.
     * Verifies that a trusted reviewer can be successfully removed.
     * 
     * @throws SQLException if there is an error accessing the database
     */
    @Test
    @Order(6)
    public void testRemoveTrustedReviewer() throws SQLException {
        dbHelper.addTrustedReviewer(TEST_STUDENT, TEST_REVIEWER, 5);
        dbHelper.removeTrustedReviewer(TEST_STUDENT, TEST_REVIEWER);
        
        List<TrustedReviewer> reviewers = dbHelper.getTrustedReviewers(TEST_STUDENT);
        assertTrue(reviewers.isEmpty(), "Trusted reviewer should be removed");
    }

    /**
     * Tests adding a new question to the system.
     * Verifies that a question can be created and retrieved.
     * 
     * @throws SQLException if there is an error accessing the database
     */
    @Test
    @Order(7)
    public void testAddQuestion() throws SQLException {
        String questionId = UUID.randomUUID().toString();
        Question question = new Question("New Test Question", "New Description", TEST_STUDENT);
        question.setId(questionId);
        dbHelper.saveQuestion(question);
        
        List<Question> questions = dbHelper.getQuestionsByUser(TEST_STUDENT);
        assertFalse(questions.isEmpty(), "Should retrieve added question");
        boolean found = false;
        for (Question q : questions) {
            if (q.getId().equals(questionId)) {
                assertEquals("New Test Question", q.getTitle(), "Question title should match");
                found = true;
                break;
            }
        }
        assertTrue(found, "Added question should be found");
    }

    /**
     * Tests adding a new answer to a question.
     * Verifies that an answer can be created and retrieved.
     * 
     * @throws SQLException if there is an error accessing the database
     */
    @Test
    @Order(8)
    public void testAddAnswer() throws SQLException {
        String answerId = UUID.randomUUID().toString();
        Answer answer = new Answer(TEST_QUESTION_ID, "New Test Answer", TEST_STUDENT);
        answer.setId(answerId);
        dbHelper.saveAnswer(answer);
        
        List<Answer> answers = dbHelper.getAnswersByUser(TEST_STUDENT);
        assertFalse(answers.isEmpty(), "Should retrieve added answer");
        boolean found = false;
        for (Answer a : answers) {
            if (a.getId().equals(answerId)) {
                assertEquals("New Test Answer", a.getAnswerText(), "Answer text should match");
                found = true;
                break;
            }
        }
        assertTrue(found, "Added answer should be found");
    }

    /**
     * Tests updating a non-existent review.
     * Verifies that the system handles attempts to update non-existent reviews gracefully.
     * 
     * @throws SQLException if there is an error accessing the database
     */
    @Test
    @Order(9)
    public void testUpdateNonExistentReview() throws SQLException {
        String nonExistentId = UUID.randomUUID().toString();
        dbHelper.updateReview(nonExistentId, "Updated content");
        // If no exception is thrown, the test should still pass as the update simply won't affect any rows
    }

    /**
     * Tests deleting a non-existent review.
     * Verifies that the system handles attempts to delete non-existent reviews gracefully.
     * 
     * @throws SQLException if there is an error accessing the database
     */
    @Test
    @Order(10)
    public void testDeleteNonExistentReview() throws SQLException {
        String nonExistentId = UUID.randomUUID().toString();
        dbHelper.deleteReview(nonExistentId);
        // If no exception is thrown, the test should still pass as the delete simply won't affect any rows
    }

    /**
     * Tests adding a review with an invalid rating.
     * Verifies that the system properly validates review ratings.
     */
    @Test
    @Order(11)
    public void testAddReviewWithInvalidRating() {
        Review review = new Review(TEST_REVIEWER, "Test review", TEST_QUESTION_ID);
        review.setId(UUID.randomUUID().toString());
        review.setCreatedDate(new Date());
        review.setUpdatedDate(new Date());
        
        assertThrows(IllegalArgumentException.class, () -> {
            review.setRating(6); // This should throw IllegalArgumentException
        });
    }

    /**
     * Tests flagging a question for review.
     * Verifies that questions can be flagged and retrieved.
     * 
     * @throws SQLException if there is an error accessing the database
     */
    @Test
    @Order(13)
    public void testFlagQuestion() throws SQLException {
        Question question = new Question("Question to flag", "Description", TEST_STUDENT);
        question.setId("flagQ1");
        dbHelper.saveQuestion(question);
        
        dbHelper.flagQuestion("flagQ1");
        List<Question> flaggedQuestions = dbHelper.getFlaggedQuestions();
        assertFalse(flaggedQuestions.isEmpty(), "Should retrieve flagged question");
        assertEquals("Question to flag", flaggedQuestions.get(0).getTitle(), "Flagged question title should match");
    }

    /**
     * Tests flagging an answer for review.
     * Verifies that answers can be flagged and retrieved.
     * 
     * @throws SQLException if there is an error accessing the database
     */
    @Test
    @Order(14)
    public void testFlagAnswer() throws SQLException {
        Answer answer = new Answer(TEST_QUESTION_ID, "Answer to flag", TEST_STUDENT);
        answer.setId("flagA1");
        dbHelper.saveAnswer(answer);
        
        dbHelper.flagAnswer("flagA1");
        List<Answer> flaggedAnswers = dbHelper.getFlaggedAnswers();
        assertFalse(flaggedAnswers.isEmpty(), "Should retrieve flagged answer");
        assertEquals("Answer to flag", flaggedAnswers.get(0).getAnswerText(), "Flagged answer text should match");
    }

    /**
     * Tests updating a reviewer's experience information.
     * Verifies that reviewer experience can be updated and retrieved.
     * 
     * @throws SQLException if there is an error accessing the database
     */
    @Test
    @Order(15)
    public void testUpdateReviewerExperience() throws SQLException {
        String experience = "Experienced in Java and Python";
        dbHelper.updateReviewerExperience(TEST_REVIEWER, experience);
        
        String retrievedExperience = dbHelper.getReviewerExperience(TEST_REVIEWER);
        assertEquals(experience, retrievedExperience, "Reviewer experience should match");
    }

    /**
     * Tests retrieving multiple reviews for a question.
     * Verifies that all reviews for a question can be retrieved.
     * 
     * @throws SQLException if there is an error accessing the database
     */
    @Test
    @Order(16)
    public void testGetMultipleReviews() throws SQLException {
        // Create multiple reviews
        for (int i = 0; i < 5; i++) {
            Review review = new Review(TEST_REVIEWER, "Review " + i, TEST_QUESTION_ID);
            review.setId(UUID.randomUUID().toString());
            review.setRating(4);
            review.setType("Question");
            review.setCreatedDate(new Date());
            review.setUpdatedDate(new Date());
            dbHelper.addReview(review);
        }
        
        List<Review> reviews = dbHelper.getReviewsForAssociatedId(TEST_QUESTION_ID);
        assertTrue(reviews.size() >= 5, "Should retrieve all reviews");
    }

    /**
     * Tests sending a message with a null sender.
     * Verifies that the system properly validates message senders.
     */
    @Test
    @Order(18)
    public void testSendMessageWithNullSender() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Message(null, TEST_STUDENT, "Test message");
        });
    }

    /**
     * Tests sending a message with a null recipient.
     * Verifies that the system properly validates message recipients.
     */
    @Test
    @Order(19)
    public void testSendMessageWithNullRecipient() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Message(TEST_REVIEWER, null, "Test message");
        });
    }

    /**
     * Tests sending a message with null content.
     * Verifies that the system properly validates message content.
     */
    @Test
    @Order(20)
    public void testSendMessageWithNullContent() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Message(TEST_REVIEWER, TEST_STUDENT, null);
        });
    }

    /**
     * Tests sending a message with maximum length content.
     * Verifies that the system can handle messages with long content.
     * 
     * @throws SQLException if there is an error accessing the database
     */
    @Test
    @Order(22)
    public void testSendMessageWithMaxLengthContent() throws SQLException {
        String longContent = "a".repeat(1000); // Assuming 1000 is max length
        Message message = new Message(TEST_REVIEWER, TEST_STUDENT, longContent);
        message.setId(UUID.randomUUID().toString());
        dbHelper.sendMessage(message);
        
        List<Message> messages = dbHelper.getMessagesForRecipient(TEST_STUDENT);
        assertFalse(messages.isEmpty(), "Should retrieve sent message");
        assertEquals(longContent, messages.get(0).getContent(), "Long content should match");
    }

    /**
     * Tests sending a message to oneself.
     * Verifies that users can send messages to themselves.
     * 
     * @throws SQLException if there is an error accessing the database
     */
    @Test
    @Order(23)
    public void testSendMessageToSelf() throws SQLException {
        Message message = new Message(TEST_REVIEWER, TEST_REVIEWER, "Self message");
        message.setId(UUID.randomUUID().toString());
        dbHelper.sendMessage(message);
        
        List<Message> messages = dbHelper.getMessagesForRecipient(TEST_REVIEWER);
        assertFalse(messages.isEmpty(), "Should retrieve self-sent message");
    }

    /**
     * Tests updating a message's related ID.
     * Verifies that a message's related ID can be updated.
     * 
     * @throws SQLException if there is an error accessing the database
     */
    @Test
    @Order(25)
    public void testUpdateMessageRelatedId() throws SQLException {
        Message message = new Message(TEST_REVIEWER, TEST_STUDENT, "Test message");
        String newRelatedId = UUID.randomUUID().toString();
        message.setRelatedId(newRelatedId);
        
        assertEquals(newRelatedId, message.getRelatedId(), "Related ID should be updated");
    }

    /**
     * Tests the message toString method.
     * Verifies that messages are properly formatted when converted to strings.
     */
    @Test
    @Order(26)
    public void testMessageToString() {
        Message message = new Message(TEST_REVIEWER, TEST_STUDENT, "Test message");
        String expected = String.format("From: %s - %s", TEST_REVIEWER, "Test message");
        assertEquals(expected, message.toString(), "toString should match expected format");
    }

    /**
     * Tests setting a null sentAt timestamp for a message.
     * Verifies that the system properly validates message timestamps.
     */
    @Test
    @Order(27)
    public void testSetNullSentAt() {
        Message message = new Message(TEST_REVIEWER, TEST_STUDENT, "Test message");
        assertThrows(IllegalArgumentException.class, () -> {
            message.setSentAt(null);
        });
    }

    /**
     * Tests sending multiple messages from the same sender.
     * Verifies that the system can handle multiple messages from a single sender.
     * 
     * @throws SQLException if there is an error accessing the database
     */
    @Test
    @Order(28)
    public void testMultipleMessagesFromSameSender() throws SQLException {
        // Clear existing messages first
        dbHelper.clearMessagesForRecipient(TEST_STUDENT);
        
        for (int i = 0; i < 5; i++) {
            Message message = new Message(TEST_REVIEWER, TEST_STUDENT, "Message " + i);
            dbHelper.sendMessage(message);
        }
        
        List<Message> messages = dbHelper.getMessagesForRecipient(TEST_STUDENT);
        long count = messages.stream()
                           .filter(m -> m.getSender().equals(TEST_REVIEWER))
                           .count();
        assertEquals(5, count, "Should have 5 messages from the same sender");
    }

    /**
     * Tests sending messages with special characters.
     * Verifies that the system can handle messages containing special characters.
     * 
     * @throws SQLException if there is an error accessing the database
     */
    @Test
    @Order(29)
    public void testMessageWithSpecialCharacters() throws SQLException {
        // Clear existing messages first
        dbHelper.clearMessagesForRecipient(TEST_STUDENT);
        
        String specialContent = "!@#$%^&*()_+-=[]{}|;:'\",.<>?/\\";
        Message message = new Message(TEST_REVIEWER, TEST_STUDENT, specialContent);
        dbHelper.sendMessage(message);
        
        List<Message> messages = dbHelper.getMessagesForRecipient(TEST_STUDENT);
        assertFalse(messages.isEmpty(), "Should have at least one message");
        assertEquals(specialContent, messages.get(0).getContent(),
                    "Special characters should be preserved");
    }

    /**
     * Tests sending messages with emoji characters.
     * Verifies that the system can handle messages containing emoji.
     * 
     * @throws SQLException if there is an error accessing the database
     */
    @Test
    @Order(30)
    public void testMessageWithEmoji() throws SQLException {
        // Clear existing messages first
        dbHelper.clearMessagesForRecipient(TEST_STUDENT);
        
        String contentWithEmoji = "Hello! 👋 How are you? 😊";
        Message message = new Message(TEST_REVIEWER, TEST_STUDENT, contentWithEmoji);
        dbHelper.sendMessage(message);
        
        List<Message> messages = dbHelper.getMessagesForRecipient(TEST_STUDENT);
        assertFalse(messages.isEmpty(), "Should have at least one message");
        assertEquals(contentWithEmoji, messages.get(0).getContent(),
                    "Emoji should be preserved");
    }

    /**
     * Tests sending messages with multiline content.
     * Verifies that the system can handle messages with multiple lines.
     * 
     * @throws SQLException if there is an error accessing the database
     */
    @Test
    @Order(31)
    public void testMessageWithMultilineContent() throws SQLException {
        // Clear existing messages first
        dbHelper.clearMessagesForRecipient(TEST_STUDENT);
        
        String multilineContent = "Line 1\nLine 2\nLine 3";
        Message message = new Message(TEST_REVIEWER, TEST_STUDENT, multilineContent);
        dbHelper.sendMessage(message);
        
        List<Message> messages = dbHelper.getMessagesForRecipient(TEST_STUDENT);
        assertFalse(messages.isEmpty(), "Should have at least one message");
        assertEquals(multilineContent, messages.get(0).getContent(),
                    "Multiline content should be preserved");
    }
}