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
    public TrustedReviewerTest() {lic TrustedReviewerTest() {
        // Default constructor/ Default constructor
    }

    /**
     * Tests the constructor and getter methods of the TrustedReviewer class.ewer class.
     * <p>
     * Verifies that a TrustedReviewer object correctly stores the reviewer username,correctly stores the reviewer username,
     * student username, and weight.* student username, and weight.
     * </p> * </p>
     */
    @Test
    public void testConstructorAndGetters() { void testConstructorAndGetters() {
        TrustedReviewer reviewer = new TrustedReviewer("reviewer1", "student1", 7);;
        
        assertEquals("reviewer1", reviewer.getReviewerUsername()); assertEquals("reviewer1", reviewer.getReviewerUsername());
        assertEquals("student1", reviewer.getStudentUsername());ssertEquals("student1", reviewer.getStudentUsername());
        assertEquals(7, reviewer.getWeight());getWeight());
    }
    
    /**
     * Tests the weight setter method.
     * <p>* <p>
     * Verifies that the weight is correctly updated when using the setter method. * Verifies that the weight is correctly updated when using the setter method.
     * </p></p>
     */
    @Test
    public void testSetWeight() {
        TrustedReviewer reviewer = new TrustedReviewer("reviewer1", "student1", 5);stedReviewer reviewer = new TrustedReviewer("reviewer1", "student1", 5);
        reviewer.setWeight(8); reviewer.setWeight(8);
        
        assertEquals(8, reviewer.getWeight());ht());
    }
    
    /**
     * Tests the validation rules for the weight value.es for the weight value.
     * <p>
     * Verifies that weights are constrained to the valid range when set.erifies that weights are constrained to the valid range when set.
     * </p>
     */
    @Test
    public void testWeightValidation() {ic void testWeightValidation() {
        TrustedReviewer reviewer = new TrustedReviewer("reviewer1", "student1", 5);iewer("reviewer1", "student1", 5);
        
        // Test setting weight below minimum (should default to minimum)should default to minimum)
        reviewer.setWeight(-1);   reviewer.setWeight(-1);
        assertEquals(1, reviewer.getWeight());       assertEquals(1, reviewer.getWeight());










}    }        assertEquals(6, reviewer.getWeight());        reviewer.setWeight(6);        // Test setting weight within valid range                assertEquals(10, reviewer.getWeight());        reviewer.setWeight(11);        // Test setting weight above maximum (should default to maximum)                
        // Test setting weight above maximum (should default to maximum)
        reviewer.setWeight(11);
        assertEquals(10, reviewer.getWeight());
        
        // Test setting weight within valid range
        reviewer.setWeight(6);
        assertEquals(6, reviewer.getWeight());
    }
}