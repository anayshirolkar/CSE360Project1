package HW2;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.SQLException;

import databasePart1.*;

/**
 * The UserLoginPage class provides a login interface for users to access their accounts.
 * It validates the user's credentials and navigates to the appropriate page upon successful login.
 */
public class UserLoginPage {
    
    private final DatabaseHelper databaseHelper;
    private String debugRole = null; // Role override for debug mode

    public UserLoginPage(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }
    
    // New constructor with role parameter for debug mode
    public UserLoginPage(DatabaseHelper databaseHelper, String debugRole) {
        this.databaseHelper = databaseHelper;
        this.debugRole = debugRole;
    }

    public void show(Stage primaryStage) {
        VBox layout = new VBox(15);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(30));
        
        Label titleLabel = new Label("Login to Your Account");
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        
        // Input field for the user's userName, password
        TextField userNameField = new TextField();
        userNameField.setPromptText("Enter userName");
        userNameField.setMaxWidth(300);

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter Password");
        passwordField.setMaxWidth(300);
        
        // Display debug mode notice if active
        Label debugModeLabel = null;
        if (debugRole != null) {
            debugModeLabel = new Label("DEBUG MODE: Will log in as " + debugRole + " role");
            debugModeLabel.setStyle("-fx-text-fill: #ff6600; -fx-font-style: italic;");
        }
        
        // Label to display error messages
        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");

        Button loginButton = new Button("Login");
        loginButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-min-width: 200px;");
        
        Button backButton = new Button("Back");
        backButton.setStyle("-fx-background-color: #9E9E9E; -fx-text-fill: white;");
        backButton.setOnAction(e -> {
            new SetupLoginSelectionPage(databaseHelper).show(primaryStage);
        });
        
        loginButton.setOnAction(a -> {
            // Retrieve user inputs
            String userName = userNameField.getText();
            String password = passwordField.getText();
            
            // Basic validation
            if (userName.length() < 4) {
                errorLabel.setText("Username too short");
            }
            else if (userName.length() > 16) {
                errorLabel.setText("Username too long");
            }
            else if(password.length() < 8) {
                errorLabel.setText("Password too short");
            }
            else {
                try {
                    User user = new User(userName, password, "");
                    WelcomeLoginPage welcomeLoginPage = new WelcomeLoginPage(databaseHelper);
                    
                    // Get the user's role from database
                    String role = databaseHelper.getUserRole(userName);
                    
                    if(role != null) {
                        // Override role if in debug mode
                        if (debugRole != null) {
                            role = debugRole;
                        }
                        
                        user.setRole(role);
                        if(databaseHelper.login(user)) {
                            welcomeLoginPage.show(primaryStage, user);
                        }
                        else {
                            // Display an error if the login fails
                            errorLabel.setText("Error logging in - incorrect password");
                        }
                    }
                    else {
                        // Display an error if the account does not exist
                        errorLabel.setText("User account doesn't exist");
                    }
                    
                } catch (SQLException e) {
                    System.err.println("Database error: " + e.getMessage());
                    e.printStackTrace();
                    errorLabel.setText("Database error: " + e.getMessage());
                }
            }
        });

        // Add components to layout
        layout.getChildren().addAll(titleLabel, userNameField, passwordField);
        
        if (debugModeLabel != null) {
            layout.getChildren().add(debugModeLabel);
        }
        
        layout.getChildren().addAll(errorLabel, loginButton, backButton);

        primaryStage.setScene(new Scene(layout, 800, 500));
        primaryStage.setTitle("User Login");
        primaryStage.show();
    }
}