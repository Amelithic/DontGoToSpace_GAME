package com.amelithic.zorkgame;

import java.io.IOException;
import java.util.Properties;

import com.amelithic.zorkgame.characters.Player;
import com.amelithic.zorkgame.gui.GUIController;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class GUI extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        Main gameState = new Main();
        CommandManager commandManager = new CommandManager();
        Player player = gameState.getPlayer();
        FXMLLoader fxmlLoader = new FXMLLoader(GUI.class.getResource("/gui/resources/gameScreen.fxml"));

        // Inject dependencies into controller
        fxmlLoader.setControllerFactory(param -> {
            if (param == GUIController.class) {
                return new GUIController(gameState, player, commandManager);
            } else {
                try {
                    return param.getDeclaredConstructor().newInstance();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });

        Scene scene = new Scene(fxmlLoader.load());
        String css = this.getClass().getResource("/gui/resources/gameScreen.css").toExternalForm(); 
        scene.getStylesheets().add(css);

        Image icon = new Image(GUI.class.getResource("/images/icon.png").toExternalForm());
        stage.getIcons().add(icon);

        stage.setResizable(false);

        String gameTitle = fetchTitle();
        stage.setTitle(gameTitle);
        stage.setScene(scene); //add to stage
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

    public static String fetchTitle() {
        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream("src\\main\\java\\com\\amelithic\\zorkgame\\config\\config.properties"));
            String gameTitle = properties.getProperty("game.title").trim();
            return gameTitle
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Game Title"; //if read file error
    }
}
