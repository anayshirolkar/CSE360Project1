package HW2;
import static org.junit.Assert.*;
import org.junit.Test;

public class TrustedReviewerTest {

    @Test
    public void testConstructorAndGetters() {
        TrustedReviewer reviewer = new TrustedReviewer("reviewer1", "student1", 7);
        
        assertEquals("reviewer1", reviewer.getReviewerUsername());
        assertEquals("student1", reviewer.getStudentUsername());
        assertEquals(7, reviewer.getWeight());
    }
    
    @Test
    public void testSetWeight() {
        TrustedReviewer reviewer = new TrustedReviewer("reviewer1", "student1", 5);
        reviewer.setWeight(8);
        
        assertEquals(8, reviewer.getWeight());
    }
    
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