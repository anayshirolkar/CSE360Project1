package HW2;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import databasePart1.DatabaseHelper;

/**
 * This class represents a page where instructors can review a student's questions and answers
 * before deciding on their reviewer request.
 */
public class StudentActivityReviewPage {
    private final DatabaseHelper databaseHelper;
    private final String studentUsername;
    private Questions questions;
    private Answers answers;

    public StudentActivityReviewPage(DatabaseHelper databaseHelper, String studentUsername) {
        this.databaseHelper = databaseHelper;
        this.studentUsername = studentUsername;
        this.questions = new Questions();
        this.answers = new Answers();
        
        loadStudentData();
    }
    
    /**
     * Loads the student's questions and answers from the database
     */
    private void loadStudentData() {
        try {
            // Load questions from the database
            List<Question> studentQuestions = databaseHelper.getQuestionsByUser(studentUsername);
            for (Question question : studentQuestions) {
                questions.addQuestion(question);
            }
            
            // Load answers from the database
            List<Answer> studentAnswers = databaseHelper.getAnswersByUser(studentUsername);
            for (Answer answer : studentAnswers) {
                answers.addAnswer(answer);
            }
        } catch (SQLException e) {
            // If there's an error, we'll just have empty lists
            System.err.println("Error loading student data: " + e.getMessage());
        }
    }

    /**
     * Shows the student activity review page
     * 
     * @param primaryStage The primary stage
     * @param onDecision A callback to be executed when a decision is made (approve/reject)
     */
    public void show(Stage primaryStage, Runnable onDecision) {
        BorderPane mainLayout = new BorderPane();
        mainLayout.setPadding(new Insets(20));
        
        // Top section
        VBox topSection = new VBox(10);
        Label titleLabel = new Label("Review Student Activity: " + studentUsername);
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        
        // Get request details
        String requestDate = "Unknown";
        try {
            Map<String, Object> requestDetails = databaseHelper.getReviewerRequestDetails(studentUsername);
            if (requestDetails.containsKey("requestDate")) {
                requestDate = requestDetails.get("requestDate").toString();
            }
        } catch (SQLException e) {
            System.err.println("Error getting request details: " + e.getMessage());
        }
        
        Label requestLabel = new Label("Request submitted on: " + requestDate);
        requestLabel.setStyle("-fx-font-style: italic;");
        
        topSection.getChildren().addAll(titleLabel, requestLabel);
        topSection.setAlignment(Pos.CENTER);
        mainLayout.setTop(topSection);
        
        // Center section - TabPane
        TabPane tabPane = new TabPane();
        
        // Questions tab
        Tab questionsTab = new Tab("Questions (" + questions.getAllQuestions().size() + ")");
        questionsTab.setContent(createQuestionsPane());
        questionsTab.setClosable(false);
        
        // Answers tab
        Tab answersTab = new Tab("Answers (" + answers.getAllAnswers().size() + ")");
        answersTab.setContent(createAnswersPane());
        answersTab.setClosable(false);
        
        tabPane.getTabs().addAll(questionsTab, answersTab);
        mainLayout.setCenter(tabPane);
        
        // Bottom section - Decision buttons
        HBox bottomSection = new HBox(20);
        bottomSection.setAlignment(Pos.CENTER);
        bottomSection.setPadding(new Insets(20, 0, 0, 0));
        
        Button approveButton = new Button("Approve Reviewer Status");
        approveButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px;");
        approveButton.setPrefWidth(200);
        
        Button rejectButton = new Button("Reject Request");
        rejectButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-size: 14px;");
        rejectButton.setPrefWidth(200);
        
        Button backButton = new Button("Back");
        backButton.setPrefWidth(100);
        
        // Action handlers
        approveButton.setOnAction(e -> {
            try {
                databaseHelper.approveReviewerRequestByUsername(studentUsername);
                showAlert("Success", "Reviewer status approved for " + studentUsername);
                onDecision.run(); // Execute callback
                primaryStage.close(); // Close this window
            } catch (SQLException ex) {
                showAlert("Error", "Failed to approve request: " + ex.getMessage());
            }
        });
        
        rejectButton.setOnAction(e -> {
            try {
                databaseHelper.rejectReviewerRequest(studentUsername);
                showAlert("Success", "Request rejected for " + studentUsername);
                onDecision.run(); // Execute callback
                primaryStage.close(); // Close this window
            } catch (SQLException ex) {
                showAlert("Error", "Failed to reject request: " + ex.getMessage());
            }
        });
        
        backButton.setOnAction(e -> primaryStage.close());
        
        bottomSection.getChildren().addAll(backButton, approveButton, rejectButton);
        mainLayout.setBottom(bottomSection);
        
        // Create and show scene
        Scene scene = new Scene(mainLayout, 800, 600);
        Stage dialog = new Stage();
        dialog.setTitle("Review Student Activity");
        dialog.setScene(scene);
        dialog.initOwner(primaryStage);
        dialog.show();
    }
    
