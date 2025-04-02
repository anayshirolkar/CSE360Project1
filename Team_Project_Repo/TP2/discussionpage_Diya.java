package application;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.geometry.Pos;
import java.util.ArrayList;
import java.util.List;

public class discussionpage {

    private static final questions questionManager = new questions();
    private static final answers answerManager = new answers();
    private final List<question> questionsList = new ArrayList<>();

    private VBox questionBoxContainer; // Make it a class level variable

    public void display(Stage primaryStage) {
        VBox mainContainer = new VBox(20);
        mainContainer.setStyle("-fx-alignment: center; -fx-padding: 20;");

        Label forumHeader = new Label("Discussion Forum");
        forumHeader.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        ScrollPane scrollContainer = new ScrollPane();
        questionBoxContainer = new VBox(10); // Initialize here
        questionBoxContainer.setAlignment(Pos.TOP_CENTER);
        scrollContainer.setContent(questionBoxContainer);
        scrollContainer.setFitToWidth(true);

        TextField questionInputField = new TextField();
        questionInputField.setPromptText("Type your question here...");
        Button postQuestionButton = new Button("Post Question");
        postQuestionButton.setOnAction(e -> postQuestion(questionInputField));

        mainContainer.getChildren().addAll(forumHeader, scrollContainer, questionInputField, postQuestionButton);
        Scene scene = new Scene(mainContainer, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Discussion Forum");
        primaryStage.show();

        loadQuestions();  // Load the questions when the page is first shown
    }

    private void postQuestion(TextField questionInputField) {
        String inputText = questionInputField.getText().trim();
        if (inputText.isEmpty()) {
            showErrorAlert("Question cannot be left empty!");
            return;
        }

        question newQuestion = new question(questionManager.getQuestions().size() + 1, inputText);
        questionManager.addQuestion(newQuestion);
        questionsList.add(newQuestion);
        questionInputField.clear();

        loadQuestions(); // Refresh the questions after posting a new question
    }

    private VBox createQuestionCard(question q) {
        VBox questionCard = new VBox(10);
        questionCard.setStyle("-fx-border-color: lightgray; -fx-border-radius: 5; -fx-background-color: #f9f9f9; -fx-padding: 15; -fx-margin: 10 0 10 0; -fx-cursor: hand;");
        questionCard.setPrefWidth(760);

        Label questionLabel = new Label(q.getText());
        questionLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        Button editButton = new Button("Edit");
        Button deleteButton = new Button("Delete");
        editButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-border-radius: 5; -fx-padding: 5;");
        deleteButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-border-radius: 5; -fx-padding: 5;");

        editButton.setOnAction(e -> editQuestion(q));
        deleteButton.setOnAction(e -> removeQuestion(q));

        HBox buttonContainer = new HBox(10, editButton, deleteButton);
        buttonContainer.setAlignment(Pos.CENTER_RIGHT);
        questionCard.getChildren().addAll(questionLabel, buttonContainer);

        VBox answersContainer = new VBox(10);
        answersContainer.setStyle("-fx-background-color: #f1f1f1; -fx-border-radius: 5; -fx-padding: 10;");
        showAnswersForQuestion(q.getId(), answersContainer);

        questionCard.getChildren().add(answersContainer);

        addAnswerSection(q, questionCard);

        return questionCard;
    }

    private void showAnswersForQuestion(int questionId, VBox answersContainer) {
        answersContainer.getChildren().clear();

        for (answer a : answerManager.getAnswers()) {
            if (a.getQuestionId() == questionId) {
                VBox answerCard = createAnswerCard(a);
                answersContainer.getChildren().add(answerCard);
            }
        }
    }

    private VBox createAnswerCard(answer a) {
        VBox answerCard = new VBox(5);
        answerCard.setStyle("-fx-background-color: #e8f4e8; -fx-padding: 10; -fx-border-radius: 5; -fx-margin: 5 0 5 0;");

        Label answerLabel = new Label(a.getText());
        answerLabel.setStyle("-fx-font-size: 13px; -fx-font-style: italic;");

        Button editButton = new Button("Edit");
        Button deleteButton = new Button("Delete");
        editButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-border-radius: 5; -fx-padding: 5;");
        deleteButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-border-radius: 5; -fx-padding: 5;");

        editButton.setOnAction(e -> editAnswer(a));
        deleteButton.setOnAction(e -> removeAnswer(a));

        HBox answerActions = new HBox(10, editButton, deleteButton);
        answerCard.getChildren().addAll(answerLabel, answerActions);
        return answerCard;
    }

    private void addAnswerSection(question q, VBox questionCard) {
        TextField answerInputField = new TextField();
        answerInputField.setPromptText("Type your answer here...");
        answerInputField.setStyle("-fx-padding: 10; -fx-border-radius: 5; -fx-border-color: lightgray;");

        Button postAnswerButton = new Button("Post Answer");
        postAnswerButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-border-radius: 5; -fx-padding: 5;");

        postAnswerButton.setOnAction(e -> submitAnswer(q.getId(), answerInputField));

        HBox answerContainer = new HBox(10, answerInputField, postAnswerButton);
        answerContainer.setAlignment(Pos.CENTER);
        questionCard.getChildren().add(answerContainer);
    }

    void loadQuestions() {
        questionBoxContainer.getChildren().clear();

        for (question q : questionManager.getQuestions()) {
            VBox questionCard = createQuestionCard(q);
            questionBoxContainer.getChildren().add(questionCard);
        }
    }

    private void editQuestion(question q) {
        TextInputDialog dialog = new TextInputDialog(q.getText());
        dialog.setTitle("Edit Question");
        dialog.setHeaderText("Update your question:");
        dialog.showAndWait().ifPresent(newText -> {
            q.setText(newText);
            questionManager.updateQuestion(q);
            loadQuestions();
        });
    }

    private void removeQuestion(question q) {
        if (showConfirmationDialog("Are you sure you want to delete this question?")) {
            questionManager.deleteQuestion(q);
            loadQuestions();
        }
    }

    private void submitAnswer(int questionId, TextField answerInputField) {
        String answerText = answerInputField.getText().trim();
        if (answerText.isEmpty()) {
            showErrorAlert("Answer cannot be left empty!");
            return;
        }

        answer newAnswer = new answer(answerManager.getAnswers().size() + 1, questionId, answerText);
        answerManager.addAnswer(newAnswer);
        answerInputField.clear();

        loadQuestions(); // Refresh the questions after posting a new answer
    }

    private void editAnswer(answer a) {
        TextInputDialog dialog = new TextInputDialog(a.getText());
        dialog.setTitle("Edit Answer");
        dialog.setHeaderText("Update your answer:");
        dialog.showAndWait().ifPresent(newText -> {
            a.setText(newText);
            answerManager.updateAnswer(a);
            loadQuestions();
        });
    }

    private void removeAnswer(answer a) {
        if (showConfirmationDialog("Are you sure you want to delete this answer?")) {
            answerManager.removeAnswer(a);
            loadQuestions();
        }
    }

    private boolean showConfirmationDialog(String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText(null);
        alert.setContentText(message);
        return alert.showAndWait().filter(response -> response == ButtonType.OK).isPresent();
    }

    private void showErrorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
