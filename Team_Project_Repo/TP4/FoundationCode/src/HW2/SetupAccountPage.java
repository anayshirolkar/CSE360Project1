package HW2;
import passwordEvaluationTestbed.*;

import userNameRecognizerTestbed.*;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.SQLException;

import databasePart1.*;

/**
 * SetupAccountPage class handles the account setup process for new users.
 * Users provide their userName, password, and a valid invitation code to register.
 */
public class SetupAccountPage {
	
    private final DatabaseHelper databaseHelper;
    private static final UserNameRecognizer userNameRecognizer = new UserNameRecognizer();
    private static final PasswordEvaluator passwordsEvaluator = new PasswordEvaluator();
    
    // DatabaseHelper to handle database operations.
    public SetupAccountPage(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    /**
     * Displays the Setup Account page in the provided stage.
     * @param primaryStage The primary stage where the scene will be displayed.
     */
    public void show(Stage primaryStage) {
    	// Input fields for userName, password, and invitation code
        TextField userNameField = new TextField();
        userNameField.setPromptText("Enter userName");
        userNameField.setMaxWidth(250);

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter Password");
        passwordField.setMaxWidth(250);
        
        TextField inviteCodeField = new TextField();
        inviteCodeField.setPromptText("Enter InvitationCode");
        inviteCodeField.setMaxWidth(250);

        // Add role selection dropdown
        Label roleLabel = new Label("Select Role:");
        ComboBox<String> roleComboBox = new ComboBox<>(
            FXCollections.observableArrayList("user", "staff")
        );
        roleComboBox.setValue("user"); // Default role
        
        // Label to display error messages for invalid input or registration issues
        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");
        

        Button setupButton = new Button("Setup");
        Button backButton = new Button("Back"); // Add back button

        setupButton.setOnAction(a -> {
        	// Retrieve user input
            String userName = userNameField.getText();
            String password = passwordField.getText();
            String code = inviteCodeField.getText();
            String selectedRole = roleComboBox.getValue(); // Get selected role
            
            try {
            	// Check if the user already exists
            	if(!databaseHelper.doesUserExist(userName)) {
            		
            		// Validate the username
            		String errMsg = userNameRecognizer.checkForValidUserName(userName);
            		
            		// Validate the password
            		errMsg += passwordsEvaluator.evaluatePassword(password);
            		
            		if (!errMsg.trim().isEmpty()) {
            			errorLabel.setText(errMsg);
            		}
            		else {
            		
            			// Validate the invitation code
            			if(databaseHelper.validateInvitationCode(code)) {
            				// Create a new user with the selected role and register them in the database
            				User user = new User(userName, password, selectedRole);
            				databaseHelper.registerUser(userName, password, selectedRole);
		                
            				// Navigate to the appropriate page based on the role
            				if ("staff".equals(selectedRole)) {
            					new StaffDashboardPage(databaseHelper, userName).show(primaryStage);
            				} else {
            					new WelcomeLoginPage(databaseHelper).show(primaryStage, user);
            				}
            			}
            			else {
            				errorLabel.setText("Please enter a valid invitation code.");
            			}
            		}
            	}
            	else {
            		errorLabel.setText("This username is taken! Please use another to set up an account.");
            	}
            	
            } catch (SQLException e) {
                System.err.println("Database error: " + e.getMessage());
                e.printStackTrace();
            }
        });

        backButton.setOnAction(a -> {
            // Navigate back to the login page
            new UserLoginPage(databaseHelper).show(primaryStage);
        });

        VBox layout = new VBox(10);
        layout.setStyle("-fx-padding: 20; -fx-alignment: center;");
        
        // Add role selection to the layout
        HBox roleBox = new HBox(10);
        roleBox.setAlignment(Pos.CENTER);
        roleBox.getChildren().addAll(roleLabel, roleComboBox);
        
        // Add buttons to a horizontal box
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.getChildren().addAll(setupButton, backButton); // Add both buttons
        
        layout.getChildren().addAll(userNameField, passwordField, inviteCodeField, roleBox, buttonBox, errorLabel);

        primaryStage.setScene(new Scene(layout, 800, 400));
        primaryStage.setTitle("Account Setup");
        primaryStage.show();
    }
}