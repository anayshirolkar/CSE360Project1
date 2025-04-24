package HW2;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.List;

import databasePart1.DatabaseHelper;

public class StaffDashboardPage {
    private final DatabaseHelper databaseHelper;
    private final String username;
    private Questions questions;
    private Answers answers;
    private Question currentQuestion = null;
    private Answer currentAnswer = null;

    public StaffDashboardPage(DatabaseHelper databaseHelper, String username) {
        this.databaseHelper = databaseHelper;
        this.username = username;
        this.questions = new Questions();
        this.answers = new Answers();
        
        // Load data from database
        loadDataFromDatabase();
    }

    public void show(Stage primaryStage) {
        BorderPane mainLayout = new BorderPane();
        mainLayout.setPadding(new Insets(20));

        // Top section
        VBox topSection = new VBox(10);
        Label titleLabel = new Label("Staff Dashboard");
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        
        Label usernameLabel = new Label("Logged in as: " + username + " (Staff)");
        usernameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        usernameLabel.setAlignment(Pos.CENTER_RIGHT);
        
        HBox userInfoSection = new HBox(usernameLabel);
        userInfoSection.setAlignment(Pos.CENTER_RIGHT);
        
        topSection.getChildren().addAll(userInfoSection, titleLabel);
        mainLayout.setTop(topSection);

        // Center section - Split pane with questions list and details
        SplitPane centerSplitPane = new SplitPane();
        
        // Left side - Questions list
        VBox questionsSection = new VBox(10);
        questionsSection.setPadding(new Insets(10, 5, 10, 10));
        
        Label questionsLabel = new Label("Questions");
        questionsLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        
        ListView<Question> questionListView = new ListView<>();
        questionListView.setPrefWidth(300);
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
        
        // Populate questions list
        ObservableList<Question> questionItems = FXCollections.observableArrayList(
            questions.getAllQuestions()
        );
        questionListView.setItems(questionItems);
        
        questionsSection.getChildren().addAll(questionsLabel, questionListView);
        
        // Right side - Question details and answers
        BorderPane detailsSection = new BorderPane();
        detailsSection.setPadding(new Insets(10));
        
        // Create a VBox for the question and answers that will be scrollable
        VBox questionAndAnswersBox = new VBox(15);
        
        // Question detail view
        VBox questionDetailView = new VBox(10);
        questionDetailView.setStyle("-fx-border-color: #e0e0e0; -fx-border-width: 0 0 1 0; -fx-padding: 0 0 15 0;");
        
        Label questionTitleLabel = new Label();
        questionTitleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 18px;");
        questionTitleLabel.setWrapText(true);
        
        Label questionAuthorLabel = new Label(); 
        questionAuthorLabel.setStyle("-fx-font-style: italic; -fx-font-size: 12px;");
        
        Label questionDescriptionLabel = new Label();
        questionDescriptionLabel.setWrapText(true);
        
        Button flagQuestionButton = new Button("Flag Question");
        flagQuestionButton.setStyle("-fx-background-color: #FF9800; -fx-text-fill: white;");
        flagQuestionButton.setDisable(true);
        
        questionDetailView.getChildren().addAll(questionTitleLabel, questionAuthorLabel, questionDescriptionLabel, flagQuestionButton);
        questionDetailView.setVisible(false); // Initially hidden
        
        // Answers section
        Label answersLabel = new Label("Answers");
        answersLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        answersLabel.setVisible(false);
        
        VBox answersContainer = new VBox(10);
        answersContainer.setVisible(false);
        
        questionAndAnswersBox.getChildren().addAll(questionDetailView, answersLabel, answersContainer);
        
        // Add the question and answers box to a scroll pane
        ScrollPane scrollPane = new ScrollPane(questionAndAnswersBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        
        detailsSection.setCenter(scrollPane);
        
        // Add to split pane
        centerSplitPane.getItems().addAll(questionsSection, detailsSection);
        centerSplitPane.setDividerPositions(0.3); // 30% left, 70% right
        mainLayout.setCenter(centerSplitPane);
        
        // Question selection handler
        questionListView.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldVal, newVal) -> {
                if (newVal != null) {
                    // Update the current question reference
                    currentQuestion = newVal;
                    
                    // Update question detail view
                    questionTitleLabel.setText(newVal.getTitle());
                    questionDescriptionLabel.setText(newVal.getDescription());
                    questionAuthorLabel.setText("Posted by: " + newVal.getAuthor());
                    questionDetailView.setVisible(true);
                    
                    // Enable flag button
                    flagQuestionButton.setDisable(false);
                    
                    // Show answers section
                    answersLabel.setVisible(true);
                    answersContainer.setVisible(true);
                    
                    // Refresh answers for this question
                    refreshAnswersContainer(answersContainer, newVal);
                } else {
                    questionDetailView.setVisible(false);
                    answersLabel.setVisible(false);
                    answersContainer.setVisible(false);
                    answersContainer.getChildren().clear();
                    flagQuestionButton.setDisable(true);
                }
            }
        );
        
        // Flag question button handler
        flagQuestionButton.setOnAction(e -> {
            if (currentQuestion != null) {
                try {
                    databaseHelper.flagQuestion(currentQuestion.getId());
                    showAlert("Success", "Question flagged for admin review.");
                } catch (SQLException ex) {
                    showAlert("Error", "Failed to flag question: " + ex.getMessage());
                }
            }
        });

