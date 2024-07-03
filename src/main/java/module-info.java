module it.unicam.pentago {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;


    opens it.unicam.pentago to javafx.fxml;
    exports it.unicam.pentago;
}