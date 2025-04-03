package HW2;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.sql.SQLException; // Add this import statement

import databasePart1.DatabaseHelper;

/**
 * This page displays a simple welcome message for the user and buttons to navigate to different features.
 */
public class UserHomePage {
    private DatabaseHelper databaseHelper;
    private String username;
    private String role; // Add this field

    // Existing constructors
    public UserHomePage(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
        this.username = "User";
        this.role = "user"; // Default role
    }

    public UserHomePage(DatabaseHelper databaseHelper, String username) {
        this.databaseHelper = databaseHelper;
        this.username = username;
        this.role = "user"; // Default role 
    }

    // Add a new constructor that accepts role
    public UserHomePage(DatabaseHelper databaseHelper, String username, String role) {
        this.databaseHelper = databaseHelper;
        this.username = username;
        this.role = role;
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
        
        // Check user's actual role in the database instead of relying on the passed role parameter
        boolean isReviewer = false;
        if (databaseHelper != null && username != null) {
            try {
                String actualRole = databaseHelper.getUserRole(username);
                isReviewer = "reviewer".equals(actualRole);
            } catch (Exception e) {
                System.err.println("Error checking user role: " + e.getMessage());
                e.printStackTrace();
                // Fall back to the passed role if database check fails
                isReviewer = "reviewer".equals(role);
            }
        }
        
        // Create Button instance outside of conditional blocks
        final Button reviewerActionButton;
        
        if (isReviewer) {
            // User is a reviewer, create the dashboard button
            reviewerActionButton = new Button("Go to Reviewer Dashboard");
            reviewerActionButton.setStyle("-fx-font-size: 14px; -fx-background-color: #FF9800; -fx-text-fill: white; -fx-min-width: 220px;");
            
            // Action handler for reviewer dashboard button
            reviewerActionButton.setOnAction(e -> {
                ReviewerHomePage reviewerPage = new ReviewerHomePage(databaseHelper, username);
                reviewerPage.show(primaryStage);
            });
        } else {
            // User is not a reviewer, check if they have a pending request
            boolean hasPendingRequest = false;
            if (databaseHelper != null && username != null) {
                try {
                    java.util.Map<String, Object> requestDetails = databaseHelper.getReviewerRequestDetails(username);
                    hasPendingRequest = !requestDetails.isEmpty() && "PENDING".equals(requestDetails.get("status"));
                } catch (Exception e) {
                    System.err.println("Error checking pending requests: " + e.getMessage());
                    // Assume no pending request if check fails
                }
            }
            
            if (hasPendingRequest) {
                // Create a disabled "pending" button
                reviewerActionButton = new Button("Reviewer Request Pending");
                reviewerActionButton.setStyle("-fx-font-size: 14px; -fx-background-color: #9E9E9E; -fx-text-fill: white; -fx-min-width: 220px;");
                reviewerActionButton.setDisable(true);
            } else {
                // Create a "request" button
                reviewerActionButton = new Button("Request Reviewer Status");
                reviewerActionButton.setStyle("-fx-font-size: 14px; -fx-background-color: #FF9800; -fx-text-fill: white; -fx-min-width: 220px;");
                
                // Create a placeholder in layout for the button
                final int buttonIndex = layout.getChildren().size() + 3; // +3 accounts for userLabel, discussionButton, and reviewsButton
                
                // For capturing the button in lambda
                final Button reviewerButton = reviewerActionButton;
                
                reviewerActionButton.setOnAction(e -> {
                    if (databaseHelper != null && username != null) {
                        try {
                            // Use submitReviewerRequest
                            databaseHelper.submitReviewerRequest(username);
                            
                            // Show confirmation dialog
                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setTitle("Request Submitted");
                            alert.setHeaderText(null);
                            alert.setContentText("Your request for reviewer status has been submitted. An administrator will review your request.");
                            alert.showAndWait();
                            
                            // Create a new button to replace the current one
                            Button newButton = new Button("Reviewer Request Pending");
                            newButton.setStyle("-fx-font-size: 14px; -fx-background-color: #9E9E9E; -fx-text-fill: white; -fx-min-width: 220px;");
                            newButton.setDisable(true);
                            
                            // Replace the button in the layout
                            layout.getChildren().remove(reviewerButton);
                            layout.getChildren().add(buttonIndex - 1, newButton); // -1 because we just removed an item
                            
                        } catch (Exception ex) {
                            // Show error dialog
                            Alert alert = new Alert(Alert.AlertType.ERROR);
                            alert.setTitle("Error");
                            alert.setHeaderText(null);
                            alert.setContentText("Error submitting request: " + ex.getMessage());
                            alert.showAndWait();
                        }
                    }
                });
            }
        }
        
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

        // Add label and buttons to layout
        layout.getChildren().add(userLabel);
        layout.getChildren().add(discussionButton);
        layout.getChildren().add(reviewsButton);
        
        // Add reviewer action button
        layout.getChildren().add(reviewerActionButton);

        // Button to log out
        Button logoutButton = new Button("Logout");
        logoutButton.setStyle("-fx-font-size: 14px; -fx-background-color: #f44336; -fx-text-fill: white; -fx-min-width: 220px;");
        
        logoutButton.setOnAction(e -> {
            new SetupLoginSelectionPage(databaseHelper).show(primaryStage);
        });
        
        layout.getChildren().add(logoutButton);

        // Create scene and set it on the primary stage
        Scene userScene = new Scene(layout, 800, 400);
        primaryStage.setScene(userScene);
        primaryStage.show();
    }
}