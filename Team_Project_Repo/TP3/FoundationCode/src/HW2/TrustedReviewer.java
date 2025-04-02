package HW2;

public class TrustedReviewer {
    private String username;
    private String studentUsername; // The student who trusts this reviewer
    private int weight; // Weightage value from 1-10
    
    public TrustedReviewer(String username, String studentUsername, int weight) {
        this.username = username;
        this.studentUsername = studentUsername;
        setWeight(weight);
    }
    
    // Getters and setters
    public String getUsername() { return username; }
    public String getStudentUsername() { return studentUsername; }
    public int getWeight() { return weight; }
    
    public void setWeight(int weight) {
        if (weight < 1 || weight > 10) {
            throw new IllegalArgumentException("Weight must be between 1 and 10");
        }
        this.weight = weight;
    }
}