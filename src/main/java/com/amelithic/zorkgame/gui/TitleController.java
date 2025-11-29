package com.amelithic.zorkgame.gui;

import java.io.IOException;
import java.util.Optional;

import com.amelithic.zorkgame.Command;
import com.amelithic.zorkgame.CommandManager;
import com.amelithic.zorkgame.GUI;
import com.amelithic.zorkgame.Main;
import com.amelithic.zorkgame.characters.Player;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.stage.Stage;

public class TitleController extends GUIController {
    //fields

    //constructor
    public TitleController(GUI gui, Main gameState, Player player, CommandManager commandManager) {
        super(gui, gameState, player, commandManager);
    }

    //FXML Components

    @FXML
    public void initialize() {
        //TODO: if saves empty, don't add load saves options
        //TODO: if saves not empty add load saves
        //TODO: if properties admin true, then show icon in bottom left
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
    public void game(ActionEvent event) throws IOException {
        switchToGame(event);
    }//end game
}