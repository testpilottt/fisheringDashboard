module com.example.fisherydashboard {
    requires javafx.controls;
    requires javafx.fxml;
    requires json.simple;
    requires okhttp;
    requires lombok;
    requires java.sql;


    opens com.example.fisherydashboard to javafx.fxml;
    exports com.example.fisherydashboard;
}