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
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import databasePart1.DatabaseHelper;

import javafx.scene.control.Slider;
import javafx.scene.control.Dialog;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextArea;

public class ReviewerHomePage {
    private Reviews reviews;
    private Messages messages;
    private String currentReviewer;
    private Questions questions;
    private Answers answers;
    private DatabaseHelper databaseHelper; // Add this

    // Update this constructor
    public ReviewerHomePage(DatabaseHelper databaseHelper, String currentReviewer) {
        System.out.println("ReviewerHomePage constructor called with reviewer: " + currentReviewer); // Debug log
        this.databaseHelper = databaseHelper;
        this.currentReviewer = currentReviewer;
        this.reviews = new Reviews();
        this.messages = new Messages();
        this.questions = new Questions();
        this.answers = new Answers();
        
        // Debug the state at startup
        System.out.println("Initializing ReviewerHomePage with reviewer: " + this.currentReviewer);
        
        // Check if we can retrieve trusted reviewers
        try {
            List<TrustedReviewer> test = databaseHelper.getTrustedReviewers(this.currentReviewer);
            System.out.println("Found " + test.size() + " trusted reviewers on initialization");
            
            for (TrustedReviewer tr : test) {
                System.out.println("  Reviewer: " + tr.getReviewerUsername() + ", Weight: " + tr.getWeight());
            }
        } catch (SQLException e) {
            System.err.println("Database access error during initialization: " + e.getMessage());
            e.printStackTrace();
        }
        
        loadDataFromDatabase();
    }
    
    // Keep the existing constructor for backward compatibility
    public ReviewerHomePage(Reviews reviews, Messages messages, String currentReviewer) {
        this.reviews = reviews;
        this.messages = messages;
        this.currentReviewer = currentReviewer;
        this.questions = new Questions(); // Initialize with sample questions
        this.answers = new Answers(); // Initialize with sample answers
        
        // Add sample data
        addSampleData();
    }
    
    private void addSampleData() {
        // Sample questions
        Question q1 = new Question("Java Basics", "What is the difference between == and .equals()?", "User1");
        Question q2 = new Question("Database Design", "How to normalize a database?", "User2");
        questions.addQuestion(q1);
        questions.addQuestion(q2);
        
        // Sample answers
        Answer a1 = new Answer(q1.getId(), "== compares references, .equals() compares content", "User3");
        Answer a2 = new Answer(q1.getId(), "For primitive types, == compares values. For objects, it compares references.", "User4");
        Answer a3 = new Answer(q2.getId(), "Database normalization involves organizing data to reduce redundancy.", "User5");
        answers.addAnswer(a1);
        answers.addAnswer(a2);
        answers.addAnswer(a3);
        
        // Sample reviews
        Review r1 = new Review(currentReviewer, "This answer is accurate but could include examples.", a1.getId());
        Review r2 = new Review(currentReviewer, "Good explanation, covers the basics well.", q1.getId());
        reviews.addReview(r1);
        reviews.addReview(r2);
        
        // Sample messages
        Message m1 = new Message("User3", currentReviewer, "Thanks for your review. Could you clarify what examples would be helpful?");
        Message m2 = new Message("User1", currentReviewer, "I appreciate your feedback on my question!");
        messages.sendMessage(m1);
        messages.sendMessage(m2);
    }

    // Update the loadDataFromDatabase method
    private void loadDataFromDatabase() {
        try {
            if (databaseHelper != null) {
                System.out.println("Loading reviews for reviewer: " + currentReviewer);
                
                // Load reviewer's reviews
                List<Review> reviewerReviews = databaseHelper.getReviewsByReviewer(currentReviewer);
                System.out.println("Found " + reviewerReviews.size() + " reviews");
                
                for (Review review : reviewerReviews) {
                    reviews.addReview(review);
                }
                
                // Load messages for the reviewer
                List<Message> reviewerMessages = databaseHelper.getMessagesForRecipient(currentReviewer);
                System.out.println("Found " + reviewerMessages.size() + " messages");
                
                for (Message message : reviewerMessages) {
                    messages.sendMessage(message);
                }
                
                // Add this line at the end of the method
                displayFeedbackCounts();
            } else {
                System.err.println("DatabaseHelper is null, using sample data instead");
                addSampleData();
            }
        } catch (SQLException e) {
            System.err.println("Error loading data from database: " + e.getMessage());
            e.printStackTrace();
            
            // Fallback to sample data if database loading fails
            addSampleData();
        }
    }

