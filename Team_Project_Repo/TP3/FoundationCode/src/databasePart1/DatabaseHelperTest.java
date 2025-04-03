package databasePart1;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

import HW2.Review;
import HW2.Question;
import HW2.Answer;
import HW2.Questions;
import HW2.Answers;
import HW2.TrustedReviewer;

public class DatabaseHelperTest {
    private DatabaseHelper dbHelper;
    private String testReviewer = "testReviewer";
    private String testStudent = "testStudent";
    private String testAssociatedId;
    
    @Before
    public void setUp() throws SQLException {
        dbHelper = new DatabaseHelper();
        dbHelper.connectToDatabase();
        
        // Generate a unique ID for this test run to avoid conflicts
        testAssociatedId = UUID.randomUUID().toString();
    }
    
    @After
    public void tearDown() {
        if (dbHelper != null) {
            dbHelper.closeConnection();
        }
    }
    
    @Test
    public void testAddAndGetReview() throws SQLException {
        // Create a review to add
        Review review = new Review(testReviewer, "Test review content", testAssociatedId, 4, "Question");
        
        // Add review to database
        dbHelper.addReview(review);
        
        // Retrieve reviews for the associated ID
        List<Review> reviews = dbHelper.getReviewsForAssociatedId(testAssociatedId);
        
        // Verify review was added
        assertEquals("Should have 1 review", 1, reviews.size());
        assertEquals("Review content should match", "Test review content", reviews.get(0).getContent());
        assertEquals("Reviewer should match", testReviewer, reviews.get(0).getReviewer());
        assertEquals("Rating should match", 4, reviews.get(0).getRating());
        assertEquals("Type should match", "Question", reviews.get(0).getType());
        
        // Clean up
        dbHelper.deleteReview(reviews.get(0).getId());
    }
    
    @Test
    public void testUpdateReview() throws SQLException {
        // Create and add a review
        Review review = new Review(testReviewer, "Original content", testAssociatedId);
        dbHelper.addReview(review);
        
        // Update the review
        dbHelper.updateReview(review.getId(), "Updated content");
        
        // Retrieve the updated review
        List<Review> reviews = dbHelper.getReviewsForAssociatedId(testAssociatedId);
        
        // Verify update worked
        assertEquals("Content should be updated", "Updated content", reviews.get(0).getContent());
        
        // Clean up
        dbHelper.deleteReview(review.getId());
    }
    
    @Test
    public void testDeleteReview() throws SQLException {
        // Create and add a review
        Review review = new Review(testReviewer, "Content to delete", testAssociatedId);
        dbHelper.addReview(review);
        
        // Delete the review
        dbHelper.deleteReview(review.getId());
        
        // Try to retrieve the review
        List<Review> reviews = dbHelper.getReviewsForAssociatedId(testAssociatedId);
        
        // Verify deletion worked
        assertEquals("Review list should be empty", 0, reviews.size());
    }
    
    
    @Test
    public void testTrustedReviewers() throws SQLException {
        // Add a trusted reviewer
        dbHelper.addTrustedReviewer(testStudent, testReviewer, 8);
        
        // Get the trusted reviewers
        List<TrustedReviewer> trustedReviewers = dbHelper.getTrustedReviewers(testStudent);
        
        // Verify
        assertTrue("Should have at least one trusted reviewer", trustedReviewers.size() >= 1);
        
        boolean found = false;
        for (TrustedReviewer tr : trustedReviewers) {
            if (tr.getReviewerUsername().equals(testReviewer) && tr.getWeight() == 8) {
                found = true;
                break;
            }
        }
        
        assertTrue("Should find the added trusted reviewer", found);
        
        // Test updating the weight
        dbHelper.updateTrustedReviewerWeight(testStudent, testReviewer, 10);
        
        // Get the updated trusted reviewers
        trustedReviewers = dbHelper.getTrustedReviewers(testStudent);
        
        // Verify the update
        found = false;
        for (TrustedReviewer tr : trustedReviewers) {
            if (tr.getReviewerUsername().equals(testReviewer) && tr.getWeight() == 10) {
                found = true;
                break;
            }
        }
        
        assertTrue("Should find the updated trusted reviewer weight", found);
        
        // Clean up
        dbHelper.removeTrustedReviewer(testStudent, testReviewer);
    }
    
