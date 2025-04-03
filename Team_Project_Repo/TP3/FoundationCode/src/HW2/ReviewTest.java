package HW2;
import static org.junit.Assert.*;
import org.junit.Test;
import java.util.Date;

/**
 * Test class for {@link Review}.
 * <p>
 * This class contains unit tests for verifying the functionality of the Review class,
 * including constructor variants, property getters/setters, and validation rules.
 * </p>
 * 
 * @author CSE360 Team
 * @version 1.0
 * @see Review
 */
public class ReviewTest {

    /**
     * Tests the basic constructor and getter methods of the Review class.
     * <p>
     * Verifies that a Review created with the basic constructor correctly stores
     * the reviewer name, content, and associated ID, and initializes default values
     * for other properties.
     * </p>
     */
    @Test
    public void testBasicConstructorAndGetters() {
        Review review = new Review("reviewer1", "test content", "test-id");
        
        assertEquals("reviewer1", review.getReviewer());
        assertEquals("test content", review.getContent());
        assertEquals("test-id", review.getAssociatedId());
        assertEquals(0, review.getRating()); // Default rating
        assertEquals("Question", review.getType()); // Default type
        assertNotNull(review.getId());
        assertNotNull(review.getCreatedAt());
        assertNotNull(review.getUpdatedAt());
    }
    
    /**
     * Tests the constructor with rating parameter.
     * <p>
     * Verifies that a Review created with the rating constructor correctly stores
     * the specified rating while maintaining default values for other properties.
     * </p>
     */
    @Test
    public void testConstructorWithRating() {
        Review review = new Review("reviewer1", "test content", "test-id", 4);
        
        assertEquals(4, review.getRating());
        assertEquals("Question", review.getType()); // Default type
    }
    
    /**
     * Tests the constructor with type parameter.
     * <p>
     * Verifies that a Review created with the type constructor correctly stores
     * the specified type while maintaining default values for other properties.
     * </p>
     */
    @Test
    public void testConstructorWithType() {
        Review review = new Review("reviewer1", "test content", "test-id", "Answer");
        
        assertEquals(0, review.getRating()); // Default rating
        assertEquals("Answer", review.getType());
    }
    
    /**
     * Tests the full constructor with both rating and type parameters.
     * <p>
     * Verifies that a Review created with the full constructor correctly stores
     * both the specified rating and type.
     * </p>
     */
    @Test
    public void testFullConstructor() {
        Review review = new Review("reviewer1", "test content", "test-id", 5, "Comment");
        
        assertEquals(5, review.getRating());
        assertEquals("Comment", review.getType());
    }
    
    /**
     * Tests the setContent method.
     * <p>
     * Verifies that when a review's content is updated, the content is changed and
     * the updatedAt timestamp is refreshed.
     * </p>
     */
    @Test
    public void testSetContent() {
        Review review = new Review("reviewer1", "original content", "test-id");
        Date originalUpdatedAt = review.getUpdatedAt();
        
        // Wait a moment to ensure timestamps will be different
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            // Ignore
        }
        
        review.setContent("updated content");
        
