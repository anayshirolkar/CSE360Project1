package HW2;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;

import databasePart1.DatabaseHelper;
import java.sql.SQLException;
import java.util.List;

/**
 * FlagContentTest is a test suite for verifying flagging functionality in the system.
 * <p>
 * This class tests the core workflows of the flagging system, including:
 * - Staff members flagging inappropriate content (questions, answers, reviews)
 * - Admin users viewing flagged content
 * - Admin users resolving flagged content
 * </p>
 * <p>
 * These tests ensure that the moderation system works correctly,
 * allowing staff to report problematic content and admins to manage it.
 * </p>
 * 
 * @author CSE360 Team
 * @version 1.0
 */
public class FlaggedContentTest {
    private DatabaseHelper dbHelper;
    private String testQuestionId;
    private String testAnswerId;
    private String testReviewId;
    private String testUsername = "staffUser";

    /**
     * Default constructor for FlaggedContentTest.
     * <p>
     * Initializes a new test suite for verifying the content flagging and moderation system.
     * Test data and database connections are initialized in the setUp method rather than here.
     * </p>
     */
    public FlaggedContentTest() {
        // Default constructor - test initialization occurs in setUp()
    }

    /**
     * Sets up the test environment before each test runs.
     * <p>
     * This method:
     * 1. Creates a database connection
     * 2. Creates test content (question, answer, review)
     * 3. Does NOT flag the content yet (flagging happens in individual tests)
     * </p>
     *
     * @throws SQLException if database operations fail
     */
    @Before
    public void setUp() throws SQLException {
        try {
            // Step 1: Initialize database connection for testing
            dbHelper = new DatabaseHelper();
            dbHelper.connectToDatabase();
            
            // Initialize flagging schema - add this line
            dbHelper.initializeFlaggingSchema();
            
            // Step 2: Create a test question that will be flagged during tests
            // Note: We create unflagged content first to test the flagging process itself
            Question question = new Question("Test Question", "Content to flag", testUsername);
            testQuestionId = question.getId();
            dbHelper.saveQuestion(question);
            
            // Step 3: Create a test answer associated with our test question
            Answer answer = new Answer(testQuestionId, "Answer to flag", testUsername);
            testAnswerId = answer.getId();
            dbHelper.saveAnswer(answer);
            
            // Step 4: Create a test review associated with our test question
            Review review = new Review("reviewer1", "Review to flag", testQuestionId, 3, "Question");
            testReviewId = review.getId();
            dbHelper.addReview(review);
        } catch (Exception e) {
            System.err.println("Error in test setup: " + e.getMessage());
            e.printStackTrace();
            if (dbHelper != null) {
                dbHelper.closeConnection();
            }
            throw e;
        }
    }
    
    /**
     * Cleans up test data after each test completes.
     * <p>
     * This method:
     * 1. Removes all test content from the database
     * 2. Closes the database connection
     * </p>
     *
     * @throws SQLException if database operations fail
     */
    @After
    public void tearDown() throws SQLException {
        // Step 1: Remove all test data to prevent test pollution
        try {
            dbHelper.deleteQuestion(testQuestionId);
            dbHelper.deleteAnswer(testAnswerId);
            dbHelper.deleteReview(testReviewId);
        } catch (SQLException e) {
            // Ignore cleanup errors - they shouldn't affect test results
        }
        
        // Step 2: Close the database connection
        dbHelper.closeConnection();
    }
    