    @Test
    public void testReviewsFromTrustedReviewers() throws SQLException {
        // Setup: Add a trusted reviewer
        dbHelper.addTrustedReviewer(testStudent, testReviewer, 7);
        
        // Add a review from the trusted reviewer
        Review review = new Review(testReviewer, "Trusted review content", testAssociatedId);
        dbHelper.addReview(review);
        
        // Get reviews from trusted reviewers
        List<Review> trustedReviews = dbHelper.getReviewsFromTrustedReviewers(testStudent, testAssociatedId);
        
        // Verify
        assertEquals("Should have 1 trusted review", 1, trustedReviews.size());
        assertEquals("Review content should match", "Trusted review content", trustedReviews.get(0).getContent());
        
        // Clean up
        dbHelper.deleteReview(review.getId());
        dbHelper.removeTrustedReviewer(testStudent, testReviewer);
    }
    
    @Test
    public void testLoadAllDiscussionContent() throws SQLException {
        // Create test data
        Question testQuestion = new Question("Test title", "Test description", "testAuthor");
        Answer testAnswer = new Answer(testQuestion.getId(), "Test answer", "testAuthor");
        
        // Save to database
        dbHelper.saveQuestion(testQuestion);
        dbHelper.saveAnswer(testAnswer);
        
        // Create empty collections to load into
        Questions questions = new Questions();
        Answers answers = new Answers();
        
        // Load all content
        dbHelper.loadAllDiscussionContent(questions, answers);
        
        // Verify content was loaded
        boolean foundQuestion = false;
        boolean foundAnswer = false;
        
        for (Question q : questions.getAllQuestions()) {
            if (q.getId().equals(testQuestion.getId())) {
                foundQuestion = true;
                break;
            }
        }
        
        for (Answer a : answers.getAllAnswers()) {
            if (a.getId().equals(testAnswer.getId())) {
                foundAnswer = true;
                break;
            }
        }
        
        assertTrue("Should find the test question", foundQuestion);
        assertTrue("Should find the test answer", foundAnswer);
        
        // Clean up
        dbHelper.deleteQuestion(testQuestion.getId());
    }
    
    @Test
    public void testFixReviewsTable() throws SQLException {
        // This test just verifies that the method runs without errors
        try {
            dbHelper.fixReviewsTable();
            // If we reach here, no exception was thrown
            assertTrue(true);
        } catch (SQLException e) {
            fail("fixReviewsTable threw an exception: " + e.getMessage());
        }
    }

    @Test
    public void testReviewsWithRating() throws SQLException {
        // Create reviews with different ratings
        Review review1 = new Review(testReviewer, "5-star review", testAssociatedId, 5, "Question");
        Review review2 = new Review(testReviewer, "3-star review", testAssociatedId, 3, "Answer");
        Review review3 = new Review(testReviewer, "1-star review", testAssociatedId, 1, "Comment");
        
        // Add to database
        dbHelper.addReview(review1);
        dbHelper.addReview(review2);
        dbHelper.addReview(review3);
        
        // Get all reviews for associated ID
        List<Review> reviews = dbHelper.getReviewsForAssociatedId(testAssociatedId);
        
        // Verify correct number of reviews
        assertEquals("Should have 3 reviews", 3, reviews.size());
        
        // Calculate average rating
        double avgRating = reviews.stream().mapToInt(Review::getRating).average().orElse(0);
        assertEquals("Average rating should be 3.0", 3.0, avgRating, 0.01);
        
        // Find highest and lowest rating
        int highestRating = reviews.stream().mapToInt(Review::getRating).max().orElse(0);
        int lowestRating = reviews.stream().mapToInt(Review::getRating).min().orElse(0);
        
        assertEquals("Highest rating should be 5", 5, highestRating);
        assertEquals("Lowest rating should be 1", 1, lowestRating);
        
        // Clean up
        for (Review review : reviews) {
            dbHelper.deleteReview(review.getId());
        }
    }
}