        // Bottom section - Back button
        Button backButton = new Button("Back");
        backButton.setOnAction(e -> new UserLoginPage(databaseHelper).show(primaryStage));
        HBox bottomSection = new HBox(backButton);
        bottomSection.setAlignment(Pos.CENTER);
        bottomSection.setPadding(new Insets(10, 0, 0, 0));
        mainLayout.setBottom(bottomSection);

        Scene scene = new Scene(mainLayout, 900, 700);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Staff Dashboard - " + username);
        primaryStage.show();
    }

    private void refreshAnswersContainer(VBox answersContainer, Question question) {
        answersContainer.getChildren().clear();
        List<Answer> questionAnswers = answers.getAnswersForQuestion(question.getId());
        
        if (questionAnswers.isEmpty()) {
            Label noAnswersLabel = new Label("No answers yet for this question.");
            noAnswersLabel.setStyle("-fx-font-style: italic;");
            answersContainer.getChildren().add(noAnswersLabel);
            return;
        }
        
        for (Answer answer : questionAnswers) {
            VBox answerBox = createAnswerBox(answer);
            answersContainer.getChildren().add(answerBox);
        }
    }
    
    private VBox createAnswerBox(Answer answer) {
        VBox answerBox = new VBox(5);
        answerBox.setPadding(new Insets(8));
        answerBox.setStyle("-fx-border-color: #e0e0e0; -fx-border-radius: 5;");
        
        // User info section
        HBox userInfoBox = new HBox(10);
        Label userLabel = new Label("By: " + answer.getAuthor());
        userLabel.setStyle("-fx-font-weight: bold;");
        
        userInfoBox.getChildren().add(userLabel);
        
        // Text content
        Label answerTextLabel = new Label(answer.getAnswerText());
        answerTextLabel.setWrapText(true);
        
        // Flag button
        Button flagButton = new Button("Flag Answer");
        flagButton.setStyle("-fx-background-color: #FF9800; -fx-text-fill: white;");
        flagButton.setOnAction(e -> {
            try {
                databaseHelper.flagAnswer(answer.getId());
                showAlert("Success", "Answer flagged for admin review.");
            } catch (SQLException ex) {
                showAlert("Error", "Failed to flag answer: " + ex.getMessage());
            }
        });
        
        answerBox.getChildren().addAll(userInfoBox, answerTextLabel, flagButton);
        
        // Add reviews section if reviews exist for this answer
        try {
            List<Review> reviews = databaseHelper.getReviewsForAssociatedId(answer.getId());
            
            if (!reviews.isEmpty()) {
                Separator separator = new Separator();
                separator.setPadding(new Insets(5, 0, 5, 0));
                
                Label reviewsLabel = new Label("Reviews (" + reviews.size() + ")");
                reviewsLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #2196F3;");
                
                answerBox.getChildren().addAll(separator, reviewsLabel);
                
                for (Review review : reviews) {
                    VBox reviewBox = createReviewBox(review);
                    answerBox.getChildren().add(reviewBox);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving reviews: " + e.getMessage());
        }
        
        return answerBox;
    }
    
    private VBox createReviewBox(Review review) {
        VBox reviewBox = new VBox(3);
        reviewBox.setPadding(new Insets(5));
        reviewBox.setStyle("-fx-background-color: #f5f5f5; -fx-border-radius: 3;");
        
        // Reviewer info
        HBox headerBox = new HBox(10);
        Label reviewerLabel = new Label("Reviewer: " + review.getReviewer());
        reviewerLabel.setStyle("-fx-font-size: 11px; -fx-font-weight: bold;");
        
        // Create rating stars
        HBox ratingBox = new HBox(2);
        for (int i = 0; i < 5; i++) {
            Label star = new Label(i < review.getRating() ? "★" : "☆");
            star.setStyle("-fx-text-fill: " + (i < review.getRating() ? "#FFD700" : "#AAAAAA") + "; -fx-font-size: 11px;");
            ratingBox.getChildren().add(star);
        }
        
        headerBox.getChildren().addAll(reviewerLabel, ratingBox);
        
        // Review content
        Label contentLabel = new Label(review.getContent());
        contentLabel.setWrapText(true);
        contentLabel.setStyle("-fx-font-size: 12px;");
        
        // Flag button
        Button flagReviewButton = new Button("Flag Review");
        flagReviewButton.setStyle("-fx-background-color: #FF9800; -fx-text-fill: white; -fx-font-size: 10px;");
        flagReviewButton.setOnAction(e -> {
            try {
                databaseHelper.flagReview(review.getId());
                showAlert("Success", "Review flagged for admin review.");
            } catch (SQLException ex) {
                showAlert("Error", "Failed to flag review: " + ex.getMessage());
            }
        });
        
        reviewBox.getChildren().addAll(headerBox, contentLabel, flagReviewButton);
        return reviewBox;
    }
    
    private void loadDataFromDatabase() {
        if (databaseHelper == null) {
            System.err.println("DatabaseHelper is null, can't load data from database");
            return;
        }
        
        try {
            // Load all content
            databaseHelper.loadAllDiscussionContent(questions, answers);
            System.out.println("Successfully loaded " + questions.getAllQuestions().size() + 
                               " questions and " + answers.getAllAnswers().size() + " answers");
        } catch (SQLException e) {
            System.err.println("Error loading data from database: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
