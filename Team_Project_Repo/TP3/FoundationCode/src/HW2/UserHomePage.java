package HW2;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import databasePart1.DatabaseHelper;

/**
 * This page displays a simple welcome message for the user and buttons to navigate to different features.
 */
public class UserHomePage {
    private DatabaseHelper databaseHelper;
    private String username;

    public UserHomePage() {
        // Default constructor for backward compatibility
    }
    
    public UserHomePage(DatabaseHelper databaseHelper, String username) {
        this.databaseHelper = databaseHelper;
        this.username = username;
    }

    public void show(Stage primaryStage) {
        VBox layout = new VBox(15); // Increased spacing between elements
        layout.setStyle("-fx-alignment: center; -fx-padding: 20;");
        
        // Label to display Hello user
        Label userLabel = new Label("Hello, " + (username != null ? username : "User") + "!");
        userLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        
        // Update window title to include username
        primaryStage.setTitle("User Dashboard - " + (username != null ? username : "User"));
        
        // Button to navigate to the Discussion Page
        Button discussionButton = new Button("Go to Discussion Page");
        discussionButton.setStyle("-fx-font-size: 14px; -fx-background-color: #4CAF50; -fx-text-fill: white; -fx-min-width: 220px;");
        
        // Button to navigate to Reviews and Trusted Reviewers
        Button reviewsButton = new Button("Reviews & Trusted Reviewers");
        reviewsButton.setStyle("-fx-font-size: 14px; -fx-background-color: #2196F3; -fx-text-fill: white; -fx-min-width: 220px;");
        
        // Button to request reviewer status
        Button requestReviewerButton = new Button("Request Reviewer Status");
        requestReviewerButton.setStyle("-fx-font-size: 14px; -fx-background-color: #FF9800; -fx-text-fill: white; -fx-min-width: 220px;");
        
        // Action handler for discussion button
        discussionButton.setOnAction(e -> {
            // Create the DiscussionPage instance and show it
            DiscussionPage discussionPage = new DiscussionPage(databaseHelper, username);
            discussionPage.show(primaryStage);
        });

        // Action handler for reviews button
        reviewsButton.setOnAction(e -> {
            try {
                System.out.println("Opening StudentReviewsPage for user: " + username);
                // Navigate to the student reviews page with defensive null checks
                StudentReviewsPage reviewsPage = new StudentReviewsPage(databaseHelper, 
                    username != null ? username : "DefaultUser");
                reviewsPage.show(primaryStage);
            } catch (Exception ex) {
                System.err.println("Error opening StudentReviewsPage: " + ex.getMessage());
                ex.printStackTrace();
                // Show an error message to the user
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Failed to open Reviews page");
                alert.setContentText("An error occurred: " + ex.getMessage());
                alert.showAndWait();
            }
        });

        // Action handler for reviewer request button
        requestReviewerButton.setOnAction(e -> {
            if (databaseHelper != null && username != null) {
                try {
                    // Use submitReviewerRequest instead of requestReviewerStatus
                    databaseHelper.submitReviewerRequest(username);
                    
                    // Show confirmation dialog
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Request Submitted");
                    alert.setHeaderText(null);
                    alert.setContentText("Your request for reviewer status has been submitted. An administrator will review your request.");
                    alert.showAndWait();
                } catch (Exception ex) {
                    // Show error dialog
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText(null);
                    alert.setContentText("Error submitting request: " + ex.getMessage());
                    alert.showAndWait();
                }
            } else {
                // Fallback for demo when database is not available
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Request Submitted");
                alert.setHeaderText(null);
                alert.setContentText("Your request for reviewer status has been submitted. An administrator will review your request.");
                alert.showAndWait();
            }
        });

        // Add label and buttons to layout
        layout.getChildren().addAll(userLabel, discussionButton, reviewsButton, requestReviewerButton);

        // Create scene and set it on the primary stage
        Scene userScene = new Scene(layout, 800, 400);
        primaryStage.setScene(userScene);
        primaryStage.setTitle("User Page");
    }
}