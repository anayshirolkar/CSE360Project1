package HW2;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.stage.Modality;
import javafx.geometry.Insets;
import javafx.geometry.Pos;

import java.sql.SQLException;

import databasePart1.DatabaseHelper;
import javafx.animation.PauseTransition;
import javafx.util.Duration;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;

public class DiscussionPage {
    private Questions questions;
    private Answers answers;
    private Question currentQuestion = null;
    private Answer currentAnswer = null;
    private BorderPane mainLayout;
    private Button editAnswerButton;
    private Button deleteAnswerButton;
    private Button submitAnswerButton;  // Added class field for submit button
    private TextArea newAnswerArea;
    private Label answersLabel;
    private VBox answersContainer;
    private Button replyButton; // New button for replying to answers
    private String currentUsername; // Simulated current user - you would replace this with real user authentication
    private String userRole; // Add this field 
    private DatabaseHelper databaseHelper;

    public DiscussionPage(DatabaseHelper databaseHelper, String username) {
        questions = new Questions();
        answers = new Answers();
        this.databaseHelper = databaseHelper;
        this.currentUsername = username != null ? username : "DefaultUser";
        
        // Determine role
        this.userRole = "user"; // Default
        try {
            if (databaseHelper != null) {
                String role = databaseHelper.getUserRole(username);
                if (role != null) {
                    this.userRole = role;
                }
            }
        } catch (Exception e) {
            System.err.println("Error determining user role: " + e.getMessage());
        }
        
        System.out.println("DiscussionPage initialized with username: " + this.currentUsername + ", role: " + this.userRole);
    }

    // Maintain a default constructor for backward compatibility
    public DiscussionPage() {
        questions = new Questions();
        answers = new Answers();
        this.currentUsername = "CurrentUser";
    }

