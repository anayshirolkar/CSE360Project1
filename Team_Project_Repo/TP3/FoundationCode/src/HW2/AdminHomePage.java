package HW2;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.application.Platform;

import databasePart1.DatabaseHelper;

/**
 * AdminPage class represents the user interface for the admin user.
 * This page displays admin functionalities including managing reviewer requests.
 */

public class AdminHomePage {
    
    private DatabaseHelper databaseHelper;
    private String username;
    
    /**
     * Default constructor
     */
    public AdminHomePage() {
        // Default constructor
    }
    
    /**
     * Constructor with databaseHelper parameter
     */
    public AdminHomePage(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
        this.username = "Admin";
    }
    
    /**
     * Constructor with both databaseHelper and username parameters
     */
    public AdminHomePage(DatabaseHelper databaseHelper, String username) {
        this.databaseHelper = databaseHelper;
        this.username = username != null ? username : "Admin";
    }

    /**
     * Displays the admin page in the provided primary stage.
     * @param primaryStage The primary stage where the scene will be displayed.
     */
    public void show(Stage primaryStage) {
        BorderPane mainLayout = new BorderPane();
        
        // Top section with username display and navigation bar
        VBox topContainer = new VBox(10);
        
        // User info section
        HBox userInfoSection = new HBox(10);
        userInfoSection.setAlignment(Pos.CENTER_RIGHT);
        userInfoSection.setPadding(new Insets(10, 10, 0, 10));
        
        Label usernameLabel = new Label("Logged in as: " + username);
        usernameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        userInfoSection.getChildren().add(usernameLabel);
        
        // Navigation bar
        HBox navigationBar = new HBox(10);
        navigationBar.setStyle("-fx-background-color: #f0f0f0; -fx-padding: 10; -fx-border-color: #cccccc; -fx-border-width: 0 0 1 0;");
        navigationBar.setAlignment(Pos.CENTER);
        
        Button homeButton = createNavButton("Admin Dashboard", true);
        Button discussionButton = createNavButton("Discussion Forum", false);
        Button reviewsButton = createNavButton("Reviews", false);
        Button logoutButton = createNavButton("Logout", false);
        logoutButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");
        
        navigationBar.getChildren().addAll(homeButton, discussionButton, reviewsButton, logoutButton);
        
        topContainer.getChildren().addAll(userInfoSection, navigationBar);
        mainLayout.setTop(topContainer);
        
        // Main content
        VBox contentSection = new VBox(20);
        contentSection.setAlignment(Pos.CENTER);
        contentSection.setPadding(new Insets(30, 20, 20, 20));
        
        // Label to display the welcome message for the admin
        Label adminLabel = new Label("Hello, " + username + "!");
        adminLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
        
        // Create an HBox for admin function cards
        HBox cardContainer = new HBox(30);
        cardContainer.setAlignment(Pos.CENTER);
        
        // Card for Invitation Codes
        VBox inviteCard = createFunctionCard(
            "Manage Invitation Codes", 
            "Create and manage invitation codes for new users.",
            "#4CAF50",
            e -> {
                // Show invitation page
                new InvitationPage().show(databaseHelper, primaryStage);
            }
        );
        
        // Card for Reviewer Requests
        VBox reviewerRequestsCard = createFunctionCard(
            "Manage Reviewer Requests", 
            "Approve or reject reviewer status requests from users.",
            "#2196F3",
            e -> {
                showManageReviewersDialog(primaryStage);
            }
        );
        
        // Card for User Activity
        VBox userActivityCard = createFunctionCard(
            "View Discussion Forum", 
            "View and participate in the discussion forum.",
            "#FF9800",
            e -> {
                // Navigate to DiscussionPage
                DiscussionPage discussionPage = new DiscussionPage(databaseHelper, username);
                discussionPage.show(primaryStage);
            }
        );
        
        cardContainer.getChildren().addAll(inviteCard, reviewerRequestsCard, userActivityCard);
        
        // Bottom navigation with logout
        HBox bottomSection = new HBox(10);
        bottomSection.setAlignment(Pos.CENTER);
        bottomSection.setPadding(new Insets(20, 0, 10, 0));
        
        Button exitButton = new Button("Exit Application");
        exitButton.setStyle("-fx-background-color: #757575; -fx-text-fill: white;");
        exitButton.setOnAction(e -> {
            if (databaseHelper != null) {
                databaseHelper.closeConnection();
            }
            Platform.exit();
        });
        
        bottomSection.getChildren().add(exitButton);
        
        // Add elements to the content section
        contentSection.getChildren().addAll(adminLabel, cardContainer);
        
        mainLayout.setCenter(contentSection);
        mainLayout.setBottom(bottomSection);
        
        // Set up navigation actions
        homeButton.setOnAction(e -> {
            // Already on home page, just refresh
            new AdminHomePage(databaseHelper, username).show(primaryStage);
        });
        
        discussionButton.setOnAction(e -> {
            DiscussionPage discussionPage = new DiscussionPage(databaseHelper, username);
            discussionPage.show(primaryStage);
        });
        
        reviewsButton.setOnAction(e -> {
            // Navigate to reviews page if exists, or use StudentReviewsPage
            StudentReviewsPage reviewsPage = new StudentReviewsPage(databaseHelper, username);
            reviewsPage.show(primaryStage);
        });
        
        logoutButton.setOnAction(e -> {
            // Return to the login selection page
            new SetupLoginSelectionPage(databaseHelper).show(primaryStage);
        });
        
        // Create and set scene
        Scene adminScene = new Scene(mainLayout, 900, 600);
        primaryStage.setScene(adminScene);
        primaryStage.setTitle("Admin Dashboard - " + username);
        primaryStage.show();
    }
    
