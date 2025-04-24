package HW2;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import databasePart1.*;

/**
 * The SetupLoginSelectionPage class allows users to choose between setting up a new account
 * or logging into an existing account. It provides buttons for navigation to the respective pages.
 */
public class SetupLoginSelectionPage {
    
    private final DatabaseHelper databaseHelper;

    public SetupLoginSelectionPage(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    public void show(Stage primaryStage) {
        VBox layout = new VBox(20);
        layout.setStyle("-fx-padding: 40; -fx-alignment: center;");
        
        Label welcomeLabel = new Label("Welcome to the CSE360 Discussion Platform");
        welcomeLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
        
        // Create role selection section (only visible when coming from logout)
        CheckBox debugModeBox = new CheckBox("Debug Mode (Change User Role)");
        debugModeBox.setStyle("-fx-font-size: 12px;");
        
        HBox roleSelectionBox = new HBox(10);
        roleSelectionBox.setAlignment(Pos.CENTER);
        roleSelectionBox.setVisible(false);
        
        Label roleLabel = new Label("Select Role:");
        ComboBox<String> roleComboBox = new ComboBox<>(
            FXCollections.observableArrayList("user", "admin", "reviewer", "staff") // Added "staff"
        );
        roleComboBox.setValue("user");
        roleSelectionBox.getChildren().addAll(roleLabel, roleComboBox);
        
        debugModeBox.setOnAction(e -> {
            roleSelectionBox.setVisible(debugModeBox.isSelected());
        });
        
        // Back button
        Button backButton = new Button("Back");
        backButton.setStyle("-fx-background-color: #9E9E9E; -fx-text-fill: white;");
        backButton.setOnAction(e -> {
            new WelcomeLoginPage(databaseHelper).show(primaryStage,null); // Navigate back to the welcome page
        });

        // Buttons with improved styling
        Button setupButton = new Button("Create New Account");
        setupButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px; -fx-min-width: 200px; -fx-min-height: 40px;");
        
        Button loginButton = new Button("Login to Existing Account");
        loginButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-size: 14px; -fx-min-width: 200px; -fx-min-height: 40px;");
        
        setupButton.setOnAction(a -> {
            new SetupAccountPage(databaseHelper).show(primaryStage);
        });
        
        loginButton.setOnAction(a -> {
            if (debugModeBox.isSelected()) {
                // In debug mode, pass the selected role to the login page
                new UserLoginPage(databaseHelper, roleComboBox.getValue()).show(primaryStage);
            } else {
                // Normal mode, no role override
                new UserLoginPage(databaseHelper).show(primaryStage);
            }
        });

        // Add elements to layout
        layout.getChildren().addAll(
            welcomeLabel,
            new Separator(),
            setupButton, 
            loginButton,
            new Separator(),
            debugModeBox,
            roleSelectionBox,
            backButton // Add back button to layout
        );

        Scene scene = new Scene(layout, 800, 500);
        primaryStage.setScene(scene);
        primaryStage.setTitle("CSE360 - Login Selection");
        primaryStage.show();
    }
}