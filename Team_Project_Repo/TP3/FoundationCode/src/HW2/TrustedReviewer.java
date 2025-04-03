package HW2;

/**
 * Represents a trusted reviewer relationship between a student and a reviewer
 */
public class TrustedReviewer {
    private String reviewerUsername;
    private String studentUsername;
    private int weight; // On a scale of 1-10
    
    private static final int MIN_WEIGHT = 1;
    private static final int MAX_WEIGHT = 10;
    private static final int DEFAULT_WEIGHT = 5;
    
    /**
     * Creates a new trusted reviewer relationship
     * @param reviewerUsername The username of the reviewer
     * @param studentUsername The username of the student
     * @param weight The weight/trust level (1-10)
     */
    public TrustedReviewer(String reviewerUsername, String studentUsername, int weight) {
        this.reviewerUsername = reviewerUsername;
        this.studentUsername = studentUsername;
        setWeight(weight); // Use the setter to apply validation
    }
    
    public String getReviewerUsername() {
        return reviewerUsername;
    }
    
    public String getStudentUsername() {
        return studentUsername;
    }
    
    public int getWeight() {
        return weight;
    }
    
    /**
     * Sets the weight/trust level of this reviewer
     * @param weight Value between 1-10
     */
    public void setWeight(int weight) {
        // Apply validation rules
        if (weight < MIN_WEIGHT) {
            this.weight = MIN_WEIGHT;
        } else if (weight > MAX_WEIGHT) {
            this.weight = MAX_WEIGHT;
        } else {
            this.weight = weight;
        }
    }
}