module com.example.googleformautoresponse {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.bootstrapfx.core;

    opens com.example.googleformautoresponse to javafx.fxml;
    exports com.example.googleformautoresponse;
    exports com.example.googleformautoresponse.Classes;
    opens com.example.googleformautoresponse.Classes to javafx.fxml;
}