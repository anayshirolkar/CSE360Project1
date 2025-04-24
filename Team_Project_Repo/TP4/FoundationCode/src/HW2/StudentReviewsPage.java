package HW2;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import databasePart1.DatabaseHelper;

public class StudentReviewsPage {
    private DatabaseHelper databaseHelper;
    private String currentUsername;
    private TabPane tabPane;
    private TableView<Review> answerReviewsTable;
    private TableView<TrustedReviewer> trustedReviewersTable;
    private Map<String, Integer> reviewerWeights = new HashMap<>();

    public StudentReviewsPage(DatabaseHelper databaseHelper, String currentUsername) {
        this.databaseHelper = databaseHelper;
        this.currentUsername = currentUsername;
    }

    public void show(Stage primaryStage) {
        BorderPane mainLayout = new BorderPane();
        mainLayout.setPadding(new Insets(20));
        
        // Top section - Title
        VBox topSection = new VBox(10);
        Label titleLabel = new Label("Reviews & Trusted Reviewers");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        
        Label usernameLabel = new Label("Logged in as: " + currentUsername);
        usernameLabel.setStyle("-fx-font-size: 14px; -fx-font-style: italic;");
        
        topSection.getChildren().addAll(titleLabel, usernameLabel);
        topSection.setAlignment(Pos.CENTER);
        mainLayout.setTop(topSection);
        
        // Center section - Tab pane
        tabPane = new TabPane();
        
        // Tab 1: Answer Reviews
        Tab reviewsTab = new Tab("Answer Reviews");
        reviewsTab.setContent(createAnswerReviewsPane());
        reviewsTab.setClosable(false);
        
        // Tab 2: Trusted Reviewers
        Tab trustedReviewersTab = new Tab("Trusted Reviewers");
        trustedReviewersTab.setContent(createTrustedReviewersPane());
        trustedReviewersTab.setClosable(false);
        
        tabPane.getTabs().addAll(reviewsTab, trustedReviewersTab);
        mainLayout.setCenter(tabPane);
        
        // Bottom section - Back button
        Button backButton = new Button("Back");
        backButton.setOnAction(e -> {
            new UserHomePage(databaseHelper, currentUsername, "student").show(primaryStage);
        });
        
        HBox bottomSection = new HBox(backButton);
        bottomSection.setAlignment(Pos.CENTER);
        bottomSection.setPadding(new Insets(10, 0, 0, 0));
        mainLayout.setBottom(bottomSection);
        
        Scene scene = new Scene(mainLayout, 800, 600);
        primaryStage.setTitle("Reviews & Trusted Reviewers");
        primaryStage.setScene(scene);
        primaryStage.show();
        
        // Load data when tab changes
        tabPane.getSelectionModel().selectedItemProperty().addListener((obs, oldTab, newTab) -> {
            if (newTab == reviewsTab) {
                loadAnswerReviews();
            } else if (newTab == trustedReviewersTab) {
                loadTrustedReviewers();
            }
        });
        
        // Initial data load
        loadAnswerReviews();
    }

