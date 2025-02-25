package application;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class UserHomePage {

    private DiscussionForum discussionPage = new DiscussionForum();  // Create an instance of DiscussionPage

    public void show(Stage primaryStage) {
        VBox layout = new VBox(20);
        layout.setStyle("-fx-alignment: center; -fx-padding: 20;");

        // Label to display Hello user
        Label userLabel = new Label("Hello, User!");
        userLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        // Create a button to go to the discussion page
        Button goToDiscussionButton = new Button("Go to Discussion Page");
        goToDiscussionButton.setStyle("-fx-font-size: 14px;");

        // Set button action to go to the discussion page
        goToDiscussionButton.setOnAction(e -> {
            discussionPage.show(primaryStage);  // Navigate to DiscussionPage
        });

        layout.getChildren().addAll(userLabel, goToDiscussionButton);
        Scene userScene = new Scene(layout, 800, 400);

        // Set the scene to primary stage
        primaryStage.setScene(userScene);
        primaryStage.setTitle("User Page");
    }
}
