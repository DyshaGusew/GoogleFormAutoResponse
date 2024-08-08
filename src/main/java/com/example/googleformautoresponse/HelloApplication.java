package com.example.googleformautoresponse;

import com.example.googleformautoresponse.Classes.Answer;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
//https://docs.google.com/forms/d/e/1FAIpQLSeHfFYw2S0URXGh-U6uUqSZzVNUT49NOO1Ts6ErYS6Wnu_9iQ/formResponse
public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 600, 400);
        stage.setTitle("GoogleHack");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}