    /**
     * Creates a styled button for the navigation bar
     */
    private Button createNavButton(String text, boolean isActive) {
        Button button = new Button(text);
        button.setPrefWidth(150);
        button.setPrefHeight(40);
        
        if (isActive) {
            button.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-weight: bold;");
        } else {
            button.setStyle("-fx-background-color: transparent; -fx-text-fill: #2196F3;");
            
            // Add hover effect
            button.setOnMouseEntered(e -> button.setStyle("-fx-background-color: #e3f2fd; -fx-text-fill: #2196F3;"));
            button.setOnMouseExited(e -> button.setStyle("-fx-background-color: transparent; -fx-text-fill: #2196F3;"));
        }
        
        return button;
    }
    
    /**
     * Creates a styled function card
     */
    private VBox createFunctionCard(String title, String description, String color, javafx.event.EventHandler<javafx.event.ActionEvent> action) {
        VBox card = new VBox(15);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(20));
        card.setMaxWidth(250);
        card.setMinHeight(200);
        card.setStyle("-fx-background-color: white; -fx-border-color: #e0e0e0; -fx-border-radius: 5; -fx-background-radius: 5; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 0);");
        
        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        titleLabel.setWrapText(true);
        titleLabel.setAlignment(Pos.CENTER);
        
        Label descLabel = new Label(description);
        descLabel.setWrapText(true);
        descLabel.setAlignment(Pos.CENTER);
        descLabel.setMinHeight(50);
        
        Button actionButton = new Button("Open");
        actionButton.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white;");
        actionButton.setPrefWidth(150);
        actionButton.setOnAction(action);
        
        // Add hover effect for the card
        card.setOnMouseEntered(e -> card.setStyle("-fx-background-color: #f9f9f9; -fx-border-color: " + color + "; -fx-border-radius: 5; -fx-background-radius: 5; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 10, 0, 0, 0);"));
        card.setOnMouseExited(e -> card.setStyle("-fx-background-color: white; -fx-border-color: #e0e0e0; -fx-border-radius: 5; -fx-background-radius: 5; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 0);"));
        
