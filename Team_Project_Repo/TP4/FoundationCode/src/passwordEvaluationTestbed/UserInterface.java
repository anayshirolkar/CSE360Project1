
package passwordEvaluationTestbed;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

/**
 * <p> Title: UserInterface Class. </p>
 * 
 * <p> Description: The Java/FX-based user interface testbed to develop and test UI ideas.</p>
 * 
 * <p> Copyright: Lynn Robert Carter © 2022 </p>
 * 
 * @author Lynn Robert Carter
 * 
 * @version 1.00		2022-02-21 The JavaFX-based GUI for the implementation of a testbed
 *  
 */

public class UserInterface {
	
	/**********************************************************************************************

	Attributes
	
	**********************************************************************************************/
	
	// These are the application values required by the user interface
	private Label label_ApplicationTitle = new Label("Password Evaluation Testbed");
	private Label label_Password = new Label("Enter the password here");
	private TextField text_Password = new TextField();
	private Label label_errPassword = new Label("");	
    private Label noInputFound = new Label("");
	private TextFlow errPassword;
    private Text errPasswordPart1 = new Text();
    private Text errPasswordPart2 = new Text();
    private Label errPasswordPart3 = new Label("");
    private Label validPassword = new Label("");
    private Label label_Requirements = new Label("A valid password must satisfy the following requirements:");
    private Label label_UpperCase = new Label("At least one upper case letter");
    private Label label_LowerCase = new Label("At least one lower case letter");
    private Label label_NumericDigit = new Label("At least one numeric digit");
    private Label label_SpecialChar = new Label("At least one special character");
    private Label label_LongEnough = new Label("At least eight characters");
	
	/**********************************************************************************************

	Constructors
	
	**********************************************************************************************/

