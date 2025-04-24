package HW2;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

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
            // Clear previous error message
            errorLabel.setText("");
            
            // Retrieve user inputs
            String userName = userNameField.getText().trim();
            String password = passwordField.getText();
            
            // Basic validation
            if (userName.isEmpty()) {
                errorLabel.setText("Please enter a username");
                return;
            }
            if (password.isEmpty()) {
                errorLabel.setText("Please enter a password");
                return;
            }
            if (userName.length() < 4) {
                errorLabel.setText("Username must be at least 4 characters long");
                return;
            }
            if (userName.length() > 16) {
                errorLabel.setText("Username cannot be longer than 16 characters");
                return;
            }
            if(password.length() < 8) {
                errorLabel.setText("Password must be at least 8 characters long");
                return;
            }

            try {
                // First check if user exists
                if (!databaseHelper.doesUserExist(userName)) {
                    errorLabel.setText("User account doesn't exist");
                    return;
                }

                // Get the user's role
                String role = databaseHelper.getUserRole(userName);
                if (role == null) {
                    errorLabel.setText("Error: Unable to determine user role");
                    return;
                }

                // Override role if in debug mode
                if (debugRole != null) {
                    role = debugRole;
                }

                // Create user object
                User user = new User(userName, password, role);

                // Verify password
                if (databaseHelper.verifyPassword(userName, password)) {
                    // Successful login
                    if ("staff".equals(role)) {
                        new StaffDashboardPage(databaseHelper, userName).show(primaryStage);
                    } else {
                        new WelcomeLoginPage(databaseHelper).show(primaryStage, user);
                    }
                } else {
                    errorLabel.setText("Incorrect password");
                }
                
            } catch (SQLException e) {
                System.err.println("Database error during login: " + e.getMessage());
                e.printStackTrace();
                errorLabel.setText("Login failed: Database error occurred");
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