        assertEquals("updated content", review.getContent());
        assertTrue("UpdatedAt timestamp should be newer",
                review.getUpdatedAt().after(originalUpdatedAt));
    }
    
    /**
     * Tests the setRating method.
     * <p>
     * Verifies that the rating is correctly updated when using the setter method.
     * </p>
     */
    @Test
    public void testSetRating() {
        Review review = new Review("reviewer1", "test content", "test-id");
        review.setRating(3);
        
        assertEquals(3, review.getRating());
    }
    
    /**
     * Tests the setType method.
     * <p>
     * Verifies that the type is correctly updated when using the setter method.
     * </p>
     */
    @Test
    public void testSetType() {
        Review review = new Review("reviewer1", "test content", "test-id");
        review.setType("Answer");
        
        assertEquals("Answer", review.getType());
    }
    
    /**
     * Tests the validation rules for the rating value.
     * <p>
     * Verifies that ratings are constrained to the valid range (0-5) when set.
     * </p>
     */
    @Test
    public void testRatingValidation() {
        Review review = new Review("reviewer1", "test content", "test-id");
        
        // Test setting rating below minimum (should default to minimum)
        review.setRating(-1);
        assertEquals(0, review.getRating());
        
        // Test setting rating above maximum (should default to maximum)
        review.setRating(6);
        assertEquals(5, review.getRating());
        
        // Test setting rating within valid range
        review.setRating(3);
        assertEquals(3, review.getRating());
    }

    /**
     * Tests the validation rules for the updatedAt timestamp.
     * <p>
     * Verifies that:
     * <ul>
     *   <li>Future dates are accepted</li>
     *   <li>Dates before creation date are rejected</li>
     *   <li>Null dates are rejected</li>
     * </ul>
     * </p>
     */
    @Test
    public void testSetUpdatedDateValidation() {
        Review review = new Review("reviewer1", "test content", "test-id");
        Date originalCreatedAt = review.getCreatedAt();
        Date futureDate = new Date(originalCreatedAt.getTime() + 86400000); // One day later
        
        // Valid case - future date
        review.setUpdatedDate(futureDate);
        assertEquals("Updated date should be changed", futureDate, review.getUpdatedAt());
        
        // Invalid case - date before creation
        Date pastDate = new Date(originalCreatedAt.getTime() - 86400000); // One day earlier
        try {
            review.setUpdatedDate(pastDate);
            fail("Should throw IllegalArgumentException for date before creation date");
        } catch (IllegalArgumentException e) {
            assertEquals("Exception message should match", 
                    "Updated date cannot be before creation date", e.getMessage());
        }
        
        // Invalid case - null date
        try {
            review.setUpdatedDate(null);
            fail("Should throw IllegalArgumentException for null date");
        } catch (IllegalArgumentException e) {
            assertEquals("Exception message should match", 
                    "Updated date cannot be null", e.getMessage());
        }
    }

    /**
     * Tests that different review types are consistently managed.
     * <p>
     * Verifies that:
     * <ul>
     *   <li>Different review types can be created</li>
     *   <li>Types can be changed after creation</li>
     *   <li>The updatedAt timestamp is refreshed when type is changed</li>
     * </ul>
     * </p>
     */
    @Test
    public void testReviewTypeConsistency() {
        // Create reviews with different types
        Review questionReview = new Review("reviewer1", "Question review", "q-id", "Question");
        Review answerReview = new Review("reviewer1", "Answer review", "a-id", "Answer");
        Review commentReview = new Review("reviewer1", "Comment review", "c-id", "Comment");
        
        assertEquals("Type should be Question", "Question", questionReview.getType());
        assertEquals("Type should be Answer", "Answer", answerReview.getType());
        assertEquals("Type should be Comment", "Comment", commentReview.getType());
        
        // Test changing type
        questionReview.setType("Answer");
        assertEquals("Type should be updated to Answer", "Answer", questionReview.getType());
        
        // Verify updatedAt changes when type changes
        Date originalUpdatedAt = answerReview.getUpdatedAt();
        try {
            Thread.sleep(10); // Small delay to ensure timestamp difference
        } catch (InterruptedException e) {
            // Ignore
        }
        answerReview.setType("Comment");
        assertTrue("UpdatedAt should be later after changing type", 
                answerReview.getUpdatedAt().after(originalUpdatedAt));
    }

    /**
     * Tests that all constructor variants create consistent Review objects.
     * <p>
     * Verifies that:
     * <ul>
     *   <li>Basic constructor sets default values</li>
     *   <li>Rating constructor sets rating but keeps default type</li>
     *   <li>Type constructor sets type but keeps default rating</li>
     *   <li>Full constructor sets both rating and type</li>
     *   <li>Invalid ratings are properly constrained</li>
     * </ul>
     * </p>
     */
    @Test
    public void testReviewCreationWithMultipleConstructors() {
        // Test the various constructors for consistent behavior
        
        // Basic constructor
        Review review1 = new Review("reviewer1", "basic review", "id1");
        assertEquals("Default rating should be 0", 0, review1.getRating());
        assertEquals("Default type should be Question", "Question", review1.getType());
        
        // Constructor with rating
        Review review2 = new Review("reviewer1", "rated review", "id2", 4);
        assertEquals("Rating should be set", 4, review2.getRating());
        assertEquals("Default type should be Question", "Question", review2.getType());
        
        // Constructor with type
        Review review3 = new Review("reviewer1", "typed review", "id3", "Answer");
        assertEquals("Default rating should be 0", 0, review3.getRating());
        assertEquals("Type should be set", "Answer", review3.getType());
        
        // Constructor with both rating and type
        Review review4 = new Review("reviewer1", "complete review", "id4", 5, "Comment");
        assertEquals("Rating should be set", 5, review4.getRating());
        assertEquals("Type should be set", "Comment", review4.getType());
        
        // Verify invalid rating is corrected
        Review review5 = new Review("reviewer1", "invalid rating review", "id5", 10);
        assertEquals("Rating should be capped at MAX_RATING (5)", 5, review5.getRating());
    }
}