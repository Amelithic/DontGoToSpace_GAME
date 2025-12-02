package com.amelithic.zorkgame.gui;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import com.amelithic.zorkgame.Command;
import com.amelithic.zorkgame.CommandManager;
import com.amelithic.zorkgame.GUI;
import com.amelithic.zorkgame.Main;
import com.amelithic.zorkgame.characters.Player;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Popup;

public class NewGameController extends GUIController {
    //fields
    private static ObjectMapper objmap = getDefaultObjectMapper(); //for JSON parsing

    private static ObjectMapper getDefaultObjectMapper() {
        ObjectMapper defaultObjectMapper = new ObjectMapper();
        //config ...
        return defaultObjectMapper;
    }

    public static JsonNode parse(String src) throws IOException {
        return objmap.readTree(src);
    }

    //constructor
    public NewGameController(GUI gui, Main gameState, Player player, CommandManager commandManager) {
        super(gui, gameState, player, commandManager);
    }

    //FXML Components
    @FXML
    private Label title;
    @FXML
    private Label desc;
    @FXML
    private TextField name;


    @FXML
    public void initialize() {
        title.setText(gui.fetchTitle());
        desc.setWrapText(true);

        try {
            String mapFileStr = Files.readString(Path.of("src\\main\\java\\com\\amelithic\\zorkgame\\config\\lore.json"));
            JsonNode lore = parse(mapFileStr);
            desc.setText(lore.get("startGame").asText());
        } catch (IOException e) {
            System.err.println("Exception when reading the JSON lore file...");
            e.printStackTrace();
        }
    }//end initialize

    @FXML
    public void inputName(ActionEvent event) throws IOException { 
        TextField inputField;

        if (event.getSource() instanceof TextField textField) {
            inputField = textField;
            String inputString = inputField.getText();
            System.out.println(inputString);
            player.setName(inputString);

            //auto-save
            Optional<Command> cmdCheck = commandManager.parse(gameState, player, "save");
            if (cmdCheck.isPresent()) {
                Command cmd = cmdCheck.get();
                String result = cmd.execute();
                switchToGame(event);
            } else {
                Popup errorLoadPopup = new Popup();
                Label popupContent = new Label();
                popupContent.setText("Error with creating new save...");
                errorLoadPopup.getContent().add(popupContent);
            }
        }
    }//end inputName

    @FXML
    public void exit(ActionEvent event) throws IOException {
        switchToTitle(event);
    }//end exit

    @FXML
    public void loadGame(ActionEvent event) throws IOException {
        switchToSave(event);
    }//end game

    @FXML
    public void newGame(ActionEvent event) throws IOException {
        //TODO: info popup with version info
        switchToNew(event);
    }//end game

    @FXML
    public void info(ActionEvent event) throws IOException {
        //TODO: info popup with version info
        switchToGame(event);
    }//end game
}
