module CSE360Project1 {
	requires javafx.controls;
	requires java.sql;
	requires junit;
	requires java.desktop;
	requires javafx.graphics;
	requires java.xml;
	requires java.xml.crypto;
	// If tests exist directly in HW2 package (check AnswersTest etc.)
	opens HW2 to javafx.graphics, javafx.fxml;
}