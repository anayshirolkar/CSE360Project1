package HW2;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * This page displays a simple welcome message for the user and a button to navigate to the Discussion Page.
 */

public class UserHomePage {

    public void show(Stage primaryStage) {
        VBox layout = new VBox();
        layout.setStyle("-fx-alignment: center; -fx-padding: 20;");
        
        // Label to display Hello user
        Label userLabel = new Label("Hello, User!");
        userLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        
        // Button to navigate to the Discussion Page
        Button discussionButton = new Button("Go to Discussion Page");
        discussionButton.setStyle("-fx-font-size: 14px; -fx-background-color: #4CAF50; -fx-text-fill: white;");
        
        // Action handler for button
        discussionButton.setOnAction(e -> {
            // Create the DiscussionPage instance and show it
            DiscussionPage discussionPage = new DiscussionPage();
            discussionPage.show(primaryStage);
        });

        // Add label and button to layout
        layout.getChildren().addAll(userLabel, discussionButton);

        // Create scene and set it on the primary stage
        Scene userScene = new Scene(layout, 800, 400);
        primaryStage.setScene(userScene);
        primaryStage.setTitle("User Page");
    }
}
