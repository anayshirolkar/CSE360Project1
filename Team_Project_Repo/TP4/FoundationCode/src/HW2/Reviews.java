package HW2;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Reviews {
    private List<Review> reviews;

    public Reviews() {
        this.reviews = new ArrayList<>();
    }

    // Create
    public void addReview(Review review) {
        if (review == null) {
            throw new IllegalArgumentException("Review cannot be null");
        }
        reviews.add(review);
    }

    // Read
    public List<Review> getReviewsByReviewer(String reviewer) {
        return reviews.stream()
            .filter(r -> r.getReviewer().equals(reviewer))
            .collect(Collectors.toList());
    }

    public List<Review> getReviewsForAssociatedId(String associatedId) {
        return reviews.stream()
            .filter(r -> r.getAssociatedId().equals(associatedId))
            .collect(Collectors.toList());
    }

    /**
     * Gets all reviews in the collection
     * @return A list of all reviews
     */
    public List<Review> getReviews() {
        return new ArrayList<>(reviews); // Return a defensive copy
    }

    // Update
    public void updateReview(Review updatedReview) {
        for (int i = 0; i < reviews.size(); i++) {
            Review review = reviews.get(i);
            if (review.getId().equals(updatedReview.getId())) {
                reviews.set(i, updatedReview); // Update the review in the list
                return;
            }
        }
        throw new IllegalArgumentException("Review not found: " + updatedReview.getId());
    }

    // Delete
    public void deleteReview(Review review) {
        reviews.removeIf(r -> r.getId().equals(review));
    }

	public void removeReview(Review review) {
        if (review == null) {
            throw new IllegalArgumentException("Review cannot be null");
        }
        
        boolean removed = reviews.removeIf(r -> r.getId().equals(review.getId()));
        
        if (!removed) {
            System.out.println("Review not found with ID: " + review.getId());
        }
    }
	
}