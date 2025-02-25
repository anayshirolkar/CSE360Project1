package application;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.geometry.Pos;

public class DiscussionForum {

    private final Questions questionManager = new Questions();
    private final Answers answerManager = new Answers();

    public void show(Stage primaryStage) {
        VBox mainLayout = new VBox(20);
        mainLayout.setStyle("-fx-alignment: center; -fx-padding: 20;");

        Label discussionLabel = new Label("Discussion Page");
        discussionLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        VBox questionListView = new VBox(20);
        questionListView.setAlignment(Pos.TOP_CENTER);
        ScrollPane scrollPane = new ScrollPane(questionListView);
        scrollPane.setFitToWidth(true);

        TextField questionField = new TextField();
        questionField.setPromptText("Enter question here...");
        questionField.setMaxWidth(400);

        Button addQuestionButton = new Button("Add Question");
        addQuestionButton.setStyle("-fx-font-size: 14px;");

        TextField answerField = new TextField();
        answerField.setPromptText("Enter answer here...");
        answerField.setMaxWidth(400);

        Button addAnswerButton = new Button("Add Answer");
        addAnswerButton.setStyle("-fx-font-size: 14px;");

        addQuestionButton.setOnAction(e -> {
            String questionText = questionField.getText().trim();
            if (questionText.isEmpty()) {
                showErrorDialog("Your Question cannot be empty");
            } else {
                Question newQuestion = new Question(questionManager.getQuestions().size() + 1, questionText);
                questionManager.addQuestion(newQuestion);
                displayQuestions(questionListView);
                questionField.clear();
            }
        });

        addAnswerButton.setOnAction(e -> {
            String answerText = answerField.getText().trim();
            if (answerText.isEmpty()) {
                showErrorDialog("Your Answer cannot be empty");
            } else {
                Answer newAnswer = new Answer(answerManager.getAnswers().size() + 1, 1, answerText);
                answerManager.addAnswer(newAnswer);
                displayQuestions(questionListView);
                answerField.clear();
            }
        });

        mainLayout.getChildren().addAll(discussionLabel, scrollPane, questionField, addQuestionButton, answerField, addAnswerButton);

        Scene discussionScene = new Scene(mainLayout, 800, 600);
        primaryStage.setScene(discussionScene);
        primaryStage.setTitle("Discussion Page");
        primaryStage.show();

        displayQuestions(questionListView);
    }

    private void displayQuestions(VBox questionListView) {
        questionListView.getChildren().clear();

        for (Question q : questionManager.getQuestions()) {
            VBox questionBox = new VBox(10);
            questionBox.setStyle("-fx-border-color: lightgray; -fx-border-width: 1px; -fx-padding: 10;");

            Label questionLabel = new Label(q.getText());
            questionLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

            Button deleteButton = new Button("Delete");
            deleteButton.setStyle("-fx-font-size: 12px;");
            deleteButton.setOnAction(e -> {
                questionManager.deleteQuestion(q);
                displayQuestions(questionListView);
            });

            Button editButton = new Button("Edit");
            editButton.setStyle("-fx-font-size: 12px;");
            editButton.setOnAction(e -> {
                TextInputDialog dialog = new TextInputDialog(q.getText());
                dialog.setTitle("Edit Question");
                dialog.setHeaderText("Edit Question Text:");
                dialog.showAndWait().ifPresent(newText -> {
                    q.setText(newText);
                    questionManager.updateQuestion(q);
                    displayQuestions(questionListView);
                });
            });

            HBox editDeleteBox = new HBox(10, editButton, deleteButton);
            editDeleteBox.setAlignment(Pos.CENTER_RIGHT);

            questionBox.getChildren().addAll(questionLabel, editDeleteBox);
            displayAnswersForQuestion(q.getId(), questionBox);
            questionListView.getChildren().add(questionBox);
        }
    }

    private void displayAnswersForQuestion(int questionId, VBox questionBox) {
        VBox answerBox = new VBox(10);
        questionBox.getChildren().add(answerBox);

        for (Answer a : answerManager.getAnswers()) {
            if (a.getQuestionId() == questionId) {
                Label answerLabel = new Label(a.getText());
                answerLabel.setStyle("-fx-font-size: 14px;");
                Button editAnswerButton = new Button("Edit");
                editAnswerButton.setStyle("-fx-font-size: 12px;");
                editAnswerButton.setOnAction(e -> {
                    TextInputDialog dialog = new TextInputDialog(a.getText());
                    dialog.setTitle("Edit Answer");
                    dialog.setHeaderText("Edit Answer Text:");
                    dialog.showAndWait().ifPresent(newText -> {
                        a.setText(newText);
                        answerLabel.setText(newText); 
                        answerManager.updateAnswer(a);
                    });
                });

                Button deleteAnswerButton = new Button("Delete");
                deleteAnswerButton.setStyle("-fx-font-size: 12px;");
                deleteAnswerButton.setOnAction(e -> {
                    answerManager.removeAnswer(a);
                    answerBox.getChildren().remove(answerLabel.getParent());
                });

                HBox answerItemBox = new HBox(10, answerLabel, editAnswerButton, deleteAnswerButton);
                answerItemBox.setAlignment(Pos.CENTER_LEFT);
                answerBox.getChildren().add(answerItemBox);
            }
        }
    }

    private void showErrorDialog(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Input Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
