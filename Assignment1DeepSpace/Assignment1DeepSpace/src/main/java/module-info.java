module com.example.assignment1deepspace {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.assignment1deepspace to javafx.fxml;
    exports com.example.assignment1deepspace;
}