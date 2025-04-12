package HW2;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 * Test class for {@link TrustedReviewer}.
 * <p>
 * This class contains unit tests for verifying the functionality of the TrustedReviewer class,
 * including constructor, getters, and weight validation.
 * </p>
 * 
 * @author CSE360 Team
 * @version 1.0
 * @see TrustedReviewer
 */
public class TrustedReviewerTest {

    /**
     * Default constructor for TrustedReviewerTest.
     * <p>
     * Initializes a new test instance for testing TrustedReviewer functionality.
     * </p>
     */
    public TrustedReviewerTest() {
        // Default constructor
    }

    /**
     * Tests the constructor and getter methods of the TrustedReviewer class.
     * <p>
     * Verifies that a TrustedReviewer object correctly stores the reviewer username,
     * student username, and weight.
     * </p>
     */
    @Test
    public void testConstructorAndGetters() {
        TrustedReviewer reviewer = new TrustedReviewer("reviewer1", "student1", 7);
        
        assertEquals("reviewer1", reviewer.getReviewerUsername());
        assertEquals("student1", reviewer.getStudentUsername());
        assertEquals(7, reviewer.getWeight());
    }
    
    /**
     * Tests the weight setter method.
     * <p>
     * Verifies that the weight is correctly updated when using the setter method.
     * </p>
     */
    @Test
    public void testSetWeight() {
        TrustedReviewer reviewer = new TrustedReviewer("reviewer1", "student1", 5);
        reviewer.setWeight(8);
        
        assertEquals(8, reviewer.getWeight());
    }
    
    /**
     * Tests the validation rules for the weight value.
     * <p>
     * Verifies that weights are constrained to the valid range when set.
     * </p>
     */
    @Test
    public void testWeightValidation() {
        TrustedReviewer reviewer = new TrustedReviewer("reviewer1", "student1", 5);
        
        // Test setting weight below minimum (should default to minimum)
        reviewer.setWeight(-1);
        assertEquals(1, reviewer.getWeight());
        
        // Test setting weight above maximum (should default to maximum)
        reviewer.setWeight(11);
        assertEquals(10, reviewer.getWeight());
        
        // Test setting weight within valid range
        reviewer.setWeight(6);
        assertEquals(6, reviewer.getWeight());
    }
}