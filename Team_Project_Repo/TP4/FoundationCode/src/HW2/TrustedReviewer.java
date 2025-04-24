package HW2;

/**
 * Represents a trusted reviewer relationship between a student and a reviewer
 */
public class TrustedReviewer {
    private String reviewerUsername;
    private String studentUsername;
    private int weight;
    
    // Constructor - ensures consistent property names
    public TrustedReviewer(String reviewerUsername, String studentUsername, int weight) {
        this.reviewerUsername = reviewerUsername;
        this.studentUsername = studentUsername;
        setWeight(weight);
    }
    
    // Getters
    public String getReviewerUsername() {
        return reviewerUsername;
    }
    
    public String getStudentUsername() {
        return studentUsername;
    }
    
    public int getWeight() {
        return weight;
    }
    
    // Setter with validation
    public void setWeight(int weight) {
        if (weight < 1) {
            this.weight = 1;
        } else if (weight > 10) {
            this.weight = 10;
        } else {
            this.weight = weight;
        }
    }
}