    /**
     * Tests the process of flagging a question for admin review.
     * <p>
     * This test simulates a staff member flagging a question that violates
     * community guidelines and verifies that it appears in the admin's
     * flagged content list.
     * </p>
     *
     * @throws SQLException if database operations fail
     */
    @Test
    public void testFlagQuestion() throws SQLException {
        System.out.println("Starting testFlagQuestion...");
        try {
            // Step 1: Staff member flags a question for admin review
            System.out.println("Flagging question with ID: " + testQuestionId);
            dbHelper.flagQuestion(testQuestionId);
            
            // Step 2: Simulate admin retrieving all flagged questions
            System.out.println("Retrieving flagged questions...");
            List<Question> flaggedQuestions = dbHelper.getFlaggedQuestions();
            System.out.println("Found " + flaggedQuestions.size() + " flagged questions");
            
            // Step 3: Check if our flagged question appears in the admin's list
            boolean found = false;
            for (Question q : flaggedQuestions) {
                if (q.getId().equals(testQuestionId)) {
                    found = true;
                    break;
                }
            }
            
            // Step 4: Verify the question is properly flagged in the system
            assertTrue("Question should appear in flagged questions list", found);
        } catch (Exception e) {
            System.err.println("Error in testFlagQuestion: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
    
    /**
     * Tests the process of flagging an answer for admin review.
     * <p>
     * This test simulates a staff member flagging an answer that violates
     * community guidelines and verifies that it appears in the admin's
     * flagged content list with the correct content.
     * </p>
     *
     * @throws SQLException if database operations fail
     */
    @Test
    public void testFlagAnswer() throws SQLException {
        // Step 1: Staff member flags an answer for admin review
        dbHelper.flagAnswer(testAnswerId);
        
        // Step 2: Simulate admin retrieving all flagged answers
        List<Answer> flaggedAnswers = dbHelper.getFlaggedAnswers();
        
        // Step 3: Check if our flagged answer appears in the admin's list
        boolean found = false;
        for (Answer a : flaggedAnswers) {
            if (a.getId().equals(testAnswerId)) {
                found = true;
                // Verify the content matches what was flagged
                assertEquals("Answer to flag", a.getAnswerText());
                break;
            }
        }
        
        // Step 4: Verify the answer is properly flagged in the system
        assertTrue("Answer should appear in flagged answers list", found);
    }
    
    /**
     * Tests the process of flagging a review for admin review.
     * <p>
     * This test simulates a staff member flagging a review that violates
     * community guidelines and verifies that it appears in the admin's
     * flagged content list with the correct rating and content.
     * </p>
     *
     * @throws SQLException if database operations fail
     */
    @Test
    public void testFlagReview() throws SQLException {
        // Step 1: Staff member flags a review for admin review
        dbHelper.flagReview(testReviewId);
        
        // Step 2: Simulate admin retrieving all flagged reviews
        List<Review> flaggedReviews = dbHelper.getFlaggedReviews();
        
        // Step 3: Check if our flagged review appears in the admin's list
        boolean found = false;
        for (Review r : flaggedReviews) {
            if (r.getId().equals(testReviewId)) {
                found = true;
                // Verify the rating matches what was flagged
                assertEquals(3, r.getRating());
                break;
            }
        }
        
        // Step 4: Verify the review is properly flagged in the system
        assertTrue("Review should appear in flagged reviews list", found);
    }
    
    /**
     * Tests the end-to-end workflow of flagging content and admin resolution.
     * <p>
     * This comprehensive test verifies:
     * 1. Staff can flag all types of content
     * 2. Admins can mark each flagged item as resolved
     * 3. Resolved items no longer appear in flagged content lists
     * </p>
     * <p>
     * This test ensures the full moderation workflow functions correctly.
     * </p>
     *
     * @throws SQLException if database operations fail
     */
    @Test
    public void testAdminResolvesFlaggedContent() throws SQLException {
        // Step 1: Staff member flags multiple content types for review
        dbHelper.flagQuestion(testQuestionId);
        dbHelper.flagAnswer(testAnswerId);
        dbHelper.flagReview(testReviewId);
        
        // Step 2: Admin reviews and resolves each flagged item
        dbHelper.markQuestionAsResolved(testQuestionId);
        dbHelper.markAnswerAsResolved(testAnswerId);
        dbHelper.markReviewAsResolved(testReviewId);
        
        // Step 3: Simulate admin checking for any remaining flagged content
        List<Question> flaggedQuestions = dbHelper.getFlaggedQuestions();
        List<Answer> flaggedAnswers = dbHelper.getFlaggedAnswers();
        List<Review> flaggedReviews = dbHelper.getFlaggedReviews();
        
        // Step 4: Check if any of our resolved content still appears as flagged
        boolean questionFound = false;
        boolean answerFound = false;
        boolean reviewFound = false;
        
        for (Question q : flaggedQuestions) {
            if (q.getId().equals(testQuestionId)) questionFound = true;
        }
        
        for (Answer a : flaggedAnswers) {
            if (a.getId().equals(testAnswerId)) answerFound = true;
        }
        
        for (Review r : flaggedReviews) {
            if (r.getId().equals(testReviewId)) reviewFound = true;
        }
        
        // Step 5: Verify all content has been successfully resolved
        assertFalse("Question should no longer be flagged", questionFound);
        assertFalse("Answer should no longer be flagged", answerFound);
        assertFalse("Review should no longer be flagged", reviewFound);
    }
}