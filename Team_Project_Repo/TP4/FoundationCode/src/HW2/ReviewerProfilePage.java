package HW2;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import databasePart1.DatabaseHelper;
import java.sql.SQLException;
import java.util.List;

/**
 * This page displays a reviewer's profile, including experience, reviews, and feedback.
 */
public class ReviewerProfilePage {
    private final DatabaseHelper databaseHelper;
    private final String reviewerUsername;
    private final String currentUsername; // The user viewing the profile

    public ReviewerProfilePage(DatabaseHelper databaseHelper, String reviewerUsername, String currentUsername) {
        this.databaseHelper = databaseHelper;
        this.reviewerUsername = reviewerUsername;
        this.currentUsername = currentUsername;
    }

    public void show(Stage primaryStage) {
        BorderPane mainLayout = new BorderPane();
        mainLayout.setPadding(new Insets(20));
        
        // Top section - Reviewer name and stats
        VBox topSection = new VBox(10);
        
        Label titleLabel = new Label("Reviewer Profile: " + reviewerUsername);
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        
        // Load stats
        int totalReviews = 0;
        double avgRating = 0.0;
        
        try {
            if (databaseHelper != null) {
                totalReviews = databaseHelper.getReviewCountForReviewer(reviewerUsername);
                avgRating = databaseHelper.getAverageRatingForReviewer(reviewerUsername);
            }
        } catch (SQLException e) {
            System.err.println("Error loading reviewer stats: " + e.getMessage());
        }
        
        Label statsLabel = new Label(String.format("Total Reviews: %d | Average Rating: %.1f", 
                                     totalReviews, avgRating));
        statsLabel.setStyle("-fx-font-style: italic;");
        
        TextArea experienceArea = new TextArea();
        experienceArea.setPromptText("No experience information provided");
        experienceArea.setPrefHeight(100);
        experienceArea.setEditable(reviewerUsername.equals(currentUsername));
        
        // Load experience
        try {
            if (databaseHelper != null) {
                String experience = databaseHelper.getReviewerExperience(reviewerUsername);
                if (experience != null && !experience.isEmpty()) {
                    experienceArea.setText(experience);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error loading experience: " + e.getMessage());
        }
        
        Button saveExperienceButton = new Button("Save Experience");
        saveExperienceButton.setVisible(reviewerUsername.equals(currentUsername));
        
        saveExperienceButton.setOnAction(e -> {
            try {
                databaseHelper.updateReviewerExperience(reviewerUsername, experienceArea.getText());
                showAlert("Experience Saved", "Your experience information has been updated.");
            } catch (SQLException ex) {
                showAlert("Error", "Could not save experience: " + ex.getMessage());
            }
        });
        
        topSection.getChildren().addAll(titleLabel, statsLabel, 
                                       new Label("Experience:"), experienceArea);
        
        if (reviewerUsername.equals(currentUsername)) {
            topSection.getChildren().add(saveExperienceButton);
        }
        
        mainLayout.setTop(topSection);
        
        // Center section - TabPane for reviews and feedback
        TabPane tabPane = new TabPane();
        
        // Tab 1: Reviews by this reviewer
        Tab reviewsTab = new Tab("Reviews");
        reviewsTab.setContent(createReviewsPane());
        reviewsTab.setClosable(false);
        
        // Tab 2: Feedback received
        Tab feedbackTab = new Tab("Feedback Received");
        feedbackTab.setContent(createFeedbackPane());
        feedbackTab.setClosable(false);
        
        tabPane.getTabs().addAll(reviewsTab, feedbackTab);
        mainLayout.setCenter(tabPane);
        
        // Bottom section - Back button
        Button backButton = new Button("Back");
        backButton.setOnAction(e -> {
            // Return to previous page
            if (reviewerUsername.equals(currentUsername)) {
                new ReviewerHomePage(databaseHelper, currentUsername).show(primaryStage);
            } else {
                new StudentReviewsPage(databaseHelper, currentUsername).show(primaryStage);
            }
        });
        
        HBox bottomSection = new HBox(backButton);
        bottomSection.setAlignment(Pos.CENTER);
        bottomSection.setPadding(new Insets(20, 0, 0, 0));
        
        mainLayout.setBottom(bottomSection);
        
        Scene scene = new Scene(mainLayout, 800, 600);
        primaryStage.setTitle("Reviewer Profile");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    private Pane createReviewsPane() {
        VBox reviewsPane = new VBox(10);
        reviewsPane.setPadding(new Insets(10));
        
        ListView<Review> reviewsListView = new ListView<>();
        
        try {
            if (databaseHelper != null) {
                List<Review> reviewsList = databaseHelper.getReviewsByReviewer(reviewerUsername);
                reviewsListView.getItems().addAll(reviewsList);
            }
        } catch (SQLException e) {
            System.err.println("Error loading reviews: " + e.getMessage());
        }
        
        reviewsListView.setCellFactory(lv -> new ListCell<Review>() {
            @Override
            protected void updateItem(Review review, boolean empty) {
                super.updateItem(review, empty);
                if (empty || review == null) {
                    setText(null);
                } else {
                    setText(review.getContent() + " (Rating: " + review.getRating() + ")");
                }
            }
        });
        
        reviewsPane.getChildren().add(reviewsListView);
        return reviewsPane;
    }
    
    private Pane createFeedbackPane() {
        VBox feedbackPane = new VBox(10);
        feedbackPane.setPadding(new Insets(10));
        
        ListView<Message> feedbackListView = new ListView<>();
        
        try {
            if (databaseHelper != null) {
                List<Message> feedbackList = databaseHelper.getFeedbackForReviewer(reviewerUsername);
                feedbackListView.getItems().addAll(feedbackList);
            }
        } catch (SQLException e) {
            System.err.println("Error loading feedback: " + e.getMessage());
        }
        
        feedbackListView.setCellFactory(lv -> new ListCell<Message>() {
            @Override
            protected void updateItem(Message message, boolean empty) {
                super.updateItem(message, empty);
                if (empty || message == null) {
                    setText(null);
                } else {
                    setText("From: " + message.getSender() + "\n" + message.getContent());
                }
            }
        });
        
        feedbackPane.getChildren().add(feedbackListView);
        return feedbackPane;
    }
    
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}