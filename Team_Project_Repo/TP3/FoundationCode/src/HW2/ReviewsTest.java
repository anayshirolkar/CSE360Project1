package HW2;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

public class ReviewsTest {
    private Reviews reviewManager;
    private String testAssociatedId = "test-associated-id";

    @Before
    public void setUp() {
        reviewManager = new Reviews();
    }

    @Test
    public void testAddReview() {
        Review newReview = new Review("reviewer1", "test review content", testAssociatedId);
        reviewManager.addReview(newReview);
        assertEquals("review list should contain one review", 
                    1, reviewManager.getReviewsForAssociatedId(testAssociatedId).size());
    }

    @Test
    public void testRemoveReview() {
        Review review = new Review("reviewer1", "review to delete", testAssociatedId);
        reviewManager.addReview(review);
        reviewManager.removeReview(review);
        assertEquals("review list empty after deletion", 
                    0, reviewManager.getReviewsForAssociatedId(testAssociatedId).size());
    }

    @Test
    public void testUpdateReview() {
        Review review = new Review("reviewer1", "old review content", testAssociatedId);
        reviewManager.addReview(review);
        
        // Create an updated version of the review
        Review updatedReview = new Review(review.getReviewer(), "updated review content", review.getAssociatedId());
        updatedReview.setId(review.getId()); // Need to match the ID for update to work
        
        reviewManager.updateReview(updatedReview);
        
        // Verify the review was updated
        assertEquals("Review content should be updated", 
                    "updated review content", 
                    reviewManager.getReviewsForAssociatedId(testAssociatedId).get(0).getContent());
    }

    @Test
    public void testGetReviewsByReviewer() {
        Review review1 = new Review("reviewer1", "first review", "id1");
        Review review2 = new Review("reviewer1", "second review", "id2");
        Review review3 = new Review("reviewer2", "third review", "id3");
        
        reviewManager.addReview(review1);
        reviewManager.addReview(review2);
        reviewManager.addReview(review3);
        
        assertEquals("Should be 2 reviews by reviewer1", 
                    2, reviewManager.getReviewsByReviewer("reviewer1").size());
        assertEquals("Should be 1 review by reviewer2", 
                    1, reviewManager.getReviewsByReviewer("reviewer2").size());
    }

    @Test
    public void testReviewWithRating() {
        Review review = new Review("reviewer1", "rated review", testAssociatedId, 5);
        reviewManager.addReview(review);
        
        assertEquals("Rating should be 5", 
                    5, reviewManager.getReviewsForAssociatedId(testAssociatedId).get(0).getRating());
    }

    @Test
    public void testReviewWithType() {
        Review review = new Review("reviewer1", "typed review", testAssociatedId, "Question");
        reviewManager.addReview(review);
        
        assertEquals("Type should be Question", 
                    "Question", reviewManager.getReviewsForAssociatedId(testAssociatedId).get(0).getType());
    }
    
    @Test
    public void testCompleteReview() {
        Review review = new Review("reviewer1", "complete review", testAssociatedId, 4, "Answer");
        reviewManager.addReview(review);
        
        Review retrievedReview = reviewManager.getReviewsForAssociatedId(testAssociatedId).get(0);
        assertEquals("Rating should be 4", 4, retrievedReview.getRating());
        assertEquals("Type should be Answer", "Answer", retrievedReview.getType());
    }
}