	/**********
	 * This method initializes all of the elements of the graphical user interface. These assignments
	 * determine the location, size, font, color, and change and event handlers for each GUI object.
	 */
	public UserInterface(Pane theRoot) {
		
		// Label theScene with the name of the testbed, centered at the top of the pane
		setupLabelUI(label_ApplicationTitle, "Arial", 24, PasswordEvaluationGUITestbed.WINDOW_WIDTH, 
				Pos.CENTER, 0, 10);
		
		// Label the password input field with a title just above it, left aligned
		setupLabelUI(label_Password, "Arial", 14, PasswordEvaluationGUITestbed.WINDOW_WIDTH-10, 
				Pos.BASELINE_LEFT, 10, 50);
		
		// Establish the text input operand field and when anything changes in the password,
		// the code will process the entire input to ensure that it is valid or in error.
		setupTextUI(text_Password, "Arial", 18, PasswordEvaluationGUITestbed.WINDOW_WIDTH-20,
				Pos.BASELINE_LEFT, 10, 70, true);
		text_Password.textProperty().addListener((observable, oldValue, newValue) 
				-> {setPassword(); });
		
		// Establish an error message for when there is no input
		noInputFound.setTextFill(Color.RED);
		noInputFound.setAlignment(Pos.BASELINE_LEFT);
		setupLabelUI(noInputFound, "Arial", 14, PasswordEvaluationGUITestbed.WINDOW_WIDTH-10, 
				Pos.BASELINE_LEFT, 10, 110);		
		
		// Establish an error message for the password, left aligned
		label_errPassword.setTextFill(Color.RED);
		label_errPassword.setAlignment(Pos.BASELINE_RIGHT);
		setupLabelUI(label_errPassword, "Arial", 14,  
						PasswordEvaluationGUITestbed.WINDOW_WIDTH-150-10, Pos.BASELINE_LEFT, 22, 126);		
		
		// Establish the Go button, position it, and link it to methods to accomplish its work
		//setupButtonUI(button_Go, "Symbol", 24, BUTTON_WIDTH, Pos.BASELINE_LEFT, 
		//				PasswordEvaluationTestbed.WINDOW_WIDTH/2-BUTTON_WIDTH, 130);
		//button_Go.setOnAction((event) -> { performGo(); });
		
		// Error Message components for the Password
		errPasswordPart1.setFill(Color.BLACK);
	    errPasswordPart1.setFont(Font.font("Arial", FontPosture.REGULAR, 18));
	    errPasswordPart2.setFill(Color.RED);
	    errPasswordPart2.setFont(Font.font("Arial", FontPosture.REGULAR, 24));
	    errPassword = new TextFlow(errPasswordPart1, errPasswordPart2);
		errPassword.setMinWidth(PasswordEvaluationGUITestbed.WINDOW_WIDTH-10); 
		errPassword.setLayoutX(22);  
		errPassword.setLayoutY(100);
		
		setupLabelUI(errPasswordPart3, "Arial", 14, 200, Pos.BASELINE_LEFT, 20, 125);	

		
		// Position the requirements assessment display
	    setupLabelUI(label_Requirements, "Arial", 16, PasswordEvaluationGUITestbed.WINDOW_WIDTH-10, 
				Pos.BASELINE_LEFT, 10, 200);
	    setupLabelUI(label_UpperCase, "Arial", 14, PasswordEvaluationGUITestbed.WINDOW_WIDTH-10, 
				Pos.BASELINE_LEFT, 30, 230);
	    label_UpperCase.setTextFill(Color.RED);
	    	    label_LowerCase.setText("At least one lower case letter");

	    	    setupLabelUI(label_LowerCase, "Arial", 14, PasswordEvaluationGUITestbed.WINDOW_WIDTH-10, 
				Pos.BASELINE_LEFT, 30, 260);
	    label_LowerCase.setTextFill(Color.RED);
	    
	    setupLabelUI(label_NumericDigit, "Arial", 14, PasswordEvaluationGUITestbed.WINDOW_WIDTH-10, 
				Pos.BASELINE_LEFT, 30, 290);
	    label_NumericDigit.setTextFill(Color.RED);
	    
	    setupLabelUI(label_SpecialChar, "Arial", 14, PasswordEvaluationGUITestbed.WINDOW_WIDTH-10, 
				Pos.BASELINE_LEFT, 30, 320);
	    label_SpecialChar.setTextFill(Color.RED);
	    
	    setupLabelUI(label_LongEnough, "Arial", 14, PasswordEvaluationGUITestbed.WINDOW_WIDTH-10, 
				Pos.BASELINE_LEFT, 30, 350);
	    label_LongEnough.setTextFill(Color.RED);
		resetAssessments();
		
		// Setup the valid Password message
		validPassword.setTextFill(Color.GREEN);
		validPassword.setAlignment(Pos.BASELINE_RIGHT);
		setupLabelUI(validPassword, "Arial", 18,  
						PasswordEvaluationGUITestbed.WINDOW_WIDTH-150-10, Pos.BASELINE_LEFT, 10, 380);				

		// Place all of the just-initialized GUI elements into the pane, whether they have text or not
		theRoot.getChildren().addAll(label_ApplicationTitle, label_Password, text_Password, 
				noInputFound, label_errPassword, errPassword, errPasswordPart3, validPassword,
				label_Requirements, label_UpperCase, label_LowerCase, label_NumericDigit,
				label_SpecialChar, label_LongEnough);
	}
	
	/**********
	 * Private local method to initialize the standard fields for a label
	 */
	private void setupLabelUI(Label l, String ff, double f, double w, Pos p, double x, double y){
		l.setFont(Font.font(ff, f));
		l.setMinWidth(w);
		l.setAlignment(p);
		l.setLayoutX(x);
		l.setLayoutY(y);		
	}
	
	/**********
	 * Private local method to initialize the standard fields for a text field
	 */
	private void setupTextUI(TextField t, String ff, double f, double w, Pos p, double x, double y, boolean e){
		t.setFont(Font.font(ff, f));
		t.setMinWidth(w);
		t.setMaxWidth(w);
		t.setAlignment(p);
		t.setLayoutX(x);
		t.setLayoutY(y);		
		t.setEditable(e);
	}	
	
	/**********************************************************************************************

	User Interface Actions
	
	**********************************************************************************************/

