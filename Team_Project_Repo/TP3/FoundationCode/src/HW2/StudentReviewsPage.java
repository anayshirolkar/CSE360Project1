package HW2;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.application.Platform;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.application.Platform;

import java.sql.SQLException;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import databasePart1.DatabaseHelper;

public class StudentReviewsPage {
    private final DatabaseHelper databaseHelper;
    private final String username;
    private final String role; // Add this field
    private Answers answers;
    private Questions questions;
    private Reviews reviews;
    private Messages messages;
    // Add a map to store Answer objects by TreeItem
    private Map<TreeItem<String>, Answer> answerItemMap = new HashMap<>();
    
    // Update the constructor to accept and store the role
    public StudentReviewsPage(DatabaseHelper databaseHelper, String username, String role) {
        System.out.println("Initializing StudentReviewsPage for user: " + username + " with role: " + role);
        this.databaseHelper = databaseHelper;
        this.username = username != null ? username : "DefaultUser";
        this.role = role != null ? role : "user"; // Default to "user" if role is null
        this.answers = new Answers();
        this.questions = new Questions();
        this.reviews = new Reviews();
        this.messages = new Messages();
        
        // Add sample data for demonstration
        try {
            loadSampleData();
            System.out.println("Sample data loaded successfully");
        } catch (Exception e) {
            System.err.println("Error loading sample data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Add a constructor that determines the role from the database
    public StudentReviewsPage(DatabaseHelper databaseHelper, String username) {
        this.databaseHelper = databaseHelper;
        this.username = username != null ? username : "DefaultUser";
        
        // Determine role from database
        String determinedRole = "user"; // Default
        try {
            if (databaseHelper != null) {
                determinedRole = databaseHelper.getUserRole(username);
                if (determinedRole == null) {
                    determinedRole = "user";
                }
            }
        } catch (Exception e) {
            System.err.println("Error determining user role: " + e.getMessage());
        }
        this.role = determinedRole;
        
        this.answers = new Answers();
        this.questions = new Questions();
        this.reviews = new Reviews();
        this.messages = new Messages();
        
        // Add sample data for demonstration
        try {
            loadSampleData();
            System.out.println("Sample data loaded successfully");
        } catch (Exception e) {
            System.err.println("Error loading sample data: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void loadSampleData() {
        // Sample questions
        Question q1 = new Question("Java Collections", "How do I use HashMaps effectively?", username);
        Question q2 = new Question("Database Query", "What's the difference between JOIN types?", username);
        questions.addQuestion(q1);
        questions.addQuestion(q2);
        
        // Sample answers
        Answer a1 = new Answer(q1.getId(), "HashMaps store key-value pairs and provide O(1) access.", "Expert1");
        Answer a2 = new Answer(q1.getId(), "Always override hashCode() when you override equals() method.", "Expert2");
        Answer a3 = new Answer(q2.getId(), "INNER JOIN returns rows when there is a match in both tables.", "Expert3");
        answers.addAnswer(a1);
        answers.addAnswer(a2);
        answers.addAnswer(a3);
        
        // Sample reviews
        Review r1 = new Review("Reviewer1", "This answer is correct but incomplete.", a1.getId(), 3);
        Review r2 = new Review("Reviewer2", "Great explanation! Very helpful.", a1.getId(), 5);
        Review r3 = new Review("Reviewer3", "This could be more detailed.", a2.getId(), 2);
        Review r4 = new Review("Reviewer4", "The explanation about JOIN is accurate.", a3.getId(), 4);
        reviews.addReview(r1);
        reviews.addReview(r2);
        reviews.addReview(r3);
        reviews.addReview(r4);
        
        // Sample messages
        Message m1 = new Message(username, "Reviewer1", "Thanks for your review. Could you please provide more details?");
        Message m2 = new Message("Reviewer1", username, "Sure, what specifically would you like to know?");
        messages.sendMessage(m1);
        messages.sendMessage(m2);
    }

    public void show(Stage primaryStage) {
        try {
            System.out.println("Showing StudentReviewsPage UI for user: " + username);
            BorderPane mainLayout = new BorderPane();
            mainLayout.setPadding(new Insets(20));
            
            // Top section - Username and Title
            VBox topSection = new VBox(10);
            
            // Username display
            Label usernameLabel = new Label("Logged in as: " + username);
            usernameLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
            usernameLabel.setAlignment(Pos.CENTER_RIGHT);
            
            Label titleLabel = new Label("Reviews and Trusted Reviewers");
            titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
            
            topSection.getChildren().addAll(usernameLabel, titleLabel);
            topSection.setAlignment(Pos.CENTER);
            topSection.setPadding(new Insets(0, 0, 20, 0));
            mainLayout.setTop(topSection);
            
            // Center section - TabPane
            TabPane tabPane = new TabPane();
            
            // Tab 1: Reviews for Answers
            Tab reviewsTab = new Tab("Answer Reviews");
            try {
                reviewsTab.setContent(createAnswerReviewsPane());
            } catch (Exception e) {
                System.err.println("Error creating Answer Reviews pane: " + e.getMessage());
                e.printStackTrace();
                reviewsTab.setContent(new Label("Failed to load Answer Reviews. See console for details."));
            }
            reviewsTab.setClosable(false);
            
            // Tab 2: Trusted Reviewers
            Tab trustedReviewersTab = new Tab("Trusted Reviewers");
            try {
                trustedReviewersTab.setContent(createTrustedReviewersPane());
            } catch (Exception e) {
                System.err.println("Error creating Trusted Reviewers pane: " + e.getMessage());
                e.printStackTrace();
                trustedReviewersTab.setContent(new Label("Failed to load Trusted Reviewers. See console for details."));
            }
            trustedReviewersTab.setClosable(false);
            
            // Tab 3: Messages with Reviewers
            Tab messagesTab = new Tab("Messages");
            try {
                messagesTab.setContent(createMessagesPane());
            } catch (Exception e) {
                System.err.println("Error creating Messages pane: " + e.getMessage());
                e.printStackTrace();
                messagesTab.setContent(new Label("Failed to load Messages. See console for details."));
            }
            messagesTab.setClosable(false);
            
            tabPane.getTabs().addAll(reviewsTab, trustedReviewersTab, messagesTab);
            mainLayout.setCenter(tabPane);
            
            // Bottom section - Back button and Request Reviewer status
            HBox bottomSection = new HBox(15);
            bottomSection.setAlignment(Pos.CENTER);
            bottomSection.setPadding(new Insets(20, 0, 0, 0));
            
            Button backButton = new Button("Back");
            backButton.setOnAction(e -> {
                System.out.println("Returning to dashboard based on user role: " + role);
                if ("admin".equals(role)) {
                    // Navigate to AdminHomePage
                    new AdminHomePage(databaseHelper, username).show(primaryStage);
                } else {
                    // Navigate to UserHomePage for other users
                    new UserHomePage(databaseHelper, username).show(primaryStage);
                }
            });
            
            Button requestReviewerButton = new Button("Request Reviewer Status");
            requestReviewerButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");
            requestReviewerButton.setOnAction(e -> {
                System.out.println("Request reviewer status clicked");
                requestReviewerStatus();
            });
            
            bottomSection.getChildren().addAll(backButton, requestReviewerButton);
            mainLayout.setBottom(bottomSection);
            
            Scene scene = new Scene(mainLayout, 800, 600);
            
            Platform.runLater(() -> {
                try {
                    System.out.println("Setting scene for StudentReviewsPage");
                    primaryStage.setTitle("Student Reviews - " + username);
                    primaryStage.setScene(scene);
                    primaryStage.show();
                    System.out.println("StudentReviewsPage displayed successfully");
                } catch (Exception e) {
                    System.err.println("Error setting scene: " + e.getMessage());
                    e.printStackTrace();
                }
            });
            
        } catch (Exception e) {
            System.err.println("Error in show() method: " + e.getMessage());
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Failed to display Reviews page");
            alert.setContentText("An error occurred: " + e.getMessage());
            alert.showAndWait();
        }
    }
    
    private Pane createAnswerReviewsPane() {
        BorderPane pane = new BorderPane();
        pane.setPadding(new Insets(10));
        
        // Left side - Questions and Answers list
        VBox leftPane = new VBox(10);
        
        Label questionsLabel = new Label("Questions and Answers");
        questionsLabel.setStyle("-fx-font-weight: bold;");
        
        TreeView<String> qaTreeView = new TreeView<>();
        qaTreeView.setShowRoot(false);
        qaTreeView.setPrefHeight(400);
        
        // Create the root item
        TreeItem<String> rootItem = new TreeItem<>("Root");
        
        // Add questions and their answers
        for (Question question : questions.getAllQuestions()) {
            TreeItem<String> questionItem = new TreeItem<>(question.getTitle());
            
            // Get answers for this question
            List<Answer> questionAnswers = answers.getAnswersForQuestion(question.getId());
            for (Answer answer : questionAnswers) {
                // Create a tree item with the text and store the answer object in the map
                TreeItem<String> answerItem = new TreeItem<>(answer.getAnswerText().substring(0, 
                        Math.min(50, answer.getAnswerText().length())) + "...");
                
                // Store the answer in the map with the TreeItem as the key
                answerItemMap.put(answerItem, answer);
                
                questionItem.getChildren().add(answerItem);
            }
            
            rootItem.getChildren().add(questionItem);
        }
        
        qaTreeView.setRoot(rootItem);
        
        // Expand all questions by default
        rootItem.getChildren().forEach(item -> item.setExpanded(true));
        
        CheckBox trustedOnlyCheckBox = new CheckBox("Show reviews from trusted reviewers only");
        
        leftPane.getChildren().addAll(questionsLabel, qaTreeView, trustedOnlyCheckBox);
        pane.setLeft(leftPane);
        
        // Right side - Reviews for selected answer
        VBox rightPane = new VBox(10);
        rightPane.setPadding(new Insets(0, 0, 0, 15));
        
        Label reviewsLabel = new Label("Reviews");
        reviewsLabel.setStyle("-fx-font-weight: bold;");
        
        VBox reviewsContainer = new VBox(10);
        ScrollPane scrollPane = new ScrollPane(reviewsContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(350);
        
        Button messageReviewerButton = new Button("Message Reviewer");
        messageReviewerButton.setDisable(true);
        messageReviewerButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        
        rightPane.getChildren().addAll(reviewsLabel, scrollPane, messageReviewerButton);
        pane.setCenter(rightPane);
        
        // Handle selection changes
        qaTreeView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            reviewsContainer.getChildren().clear();
            messageReviewerButton.setDisable(true);
            
            if (newVal != null && answerItemMap.containsKey(newVal)) {
                Answer selectedAnswer = answerItemMap.get(newVal);
                
                // Get reviews for this answer
                List<Review> answerReviews;
                if (trustedOnlyCheckBox.isSelected()) {
                    // Get reviews from trusted reviewers only
                    try {
                        answerReviews = databaseHelper.getReviewsFromTrustedReviewers(username, selectedAnswer.getId());
                    } catch (SQLException e) {
                        // Fallback to local list filtered by reviewer
                        answerReviews = reviews.getReviewsForAssociatedId(selectedAnswer.getId());
                    }
                } else {
                    // Get all reviews
                    answerReviews = reviews.getReviewsForAssociatedId(selectedAnswer.getId());
                }
                
                if (answerReviews.isEmpty()) {
                    Label noReviewsLabel = new Label("No reviews available for this answer.");
                    noReviewsLabel.setStyle("-fx-font-style: italic;");
                    reviewsContainer.getChildren().add(noReviewsLabel);
                } else {
                    // Add each review to the container
                    for (Review review : answerReviews) {
                        VBox reviewBox = createReviewBox(review);
                        reviewsContainer.getChildren().add(reviewBox);
                    }
                }
            }
        });
        
        // Handle checkbox changes
        trustedOnlyCheckBox.selectedProperty().addListener((obs, oldVal, newVal) -> {
            // Trigger a refresh of the selected item
            TreeItem<String> selectedItem = qaTreeView.getSelectionModel().getSelectedItem();
            if (selectedItem != null && answerItemMap.containsKey(selectedItem)) {
                qaTreeView.getSelectionModel().clearSelection();
                qaTreeView.getSelectionModel().select(selectedItem);
            }
        });
        
        return pane;
    }
    
    private VBox createReviewBox(Review review) {
        VBox reviewBox = new VBox(5);
        reviewBox.setPadding(new Insets(10));
        reviewBox.setStyle("-fx-border-color: #e0e0e0; -fx-border-radius: 5;");
        
        HBox headerBox = new HBox(10);
        Label reviewerLabel = new Label("Reviewer: " + review.getReviewer());
        reviewerLabel.setStyle("-fx-font-weight: bold;");
        
        // Create rating stars based on the review rating
        HBox ratingBox = new HBox(2);
        for (int i = 0; i < 5; i++) {
            Label star = new Label(i < review.getRating() ? "★" : "☆");
            star.setStyle("-fx-text-fill: " + (i < review.getRating() ? "#FFD700" : "#AAAAAA") + "; -fx-font-size: 14px;");
            ratingBox.getChildren().add(star);
        }
        
        headerBox.getChildren().addAll(reviewerLabel, ratingBox);
        
        Label contentLabel = new Label(review.getContent());
        contentLabel.setWrapText(true);
        
        HBox actionBox = new HBox(10);
        actionBox.setAlignment(Pos.CENTER_RIGHT);
        
        Button trustButton = new Button("Trust Reviewer");
        trustButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        
        Button messageButton = new Button("Message");
        
        trustButton.setOnAction(e -> {
            showTrustReviewerDialog(review.getReviewer());
        });
        
        messageButton.setOnAction(e -> {
            showMessageDialog(review.getReviewer());
        });
        
        actionBox.getChildren().addAll(trustButton, messageButton);
        
        reviewBox.getChildren().addAll(headerBox, contentLabel, actionBox);
        return reviewBox;
    }
    
    private Pane createTrustedReviewersPane() {
        BorderPane pane = new BorderPane();
        pane.setPadding(new Insets(10));
        
        VBox topSection = new VBox(10);
        Label titleLabel = new Label("Your Trusted Reviewers");
        titleLabel.setStyle("-fx-font-weight: bold;");
        
        HBox actionsBox = new HBox(10);
        Button addReviewerButton = new Button("Add Trusted Reviewer");
        addReviewerButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        
        addReviewerButton.setOnAction(e -> showAddTrustedReviewerDialog());
        
        actionsBox.getChildren().add(addReviewerButton);
        topSection.getChildren().addAll(titleLabel, actionsBox);
        
        pane.setTop(topSection);
        
        // Trusted reviewers table
        TableView<TrustedReviewer> table = new TableView<>();
        table.setPrefHeight(400);
        
        // Define columns
        TableColumn<TrustedReviewer, String> usernameCol = new TableColumn<>("Username");
        usernameCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getReviewerUsername()));
        usernameCol.setPrefWidth(200);
        
        TableColumn<TrustedReviewer, Number> weightCol = new TableColumn<>("Weight");
        weightCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleIntegerProperty(
                cellData.getValue().getWeight()));
        weightCol.setPrefWidth(100);
        
        TableColumn<TrustedReviewer, Void> actionsCol = new TableColumn<>("Actions");
        actionsCol.setPrefWidth(200);
        
        actionsCol.setCellFactory(new Callback<TableColumn<TrustedReviewer, Void>, TableCell<TrustedReviewer, Void>>() {
            @Override
            public TableCell<TrustedReviewer, Void> call(final TableColumn<TrustedReviewer, Void> param) {
                final TableCell<TrustedReviewer, Void> cell = new TableCell<TrustedReviewer, Void>() {
                    private final Button editButton = new Button("Edit");
                    private final Button removeButton = new Button("Remove");
                    private final HBox pane = new HBox(5, editButton, removeButton);
                    
                    {
                        editButton.setOnAction(event -> {
                            TrustedReviewer reviewer = getTableView().getItems().get(getIndex());
                            showEditWeightDialog(reviewer);
                        });
                        
                        removeButton.setOnAction(event -> {
                            TrustedReviewer reviewer = getTableView().getItems().get(getIndex());
                            showRemoveReviewerConfirmation(reviewer, table);
                        });
                    }
                    
                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(pane);
                        }
                    }
                };
                return cell;
            }
        });
        