    /**
     * Creates the pane displaying the student's questions
     */
    private Pane createQuestionsPane() {
        BorderPane pane = new BorderPane();
        
        // Create the list view for questions
        ListView<Question> questionsListView = new ListView<>();
        questionsListView.setCellFactory(new Callback<ListView<Question>, ListCell<Question>>() {
            @Override
            public ListCell<Question> call(ListView<Question> listView) {
                return new ListCell<Question>() {
                    @Override
                    protected void updateItem(Question item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setText(null);
                        } else {
                            setText(item.getTitle());
                        }
                    }
                };
            }
        });
        
        // Add questions to the list
        ObservableList<Question> items = FXCollections.observableArrayList(questions.getAllQuestions());
        questionsListView.setItems(items);
        
        // Question details section
        VBox detailsPane = new VBox(10);
        detailsPane.setPadding(new Insets(0, 0, 0, 10));
        detailsPane.setVisible(false);
        
        Label titleLabel = new Label();
        titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        titleLabel.setWrapText(true);
        
        Label descriptionLabel = new Label();
        descriptionLabel.setWrapText(true);
        
        detailsPane.getChildren().addAll(
            new Label("Title:"), titleLabel,
            new Label("Description:"), descriptionLabel
        );
        
        // Handle selection events
        questionsListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                detailsPane.setVisible(true);
                titleLabel.setText(newVal.getTitle());
                descriptionLabel.setText(newVal.getDescription());
            } else {
                detailsPane.setVisible(false);
            }
        });
        
        // No questions message
        if (items.isEmpty()) {
            Label noQuestionsLabel = new Label("This student hasn't asked any questions yet.");
            noQuestionsLabel.setStyle("-fx-font-style: italic;");
            pane.setCenter(noQuestionsLabel);
        } else {
            // Split pane for list and details
            SplitPane splitPane = new SplitPane();
            splitPane.getItems().addAll(questionsListView, detailsPane);
            splitPane.setDividerPositions(0.4);
            pane.setCenter(splitPane);
        }
        
        return pane;
    }
    
    /**
     * Creates the pane displaying the student's answers
     */
    private Pane createAnswersPane() {
        BorderPane pane = new BorderPane();
        
        // Create the list view for answers
        ListView<Answer> answersListView = new ListView<>();
        answersListView.setCellFactory(new Callback<ListView<Answer>, ListCell<Answer>>() {
            @Override
            public ListCell<Answer> call(ListView<Answer> listView) {
                return new ListCell<Answer>() {
                    @Override
                    protected void updateItem(Answer item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setText(null);
                        } else {
                            String preview = item.getAnswerText();
                            if (preview.length() > 50) {
                                preview = preview.substring(0, 47) + "...";
                            }
                            setText(preview);
                        }
                    }
                };
            }
        });
        
        // Add answers to the list
        ObservableList<Answer> items = FXCollections.observableArrayList(answers.getAllAnswers());
        answersListView.setItems(items);
        
        // Answer details section
        VBox detailsPane = new VBox(10);
        detailsPane.setPadding(new Insets(0, 0, 0, 10));
        detailsPane.setVisible(false);
        
        Label questionLabel = new Label();
        questionLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        questionLabel.setWrapText(true);
        
        Label answerTextLabel = new Label();
        answerTextLabel.setWrapText(true);
        
        Label typeLabel = new Label();
        typeLabel.setStyle("-fx-font-style: italic;");
        
        detailsPane.getChildren().addAll(
            new Label("Related Question:"), questionLabel,
            new Label("Answer:"), answerTextLabel,
            typeLabel
        );
        
        // Handle selection events
        answersListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                detailsPane.setVisible(true);
                answerTextLabel.setText(newVal.getAnswerText());
                
                // Find the related question
                Question relatedQuestion = questions.getQuestionById(newVal.getQuestionId());
                if (relatedQuestion != null) {
                    questionLabel.setText(relatedQuestion.getTitle());
                } else {
                    questionLabel.setText("[Question not found: " + newVal.getQuestionId() + "]");
                }
                
                // Check if it's a reply
                if (newVal.getParentAnswerId() != null) {
                    typeLabel.setText("This is a reply to another answer");
                } else {
                    typeLabel.setText("This is a direct answer to the question");
                }
            } else {
                detailsPane.setVisible(false);
            }
        });
        
        // No answers message
        if (items.isEmpty()) {
            Label noAnswersLabel = new Label("This student hasn't provided any answers yet.");
            noAnswersLabel.setStyle("-fx-font-style: italic;");
            pane.setCenter(noAnswersLabel);
        } else {
            // Split pane for list and details
            SplitPane splitPane = new SplitPane();
            splitPane.getItems().addAll(answersListView, detailsPane);
            splitPane.setDividerPositions(0.4);
            pane.setCenter(splitPane);
        }
        
        return pane;
    }
    
    /**
     * Shows an alert dialog
     */
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}