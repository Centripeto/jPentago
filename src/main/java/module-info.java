module it.unicam.pentago {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;
    requires java.desktop;
    requires javafx.swing;


    opens it.unicam.pentago to javafx.fxml;
    exports it.unicam.pentago;
    exports it.unicam.pentago.models;
    opens it.unicam.pentago.models to javafx.fxml;
    exports it.unicam.pentago.strategies;
    opens it.unicam.pentago.strategies to javafx.fxml;
}