package com.amelithic.zorkgame.gui;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;

import com.amelithic.zorkgame.Command;
import com.amelithic.zorkgame.CommandManager;
import com.amelithic.zorkgame.GUI;
import com.amelithic.zorkgame.Main;
import com.amelithic.zorkgame.characters.Player;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Popup;
import javafx.stage.Stage;

public class TitleController extends GUIController {
    //fields

    //constructor
    public TitleController(GUI gui, Main gameState, Player player, CommandManager commandManager) {
        super(gui, gameState, player, commandManager);
    }

    //FXML Components
    @FXML
    private Label titleText;
    @FXML
    private Button load;    
    @FXML
    private AnchorPane admin;

    @FXML
    public void initialize() {
        titleText.setText(gui.fetchTitle());
        titleText.setStyle("-fx-font-size: 28px;");

        try {
            Properties properties = gameState.getProperties();
            properties.load(new FileInputStream("src\\main\\java\\com\\amelithic\\zorkgame\\config\\config.properties"));

            String adminBoolean = properties.getProperty("engine.admin_commands").trim();

            if (adminBoolean.equalsIgnoreCase("true")) {
                admin.setVisible(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        String saveDir = "./saves/";
        File dir = new File(saveDir);
        File[] filesInDir = dir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File d, String name) {
                return name.toLowerCase().endsWith(".json");
            }
        }); //only checks for JSON files in dir
        if ((filesInDir.length > 0) && (filesInDir != null)) {
            load.setVisible(true);
            load.setManaged(true); //removes from layout without leaving gap
        } //adds valid paths
    }//end initialize


    @FXML
    public void exit(Event event) {
        Optional<Command> cmdCheck = commandManager.parse(gameState, player, "exit");
        if (cmdCheck.isPresent()) {
            Command cmd = cmdCheck.get();
            cmd.execute();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.close();
        } else {
            System.out.println("I don't understand that command.\n");
        }
    }//end exit

    @FXML
    public void loadGame(ActionEvent event) throws IOException {
        switchToSave(event);
    }//end game

    @FXML
    public void newGame(ActionEvent event) throws IOException {
        switchToNew(event);
    }//end game

    @FXML
    public void info(ActionEvent event) throws IOException {
            Popup infoPopup = new Popup();
            Label popupContent = new Label();
            popupContent.getStyleClass().add("darkMode");
            popupContent.getStyleClass().add("text");
            popupContent.setStyle("-fx-padding: 10px;");

            String popupContentString = "";

            try {
                Properties properties = gameState.getProperties();
                properties.load(new FileInputStream("src\\main\\java\\com\\amelithic\\zorkgame\\config\\config.properties"));

                String gameTitle = properties.getProperty("game.title").trim();
                String gameVersion = properties.getProperty("game.version").trim();
                String gameAuthor = properties.getProperty("game.author").trim();
                String gameDesc = properties.getProperty("game.description").trim();

                popupContentString += gameTitle + "\nVersion: "+gameVersion+"\nAuthor: "+gameAuthor+"\n\n"+gameDesc;
            } catch (Exception e) {
                e.printStackTrace();
            }

            popupContent.setText(popupContentString);
            infoPopup.getContent().add(popupContent);
            infoPopup.setHideOnEscape(true);
            infoPopup.setAutoHide(true); //doesnt show if not focused`
            infoPopup.show(((Node)event.getSource()).getScene().getWindow()); //show on screen from where its called from
    }//end info

    @FXML
    public void settings(ActionEvent event) throws IOException {
            Popup settingsPopup = new Popup();
            ScrollPane settingsScroll = new ScrollPane();
            settingsScroll.getStyleClass().add("darkMode");

            //scrollbar policy - dont want horizontal scroll, but vertical if needed
            settingsScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
            settingsScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);


            TextArea popupContent = new TextArea();
            popupContent.getStyleClass().add("darkMode");
            popupContent.getStyleClass().add("text");
            popupContent.setStyle("-fx-padding: 10px;");
            popupContent.setWrapText(true);
            popupContent.setEditable(false);
            String popupContentString = "SETTINGS:\n";
            try {
                Properties properties = gameState.getProperties();
                properties.load(new FileInputStream("src\\main\\java\\com\\amelithic\\zorkgame\\config\\config.properties"));

                for (Map.Entry<Object, Object> entry : properties.entrySet()) {
                    popupContentString += "\n"+ entry.getKey().toString() + ": " + entry.getValue().toString();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            popupContent.setText(popupContentString);

            settingsScroll.setContent(popupContent);
            settingsPopup.getContent().add(settingsScroll);
            settingsPopup.setHideOnEscape(true);
            settingsPopup.setAutoHide(true); //doesnt show if not focused`
            settingsPopup.show(((Node)event.getSource()).getScene().getWindow()); //show on screen from where its called from
    }//end settings
}