        table.getColumns().addAll(usernameCol, weightCol, actionsCol);
        
        // Load data
        ObservableList<TrustedReviewer> trustedReviewers = FXCollections.observableArrayList();
        try {
            trustedReviewers.addAll(databaseHelper.getTrustedReviewers(username));
        } catch (SQLException e) {
            // For demonstration, add some sample data
            trustedReviewers.add(new TrustedReviewer("Reviewer1", username, 8));
            trustedReviewers.add(new TrustedReviewer("Reviewer2", username, 6));
            trustedReviewers.add(new TrustedReviewer("Reviewer3", username, 9));
        }
        
        table.setItems(trustedReviewers);
        
        pane.setCenter(table);
        
        return pane;
    }
    
    private Pane createMessagesPane() {
        BorderPane pane = new BorderPane();
        pane.setPadding(new Insets(10));
        
        // Left side - Messages list
        VBox leftPane = new VBox(10);
        
        Label messagesLabel = new Label("Messages with Reviewers");
        messagesLabel.setStyle("-fx-font-weight: bold;");
        
        ListView<Message> messagesListView = new ListView<>();
        messagesListView.setPrefHeight(400);
        
        // Group messages by conversation
        ObservableList<Message> messageItems = FXCollections.observableArrayList();
        
        try {
            // For a real application, get messages from database
            // messageItems.addAll(databaseHelper.getMessagesForUser(username));
            
            // For the demo, use sample data
            messageItems.addAll(messages.getMessagesForRecipient(username));
            messageItems.addAll(messages.getMessagesForRecipient("Reviewer1").stream()
                    .filter(m -> m.getSender().equals(username))
                    .collect(Collectors.toList()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Set cell factory to display message previews
        messagesListView.setCellFactory(lv -> new ListCell<Message>() {
            @Override
            protected void updateItem(Message message, boolean empty) {
                super.updateItem(message, empty);
                if (empty || message == null) {
                    setText(null);
                } else {
                    String direction = message.getSender().equals(username) ? "To: " : "From: ";
                    String person = message.getSender().equals(username) ? message.getRecipient() : message.getSender();
                    setText(direction + person + " - " + 
                            message.getContent().substring(0, Math.min(30, message.getContent().length())) + 
                            (message.getContent().length() > 30 ? "..." : ""));
                }
            }
        });
        
        messagesListView.setItems(messageItems);
        
        Button newMessageButton = new Button("New Message");
        newMessageButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");
        newMessageButton.setOnAction(e -> showNewMessageDialog());
        
        leftPane.getChildren().addAll(messagesLabel, messagesListView, newMessageButton);
        pane.setLeft(leftPane);
        
        // Right side - Message content
        VBox rightPane = new VBox(10);
        rightPane.setPadding(new Insets(0, 0, 0, 15));
        rightPane.setVisible(false);
        
        Label fromToLabel = new Label();
        fromToLabel.setStyle("-fx-font-weight: bold;");
        
        TextArea messageContentArea = new TextArea();
        messageContentArea.setEditable(false);
        messageContentArea.setWrapText(true);
        
        Label replyLabel = new Label("Reply:");
        TextArea replyArea = new TextArea();
        replyArea.setWrapText(true);
        replyArea.setPrefRowCount(5);
        
        Button sendReplyButton = new Button("Send Reply");
        sendReplyButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        
        rightPane.getChildren().addAll(fromToLabel, messageContentArea, replyLabel, replyArea, sendReplyButton);
        pane.setCenter(rightPane);
        
        // Handle selection
        messagesListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                rightPane.setVisible(true);
                
                String direction = newVal.getSender().equals(username) ? "To: " : "From: ";
                String person = newVal.getSender().equals(username) ? newVal.getRecipient() : newVal.getSender();
                fromToLabel.setText(direction + person);
                
                messageContentArea.setText(newVal.getContent());
                replyArea.clear();
                
                // Capture the recipient for the reply
                final String recipient = newVal.getSender().equals(username) ? newVal.getRecipient() : newVal.getSender();
                
                sendReplyButton.setOnAction(e -> {
                    String replyText = replyArea.getText().trim();
                    if (!replyText.isEmpty()) {
                        Message reply = new Message(username, recipient, replyText);
                        messages.sendMessage(reply);
                        
                        // In a real app, you would save to database:
                        // try {
                        //     databaseHelper.sendMessage(reply);
                        // } catch (SQLException ex) {
                        //     ex.printStackTrace();
                        // }
                        
                        // Add new message to the list and select it
                        messageItems.add(0, reply);
                        messagesListView.getSelectionModel().select(reply);
                        
                        replyArea.clear();
                        showAlert("Message Sent", "Your message has been sent to " + recipient);
                    } else {
                        showAlert("Error", "Message cannot be empty");
                    }
                });
            } else {
                rightPane.setVisible(false);
            }
        });
        
        return pane;
    }
    
    private void requestReviewerStatus() {
        try {
            // Use submitReviewerRequest method
            databaseHelper.submitReviewerRequest(username);
            showAlert("Request Submitted", "Your request to become a reviewer has been submitted and will be reviewed by an administrator.");
        } catch (SQLException e) {
            System.err.println("Error submitting reviewer request: " + e.getMessage());
            e.printStackTrace();
            
            // Handle specific errors with user-friendly messages
            String errorMsg = e.getMessage();
            if (errorMsg != null && errorMsg.contains("already have a pending request")) {
                showAlert("Request Already Exists", "You already have a pending request to become a reviewer.");
            } else if (errorMsg != null && errorMsg.contains("already a reviewer")) {
                showAlert("Already a Reviewer", "You are already a reviewer.");
            } else {
                showAlert("Error", "Failed to submit reviewer request: " + e.getMessage());
            }
        }
    }
    
    private void showTrustReviewerDialog(String reviewerUsername) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Trust Reviewer");
        dialog.setHeaderText("Set weight for reviewer: " + reviewerUsername);
        
        // Set button types
        ButtonType trustButtonType = new ButtonType("Trust", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(trustButtonType, ButtonType.CANCEL);
        
        // Create content
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        
        Label weightLabel = new Label("Weight (1-10):");
        Spinner<Integer> weightSpinner = new Spinner<>(1, 10, 5);
        weightSpinner.setEditable(true);
        
        grid.add(weightLabel, 0, 0);
        grid.add(weightSpinner, 1, 0);
        
        dialog.getDialogPane().setContent(grid);
        
        dialog.showAndWait().ifPresent(result -> {
            if (result == trustButtonType) {
                int weight = weightSpinner.getValue();
                try {
                    databaseHelper.addTrustedReviewer(username, reviewerUsername, weight);
                    showAlert("Success", reviewerUsername + " added to your trusted reviewers with weight " + weight);
                } catch (SQLException e) {
                    showAlert("Error", "Failed to add trusted reviewer: " + e.getMessage());
                }
            }
        });
    }
    
    private void showAddTrustedReviewerDialog() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Add Trusted Reviewer");
        dialog.setHeaderText("Add a reviewer to your trusted list");
        
        // Set button types
        ButtonType addButtonType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);
        
        // Create content
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        
        TextField usernameField = new TextField();
        usernameField.setPromptText("Reviewer username");
        
        Spinner<Integer> weightSpinner = new Spinner<>(1, 10, 5);
        weightSpinner.setEditable(true);
        
        grid.add(new Label("Username:"), 0, 0);
        grid.add(usernameField, 1, 0);
        grid.add(new Label("Weight (1-10):"), 0, 1);
        grid.add(weightSpinner, 1, 1);
        
        dialog.getDialogPane().setContent(grid);
        
        dialog.showAndWait().ifPresent(result -> {
            if (result == addButtonType) {
                String reviewerUsername = usernameField.getText().trim();
                int weight = weightSpinner.getValue();
                
                if (reviewerUsername.isEmpty()) {
                    showAlert("Error", "Please enter a username");
                    return;
                }
                
                try {
                    // In a real app, verify the reviewer exists first
                    databaseHelper.addTrustedReviewer(username, reviewerUsername, weight);
                    showAlert("Success", reviewerUsername + " added to your trusted reviewers with weight " + weight);
                } catch (SQLException e) {
                    showAlert("Error", "Failed to add trusted reviewer: " + e.getMessage());
                }
            }
        });
    }
    
    private void showEditWeightDialog(TrustedReviewer reviewer) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Edit Weight");
        dialog.setHeaderText("Edit weight for reviewer: " + reviewer.getReviewerUsername());
        
        // Set button types
        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);
        
        // Create content
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        
        Spinner<Integer> weightSpinner = new Spinner<>(1, 10, reviewer.getWeight());
        weightSpinner.setEditable(true);
        
        grid.add(new Label("Weight (1-10):"), 0, 0);
        grid.add(weightSpinner, 1, 0);
        
        dialog.getDialogPane().setContent(grid);
        
        dialog.showAndWait().ifPresent(result -> {
            if (result == saveButtonType) {
                int newWeight = weightSpinner.getValue();
                try {
                    databaseHelper.updateTrustedReviewerWeight(username, reviewer.getReviewerUsername(), newWeight);
                    reviewer.setWeight(newWeight); // Update the object
                    showAlert("Success", "Weight updated for " + reviewer.getReviewerUsername());
                } catch (SQLException e) {
                    showAlert("Error", "Failed to update weight: " + e.getMessage());
                }
            }
        });
    }
    
    private void showRemoveReviewerConfirmation(TrustedReviewer reviewer, TableView<TrustedReviewer> table) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Remove Trusted Reviewer");
        alert.setHeaderText("Are you sure you want to remove " + reviewer.getReviewerUsername() + " from your trusted reviewers?");
        alert.setContentText("This action cannot be undone.");
        
        alert.showAndWait().ifPresent(result -> {
            if (result == ButtonType.OK) {
                try {
                    databaseHelper.removeTrustedReviewer(username, reviewer.getReviewerUsername());
                    table.getItems().remove(reviewer);
                    showAlert("Success", reviewer.getReviewerUsername() + " removed from your trusted reviewers");
                } catch (SQLException e) {
                    showAlert("Error", "Failed to remove reviewer: " + e.getMessage());
                }
            }
        });
    }
    
    private void showMessageDialog(String reviewerUsername) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Message Reviewer");
        dialog.setHeaderText("Send a message to " + reviewerUsername);
        
        // Set button types
        ButtonType sendButtonType = new ButtonType("Send", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(sendButtonType, ButtonType.CANCEL);
        
        // Create content
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        
        TextArea messageArea = new TextArea();
        messageArea.setPromptText("Write your message here");
        messageArea.setPrefRowCount(5);
        messageArea.setWrapText(true);
        
        grid.add(new Label("Message:"), 0, 0);
        grid.add(messageArea, 0, 1);
        
        dialog.getDialogPane().setContent(grid);
        
        dialog.showAndWait().ifPresent(result -> {
            if (result == sendButtonType) {
                String messageText = messageArea.getText().trim();
                if (messageText.isEmpty()) {
                    showAlert("Error", "Message cannot be empty");
                    return;
                }
                
                Message newMessage = new Message(username, reviewerUsername, messageText);
                messages.sendMessage(newMessage);
                
                // In a real app, save to database:
                // try {
                //     databaseHelper.sendMessage(newMessage);
                // } catch (SQLException e) {
                //     e.printStackTrace();
                // }
                
                showAlert("Message Sent", "Your message has been sent to " + reviewerUsername);
            }
        });
    }
    
    private void showNewMessageDialog() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("New Message");
        dialog.setHeaderText("Send a new message to a reviewer");
        
        // Set button types
        ButtonType sendButtonType = new ButtonType("Send", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(sendButtonType, ButtonType.CANCEL);
        
        // Create content
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        
        TextField recipientField = new TextField();
        recipientField.setPromptText("Recipient username");
        
        TextArea messageArea = new TextArea();
        messageArea.setPromptText("Write your message here");
        messageArea.setPrefRowCount(5);
        messageArea.setWrapText(true);
        
        grid.add(new Label("To:"), 0, 0);
        grid.add(recipientField, 1, 0);
        grid.add(new Label("Message:"), 0, 1);
        grid.add(messageArea, 1, 1);
        
        dialog.getDialogPane().setContent(grid);
        
        dialog.showAndWait().ifPresent(result -> {
            if (result == sendButtonType) {
                String recipient = recipientField.getText().trim();
                String messageText = messageArea.getText().trim();
                
                if (recipient.isEmpty() || messageText.isEmpty()) {
                    showAlert("Error", "Recipient and message cannot be empty");
                    return;
                }
                
                Message newMessage = new Message(username, recipient, messageText);
                messages.sendMessage(newMessage);
                
                // In a real app, save to database:
                // try {
                //     databaseHelper.sendMessage(newMessage);
                // } catch (SQLException e) {
                //     e.printStackTrace();
                // }
                
                showAlert("Message Sent", "Your message has been sent to " + recipient);
            }
        });
    }
    
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}