    public void show(Stage primaryStage) {
        // Debug logging for show method
        System.out.println("Show method called. Current reviewer: " + this.currentReviewer);
        
        BorderPane mainLayout = new BorderPane();
        mainLayout.setPadding(new Insets(20));
        
        // Top section - Title and username
        VBox topSection = new VBox(10);
        
        // Username display with improved visibility and debug
        String displayName = (this.currentReviewer != null && !this.currentReviewer.isEmpty()) ? 
            this.currentReviewer : "Unknown";
        System.out.println("Setting display name to: " + displayName); // Debug log
        
        Label usernameLabel = new Label("Logged in as: " + displayName);
        usernameLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #2196F3;");
        usernameLabel.setAlignment(Pos.CENTER_RIGHT);
        
        Label titleLabel = new Label("Reviewer Dashboard");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
        
        Button profileButton = new Button("My Profile");
        profileButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        profileButton.setOnAction(e -> {
            System.out.println("Opening profile for reviewer: " + this.currentReviewer); // Debug log
            new ReviewerProfilePage(databaseHelper, this.currentReviewer, this.currentReviewer).show(primaryStage);
        });

        topSection.getChildren().addAll(usernameLabel, titleLabel, profileButton);
        topSection.setAlignment(Pos.CENTER);
        topSection.setPadding(new Insets(0, 0, 20, 0));
        mainLayout.setTop(topSection);
        
        // Update window title with reviewer name
        String windowTitle = "Reviewer Dashboard - " + displayName;
        System.out.println("Setting window title to: " + windowTitle); // Debug log
        primaryStage.setTitle(windowTitle);
        
        // Center section - Tab pane for different functions
        TabPane tabPane = new TabPane();
        
        // Reviews tab
        Tab reviewsTab = new Tab("Manage Reviews");
        reviewsTab.setContent(createReviewsPane());
        reviewsTab.setClosable(false);
        
        // Messages tab
        Tab messagesTab = new Tab("Messages");
        messagesTab.setContent(createMessagesPane());
        messagesTab.setClosable(false);
        
        // New Discussion Content tab
        Tab discussionContentTab = new Tab("Discussion Content");
        discussionContentTab.setContent(createDiscussionContentPane());
        discussionContentTab.setClosable(false);

        // Trusted Reviewers tab
        Tab trustedReviewersTab = createTrustedReviewersTab();
        
        tabPane.getTabs().addAll(reviewsTab, messagesTab, discussionContentTab, trustedReviewersTab);
        mainLayout.setCenter(tabPane);
        
        // Bottom section - Back button
        Button backButton = new Button("Back to User Page");
        backButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");
        backButton.setOnAction(e -> {
            System.out.println("Returning to user page for: " + currentReviewer); // Debug log
            new UserHomePage(databaseHelper, currentReviewer, "reviewer").show(primaryStage);
        });
        HBox bottomSection = new HBox(backButton);
        bottomSection.setAlignment(Pos.CENTER);
        bottomSection.setPadding(new Insets(10, 0, 0, 0));
        mainLayout.setBottom(bottomSection);
        
        Scene scene = new Scene(mainLayout, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    private Pane createReviewsPane() {
        BorderPane reviewsPane = new BorderPane();
        reviewsPane.setPadding(new Insets(10));
        
        // Left side - List of reviewer's reviews
        VBox reviewListSection = new VBox(10);
        Label reviewsLabel = new Label("Your Reviews");
        reviewsLabel.setStyle("-fx-font-weight: bold;");
        
        ListView<Review> reviewListView = new ListView<>();
        reviewListView.setPrefHeight(300);
        
        // Populate with reviewer's reviews
        ObservableList<Review> reviewItems = FXCollections.observableArrayList(
                reviews.getReviewsByReviewer(currentReviewer));
        reviewListView.setItems(reviewItems);
        
        // Set cell factory to display review content
        reviewListView.setCellFactory(lv -> new ListCell<Review>() {
            @Override
            protected void updateItem(Review review, boolean empty) {
                super.updateItem(review, empty);
                if (empty || review == null) {
                    setText(null);
                } else {
                    setText("Review: " + review.getContent().substring(0, Math.min(30, review.getContent().length())) + "...");
                }
            }
        });
        
        Button createReviewButton = new Button("Create New Review");
        createReviewButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        createReviewButton.setOnAction(e -> showCreateReviewDialog(reviewListView));
        
        Button updateButton = new Button("Update Review");
        updateButton.setDisable(true);
        
        reviewListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            updateButton.setDisable(newVal == null);
        });
        
        updateButton.setOnAction(e -> {
            Review selectedReview = reviewListView.getSelectionModel().getSelectedItem();
            if (selectedReview != null) {
                updateReview(selectedReview);
            }
        });
        
        reviewListSection.getChildren().addAll(reviewsLabel, reviewListView, createReviewButton, updateButton);
        reviewsPane.setLeft(reviewListSection);
        
        // Right side - Review details and actions
        VBox reviewDetailSection = new VBox(10);
        reviewDetailSection.setPadding(new Insets(0, 0, 0, 10));
        reviewDetailSection.setVisible(false); // Initially hidden
        
        Label detailLabel = new Label("Review Details");
        detailLabel.setStyle("-fx-font-weight: bold;");
        
        TextArea reviewContentArea = new TextArea();
        reviewContentArea.setEditable(false);
        reviewContentArea.setWrapText(true);
        
        Label associatedItemLabel = new Label("Associated Item:");
        TextArea associatedItemArea = new TextArea();
        associatedItemArea.setEditable(false);
        associatedItemArea.setWrapText(true);
        associatedItemArea.setPrefRowCount(3);
        
        HBox actionButtons = new HBox(10);
        Button editButton = new Button("Edit Review");
        editButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");
        Button deleteButton = new Button("Delete Review");
        deleteButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");
        
        actionButtons.getChildren().addAll(editButton, deleteButton);
        actionButtons.setAlignment(Pos.CENTER);
        
        reviewDetailSection.getChildren().addAll(
                detailLabel, reviewContentArea, 
                associatedItemLabel, associatedItemArea, 
                actionButtons);
        reviewsPane.setCenter(reviewDetailSection);
        
        // Handle selection
        reviewListView.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldVal, newVal) -> {
                if (newVal != null) {
                    reviewDetailSection.setVisible(true);
                    reviewContentArea.setText(newVal.getContent());
                    
                    // Find the associated item (question or answer)
                    String associatedId = newVal.getAssociatedId();
                    Question question = questions.getQuestionById(associatedId);
                    if (question != null) {
                        associatedItemArea.setText("Question: " + question.getTitle() + "\n" + question.getDescription());
                    } else {
                        Answer answer = answers.getAnswerById(associatedId);
                        if (answer != null) {
                            associatedItemArea.setText("Answer: " + answer.getAnswerText());
                        } else {
                            associatedItemArea.setText("Associated item not found");
                        }
                    }
                    
                    // Setup edit button
                    editButton.setOnAction(e -> {
                        showEditReviewDialog(newVal, reviewListView, reviewContentArea);
                    });
                    
                    // Setup delete button
                    deleteButton.setOnAction(e -> {
                        showDeleteConfirmation(newVal, reviewListView);
                        reviewDetailSection.setVisible(false);
                    });
                } else {
                    reviewDetailSection.setVisible(false);
                }
            }
        );
        
        return reviewsPane;
    }
    
    private Pane createMessagesPane() {
        BorderPane messagesPane = new BorderPane();
        messagesPane.setPadding(new Insets(10));
        
        // Left side - List of messages
        VBox messageListSection = new VBox(10);
        Label messagesLabel = new Label("Your Messages");
        messagesLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        
        ListView<Message> messageListView = new ListView<>();
        messageListView.setPrefHeight(400);
        messageListView.setPrefWidth(300);
        
        // Populate with reviewer's messages
        try {
            List<Message> receivedMessages = databaseHelper.getMessagesForRecipient(currentReviewer);
            System.out.println("Loaded " + receivedMessages.size() + " messages for " + currentReviewer);
            ObservableList<Message> messageItems = FXCollections.observableArrayList(receivedMessages);
            messageListView.setItems(messageItems);
        } catch (SQLException e) {
            System.err.println("Error loading messages: " + e.getMessage());
            e.printStackTrace();
        }
        
        // Set cell factory to display message sender and preview
        messageListView.setCellFactory(lv -> new ListCell<Message>() {
            @Override
            protected void updateItem(Message message, boolean empty) {
                super.updateItem(message, empty);
                if (empty || message == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    VBox container = new VBox(5);
                    container.setPadding(new Insets(5));
                    
                    Label senderLabel = new Label("From: " + message.getSender());
                    senderLabel.setStyle("-fx-font-weight: bold;");
                    
                    Label dateLabel = new Label(message.getSentAt() != null ? 
                        new java.text.SimpleDateFormat("MM/dd/yyyy HH:mm").format(message.getSentAt()) : "");
                    dateLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: #666;");
                    
                    String preview = message.getContent().length() > 50 ? 
                        message.getContent().substring(0, 47) + "..." : 
                        message.getContent();
                    Label previewLabel = new Label(preview);
                    previewLabel.setWrapText(true);
                    
                    container.getChildren().addAll(senderLabel, dateLabel, previewLabel);
                    setGraphic(container);
                }
            }
        });
        
        messageListSection.getChildren().addAll(messagesLabel, messageListView);
        messagesPane.setLeft(messageListSection);
        
        // Right side - Message content and reply
        VBox messageDetailSection = new VBox(10);
        messageDetailSection.setPadding(new Insets(10, 0, 0, 20));
        messageDetailSection.setPrefWidth(400);
        messageDetailSection.setVisible(false); // Initially hidden
        
        Label detailLabel = new Label("Message Details");
        detailLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        
        Label fromLabel = new Label();
        fromLabel.setStyle("-fx-font-weight: bold;");
        
        Label dateLabel = new Label();
        dateLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #666;");
        
        TextArea messageContentArea = new TextArea();
        messageContentArea.setEditable(false);
        messageContentArea.setWrapText(true);
        messageContentArea.setPrefRowCount(8);
        messageContentArea.setStyle("-fx-background-color: #f8f8f8;");
        
        Label replyLabel = new Label("Reply:");
        replyLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 12px;");
        
        TextArea replyArea = new TextArea();
        replyArea.setWrapText(true);
        replyArea.setPrefRowCount(4);
        replyArea.setPromptText("Type your reply here...");
        
        Button sendReplyButton = new Button("Send Reply");
        sendReplyButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");
        
        messageDetailSection.getChildren().addAll(
                detailLabel, fromLabel, dateLabel, messageContentArea, 
                replyLabel, replyArea, sendReplyButton);
        messagesPane.setCenter(messageDetailSection);
        
        // Handle selection
        messageListView.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldVal, newVal) -> {
                if (newVal != null) {
                    messageDetailSection.setVisible(true);
                    fromLabel.setText("From: " + newVal.getSender());
                    dateLabel.setText(newVal.getSentAt() != null ? 
                        "Sent on: " + new java.text.SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(newVal.getSentAt()) : "");
                    messageContentArea.setText(newVal.getContent());
                    replyArea.clear();
                    
                    // Setup send reply button
                    final String sender = newVal.getSender(); // Capture for lambda
                    sendReplyButton.setOnAction(e -> {
                        String replyText = replyArea.getText().trim();
                        if (!replyText.isEmpty()) {
                            try {
                                Message reply = new Message(currentReviewer, sender, replyText);
                                databaseHelper.sendMessage(reply);
                                
                                // Refresh the message list
                                List<Message> updatedMessages = databaseHelper.getMessagesForRecipient(currentReviewer);
                                messageListView.setItems(FXCollections.observableArrayList(updatedMessages));
                                
                                // Clear reply area and show success message
                                replyArea.clear();
                                showAlert("Success", "Reply sent to " + sender);
                            } catch (SQLException ex) {
                                System.err.println("Error sending reply: " + ex.getMessage());
                                showAlert("Error", "Failed to send reply: " + ex.getMessage());
                            }
                        } else {
                            showAlert("Error", "Reply cannot be empty");
                        }
                    });
                } else {
                    messageDetailSection.setVisible(false);
                }
            }
        );
        
        return messagesPane;
    }
    
    private void showCreateReviewDialog(ListView<Review> listView) {
        Dialog<Review> dialog = new Dialog<>();
        dialog.setTitle("Create New Review");
        dialog.setHeaderText("Enter your review details");
        
        // Set the button types
        ButtonType submitButtonType = new ButtonType("Submit", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(submitButtonType, ButtonType.CANCEL);
        
        // Create the content
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        
        TextArea contentArea = new TextArea();
        contentArea.setPromptText("Review content");
        contentArea.setWrapText(true);
        
        TextField associatedIdField = new TextField();
        associatedIdField.setPromptText("Associated item ID");
        
        ComboBox<String> typeComboBox = new ComboBox<>();
        typeComboBox.setItems(FXCollections.observableArrayList("Question", "Answer", "Comment"));
        typeComboBox.setValue("Question");
        
        grid.add(new Label("Type:"), 0, 0);
        grid.add(typeComboBox, 1, 0);
        grid.add(new Label("Associated ID:"), 0, 1);
        grid.add(associatedIdField, 1, 1);
        grid.add(new Label("Review Content:"), 0, 2);
        grid.add(contentArea, 1, 2);
        
        dialog.getDialogPane().setContent(grid);
        
        // Convert the result to a review when the submit button is clicked
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == submitButtonType) {
                String content = contentArea.getText();
                String associatedId = associatedIdField.getText();
                String type = typeComboBox.getValue();
                
                if (content.isEmpty()) {
                    showAlert("Error", "Review content cannot be empty");
                    return null;
                }
                
                Review review = new Review(currentReviewer, content, associatedId, type);
                
                try {
                    // Save to database if available
                    if (databaseHelper != null) {
                        databaseHelper.addReview(review);
                    }
                    
                    // Also add to local list
                    reviews.addReview(review);
                    
                    // Update the UI
                    listView.getItems().setAll(reviews.getReviews());
                } catch (SQLException e) {
                    System.err.println("Error adding review to database: " + e.getMessage());
                    showAlert("Database Error", "Failed to save review: " + e.getMessage());
                }
                
                return review;
            }
            return null;
        });
        
        dialog.showAndWait();
    }
    
    private void showEditReviewDialog(Review review, ListView<Review> listView, TextArea contentArea) {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Edit Review");
        dialog.setHeaderText("Edit your review content");
        
        // Set the button types
        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);
        
        // Create the content
        TextArea editArea = new TextArea(review.getContent());
        editArea.setWrapText(true);
        
        dialog.getDialogPane().setContent(editArea);
        
        // Convert the result when the save button is clicked
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                return editArea.getText();
            }
            return null;
        });
        
        dialog.showAndWait().ifPresent(newContent -> {
            if (!newContent.isEmpty()) {
                try {
                    // Update in database if available
                    if (databaseHelper != null) {
                        databaseHelper.updateReview(review.getId(), newContent);
                    }
                    
                    // Update local copy
                    review.setContent(newContent);
                    
                    // Update UI
                    contentArea.setText(newContent);
                    listView.refresh();
                } catch (SQLException e) {
                    System.err.println("Error updating review in database: " + e.getMessage());
                    showAlert("Database Error", "Failed to update review: " + e.getMessage());
                }
            }
        });
    }

    private void showDeleteConfirmation(Review review, ListView<Review> listView) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Review");
        alert.setHeaderText("Delete Review");
        alert.setContentText("Are you sure you want to delete this review?");
        
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    // Delete from database if available
                    if (databaseHelper != null) {
                        databaseHelper.deleteReview(review.getId());
                    }
                    
                    // Remove from local list
                    reviews.removeReview(review);
                    
                    // Update UI
                    listView.getItems().remove(review);
                } catch (SQLException e) {
                    System.err.println("Error deleting review from database: " + e.getMessage());
                    showAlert("Database Error", "Failed to delete review: " + e.getMessage());
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

    private Pane createDiscussionContentPane() {
        BorderPane contentPane = new BorderPane();
        contentPane.setPadding(new Insets(10));
        
        // Load all questions and answers from the database
        loadDiscussionContent();
        
        // Create a split pane with questions on the left and details on the right
        SplitPane splitPane = new SplitPane();
        
        // Left side - TreeView with questions and their answers
        VBox leftPane = new VBox(10);
        Label contentLabel = new Label("Discussion Content");
        contentLabel.setStyle("-fx-font-weight: bold;");
        
        // Create TreeView for questions and answers
        TreeView<String> contentTreeView = new TreeView<>();
        TreeItem<String> rootItem = new TreeItem<>("All Content");
        rootItem.setExpanded(true);
        contentTreeView.setRoot(rootItem);
        contentTreeView.setShowRoot(false);
        
        // Create a map to store question and answer objects by their TreeItems
        java.util.Map<TreeItem<String>, Object> itemMap = new java.util.HashMap<>();
        
        // Populate tree with questions and their answers
        for (Question question : questions.getAllQuestions()) {
            TreeItem<String> questionItem = new TreeItem<>("Q: " + question.getTitle());
            itemMap.put(questionItem, question);
            
            // Add answers under this question
            List<Answer> questionAnswers = answers.getAnswersForQuestion(question.getId());
            for (Answer answer : questionAnswers) {
                String preview = answer.getAnswerText();
                if (preview.length() > 50) {
                    preview = preview.substring(0, 47) + "...";
                }
                TreeItem<String> answerItem = new TreeItem<>("A: " + preview);
                itemMap.put(answerItem, answer);
                questionItem.getChildren().add(answerItem);
            }
            
            rootItem.getChildren().add(questionItem);
            questionItem.setExpanded(true);
        }
        
        leftPane.getChildren().addAll(contentLabel, contentTreeView);
        VBox.setVgrow(contentTreeView, javafx.scene.layout.Priority.ALWAYS);
        
        // Right side - Content details and review form
        VBox rightPane = new VBox(10);
        rightPane.setPadding(new Insets(0, 0, 0, 10));
        
        // Content details section
        VBox detailsSection = new VBox(5);
        Label detailsLabel = new Label("Content Details");
        detailsLabel.setStyle("-fx-font-weight: bold;");
        
        Label titleLabel = new Label();
        titleLabel.setWrapText(true);
        titleLabel.setStyle("-fx-font-weight: bold;");
        
        TextArea contentArea = new TextArea();
        contentArea.setEditable(false);
        contentArea.setWrapText(true);
        contentArea.setPrefRowCount(5);
        
        Label authorLabel = new Label();
        authorLabel.setStyle("-fx-font-style: italic;");
        
        detailsSection.getChildren().addAll(detailsLabel, titleLabel, contentArea, authorLabel);
        
        // Review form section
        VBox reviewSection = new VBox(5);
        Label reviewLabel = new Label("Create Review");
        reviewLabel.setStyle("-fx-font-weight: bold;");
        
        TextArea reviewTextArea = new TextArea();
        reviewTextArea.setPromptText("Enter your review here");
        reviewTextArea.setWrapText(true);
        reviewTextArea.setPrefRowCount(4);
        
        HBox ratingBox = new HBox(5);
        ratingBox.setAlignment(Pos.CENTER_LEFT);
        Label ratingLabel = new Label("Rating: ");
        ComboBox<Integer> ratingComboBox = new ComboBox<>(
            FXCollections.observableArrayList(1, 2, 3, 4, 5));
        ratingComboBox.setValue(3); // Default to middle rating
        ratingBox.getChildren().addAll(ratingLabel, ratingComboBox);
        
        Button submitReviewButton = new Button("Submit Review");
        submitReviewButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        submitReviewButton.setDisable(true); // Initially disabled until content selected
        
        reviewSection.getChildren().addAll(reviewLabel, reviewTextArea, ratingBox, submitReviewButton);
        
        rightPane.getChildren().addAll(detailsSection, new Separator(), reviewSection);
        
        // Handle selection of content
        contentTreeView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && itemMap.containsKey(newVal)) {
                Object selectedItem = itemMap.get(newVal);
                
                if (selectedItem instanceof Question) {
                    Question question = (Question) selectedItem;
                    titleLabel.setText("Question: " + question.getTitle());
                    contentArea.setText(question.getDescription());
                    authorLabel.setText("Posted by: " + question.getAuthor());
                    submitReviewButton.setDisable(false);
                    submitReviewButton.setOnAction(e -> {
                        createReviewForItem(question.getId(), reviewTextArea.getText(), 
                                            ratingComboBox.getValue(), "Question", contentTreeView);
                    });
                } else if (selectedItem instanceof Answer) {
                    Answer answer = (Answer) selectedItem;
                    titleLabel.setText("Answer");
                    contentArea.setText(answer.getAnswerText());
                    authorLabel.setText("Posted by: " + answer.getAuthor());
                    submitReviewButton.setDisable(false);
                    submitReviewButton.setOnAction(e -> {
                        createReviewForItem(answer.getId(), reviewTextArea.getText(), 
                                           ratingComboBox.getValue(), "Answer", contentTreeView);
                    });
                }
            } else {
                titleLabel.setText("");
                contentArea.setText("");
                authorLabel.setText("");
                submitReviewButton.setDisable(true);
            }
        });
        
        splitPane.getItems().addAll(leftPane, rightPane);
        splitPane.setDividerPositions(0.4);
        contentPane.setCenter(splitPane);
        
        return contentPane;
    }

    private void loadDiscussionContent() {
        if (databaseHelper == null) {
            System.err.println("DatabaseHelper is null, using sample data");
            return;
        }
        
        try {
            // Clear existing content
            questions = new Questions();
            answers = new Answers();
            
            // Load all questions
            String questionsQuery = "SELECT * FROM questions ORDER BY createdAt DESC";
            try (java.sql.Statement stmt = databaseHelper.getConnection().createStatement();
                 java.sql.ResultSet rs = stmt.executeQuery(questionsQuery)) {
                
                while (rs.next()) {
                    Question question = new Question(
                        rs.getString("title"),
                        rs.getString("description"),
                        rs.getString("author")
                    );
                    question.setId(rs.getString("id"));
                    questions.addQuestion(question);
                }
            }
            
            // Load all answers
            String answersQuery = "SELECT * FROM answers ORDER BY createdAt DESC";
            try (java.sql.Statement stmt = databaseHelper.getConnection().createStatement();
                 java.sql.ResultSet rs = stmt.executeQuery(answersQuery)) {
                
                while (rs.next()) {
                    Answer answer;
                    String parentId = rs.getString("parentAnswerId");
                    
                    if (parentId != null && !parentId.isEmpty()) {
                        // This is a reply to another answer
                        answer = new Answer(
                            rs.getString("questionId"),
                            rs.getString("answerText"),
                            rs.getString("author"),
                            parentId
                        );
                    } else {
                        // This is a direct answer to a question
                        answer = new Answer(
                            rs.getString("questionId"),
                            rs.getString("answerText"),
                            rs.getString("author")
                        );
                    }
                    
                    answer.setId(rs.getString("id"));
                    answers.addAnswer(answer);
                }
            }
            
            System.out.println("Loaded " + questions.getAllQuestions().size() + " questions and " + 
                              answers.getAllAnswers().size() + " answers from the database.");
        } catch (SQLException e) {
            System.err.println("Error loading discussion content: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void createReviewForItem(String itemId, String reviewText, int rating, String type, TreeView<String> treeView) {
        if (reviewText == null || reviewText.trim().isEmpty()) {
            showAlert("Error", "Review content cannot be empty");
            return;
        }
        
        try {
            // Try to fix the reviews table before saving
            if (databaseHelper != null) {
                databaseHelper.fixReviewsTable();
            }
            
            // Create the review object
            Review review = new Review(currentReviewer, reviewText, itemId, rating, type);
            
            // Save to database
            if (databaseHelper != null) {
                System.out.println("Saving review to database: reviewer=" + currentReviewer + 
                                  ", itemId=" + itemId + ", rating=" + rating + ", type=" + type);
                databaseHelper.addReview(review);
                System.out.println("Review saved successfully with ID: " + review.getId());
            } else {
                System.err.println("DatabaseHelper is null, can't save review");
            }
            
            // Add to local collection
            reviews.addReview(review);
            
            // Show confirmation
            showAlert("Success", "Review submitted successfully!");
            
            // Clear the form
            treeView.getSelectionModel().clearSelection();
        } catch (SQLException e) {
            System.err.println("Error saving review: " + e.getMessage());
            e.printStackTrace();
            showAlert("Error", "Failed to save review: " + e.getMessage());
        }
    }

    private void updateReview(Review review) {
        Dialog<Review> dialog = new Dialog<>();
        dialog.setTitle("Update Review");
        dialog.setHeaderText("Update your review while preserving the original");

        // Add content
        TextArea contentField = new TextArea(review.getContent());
        contentField.setPrefWidth(400);
        contentField.setPrefHeight(150);
        
        ComboBox<Integer> ratingCombo = new ComboBox<>();
        ratingCombo.getItems().addAll(1, 2, 3, 4, 5);
        ratingCombo.setValue(review.getRating());
        
        VBox content = new VBox(10);
        content.getChildren().addAll(
            new Label("Updated review content:"),
            contentField,
            new Label("Updated rating:"),
            ratingCombo
        );
        dialog.getDialogPane().setContent(content);
        
        // Add buttons
        ButtonType updateButton = new ButtonType("Update", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(updateButton, ButtonType.CANCEL);
        
        // Convert result
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == updateButton) {
                // Create a new review with the updates
                Review updatedReview = new Review(
                    review.getReviewer(),
                    contentField.getText(),
                    review.getAssociatedId(),
                    ratingCombo.getValue(),
                    review.getType()
                );
                updatedReview.setOriginalReviewId(review.getId());
                return updatedReview;
            }
            return null;
        });
        
        // Handle result
        dialog.showAndWait().ifPresent(updatedReview -> {
            try {
                // Save the updated review with reference to the original
                databaseHelper.saveUpdatedReview(updatedReview);
                
                // Notify students who have this reviewer as trusted
                databaseHelper.notifyStudentsAboutReviewUpdate(updatedReview);
                
                // Refresh the UI
                loadDataFromDatabase();
                showAlert("Review Updated", "Your review has been updated and students will be notified.");
            } catch (SQLException e) {
                showAlert("Error", "Could not update review: " + e.getMessage());
            }
        });
    }

    private void displayFeedbackCounts() {
        try {
            if (databaseHelper != null) {
                // Get feedback counts for each review
                Map<String, Integer> feedbackCounts = databaseHelper.getFeedbackCountsForReviewer(currentReviewer);
                
                TabPane tabPane = null;
				// Update the reviews list to show counts
                ListView<Review> reviewsListView = (ListView<Review>)((Pane)tabPane.getTabs().get(0).getContent()).getChildren().get(0);
                
                reviewsListView.setCellFactory(lv -> new ListCell<Review>() {
                    @Override
                    protected void updateItem(Review review, boolean empty) {
                        super.updateItem(review, empty);
                        if (empty || review == null) {
                            setText(null);
                        } else {
                            Integer count = feedbackCounts.getOrDefault(review.getId(), 0);
                            setText(review.getContent() + " - " + 
                                   (count > 0 ? "(" + count + " feedback messages)" : "(No feedback)"));
                        }
                    }
                });
            }
        } catch (SQLException e) {
            System.err.println("Error loading feedback counts: " + e.getMessage());
        }
    }

    private Tab createTrustedReviewersTab() {
        Tab tab = new Tab("Trusted Reviewers");
        tab.setClosable(false);
        
        VBox pane = new VBox(15);
        pane.setPadding(new Insets(10));
        
        // Title
        Label titleLabel = new Label("My Trusted Reviewers");
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        
        // Create table for trusted reviewers
        TableView<TrustedReviewer> table = new TableView<>();
        
        // Set up columns with proper styling
        TableColumn<TrustedReviewer, String> nameColumn = new TableColumn<>("Reviewer");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("reviewerUsername"));
        nameColumn.setPrefWidth(200);
        nameColumn.setStyle("-fx-alignment: CENTER-LEFT;");
        
        // Add custom cell factory for the name column
        nameColumn.setCellFactory(column -> new TableCell<TrustedReviewer, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                    setStyle("");
                } else {
                    setText(item);
                    setStyle("-fx-text-fill: black; -fx-padding: 5;");
                }
            }
        });
        
        TableColumn<TrustedReviewer, Integer> weightColumn = new TableColumn<>("Trust Level");
        weightColumn.setCellValueFactory(new PropertyValueFactory<>("weight"));
        weightColumn.setPrefWidth(100);
        weightColumn.setStyle("-fx-alignment: CENTER;");
        
        // Add custom cell factory for the weight column
        weightColumn.setCellFactory(column -> new TableCell<TrustedReviewer, Integer>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                    setStyle("");
                } else {
                    setText(item.toString());
                    setStyle("-fx-text-fill: black; -fx-alignment: CENTER; -fx-padding: 5;");
                }
            }
        });
        
        // Add columns to table
        table.getColumns().addAll(nameColumn, weightColumn);
        
        // Configure table properties
        table.setPrefHeight(300);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setStyle("-fx-font-size: 12px; -fx-border-color: #ccc; -fx-border-width: 1px;");
        
        // Add a placeholder message when empty
        Label placeholder = new Label("No trusted reviewers found");
        placeholder.setStyle("-fx-text-fill: #666;");
        table.setPlaceholder(placeholder);
        
        // Load trusted reviewers from database immediately
        refreshTrustedReviewersTable(table);
        
        // Add a selection listener for debugging
        table.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                System.out.println("Selected reviewer: " + newVal.getReviewerUsername() + 
                                 " with weight: " + newVal.getWeight());
            }
        });
        
        // Also refresh when the tab is selected
        tab.setOnSelectionChanged(event -> {
            if (tab.isSelected()) {
                System.out.println("Trusted Reviewers tab selected, refreshing data...");
                refreshTrustedReviewersTable(table);
            }
        });
        
        // Add buttons for managing trusted reviewers
        Button addButton = new Button("Add Reviewer");
        addButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold;");
        
        Button editButton = new Button("Edit Weight");
        editButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");
        editButton.setDisable(true);
        
        Button removeButton = new Button("Remove Reviewer");
        removeButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");
        removeButton.setDisable(true);
        
        Button messageButton = new Button("Send Message");
        messageButton.setStyle("-fx-background-color: #FF9800; -fx-text-fill: white;");
        messageButton.setDisable(true);
        
        HBox buttonBox = new HBox(10);
        buttonBox.getChildren().addAll(addButton, editButton, removeButton, messageButton);
        buttonBox.setAlignment(Pos.CENTER_LEFT);
        buttonBox.setPadding(new Insets(10, 0, 0, 0));
        
        // Enable/disable buttons based on selection
        table.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            boolean hasSelection = newVal != null;
            editButton.setDisable(!hasSelection);
            removeButton.setDisable(!hasSelection);
            messageButton.setDisable(!hasSelection);
        });
        
        // Setup button actions
        addButton.setOnAction(e -> showAddReviewerDialog(table));
        editButton.setOnAction(e -> showEditWeightDialog(table));
        removeButton.setOnAction(e -> removeSelectedReviewer(table));
        messageButton.setOnAction(e -> sendMessageToReviewer(table));
        
        pane.getChildren().addAll(titleLabel, table, buttonBox);
        tab.setContent(pane);
        
        return tab;
    }

    private void refreshTrustedReviewersTable(TableView<TrustedReviewer> table) {
        try {
            System.out.println("Refreshing trusted reviewers table for: " + currentReviewer);
            
            // Important: Make sure we're getting the right currentReviewer value
            if (currentReviewer == null || currentReviewer.isEmpty()) {
                System.err.println("Current reviewer is null or empty!");
                Platform.runLater(() -> {
                    table.setPlaceholder(new Label("Error: Current reviewer not set"));
                });
                return;
            }
            
            // Get trusted reviewers from database
            List<TrustedReviewer> trustedReviewers = databaseHelper.getTrustedReviewers(currentReviewer);
            System.out.println("Found " + trustedReviewers.size() + " trusted reviewers");
            
            // Debug each reviewer
            for (TrustedReviewer tr : trustedReviewers) {
                System.out.println("  - " + tr.getReviewerUsername() + " (weight: " + tr.getWeight() + ")");
            }
            
            // Important: Create a new ObservableList with the data
            ObservableList<TrustedReviewer> observableList = FXCollections.observableArrayList(trustedReviewers);
            
            // Set the items on the JavaFX thread to avoid any threading issues
            Platform.runLater(() -> {
                // Clear existing items first
                table.getItems().clear();
                
                // Set new items
                table.setItems(observableList);
                
                // Force table refresh
                table.refresh();
                
                // Update placeholder text based on data
                if (trustedReviewers.isEmpty()) {
                    Label placeholder = new Label("No trusted reviewers found");
                    placeholder.setStyle("-fx-text-fill: #666;");
                    table.setPlaceholder(placeholder);
                }
            });
        } catch (SQLException e) {
            System.err.println("Error refreshing trusted reviewers: " + e.getMessage());
            e.printStackTrace();
            
            // Show an error in the UI
            Platform.runLater(() -> {
                Label errorLabel = new Label("Error loading trusted reviewers: " + e.getMessage());
                errorLabel.setStyle("-fx-text-fill: red;");
                errorLabel.setWrapText(true);
                table.setPlaceholder(errorLabel);
            });
        }
    }

    /**
     * Shows a dialog to add a new trusted reviewer
     */
    private void showAddReviewerDialog(TableView<TrustedReviewer> table) {
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
                
                return new TrustedReviewer(username, currentReviewer, weight);
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
                if (reviewer.getReviewerUsername().equals(currentReviewer)) {
                    showAlert("Error", "You cannot add yourself as a trusted reviewer");
                    return;
                }
                
                // Add to database
                databaseHelper.addTrustedReviewer(
                    currentReviewer, 
                    reviewer.getReviewerUsername(), 
                    reviewer.getWeight()
                );
                
                // Refresh table
                refreshTrustedReviewersTable(table);
                
                showAlert("Success", "Added " + reviewer.getReviewerUsername() + " as a trusted reviewer");
            } catch (SQLException e) {
                System.err.println("Error adding trusted reviewer: " + e.getMessage());
                showAlert("Error", "Failed to add trusted reviewer: " + e.getMessage());
            }
        });
    }

    /**
     * Shows a dialog to edit the weight/trust level of a selected trusted reviewer
     */
    private void showEditWeightDialog(TableView<TrustedReviewer> table) {
        TrustedReviewer selectedReviewer = table.getSelectionModel().getSelectedItem();
        if (selectedReviewer == null) {
            showAlert("No Selection", "Please select a reviewer to edit");
            return;
        }
        
        Dialog<Integer> dialog = new Dialog<>();
        dialog.setTitle("Edit Trust Level");
        dialog.setHeaderText("Update trust level for " + selectedReviewer.getReviewerUsername());
        
        // Set the button types
        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);
        
        // Create content
        VBox content = new VBox(10);
        content.setPadding(new Insets(20));
        
        Slider weightSlider = new Slider(1, 10, selectedReviewer.getWeight());
        weightSlider.setShowTickLabels(true);
        weightSlider.setShowTickMarks(true);
        weightSlider.setMajorTickUnit(1);
        weightSlider.setMinorTickCount(0);
        weightSlider.setBlockIncrement(1);
        weightSlider.setSnapToTicks(true);
        
        Label weightValueLabel = new Label(Integer.toString(selectedReviewer.getWeight()));
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
                    currentReviewer,
                    selectedReviewer.getReviewerUsername(),
                    weight
                );
                
                // Refresh table
                refreshTrustedReviewersTable(table);
                
                showAlert("Success", "Updated trust level for " + selectedReviewer.getReviewerUsername());
            } catch (SQLException e) {
                System.err.println("Error updating trusted reviewer weight: " + e.getMessage());
                showAlert("Error", "Failed to update trust level: " + e.getMessage());
            }
        });
    }

    /**
     * Removes a selected trusted reviewer from the list
     */
    private void removeSelectedReviewer(TableView<TrustedReviewer> table) {
        TrustedReviewer selectedReviewer = table.getSelectionModel().getSelectedItem();
        if (selectedReviewer == null) {
            showAlert("No Selection", "Please select a reviewer to remove");
            return;
        }
        
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Remove Trusted Reviewer");
        confirmation.setHeaderText("Remove " + selectedReviewer.getReviewerUsername() + " from your trusted reviewers");
        confirmation.setContentText("Are you sure you want to remove this reviewer?");
        
        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    // Remove from database
                    databaseHelper.removeTrustedReviewer(
                        currentReviewer,
                        selectedReviewer.getReviewerUsername()
                    );
                    
                    // Refresh table
                    refreshTrustedReviewersTable(table);
                    
                    showAlert("Success", "Removed " + selectedReviewer.getReviewerUsername() + " from trusted reviewers");
                } catch (SQLException e) {
                    System.err.println("Error removing trusted reviewer: " + e.getMessage());
                    showAlert("Error", "Failed to remove trusted reviewer: " + e.getMessage());
                }
            }
        });
    }

    /**
     * Shows a dialog to send a message to the selected trusted reviewer
     */
    private void sendMessageToReviewer(TableView<TrustedReviewer> table) {
        TrustedReviewer selectedReviewer = table.getSelectionModel().getSelectedItem();
        if (selectedReviewer == null) {
            showAlert("No Selection", "Please select a reviewer to message");
            return;
        }
        
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Send Message");
        dialog.setHeaderText("Send a message to " + selectedReviewer.getReviewerUsername());
        
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
                    currentReviewer,
                    selectedReviewer.getReviewerUsername(),
                    messageText
                );
                
                databaseHelper.sendMessage(message);
                
                showAlert("Success", "Message sent to " + selectedReviewer.getReviewerUsername());
            } catch (SQLException e) {
                System.err.println("Error sending message: " + e.getMessage());
                showAlert("Error", "Failed to send message: " + e.getMessage());
            }
        });
    }
}