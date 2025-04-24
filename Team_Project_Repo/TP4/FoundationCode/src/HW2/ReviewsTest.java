package HW2;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for {@link Reviews}.
 * <p>
 * This class contains unit tests for verifying the functionality of the Reviews class,
 * including methods for adding, updating, and removing reviews, as well as filtering
 * reviews by reviewer or associated item.
 * </p>
 * 
 * @author CSE360 Team
 * @version 1.0
 * @see Reviews
 * @see Review
 */
public class ReviewsTest {
    /** The review manager instance used for testing. */
    private Reviews reviewManager;
    
    /** A test associated ID used for creating reviews. */
    private String testAssociatedId = "test-associated-id";

    /**
     * Sets up the test environment before each test.
     * <p>
     * Creates a new Reviews instance to ensure each test starts with a clean state.
     * </p>
     */
    @Before
    public void setUp() {
        reviewManager = new Reviews();
    }

    /**
     * Tests adding a new review to the collection.
     * <p>
     * Verifies that when a review is added, it can be retrieved by its associated ID.
     * </p>
     */
    @Test
    public void testAddReview() {
        Review newReview = new Review("reviewer1", "test review content", testAssociatedId);
        reviewManager.addReview(newReview);
        assertEquals("review list should contain one review", 
                    1, reviewManager.getReviewsForAssociatedId(testAssociatedId).size());
    }

    /**
     * Tests removing a review from the collection.
     * <p>
     * Verifies that when a review is removed, it is correctly deleted from the collection.
     * </p>
     */
    @Test
    public void testRemoveReview() {
        Review review = new Review("reviewer1", "review to delete", testAssociatedId);
        reviewManager.addReview(review);
        reviewManager.removeReview(review);
        assertEquals("review list empty after deletion", 
                    0, reviewManager.getReviewsForAssociatedId(testAssociatedId).size());
    }

    /**
     * Tests updating a review in the collection.
     * <p>
     * Verifies that when a review's content is updated, the changes are reflected
     * in the stored review.
     * </p>
     */
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

    /**
     * Tests retrieving reviews by reviewer.
     * <p>
     * Verifies that the getReviewsByReviewer method correctly returns only reviews
     * created by the specified reviewer.
     * </p>
     */
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

    /**
     * Tests that a review with a rating is stored correctly.
     * <p>
     * Verifies that when a review is created with a rating, the rating is preserved.
     * </p>
     */
    @Test
    public void testReviewWithRating() {
        Review review = new Review("reviewer1", "rated review", testAssociatedId, 5);
        reviewManager.addReview(review);
        
        assertEquals("Rating should be 5", 
                    5, reviewManager.getReviewsForAssociatedId(testAssociatedId).get(0).getRating());
    }

    /**
     * Tests that a review with a specified type is stored correctly.
     * <p>
     * Verifies that when a review is created with a type, the type is preserved.
     * </p>
     */
    @Test
    public void testReviewWithType() {
        Review review = new Review("reviewer1", "typed review", testAssociatedId, "Question");
        reviewManager.addReview(review);
        
        assertEquals("Type should be Question", 
                    "Question", reviewManager.getReviewsForAssociatedId(testAssociatedId).get(0).getType());
    }
    
    /**
     * Tests that a review with both rating and type is stored correctly.
     * <p>
     * Verifies that when a review is created with both rating and type, both values are preserved.
     * </p>
     */
    @Test
    public void testCompleteReview() {
        Review review = new Review("reviewer1", "complete review", testAssociatedId, 4, "Answer");
        reviewManager.addReview(review);
        
        Review retrievedReview = reviewManager.getReviewsForAssociatedId(testAssociatedId).get(0);
        assertEquals("Rating should be 4", 4, retrievedReview.getRating());
        assertEquals("Type should be Answer", "Answer", retrievedReview.getType());
    }
}