package HW2;
import static org.junit.Assert.*;
import org.junit.Test;
import java.util.Date;

public class ReviewTest {

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
    
    @Test
    public void testConstructorWithRating() {
        Review review = new Review("reviewer1", "test content", "test-id", 4);
        
        assertEquals(4, review.getRating());
        assertEquals("Question", review.getType()); // Default type
    }
    
    @Test
    public void testConstructorWithType() {
        Review review = new Review("reviewer1", "test content", "test-id", "Answer");
        
        assertEquals(0, review.getRating()); // Default rating
        assertEquals("Answer", review.getType());
    }
    
    @Test
    public void testFullConstructor() {
        Review review = new Review("reviewer1", "test content", "test-id", 5, "Comment");
        
        assertEquals(5, review.getRating());
        assertEquals("Comment", review.getType());
    }
    
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
    
    @Test
    public void testSetRating() {
        Review review = new Review("reviewer1", "test content", "test-id");
        review.setRating(3);
        
        assertEquals(3, review.getRating());
    }
    
    @Test
    public void testSetType() {
        Review review = new Review("reviewer1", "test content", "test-id");
        review.setType("Answer");
        
        assertEquals("Answer", review.getType());
    }
    
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