package com.amelithic.zorkgame;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class GUI extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(GUI.class.getResource("/gui/resources/gameScreen.fxml"));
        Scene scene = new Scene(fxmlLoader.load());

        Image icon = new Image(GUI.class.getResource("/images/icon.png").toExternalForm());
        stage.getIcons().add(icon);

        stage.setResizable(false);

        stage.setTitle("Hello!");
        stage.setScene(scene); //add to stage
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