    public void show(Stage primaryStage) {
        // Main layout with split pane
        mainLayout = new BorderPane(); // Fixed: Now assigning to the class field
        mainLayout.setPadding(new Insets(20));
        
        // Top section - Search, Create Question button, and Username display
        VBox topContainer = new VBox(10); // Container for both rows
        
        // First row - Username display
        HBox userInfoSection = new HBox(10);
        userInfoSection.setAlignment(Pos.CENTER_RIGHT);
        
        Label usernameLabel = new Label("Logged in as: " + currentUsername);
        usernameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        userInfoSection.getChildren().add(usernameLabel);
        
        // Second row - Search and Create Question button
        HBox topSection = new HBox(10);
        topSection.setAlignment(Pos.CENTER_LEFT);
        
        Label searchLabel = new Label("Search:");
        TextField searchField = new TextField();
        searchField.setPromptText("Search questions...");
        searchField.setPrefWidth(300);
        
        Button createQuestionButton = new Button("Ask New Question");
        createQuestionButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");
        
        topSection.getChildren().addAll(searchLabel, searchField, createQuestionButton);
        
        // Add both rows to the top container
        topContainer.getChildren().addAll(userInfoSection, topSection);
        mainLayout.setTop(topContainer);
        
        // Center section - Questions list on left, Question details and answers on right
        SplitPane centerSplitPane = new SplitPane();
        
        // Left side - Questions list
        VBox questionsSection = new VBox(10);
        questionsSection.setPadding(new Insets(10, 5, 10, 10));
        
        Label questionsLabel = new Label("Questions");
        questionsLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        
        ListView<Question> questionListView = new ListView<>();
        questionListView.setPrefWidth(300);
        // Make the questions list stretch to fill available height
        VBox.setVgrow(questionListView, Priority.ALWAYS);
        
        // Set cell factory to only display question titles
        questionListView.setCellFactory(lv -> new ListCell<Question>() {
            @Override
            protected void updateItem(Question item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getTitle());
                }
            }
        });
        
        // Notification label for question actions
        Label questionNotificationLabel = new Label();
        questionNotificationLabel.setStyle("-fx-text-fill: #f44336; -fx-font-weight: bold;");
        questionNotificationLabel.setVisible(false);
        
        questionsSection.getChildren().addAll(questionsLabel, questionListView, questionNotificationLabel);
        
        // Right side - Modified to use a BorderPane with scrollable content and fixed answer input area
        BorderPane detailsSection = new BorderPane();
        detailsSection.setPadding(new Insets(10));
        
        // Create a VBox for the question and answers that will be scrollable
        VBox questionAndAnswersBox = new VBox(15);
        
        // Question detail view
        VBox questionDetailView = new VBox(10);
        questionDetailView.setStyle("-fx-border-color: #e0e0e0; -fx-border-width: 0 0 1 0; -fx-padding: 0 0 15 0;");
        
        Label questionTitleLabel = new Label();
        questionTitleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 18px;");
        questionTitleLabel.setWrapText(true); // Enable text wrapping for title
        
        Label questionDescriptionLabel = new Label();
        questionDescriptionLabel.setWrapText(true); // Enable text wrapping
        
        HBox questionActionButtons = new HBox(10);
        Button editQuestionButton = new Button("Edit");
        editQuestionButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        Button deleteQuestionButton = new Button("Delete");
        deleteQuestionButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");
        questionActionButtons.getChildren().addAll(editQuestionButton, deleteQuestionButton);
        
        questionDetailView.getChildren().addAll(questionTitleLabel, questionDescriptionLabel, questionActionButtons);
        questionDetailView.setVisible(false); // Initially hidden until a question is selected
        
        // Answers section
        answersLabel = new Label("Answers");
        answersLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        answersLabel.setVisible(false); // Fixed: Initially hide the answers label
        
        // Instead of using ListView for answers, use VBox to have them flow in the scroll pane
        answersContainer = new VBox(10);
        answersContainer.setVisible(false); // Fixed: Initially hide the answers container
        
        // Answer notification label
        Label answerNotificationLabel = new Label();
        answerNotificationLabel.setStyle("-fx-text-fill: #f44336; -fx-font-weight: bold;");
        answerNotificationLabel.setVisible(false);
        
        // Add question and answers to the VBox that will be scrolled
        questionAndAnswersBox.getChildren().addAll(
            questionDetailView, 
            answersLabel, 
            answersContainer,
            answerNotificationLabel
        );
        
        // Create a ScrollPane for the question and answers
        ScrollPane scrollPane = new ScrollPane(questionAndAnswersBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);
        
        // Your Answer section - this will be at the bottom and not part of the scroll pane
        VBox yourAnswerSection = new VBox(10);
        yourAnswerSection.setPadding(new Insets(10, 0, 0, 0));
        yourAnswerSection.setStyle("-fx-border-color: #e0e0e0; -fx-border-width: 1 0 0 0; -fx-padding: 10 0 0 0;");
        yourAnswerSection.setVisible(false); // Fixed: Initially hide the answer section
        
        Label addAnswerLabel = new Label("Your Answer");
        addAnswerLabel.setStyle("-fx-font-weight: bold;");
        
        newAnswerArea = new TextArea(); // Fixed: Now assigning to the class field
        newAnswerArea.setPromptText("Write your answer here");
        newAnswerArea.setPrefRowCount(4);
        newAnswerArea.setWrapText(true); // Enable text wrapping in the answer area
        
        submitAnswerButton = new Button("Submit Answer"); // Assign to class field
        submitAnswerButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");
        
        HBox answerActionButtons = new HBox(10);
        editAnswerButton = new Button("Edit"); // Fixed: Now assigning to the class field
        editAnswerButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        deleteAnswerButton = new Button("Delete"); // Fixed: Now assigning to the class field
        deleteAnswerButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");
        answerActionButtons.getChildren().addAll(editAnswerButton, deleteAnswerButton);
        
        // Initially disable edit and delete buttons for answers
        editAnswerButton.setDisable(true);
        deleteAnswerButton.setDisable(true);
        
        yourAnswerSection.getChildren().addAll(
            addAnswerLabel,
            newAnswerArea,
            submitAnswerButton,
            answerActionButtons
        );
        
        // Add reply button (initially hidden)
        replyButton = new Button("Reply to Answer");
        replyButton.setStyle("-fx-background-color: #FF9800; -fx-text-fill: white;");
        replyButton.setVisible(false);
        
        // Add it to the answer action section
        yourAnswerSection.getChildren().add(replyButton);
        
        // Set the scroll pane in the center and your answer section at the bottom
        detailsSection.setCenter(scrollPane);
        detailsSection.setBottom(yourAnswerSection);
        
        // Adjust widths when parent container resizes
        detailsSection.widthProperty().addListener((obs, oldVal, newVal) -> {
            double width = newVal.doubleValue() - 30;
            questionTitleLabel.setPrefWidth(width);
            questionDescriptionLabel.setPrefWidth(width);
            newAnswerArea.setPrefWidth(width);
        });
        
        // Add to split pane
        centerSplitPane.getItems().addAll(questionsSection, detailsSection);
        centerSplitPane.setDividerPositions(0.3); // 30% left, 70% right
        mainLayout.setCenter(centerSplitPane);
        
        // FUNCTIONALITY
        
        // Search functionality
        searchField.textProperty().addListener((obs, oldText, newText) -> {
            if (newText.trim().isEmpty()) {
                refreshQuestionList(questionListView);
            } else {
                questionListView.setItems(
                    javafx.collections.FXCollections.observableArrayList(
                        questions.searchQuestions(newText)
                    )
                );
            }
        });
        
        // Create question button action
        createQuestionButton.setOnAction(e -> {
            showCreateQuestionDialog(questionListView);
        });
        
        // Question selection handler
        questionListView.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldVal, newVal) -> {
                if (newVal != null) {
                    // Update the current question reference
                    currentQuestion = newVal;
                    
                    // Update question detail view
                    questionTitleLabel.setText(newVal.getTitle());
                    questionDescriptionLabel.setText(newVal.getDescription());
                    Label questionAuthorLabel = new Label("Posted by: " + newVal.getAuthor());
                    questionAuthorLabel.setStyle("-fx-font-style: italic; -fx-font-size: 12px;");
                    questionDetailView.getChildren().add(1, questionAuthorLabel); // Insert after title, before description
                    questionDetailView.setVisible(true);
                    
                    // Enable question action buttons
                    editQuestionButton.setDisable(false);
                    deleteQuestionButton.setDisable(false);
                    
                    // Show answers section and your answer section
                    answersLabel.setVisible(true);   // Fixed: Show answers label
                    answersContainer.setVisible(true); // Fixed: Show answers container
                    yourAnswerSection.setVisible(true); // Fixed: Show answer section
                    
                    // Refresh answers for this question
                    refreshAnswersContainer(answersContainer, newVal);
                    
                    // Clear any answer being edited
                    newAnswerArea.clear();
                    editAnswerButton.setDisable(true);
                    deleteAnswerButton.setDisable(true);
                    
                    // Make sure submit button is visible when selecting a question
                    submitAnswerButton.setVisible(true);
                    
                    // Scroll to top when a new question is selected
                    scrollPane.setVvalue(0);
                } else {
                    // Hide question details if no question selected
                    questionDetailView.setVisible(false);
                    
                    // Hide answers section and your answer section
                    answersLabel.setVisible(false);   // Fixed: Hide answers label
                    answersContainer.setVisible(false); // Fixed: Hide answers container
                    yourAnswerSection.setVisible(false); // Fixed: Hide answer section
                    
                    // Disable question action buttons
                    editQuestionButton.setDisable(true);
                    deleteQuestionButton.setDisable(true);
                    
                    // Clear answers
                    answersContainer.getChildren().clear();
                }
            }
        );
        
        // Edit question handler
        editQuestionButton.setOnAction(e -> {
            if (currentQuestion != null) {
                showEditQuestionDialog(currentQuestion, questionListView, questionTitleLabel, questionDescriptionLabel);
            }
        });
        
        // Delete question handler
        deleteQuestionButton.setOnAction(e -> {
            if (currentQuestion != null) {
                boolean confirmed = showConfirmationDialog(
                    "Delete Question", 
                    "Are you sure you want to delete this question?\n\n" +
                    "Title: " + currentQuestion.getTitle()
                );
                
                if (confirmed) {
                    questions.deleteQuestion(currentQuestion.getId());
                    refreshQuestionList(questionListView);
                    
                    // Show temporary notification
                    showTemporaryNotification(
                        questionNotificationLabel, 
                        "Question deleted!"
                    );
                    
                    // Clear the detail view
                    questionDetailView.setVisible(false);
                    answersLabel.setVisible(false);   // Fixed: Hide answers label
                    answersContainer.setVisible(false); // Fixed: Hide answers container
                    yourAnswerSection.setVisible(false); // Fixed: Hide answer section
                    answersContainer.getChildren().clear();
                    currentQuestion = null;
                }
            }
        });
        
        // Submit answer handler
        submitAnswerButton.setOnAction(e -> {
            if (currentQuestion == null) {
                showAlert("Error", "Please select a question first!");
                return;
            }

            String answerText = newAnswerArea.getText().trim();
            if (answerText.isEmpty()) {
                showAlert("Error", "Answer cannot be empty!");
                return;
            }

            Answer newAnswer = new Answer(currentQuestion.getId(), answerText, currentUsername);
            answers.addAnswer(newAnswer);
            
            // Save to database if available
            if (databaseHelper != null) {
                try {
                    databaseHelper.saveAnswer(newAnswer);
                } catch (SQLException ex) {
                    System.err.println("Error saving answer: " + ex.getMessage());
                }
            }
            
            refreshAnswersContainer(answersContainer, currentQuestion);
            newAnswerArea.clear();
            
            // Clear the answer selection
            currentAnswer = null;
            editAnswerButton.setDisable(true);
            deleteAnswerButton.setDisable(true);
            submitAnswerButton.setVisible(true);
            replyButton.setVisible(false);
            
            showAlert("Success", "Answer submitted successfully!");
        });
        
        // Edit answer handler
        editAnswerButton.setOnAction(e -> {
            if (currentAnswer != null && currentQuestion != null) {
                showEditAnswerDialog(currentAnswer, answersContainer, currentQuestion);
                newAnswerArea.clear();
                
                // Clear the answer selection
                currentAnswer = null;
                editAnswerButton.setDisable(true);
                deleteAnswerButton.setDisable(true);
                submitAnswerButton.setVisible(true); // Show submit button after editing
                replyButton.setVisible(false);
            }
        });
        
        // Delete answer handler
        deleteAnswerButton.setOnAction(e -> {
            if (currentAnswer != null) {
                boolean confirmed = showConfirmationDialog(
                    "Delete Answer", 
                    "Are you sure you want to delete this answer?"
                );
                
                if (confirmed) {
                    answers.deleteAnswer(currentAnswer.getId());
                    refreshAnswersContainer(answersContainer, currentQuestion);
                    
                    // Show temporary notification
                    showTemporaryNotification(
                        answerNotificationLabel, 
                        "Answer deleted!"
                    );
                    
                    newAnswerArea.clear();
                    
                    // Clear the answer selection
                    currentAnswer = null;
                    editAnswerButton.setDisable(true);
                    deleteAnswerButton.setDisable(true);
                    submitAnswerButton.setVisible(true); // Show submit button after deletion
                    replyButton.setVisible(false);
                }
            }
        });
        
        // In the show method, after setting up the delete answer handler
        replyButton.setOnAction(e -> {
            if (currentAnswer != null && currentQuestion != null) {
                String replyText = newAnswerArea.getText().trim();
                if (replyText.isEmpty()) {
                    showAlert("Error", "Reply cannot be empty!");
                    return;
                }
                
                // Create a new answer that's a reply to the current answer
                Answer newReply = new Answer(
                    currentQuestion.getId(), 
                    replyText, 
                    currentUsername, 
                    currentAnswer.getId() // Set the parent answer ID
                );
                
                answers.addAnswer(newReply);
                
                // Save to database if available
                if (databaseHelper != null) {
                    try {
                        databaseHelper.saveAnswer(newReply);
                    } catch (SQLException ex) {
                        System.err.println("Error saving reply: " + ex.getMessage());
                    }
                }
                
                refreshAnswersContainer(answersContainer, currentQuestion);
                newAnswerArea.clear();
                
                // Clear the answer selection
                currentAnswer = null;
                editAnswerButton.setDisable(true);
                deleteAnswerButton.setDisable(true);
                submitAnswerButton.setVisible(true);
                replyButton.setVisible(false);
                
                showAlert("Success", "Reply submitted successfully!");
            }
        });
        
        // Initialize the question list
        refreshQuestionList(questionListView);
        
        // Add event handler to main layout for deselection
        mainLayout.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                // Check if click was on the main layout itself (not on a child)
                if (event.getTarget().equals(mainLayout)) {
                    // Deselect question
                    questionListView.getSelectionModel().clearSelection();
                    currentQuestion = null;
                    
                    // Deselect answer
                    if (currentAnswer != null) {
                        currentAnswer = null;
                        editAnswerButton.setDisable(true);
                        deleteAnswerButton.setDisable(true);
                        submitAnswerButton.setVisible(true);
                        newAnswerArea.clear();
                        replyButton.setVisible(false);
                        
                        // Reset all answer box styles
                        answersContainer.getChildren().forEach(node -> {
                            if (node instanceof VBox) {
                                node.setStyle("-fx-border-color: #e0e0e0; -fx-border-radius: 5;");
                            }
                        });
                    }
                }
            }
        });
        
        // Add event handler to the background of detailsSection for deselection of answers
        detailsSection.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                // Check if click was directly on the detailsSection (not on a child)
                if (event.getTarget().equals(detailsSection)) {
                    // Deselect answer if one is selected
                    if (currentAnswer != null) {
                        currentAnswer = null;
                        editAnswerButton.setDisable(true);
                        deleteAnswerButton.setDisable(true);
                        submitAnswerButton.setVisible(true);
                        newAnswerArea.clear();
                        replyButton.setVisible(false);
                        
                        // Reset all answer box styles
                        answersContainer.getChildren().forEach(node -> {
                            if (node instanceof VBox) {
                                node.setStyle("-fx-border-color: #e0e0e0; -fx-border-radius: 5;");
                            }
                        });
                    }
                }
            }
        });
        
        // Add event handler for scroll pane background clicks
        scrollPane.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                // Check if click was directly on the scroll pane (not content)
                if (event.getTarget().equals(scrollPane)) {
                    // Deselect answer if one is selected
                    if (currentAnswer != null) {
                        currentAnswer = null;
                        editAnswerButton.setDisable(true);
                        deleteAnswerButton.setDisable(true);
                        submitAnswerButton.setVisible(true);
                        newAnswerArea.clear();
                        replyButton.setVisible(false);
                        
                        // Reset all answer box styles
                        answersContainer.getChildren().forEach(node -> {
                            if (node instanceof VBox) {
                                node.setStyle("-fx-border-color: #e0e0e0; -fx-border-radius: 5;");
                            }
                        });
                    }
                }
            }
        });

        // Add Back button at the bottom
        HBox bottomSection = new HBox(10);
        bottomSection.setAlignment(Pos.CENTER);
        bottomSection.setPadding(new Insets(10, 0, 0, 0));

        Button backButton = new Button("Back");
        backButton.setOnAction(e -> {
            System.out.println("Returning to dashboard based on user role: " + userRole);
            if ("admin".equals(userRole)) {
                // Navigate to AdminHomePage
                new AdminHomePage(databaseHelper, currentUsername).show(primaryStage);
            } else {
                // Navigate to UserHomePage for other users
                new UserHomePage(databaseHelper, currentUsername).show(primaryStage);
            }
        });

        bottomSection.getChildren().add(backButton);
        mainLayout.setBottom(bottomSection);

        Scene scene = new Scene(mainLayout, 900, 700);
        primaryStage.setTitle("Discussion Forum - " + currentUsername);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    // Method to refresh answers in the VBox container instead of ListView
    private void refreshAnswersContainer(VBox answersContainer, Question question) {
        answersContainer.getChildren().clear();
        
        // Get answers for the current question (only top-level answers)
        java.util.List<Answer> answersList = answers.getAnswersForQuestion(question.getId()).stream()
            .filter(a -> a.getParentAnswerId() == null)
            .collect(java.util.stream.Collectors.toList());
        
        if (answersList.isEmpty()) {
            Label noAnswersLabel = new Label("No answers yet. Be the first to answer!");
            noAnswersLabel.setStyle("-fx-font-style: italic; -fx-text-fill: #757575;");
            answersContainer.getChildren().add(noAnswersLabel);
        } else {
            // Add answers to the container
            for (Answer answer : answersList) {
                VBox answerBox = createAnswerBox(answer, question);
                answersContainer.getChildren().add(answerBox);
            }
        }
    }

    private VBox createAnswerBox(Answer answer, Question question) {
        VBox answerBox = new VBox(5);
        answerBox.setPadding(new Insets(10));
        answerBox.setStyle("-fx-border-color: #e0e0e0; -fx-border-radius: 5;");
        
        // Author and answer text
        Label authorLabel = new Label("Posted by: " + answer.getAuthor());
        authorLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 12px; -fx-text-fill: #555555;");
        
        // Create answer text with wrapping
        Label answerTextLabel = new Label(answer.getAnswerText());
        answerTextLabel.setWrapText(true);
        
        // Add action buttons for each answer
        HBox actionBox = new HBox(10);
        Button selectButton = new Button("Select");
        selectButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");
        
        actionBox.getChildren().add(selectButton);
        answerBox.getChildren().addAll(authorLabel, answerTextLabel, actionBox);
        
        // Add selection behavior
        final Answer selectedAnswer = answer; // Local final variable to use in lambda
        selectButton.setOnAction(e -> {
            // Highlight the selected answer
            answersContainer.getChildren().forEach(node -> {
                if (node instanceof VBox) {
                    node.setStyle("-fx-border-color: #e0e0e0; -fx-border-radius: 5;");
                }
            });
            answerBox.setStyle("-fx-border-color: #4CAF50; -fx-border-radius: 5; -fx-border-width: 2;");
            
            // Set this as the current answer using the class field
            currentAnswer = selectedAnswer;
            
            // Enable edit and delete buttons
            editAnswerButton.setDisable(false);
            deleteAnswerButton.setDisable(false);
            
            // Hide submit button and show reply button
            submitAnswerButton.setVisible(false);
            replyButton.setVisible(true);
            
            // Clear the answer text area instead of populating it with the selected answer
            newAnswerArea.clear();
        });
        
        // Add any replies to this answer
        VBox repliesContainer = new VBox(5);
        repliesContainer.setPadding(new Insets(0, 0, 0, 20)); // Indent replies
        
        // Get replies for this answer
        java.util.List<Answer> replies = answers.getAnswersForQuestion(question.getId()).stream()
            .filter(a -> a.getParentAnswerId() != null && a.getParentAnswerId().equals(answer.getId()))
            .collect(java.util.stream.Collectors.toList());
        
        for (Answer reply : replies) {
            VBox replyBox = new VBox(5);
            replyBox.setPadding(new Insets(8));
            replyBox.setStyle("-fx-border-color: #f0f0f0; -fx-border-radius: 5; -fx-background-color: #f9f9f9;");
            
            Label replyAuthorLabel = new Label("Reply by: " + reply.getAuthor());
            replyAuthorLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 11px; -fx-text-fill: #555555;");
            
            Label replyTextLabel = new Label(reply.getAnswerText());
            replyTextLabel.setWrapText(true);
            
            replyBox.getChildren().addAll(replyAuthorLabel, replyTextLabel);
            repliesContainer.getChildren().add(replyBox);
        }
        
        if (!replies.isEmpty()) {
            answerBox.getChildren().add(repliesContainer);
        }
        
        return answerBox;
    }
    
    private void showCreateQuestionDialog(ListView<Question> listView) {
        // Create a new dialog
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Ask New Question");
        dialog.setHeaderText("Enter your question details");
        dialog.initModality(Modality.APPLICATION_MODAL);
        
        // Set the button types
        ButtonType submitButtonType = new ButtonType("Submit", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(submitButtonType, ButtonType.CANCEL);
        
        // Create the content layout
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        
        // Create fields
        TextField titleField = new TextField();
        titleField.setPromptText("Question title");
        titleField.setPrefWidth(400);
        
        TextArea descriptionArea = new TextArea();
        descriptionArea.setPromptText("Question description");
        descriptionArea.setPrefRowCount(5);
        descriptionArea.setPrefWidth(400);
        descriptionArea.setWrapText(true); // Enable text wrapping
        
        // Add fields to grid
        grid.add(new Label("Title:"), 0, 0);
        grid.add(titleField, 1, 0);
        grid.add(new Label("Description:"), 0, 1);
        grid.add(descriptionArea, 1, 1);
        
        dialog.getDialogPane().setContent(grid);
        
        // Request focus on the title field by default
        dialog.setOnShown(e -> titleField.requestFocus());
        
        // Process the result
        dialog.showAndWait().ifPresent(buttonType -> {
            if (buttonType == submitButtonType) {
                String title = titleField.getText().trim();
                String description = descriptionArea.getText().trim();
                
                if (title.isEmpty() || description.isEmpty()) {
                    showAlert("Error", "Title and description cannot be empty!");
                    return;
                }
                
                Question newQuestion = new Question(title, description, currentUsername);
                questions.addQuestion(newQuestion);
                
                // Save to database if available
                if (databaseHelper != null) {
                    try {
                        databaseHelper.saveQuestion(newQuestion);
                    } catch (SQLException e) {
                        System.err.println("Error saving question: " + e.getMessage());
                    }
                }
                
                refreshQuestionList(listView);
                showAlert("Success", "Question submitted successfully!");
            }
        });
    }
    
    private void showEditQuestionDialog(Question question, ListView<Question> listView, 
                                      Label titleLabel, Label descriptionLabel) {
        // Create a new dialog
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Edit Question");
        dialog.setHeaderText("Edit your question");
        dialog.initModality(Modality.APPLICATION_MODAL);
        
        // Set the button types
        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);
        
        // Create the content layout
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        
        // Create fields with current values
        TextField editTitleField = new TextField(question.getTitle());
        editTitleField.setPromptText("Question title");
        editTitleField.setPrefWidth(400);
        
        TextArea editDescriptionArea = new TextArea(question.getDescription());
        editDescriptionArea.setPromptText("Question description");
        editDescriptionArea.setPrefRowCount(5);
        editDescriptionArea.setPrefWidth(400);
        editDescriptionArea.setWrapText(true); // Enable text wrapping
        
        // Add fields to grid
        grid.add(new Label("Title:"), 0, 0);
        grid.add(editTitleField, 1, 0);
        grid.add(new Label("Description:"), 0, 1);
        grid.add(editDescriptionArea, 1, 1);
        
        dialog.getDialogPane().setContent(grid);
        
        // Request focus on the title field by default
        dialog.setOnShown(e -> editTitleField.requestFocus());
        
        // Process the result
        dialog.showAndWait().ifPresent(buttonType -> {
            if (buttonType == saveButtonType) {
                String newTitle = editTitleField.getText().trim();
                String newDescription = editDescriptionArea.getText().trim();
                
                if (newTitle.isEmpty() || newDescription.isEmpty()) {
                    showAlert("Error", "Title and description cannot be empty!");
                    return;
                }
                
                question.setTitle(newTitle);
                question.setDescription(newDescription);
                
                // Update the ListView
                refreshQuestionList(listView);
                
                // Update the detail view labels
                titleLabel.setText(newTitle);
                descriptionLabel.setText(newDescription);
                
                showAlert("Success", "Question updated successfully!");
            }
        });
    }
    
    private void showEditAnswerDialog(Answer answer, VBox answersContainer, Question question) {
        // Create a new dialog
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Edit Answer");
        dialog.setHeaderText("Edit your answer");
        dialog.initModality(Modality.APPLICATION_MODAL);
        
        // Set the button types
        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);
        
        // Create the content layout
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        
        // Create field with current value
        TextArea editAnswerArea = new TextArea(answer.getAnswerText());
        editAnswerArea.setPromptText("Your answer");
        editAnswerArea.setPrefRowCount(5);
        editAnswerArea.setPrefWidth(400);
        editAnswerArea.setWrapText(true); // Enable text wrapping
        
        // Add field to grid
        grid.add(new Label("Answer:"), 0, 0);
        grid.add(editAnswerArea, 1, 0);
        
        dialog.getDialogPane().setContent(grid);
        
        // Request focus on the text area by default
        dialog.setOnShown(e -> editAnswerArea.requestFocus());
        
        // Process the result
        dialog.showAndWait().ifPresent(buttonType -> {
            if (buttonType == saveButtonType) {
                String newAnswerText = editAnswerArea.getText().trim();
                
                if (newAnswerText.isEmpty()) {
                    showAlert("Error", "Answer cannot be empty!");
                    return;
                }
                
                answer.setAnswerText(newAnswerText);
                refreshAnswersContainer(answersContainer, question);
                showAlert("Success", "Answer updated successfully!");
            }
        });
    }
    
    private void showAlert(String title, String content) {
        Alert alert = new Alert(
            title.equals("Error") ? Alert.AlertType.ERROR : Alert.AlertType.INFORMATION
        );
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
    
    private boolean showConfirmationDialog(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        
        ButtonType buttonTypeYes = new ButtonType("Yes");
        ButtonType buttonTypeNo = new ButtonType("No");
        
        alert.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo);
        
        return alert.showAndWait().orElse(buttonTypeNo) == buttonTypeYes;
    }
    
    private void showTemporaryNotification(Label notificationLabel, String message) {
        // Set notification message and make it visible
        notificationLabel.setText(message);
        notificationLabel.setVisible(true);
        
        // Create a pause transition for 3 seconds
        PauseTransition pause = new PauseTransition(Duration.seconds(3));
        pause.setOnFinished(event -> {
            notificationLabel.setVisible(false);
        });
        pause.play();
    }
    
    private void refreshQuestionList(ListView<Question> listView) {
        listView.getItems().clear();
        listView.getItems().addAll(questions.getAllQuestions());
    }
}