	private void setPassword() {
		label_errPassword.setText("");
		noInputFound.setText("");
		errPasswordPart1.setText("");
		errPasswordPart2.setText("");
		validPassword.setText("");
		resetAssessments();
		performEvaluation();			
	}
	
	
	private void performEvaluation() {
		String inputText = text_Password.getText();
		if (inputText.isEmpty())
		    noInputFound.setText("No input text found!");
		else
		{
			String errMessage = PasswordEvaluator.evaluatePassword(inputText);
			updateFlags();
			if (errMessage != "") {
				System.out.println(errMessage);
				
				label_errPassword.setText(PasswordEvaluator.passwordErrorMessage);
				if (PasswordEvaluator.passwordIndexofError <= -1) return;
				String input = PasswordEvaluator.passwordInput;
				errPasswordPart1.setText(input.substring(0, 
						PasswordEvaluator.passwordIndexofError));
				errPasswordPart2.setText("\u21EB");
				validPassword.setTextFill(Color.RED);
				errPasswordPart3.setText("The red arrow points at the character causing the error!");
				validPassword.setText("Failure! The password is not valid.");
			}
			else if (PasswordEvaluator.foundUpperCase && PasswordEvaluator.foundLowerCase &&
					PasswordEvaluator.foundNumericDigit && PasswordEvaluator.foundSpecialChar &&
					PasswordEvaluator.foundLongEnough) {
				
				System.out.println("Success! The password satisfies the requirements.");
				validPassword.setTextFill(Color.GREEN);
				validPassword.setText("Success! The password satisfies the requirements.");
			} else {
				validPassword.setTextFill(Color.RED);
				validPassword.setText("The password as currently entered is not yet valid.");
			}
		}
	}
	
	protected void resetAssessments() {
	    label_UpperCase.setText("At least one upper case letter - Not yet satisfied");
	    setupLabelUI(label_UpperCase, "Arial", 14, PasswordEvaluationGUITestbed.WINDOW_WIDTH-10, 
				Pos.BASELINE_LEFT, 30, 230);
	    label_UpperCase.setTextFill(Color.RED);
	    
	    label_LowerCase.setText("At least one lower case letter - Not yet satisfied");
	    setupLabelUI(label_LowerCase, "Arial", 14, PasswordEvaluationGUITestbed.WINDOW_WIDTH-10, 
				Pos.BASELINE_LEFT, 30, 260);
	    label_LowerCase.setTextFill(Color.RED);
	    
	    label_NumericDigit.setText("At least one numeric digit - Not yet satisfied");
	    setupLabelUI(label_NumericDigit, "Arial", 14, PasswordEvaluationGUITestbed.WINDOW_WIDTH-10, 
				Pos.BASELINE_LEFT, 30, 290);
	    label_NumericDigit.setTextFill(Color.RED);
	    
	    label_SpecialChar.setText("At least one special character - Not yet satisfied");
	    setupLabelUI(label_SpecialChar, "Arial", 14, PasswordEvaluationGUITestbed.WINDOW_WIDTH-10, 
				Pos.BASELINE_LEFT, 30, 320);
	    label_SpecialChar.setTextFill(Color.RED);
	    
	    label_LongEnough.setText("At least eight characters - Not yet satisfied");
	    setupLabelUI(label_LongEnough, "Arial", 14, PasswordEvaluationGUITestbed.WINDOW_WIDTH-10, 
				Pos.BASELINE_LEFT, 30, 350);
	    label_LongEnough.setTextFill(Color.RED);
	    errPasswordPart3.setText("");
	}
	
	private void updateFlags() {
		if (PasswordEvaluator.foundUpperCase) {
			label_UpperCase.setText("At least one upper case letter - Satisfied");
			label_UpperCase.setTextFill(Color.GREEN);
		}

		if (PasswordEvaluator.foundLowerCase) {
			label_LowerCase.setText("At least one lower case letter - Satisfied");
			label_LowerCase.setTextFill(Color.GREEN);
		}

		if (PasswordEvaluator.foundNumericDigit) {
			label_NumericDigit.setText("At least one numeric digit - Satisfied");
			label_NumericDigit.setTextFill(Color.GREEN);
		}

		if (PasswordEvaluator.foundSpecialChar) {
			label_SpecialChar.setText("At least one special character - Satisfied");
			label_SpecialChar.setTextFill(Color.GREEN);
		}

		if (PasswordEvaluator.foundLongEnough) {
			label_LongEnough.setText("At least eight characters - Satisfied");
			label_LongEnough.setTextFill(Color.GREEN);
		}
	}
}
