package com.amelithic.zorkgame.gui;

import java.io.IOException;

import com.amelithic.zorkgame.CommandManager;
import com.amelithic.zorkgame.GUI;
import com.amelithic.zorkgame.Main;
import com.amelithic.zorkgame.characters.Player;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class NewGameController extends GUIController {
    //fields

    //constructor
    public NewGameController(GUI gui, Main gameState, Player player, CommandManager commandManager) {
        super(gui, gameState, player, commandManager);
    }

    //FXML Components
    @FXML
    private Label title;
    private Label desc;
    private TextField name;


    @FXML
    public void initialize() {
        title.setText(gui.fetchTitle());
        //TODO: lore.json -> for when events are completed, show storybeats
    }//end initialize

    @FXML
    public void inputName(ActionEvent event){ 
        TextField inputField;

        if (event.getSource() instanceof TextField textField) {
            inputField = textField;
            String inputString = inputField.getText();
            System.out.println(inputString);
            player.setName(inputString);
            System.err.println("Name changed");
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
