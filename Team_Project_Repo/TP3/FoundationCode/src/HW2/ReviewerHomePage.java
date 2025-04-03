package HW2;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.List;

import databasePart1.DatabaseHelper;

public class ReviewerHomePage {
    private Reviews reviews;
    private Messages messages;
    private String currentReviewer;
    private Questions questions;
    private Answers answers;
    private DatabaseHelper databaseHelper; // Add this

    // Add this constructor
    public ReviewerHomePage(DatabaseHelper databaseHelper, String currentReviewer) {
        this.databaseHelper = databaseHelper;
        this.currentReviewer = currentReviewer;
        
        this.reviews = new Reviews();
        this.messages = new Messages();
        this.questions = new Questions();
        this.answers = new Answers();
        
        // Load real data from database instead of sample data
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

    // Add this method to load data from the database
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
        BorderPane mainLayout = new BorderPane();
        mainLayout.setPadding(new Insets(20));
        
        // Top section - Title and username
        VBox topSection = new VBox(10);
        
        // Username display
        Label usernameLabel = new Label("Logged in as: " + currentReviewer);
        usernameLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-alignment: center-right;");
        usernameLabel.setAlignment(Pos.CENTER_RIGHT);
        
        Label titleLabel = new Label("Reviewer Dashboard");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        
        topSection.getChildren().addAll(usernameLabel, titleLabel);
        topSection.setAlignment(Pos.CENTER);
        topSection.setPadding(new Insets(0, 0, 10, 0));
        mainLayout.setTop(topSection);
        
        // Update window title
        primaryStage.setTitle("Reviewer Dashboard - " + currentReviewer);
        
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
        
        tabPane.getTabs().addAll(reviewsTab, messagesTab, discussionContentTab);
        mainLayout.setCenter(tabPane);
        
        // Bottom section - Back button
        Button backButton = new Button("Back to User Page");
        backButton.setOnAction(e -> {
            // Return to the user page
            new UserHomePage(databaseHelper, currentReviewer, "reviewer").show(primaryStage);
        });
        HBox bottomSection = new HBox(backButton);
        bottomSection.setAlignment(Pos.CENTER);
        bottomSection.setPadding(new Insets(10, 0, 0, 0));
        mainLayout.setBottom(bottomSection);
        
        Scene scene = new Scene(mainLayout, 800, 500);
        primaryStage.setTitle("Reviewer Dashboard");
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
        
        reviewListSection.getChildren().addAll(reviewsLabel, reviewListView, createReviewButton);
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
        messagesLabel.setStyle("-fx-font-weight: bold;");
        
        ListView<Message> messageListView = new ListView<>();
        messageListView.setPrefHeight(300);
        
        // Populate with reviewer's messages
        ObservableList<Message> messageItems = FXCollections.observableArrayList(
                messages.getMessagesForRecipient(currentReviewer));
        messageListView.setItems(messageItems);
        
        // Set cell factory to display message sender and preview
        messageListView.setCellFactory(lv -> new ListCell<Message>() {
            @Override
            protected void updateItem(Message message, boolean empty) {
                super.updateItem(message, empty);
                if (empty || message == null) {
                    setText(null);
                } else {
                    setText("From: " + message.getSender() + " - " + 
                            message.getContent().substring(0, Math.min(20, message.getContent().length())) + "...");
                }
            }
        });
        
        messageListSection.getChildren().addAll(messagesLabel, messageListView);
        messagesPane.setLeft(messageListSection);
        
        // Right side - Message content and reply
        VBox messageDetailSection = new VBox(10);
        messageDetailSection.setPadding(new Insets(0, 0, 0, 10));
        messageDetailSection.setVisible(false); // Initially hidden
        
        Label detailLabel = new Label("Message Details");
        detailLabel.setStyle("-fx-font-weight: bold;");
        
        Label fromLabel = new Label();
        TextArea messageContentArea = new TextArea();
        messageContentArea.setEditable(false);
        messageContentArea.setWrapText(true);
        
        Label replyLabel = new Label("Reply:");
        TextArea replyArea = new TextArea();
        replyArea.setWrapText(true);
        replyArea.setPrefRowCount(3);
        
        Button sendReplyButton = new Button("Send Reply");
        sendReplyButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");
        
        messageDetailSection.getChildren().addAll(
                detailLabel, fromLabel, messageContentArea, 
                replyLabel, replyArea, sendReplyButton);
        messagesPane.setCenter(messageDetailSection);
        
        // Handle selection
        messageListView.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldVal, newVal) -> {
                if (newVal != null) {
                    messageDetailSection.setVisible(true);
                    fromLabel.setText("From: " + newVal.getSender());
                    messageContentArea.setText(newVal.getContent());
                    replyArea.clear();
                    
                    // Setup send reply button
                    final String sender = newVal.getSender(); // Capture for lambda
                    sendReplyButton.setOnAction(e -> {
                        String replyText = replyArea.getText().trim();
                        if (!replyText.isEmpty()) {
                            Message reply = new Message(currentReviewer, sender, replyText);
                            messages.sendMessage(reply);
                            showAlert("Reply Sent", "Your reply has been sent to " + sender);
                            replyArea.clear();
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
}