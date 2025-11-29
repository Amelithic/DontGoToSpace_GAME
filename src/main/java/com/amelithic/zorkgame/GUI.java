package com.amelithic.zorkgame;

import java.io.IOException;
import java.io.FileInputStream;
import java.util.Properties;

import com.amelithic.zorkgame.characters.Player;
import com.amelithic.zorkgame.gui.GUIController;
import com.amelithic.zorkgame.gui.TitleController;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class GUI extends Application {
    protected Stage stage;
    protected Scene scene;
    protected Parent root;

    protected Main gameState;
    protected CommandManager commandManager;
    protected Player player;


    @Override
    public void start(Stage stage) throws IOException {
        this.stage = stage;

        gameState = new Main();
        commandManager = new CommandManager();
        player = gameState.getPlayer();

        //first screen -> title screen
        FXMLLoader fxmlLoader = new FXMLLoader(GUI.class.getResource("/gui/resources/titleScreen.fxml"));

        // Inject dependencies into controller
        fxmlLoader.setControllerFactory(param -> {
            if (param == TitleController.class) {
                return new TitleController(this, gameState, player, commandManager);
            } else {
                try {
                    return param.getDeclaredConstructor().newInstance();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });

        this.root = fxmlLoader.load();
        scene = new Scene(root);
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
  
    public void switchScreen(String fxmlPath) throws IOException {
        FXMLLoader loader = new FXMLLoader(GUI.class.getResource(fxmlPath));

        loader.setControllerFactory(param -> {
            try {
                // If the requested controller is a subtype of GUIController
                if (GUIController.class.isAssignableFrom(param)) {
                    // Find a constructor that matches your dependencies
                    return param.getConstructor(GUI.class, Main.class, Player.class, CommandManager.class)
                                .newInstance(this, gameState, player, commandManager);
                } else {
                    // Default: no-arg constructor
                    return param.getDeclaredConstructor().newInstance();
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        Parent newRoot = loader.load();

        // Instead of creating a new Scene, just swap the root
        scene.setRoot(newRoot);
    }
}
