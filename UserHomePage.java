package HW2;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class UserHomePage {

    public void show(Stage primaryStage) {
        VBox layout = new VBox(10);
        layout.setStyle("-fx-alignment: center; -fx-padding: 20;");
        
        // Label to display "Hello, User"
        Label userLabel = new Label("Hello, User!");
        userLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        
        // Button to go to the discussion page
        Button discussionButton = new Button("Go to Discussion Page");
        discussionButton.setStyle("-fx-font-size: 14px;");
        
        // Action for the button click to go to the DiscussionPage
        discussionButton.setOnAction(e -> {
            DiscussionPage discussionPage = new DiscussionPage();
            discussionPage.show(primaryStage);
        });
        
        // Add label and button to the layout
        layout.getChildren().addAll(userLabel, discussionButton);
        
        // Create the scene and set it on the stage
        Scene userScene = new Scene(layout, 800, 400);
        primaryStage.setScene(userScene);
        primaryStage.setTitle("User Page");
        primaryStage.show();
    }
}