    private Pane createAnswerReviewsPane() {
        VBox pane = new VBox(15);
        pane.setPadding(new Insets(10));
        
        Label infoLabel = new Label("Reviews of your answers by reviewers");
        infoLabel.setStyle("-fx-font-weight: bold;");
        
        // Create table for reviews
        answerReviewsTable = new TableView<>();
        
        // Set up columns
        TableColumn<Review, String> contentColumn = new TableColumn<>("Review Content");
        contentColumn.setCellValueFactory(new PropertyValueFactory<>("content"));
        contentColumn.setPrefWidth(300);
        
        TableColumn<Review, String> reviewerColumn = new TableColumn<>("Reviewer");
        reviewerColumn.setCellValueFactory(new PropertyValueFactory<>("reviewer"));
        reviewerColumn.setPrefWidth(120);
        
        TableColumn<Review, Integer> ratingColumn = new TableColumn<>("Rating");
        ratingColumn.setCellValueFactory(new PropertyValueFactory<>("rating"));
        ratingColumn.setPrefWidth(60);
        
        answerReviewsTable.getColumns().addAll(contentColumn, reviewerColumn, ratingColumn);
        answerReviewsTable.setPrefHeight(350);
        
        // Add a placeholder message when empty
        answerReviewsTable.setPlaceholder(new Label("No reviews found for your answers"));
        
        // Action buttons
        HBox buttonBox = new HBox(10);
        
        Button messageButton = new Button("Message Reviewer");
        messageButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");
        messageButton.setDisable(true);
        
        Button addTrustedButton = new Button("Add to Trusted Reviewers");
        addTrustedButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        addTrustedButton.setDisable(true);
        
        Button viewProfileButton = new Button("View Reviewer Profile");
        viewProfileButton.setDisable(true);
        
        buttonBox.getChildren().addAll(messageButton, addTrustedButton, viewProfileButton);
        buttonBox.setAlignment(Pos.CENTER_LEFT);
        
        // Enable/disable buttons based on selection
        answerReviewsTable.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldSelection, newSelection) -> {
                boolean hasSelection = newSelection != null;
                messageButton.setDisable(!hasSelection);
                addTrustedButton.setDisable(!hasSelection);
                viewProfileButton.setDisable(!hasSelection);
            }
        );
        
        // Set up button actions
        messageButton.setOnAction(e -> {
            Review selectedReview = answerReviewsTable.getSelectionModel().getSelectedItem();
            if (selectedReview != null) {
                sendMessageToReviewer(selectedReview.getReviewer());
            }
        });
        
        addTrustedButton.setOnAction(e -> {
            Review selectedReview = answerReviewsTable.getSelectionModel().getSelectedItem();
            if (selectedReview != null) {
                addReviewerToTrustedList(selectedReview.getReviewer());
            }
        });
        
        viewProfileButton.setOnAction(e -> {
            Review selectedReview = answerReviewsTable.getSelectionModel().getSelectedItem();
            if (selectedReview != null) {
                viewReviewerProfile(selectedReview.getReviewer());
            }
        });
        
        pane.getChildren().addAll(infoLabel, answerReviewsTable, buttonBox);
        return pane;
    }

    private Pane createTrustedReviewersPane() {
        VBox pane = new VBox(15);
        pane.setPadding(new Insets(10));
        
        Label infoLabel = new Label("Manage your trusted reviewers list");
        infoLabel.setStyle("-fx-font-weight: bold;");
        
        // Create table for trusted reviewers
        trustedReviewersTable = new TableView<>();
        
        // Set up columns - make sure these match exactly with the getter method names
        TableColumn<TrustedReviewer, String> nameColumn = new TableColumn<>("Reviewer");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("reviewerUsername"));
        nameColumn.setPrefWidth(200);
        
        // Add custom cell factory to ensure text is visible
        nameColumn.setCellFactory(column -> {
            return new TableCell<TrustedReviewer, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        setText(item);
                        setStyle("-fx-text-fill: black;"); // Ensure text is visible
                    }
                }
            };
        });
        
        TableColumn<TrustedReviewer, Integer> weightColumn = new TableColumn<>("Trust Level");
        weightColumn.setCellValueFactory(new PropertyValueFactory<>("weight"));
        weightColumn.setPrefWidth(100);
        
        // Add custom cell factory for weight column too
        weightColumn.setCellFactory(column -> {
            return new TableCell<TrustedReviewer, Integer>() {
                @Override
                protected void updateItem(Integer item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        setText(item.toString());
                        setStyle("-fx-text-fill: black;"); // Ensure text is visible
                    }
                }
            };
        });
        
        trustedReviewersTable.getColumns().addAll(nameColumn, weightColumn);
        trustedReviewersTable.setPrefHeight(350);
        
        // Add a placeholder message when empty
        trustedReviewersTable.setPlaceholder(new Label("No trusted reviewers found"));
        
        // Action buttons
        HBox buttonBox = new HBox(10);
        
        Button addButton = new Button("Add Reviewer");
        addButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        
        Button editWeightButton = new Button("Edit Trust Level");
        editWeightButton.setDisable(true);
        
        Button removeButton = new Button("Remove");
        removeButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");
        removeButton.setDisable(true);
        
        Button messageButton = new Button("Send Message");
        messageButton.setDisable(true);
        
        buttonBox.getChildren().addAll(addButton, editWeightButton, removeButton, messageButton);
        buttonBox.setAlignment(Pos.CENTER_LEFT);
        
        // Enable/disable buttons based on selection
        trustedReviewersTable.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldSelection, newSelection) -> {
                boolean hasSelection = newSelection != null;
                editWeightButton.setDisable(!hasSelection);
                removeButton.setDisable(!hasSelection);
                messageButton.setDisable(!hasSelection);
            }
        );
        
        // Set up button actions
        addButton.setOnAction(e -> showAddReviewerDialog());
        
        editWeightButton.setOnAction(e -> {
            TrustedReviewer selectedReviewer = trustedReviewersTable.getSelectionModel().getSelectedItem();
            if (selectedReviewer != null) {
                showEditWeightDialog(selectedReviewer);
            }
        });
        
        removeButton.setOnAction(e -> {
            TrustedReviewer selectedReviewer = trustedReviewersTable.getSelectionModel().getSelectedItem();
            if (selectedReviewer != null) {
                removeSelectedReviewer(selectedReviewer);
            }
        });
        
        messageButton.setOnAction(e -> {
            TrustedReviewer selectedReviewer = trustedReviewersTable.getSelectionModel().getSelectedItem();
            if (selectedReviewer != null) {
                sendMessageToReviewer(selectedReviewer.getReviewerUsername());
            }
        });
        
        pane.getChildren().addAll(infoLabel, trustedReviewersTable, buttonBox);
        return pane;
    }

    private void loadAnswerReviews() {
        try {
            // Get all answers by this user
            List<Answer> userAnswers = databaseHelper.getAnswersByUser(currentUsername);
            List<Review> reviews = new ArrayList<>();
            
            System.out.println("Found " + userAnswers.size() + " answers for user: " + currentUsername);
            
            // For each answer, get reviews
            for (Answer answer : userAnswers) {
                System.out.println("Fetching reviews for answer ID: " + answer.getId());
                
                List<Review> answerReviews = null;
                
                // Try multiple methods in case one doesn't exist or has a different name
                try {
                    // First attempt: try getReviewsForAssociatedId
                    answerReviews = databaseHelper.getReviewsForAssociatedId(answer.getId());
                } catch (Exception e1) {
                    System.out.println("getReviewsForAssociatedId failed: " + e1.getMessage());
                    // If method fails, create empty list
                    answerReviews = new ArrayList<>();
                }
                
                if (answerReviews != null && !answerReviews.isEmpty()) {
                    System.out.println("Found " + answerReviews.size() + " reviews for answer " + answer.getId());
                    reviews.addAll(answerReviews);
                }
            }
            
            // Update the table
            System.out.println("Total reviews found: " + reviews.size());
            answerReviewsTable.setItems(FXCollections.observableArrayList(reviews));
            
        } catch (SQLException e) {
            System.err.println("Error loading answer reviews: " + e.getMessage());
            e.printStackTrace();
            showAlert("Error", "Could not load reviews: " + e.getMessage());
        }
    }

    private void loadTrustedReviewers() {
        try {
            List<TrustedReviewer> trustedReviewers = databaseHelper.getTrustedReviewers(currentUsername);
            
            // Debug logging
            System.out.println("Loaded " + trustedReviewers.size() + " trusted reviewers:");
            for (TrustedReviewer reviewer : trustedReviewers) {
                System.out.println("  - " + reviewer.getReviewerUsername() + " (weight: " + reviewer.getWeight() + ")");
            }
            
            // Store weights for easy access
            reviewerWeights.clear();
            for (TrustedReviewer reviewer : trustedReviewers) {
                reviewerWeights.put(reviewer.getReviewerUsername(), reviewer.getWeight());
            }
            
            // Update the table
            trustedReviewersTable.setItems(FXCollections.observableArrayList(trustedReviewers));
            trustedReviewersTable.refresh(); // Force refresh to ensure display updates
            
        } catch (SQLException e) {
            System.err.println("Error loading trusted reviewers: " + e.getMessage());
            showAlert("Error", "Could not load trusted reviewers: " + e.getMessage());
        }
    }

    private void sendMessageToReviewer(String reviewerUsername) {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Send Message");
        dialog.setHeaderText("Send a message to " + reviewerUsername);
        
        // Set button types
        ButtonType sendButtonType = new ButtonType("Send", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(sendButtonType, ButtonType.CANCEL);
        
        // Create content
        VBox content = new VBox(10);
        content.setPadding(new Insets(20));
        
        TextArea messageArea = new TextArea();
        messageArea.setPromptText("Enter your message here");
        messageArea.setPrefHeight(150);
        messageArea.setWrapText(true);
        
        content.getChildren().addAll(new Label("Message:"), messageArea);
        dialog.getDialogPane().setContent(content);
        
        // Request focus on the message area
        Platform.runLater(() -> messageArea.requestFocus());
        
        // Convert the result
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == sendButtonType) {
                return messageArea.getText().trim();
            }
            return null;
        });
        
        // Handle the result
        dialog.showAndWait().ifPresent(messageText -> {
            if (messageText.isEmpty()) {
                showAlert("Error", "Message cannot be empty");
                return;
            }
            
            try {
                // Create and send the message
                Message message = new Message(
                    currentUsername,
                    reviewerUsername,
                    messageText
                );
                
                databaseHelper.sendMessage(message);
                
                showAlert("Success", "Message sent to " + reviewerUsername);
            } catch (SQLException e) {
                System.err.println("Error sending message: " + e.getMessage());
                showAlert("Error", "Failed to send message: " + e.getMessage());
            }
        });
    }

    private void addReviewerToTrustedList(String reviewerUsername) {
        // Check if already in trusted list
        if (reviewerWeights.containsKey(reviewerUsername)) {
            showAlert("Already Trusted", reviewerUsername + " is already in your trusted reviewers list");
            return;
        }
        
        // Show dialog to set weight
        Dialog<Integer> dialog = new Dialog<>();
        dialog.setTitle("Add Trusted Reviewer");
        dialog.setHeaderText("Set trust level for " + reviewerUsername);
        
        // Set button types
        ButtonType addButtonType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);
        
        // Create content
        VBox content = new VBox(10);
        content.setPadding(new Insets(20));
        
        Slider weightSlider = new Slider(1, 10, 5);
        weightSlider.setShowTickLabels(true);
        weightSlider.setShowTickMarks(true);
        weightSlider.setMajorTickUnit(1);
        weightSlider.setMinorTickCount(0);
        weightSlider.setBlockIncrement(1);
        weightSlider.setSnapToTicks(true);
        
        Label weightValueLabel = new Label("5");
        weightSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            weightValueLabel.setText(String.format("%d", newVal.intValue()));
        });
        
        HBox weightBox = new HBox(10);
        weightBox.getChildren().addAll(weightSlider, weightValueLabel);
        
        content.getChildren().addAll(new Label("Trust Level (1-10):"), weightBox);
        dialog.getDialogPane().setContent(content);
        
        // Convert the result
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                return (int) weightSlider.getValue();
            }
            return null;
        });
        
        // Handle the result
        dialog.showAndWait().ifPresent(weight -> {
            try {
                // Add to database
                databaseHelper.addTrustedReviewer(
                    currentUsername, 
                    reviewerUsername,
                    weight
                );
                
                // Refresh trusted reviewers table
                loadTrustedReviewers();
                
                // Switch to trusted reviewers tab
                tabPane.getSelectionModel().select(1); // Index 1 is the trusted reviewers tab
                
                showAlert("Success", reviewerUsername + " added to your trusted reviewers with trust level " + weight);
            } catch (SQLException e) {
                System.err.println("Error adding trusted reviewer: " + e.getMessage());
                showAlert("Error", "Failed to add trusted reviewer: " + e.getMessage());
            }
        });
    }

    private void viewReviewerProfile(String reviewerUsername) {
        // Open the reviewer profile page
        Stage stage = (Stage) tabPane.getScene().getWindow();
        new ReviewerProfilePage(databaseHelper, reviewerUsername, currentUsername).show(stage);
    }

    private void showAddReviewerDialog() {
        Dialog<TrustedReviewer> dialog = new Dialog<>();
        dialog.setTitle("Add Trusted Reviewer");
        dialog.setHeaderText("Add a reviewer to your trusted reviewers list");
        
        // Set the button types
        ButtonType addButtonType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);
        
        // Create the form content
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        
        TextField usernameField = new TextField();
        usernameField.setPromptText("Reviewer username");
        
        Slider weightSlider = new Slider(1, 10, 5);
        weightSlider.setShowTickLabels(true);
        weightSlider.setShowTickMarks(true);
        weightSlider.setMajorTickUnit(1);
        weightSlider.setMinorTickCount(0);
        weightSlider.setBlockIncrement(1);
        weightSlider.setSnapToTicks(true);
        
        Label weightValueLabel = new Label("5");
        weightSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            weightValueLabel.setText(String.format("%d", newVal.intValue()));
        });
        
        HBox weightBox = new HBox(10);
        weightBox.getChildren().addAll(weightSlider, weightValueLabel);
        
        grid.add(new Label("Username:"), 0, 0);
        grid.add(usernameField, 1, 0);
        grid.add(new Label("Trust Level:"), 0, 1);
        grid.add(weightBox, 1, 1);
        
        dialog.getDialogPane().setContent(grid);
        
        // Request focus on the username field by default
        Platform.runLater(() -> usernameField.requestFocus());
        
        // Convert the result
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                String username = usernameField.getText().trim();
                int weight = (int) weightSlider.getValue();
                
                if (username.isEmpty()) {
                    showAlert("Error", "Username cannot be empty");
                    return null;
                }
                
                return new TrustedReviewer(username, currentUsername, weight);
            }
            return null;
        });
        
        dialog.showAndWait().ifPresent(reviewer -> {
            try {
                // Check if user exists
                if (!databaseHelper.userExists(reviewer.getReviewerUsername())) {
                    showAlert("Error", "User '" + reviewer.getReviewerUsername() + "' does not exist");
                    return;
                }
                
                // Check if adding self
                if (reviewer.getReviewerUsername().equals(currentUsername)) {
                    showAlert("Error", "You cannot add yourself as a trusted reviewer");
                    return;
                }
                
                // Add to database
                databaseHelper.addTrustedReviewer(
                    currentUsername, 
                    reviewer.getReviewerUsername(), 
                    reviewer.getWeight()
                );
                
                // Refresh table
                loadTrustedReviewers();
                
                showAlert("Success", "Added " + reviewer.getReviewerUsername() + " as a trusted reviewer");
            } catch (SQLException e) {
                System.err.println("Error adding trusted reviewer: " + e.getMessage());
                showAlert("Error", "Failed to add trusted reviewer: " + e.getMessage());
            }
        });
    }

    private void showEditWeightDialog(TrustedReviewer reviewer) {
        Dialog<Integer> dialog = new Dialog<>();
        dialog.setTitle("Edit Trust Level");
        dialog.setHeaderText("Update trust level for " + reviewer.getReviewerUsername());
        
        // Set the button types
        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);
        
        // Create content
        VBox content = new VBox(10);
        content.setPadding(new Insets(20));
        
        Slider weightSlider = new Slider(1, 10, reviewer.getWeight());
        weightSlider.setShowTickLabels(true);
        weightSlider.setShowTickMarks(true);
        weightSlider.setMajorTickUnit(1);
        weightSlider.setMinorTickCount(0);
        weightSlider.setBlockIncrement(1);
        weightSlider.setSnapToTicks(true);
        
        Label weightValueLabel = new Label(Integer.toString(reviewer.getWeight()));
        weightSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            weightValueLabel.setText(String.format("%d", newVal.intValue()));
        });
        
        HBox weightBox = new HBox(10);
        weightBox.getChildren().addAll(weightSlider, weightValueLabel);
        
        content.getChildren().addAll(new Label("Trust Level:"), weightBox);
        dialog.getDialogPane().setContent(content);
        
        // Convert the result
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                return (int) weightSlider.getValue();
            }
            return null;
        });
        
        // Handle the result
        dialog.showAndWait().ifPresent(weight -> {
            try {
                // Update in database
                databaseHelper.updateTrustedReviewerWeight(
                    currentUsername,
                    reviewer.getReviewerUsername(),
                    weight
                );
                
                // Refresh table
                loadTrustedReviewers();
                
                showAlert("Success", "Updated trust level for " + reviewer.getReviewerUsername());
            } catch (SQLException e) {
                System.err.println("Error updating trusted reviewer weight: " + e.getMessage());
                showAlert("Error", "Failed to update trust level: " + e.getMessage());
            }
        });
    }

    private void removeSelectedReviewer(TrustedReviewer reviewer) {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Remove Trusted Reviewer");
        confirmation.setHeaderText("Remove " + reviewer.getReviewerUsername() + " from your trusted reviewers?");
        confirmation.setContentText("Are you sure you want to remove this reviewer from your trusted list?");
        
        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    // Remove from database
                    databaseHelper.removeTrustedReviewer(
                        currentUsername,
                        reviewer.getReviewerUsername()
                    );
                    
                    // Refresh table
                    loadTrustedReviewers();
                    
                    showAlert("Success", "Removed " + reviewer.getReviewerUsername() + " from trusted reviewers");
                } catch (SQLException e) {
                    System.err.println("Error removing trusted reviewer: " + e.getMessage());
                    showAlert("Error", "Failed to remove trusted reviewer: " + e.getMessage());
                }
            }
        });
    }
    
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}