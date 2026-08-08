module playerregistration.app {
	requires javafx.controls;
	requires javafx.graphics;
	requires javafx.fxml;
	requires java.sql; //Add this line for java.sql access
	requires javafx.base;
	
	opens playerregistration to javafx.graphics, javafx.fxml, javafx.base;
}