        card.getChildren().addAll(titleLabel, descLabel, actionButton);
        return card;
    }
    
    /**
     * Shows a dialog for managing reviewer requests.
     * 
     * @param primaryStage The primary stage
     */
    private void showManageReviewersDialog(Stage primaryStage) {
        // Create a dialog
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(primaryStage);
        dialog.setTitle("Manage Reviewer Requests");
        dialog.setHeaderText("Approve or deny reviewer status requests");
        
        // Set the button types
        ButtonType closeButtonType = new ButtonType("Close", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().add(closeButtonType);
        
        // Create the content
        BorderPane content = new BorderPane();
        content.setPadding(new Insets(20));
        
        // Add a label to show status
        Label statusLabel = new Label("Loading reviewer requests...");
        content.setTop(statusLabel);
        
        // Create a list of pending requests
        ListView<String> requestsListView = new ListView<>();
        
        // Create a method to refresh the list items
        Runnable refreshRequestsList = () -> {
            statusLabel.setText("Refreshing reviewer requests...");
            try {
                if (databaseHelper != null) {
                    java.util.List<java.util.Map<String, Object>> requests = databaseHelper.getPendingReviewerRequests();
                    ObservableList<String> newItems = FXCollections.observableArrayList();
                    
                    System.out.println("Found " + requests.size() + " pending requests");
                    
                    for (java.util.Map<String, Object> request : requests) {
                        String userId = (String) request.get("userId");
                        java.sql.Timestamp requestDate = (java.sql.Timestamp) request.get("requestDate");
                        newItems.add(userId + " - Requested on " + requestDate.toString());
                        System.out.println("Adding request to list: " + userId);
                    }
                    
                    requestsListView.setItems(newItems);
                    statusLabel.setText("Found " + requests.size() + " pending reviewer requests");
                    
                    if (requests.isEmpty()) {
                        statusLabel.setText("No pending reviewer requests");
                    }
                } else {
                    throw new Exception("DatabaseHelper is null");
                }
            } catch (Exception e) {
                e.printStackTrace();
                statusLabel.setText("Error: " + e.getMessage());
                
                // Fallback to sample data if needed
                ObservableList<String> sampleItems = FXCollections.observableArrayList(
                    "User1 - Requested on 2023-04-15",
                    "User2 - Requested on 2023-04-16",
                    "User3 - Requested on 2023-04-17"
                );
                requestsListView.setItems(sampleItems);
            }
        };
        
        // Initial population of the list
        refreshRequestsList.run();
        
        // For debugging - print the database state
        try {
            System.out.println("DEBUG: Fetching reviewer requests from database");
            if (databaseHelper != null) {
                java.util.List<java.util.Map<String, Object>> requests = databaseHelper.getPendingReviewerRequests();
                System.out.println("DEBUG: Found " + requests.size() + " reviewer requests");
                
                for (java.util.Map<String, Object> request : requests) {
                    System.out.println("DEBUG: Request - userId: " + request.get("userId") + 
                                       ", date: " + request.get("requestDate"));
                }
            }
        } catch (Exception e) {
            System.err.println("DEBUG ERROR: " + e.getMessage());
            e.printStackTrace();
        }
        
        VBox actionButtons = new VBox(10);
        actionButtons.setPadding(new Insets(0, 0, 0, 10));
        
        Button viewActivityButton = new Button("View Activity");
        viewActivityButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");
        viewActivityButton.setMaxWidth(Double.MAX_VALUE);
        
        Button approveButton = new Button("Approve");
        approveButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        approveButton.setMaxWidth(Double.MAX_VALUE);
        
        Button denyButton = new Button("Deny");
        denyButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");
        denyButton.setMaxWidth(Double.MAX_VALUE);
        
        Button refreshButton = new Button("Refresh List");
        refreshButton.setStyle("-fx-background-color: #FF9800; -fx-text-fill: white;");
        refreshButton.setMaxWidth(Double.MAX_VALUE);
        
        actionButtons.getChildren().addAll(viewActivityButton, approveButton, denyButton, refreshButton);
        
        content.setCenter(requestsListView);
        content.setRight(actionButtons);
        
        // View activity button handler
        viewActivityButton.setOnAction(e -> {
            String selectedRequest = requestsListView.getSelectionModel().getSelectedItem();
            if (selectedRequest != null) {
                // Extract username from the request
                String username = selectedRequest.split(" - ")[0];
                
                // Open the StudentActivityReviewPage
                StudentActivityReviewPage activityPage = new StudentActivityReviewPage(databaseHelper, username);
                activityPage.show(primaryStage, () -> {
                    // This is the callback - refresh the list
                    refreshRequestsList.run();
                });
            } else {
                // Show error if no request is selected
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("No Selection");
                alert.setHeaderText(null);
                alert.setContentText("Please select a request to view activity.");
                alert.showAndWait();
            }
        });
        
        // Handle approve button
        approveButton.setOnAction(e -> {
            String selectedRequest = requestsListView.getSelectionModel().getSelectedItem();
            if (selectedRequest != null) {
                // Extract username from the request
                String username = selectedRequest.split(" - ")[0];
                
                try {
                    if (databaseHelper != null) {
                        statusLabel.setText("Approving request for " + username + "...");
                        
                        // Update the database - use the correct method name
                        databaseHelper.approveReviewerRequestByUsername(username);
                        
                        // Refresh the list
                        refreshRequestsList.run();
                        
                        // Show confirmation
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Request Approved");
                        alert.setHeaderText(null);
                        alert.setContentText(username + " has been granted reviewer status.");
                        alert.showAndWait();
                    } else {
                        throw new Exception("DatabaseHelper is null");
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    // Show error
                    statusLabel.setText("Error: " + ex.getMessage());
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText(null);
                    alert.setContentText("Error approving request: " + ex.getMessage());
                    alert.showAndWait();
                }
            } else {
                // Show error if no request is selected
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("No Selection");
                alert.setHeaderText(null);
                alert.setContentText("Please select a request to approve.");
                alert.showAndWait();
            }
        });
        
        // Handle deny button
        denyButton.setOnAction(e -> {
            String selectedRequest = requestsListView.getSelectionModel().getSelectedItem();
            if (selectedRequest != null) {
                // Extract username from the request
                String username = selectedRequest.split(" - ")[0];
                
                try {
                    if (databaseHelper != null) {
                        // Update the database
                        databaseHelper.rejectReviewerRequest(username);
                        
                        // Refresh the list
                        refreshRequestsList.run();
                        
                        // Show confirmation
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Request Denied");
                        alert.setHeaderText(null);
                        alert.setContentText(username + "'s request has been denied.");
                        alert.showAndWait();
                    } else {
                        throw new Exception("DatabaseHelper is null");
                    }
                } catch (Exception ex) {
                    // Show error
                    statusLabel.setText("Error: " + ex.getMessage());
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText(null);
                    alert.setContentText("Error denying request: " + ex.getMessage());
                    alert.showAndWait();
                }
            } else {
                // Show error if no request is selected
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("No Selection");
                alert.setHeaderText(null);
                alert.setContentText("Please select a request to deny.");
                alert.showAndWait();
            }
        });
        
        // Handle refresh button
        refreshButton.setOnAction(e -> {
            refreshRequestsList.run();
        });
        
        // Set the content
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().setPrefSize(600, 400);
        
        // Show the dialog
        dialog.showAndWait();
    }
}