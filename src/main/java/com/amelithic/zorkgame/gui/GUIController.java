package com.amelithic.zorkgame.gui;

import java.io.IOException;

import com.amelithic.zorkgame.CommandManager;
import com.amelithic.zorkgame.GUI;
import com.amelithic.zorkgame.Main;
import com.amelithic.zorkgame.characters.Player;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public abstract class GUIController {
    //fields
    protected static Main gameState;
    protected static Player player;
    protected CommandManager commandManager;
    protected GUI gui;

    //constructors
    public GUIController(GUI gui, Main gameState, Player player, CommandManager commandManager) {
        this.gui = gui;
        this.gameState = gameState;
        this.player = player;
        this.commandManager = commandManager;
    }

    //SCREEN SWITCHERS!!!
    @FXML
    protected void switchToGame(ActionEvent event) throws IOException {
        gui.switchScreen("/gui/resources/gameScreen.fxml");
    }

    @FXML
    protected void switchToTitle(ActionEvent event) throws IOException {
        gui.switchScreen("/gui/resources/titleScreen.fxml");
    }

    @FXML
    protected void switchToSave(ActionEvent event) throws IOException {
        gui.switchScreen("/gui/resources/saveFileScreen.fxml");
    }

    @FXML
    protected void switchToNew(ActionEvent event) throws IOException {
        gui.switchScreen("/gui/resources/newSaveScreen.fxml");
    }

    @FXML
    protected void switchToWin(ActionEvent event) throws IOException {
        gui.switchScreen("/gui/resources/winScreen.fxml");
    }

    //abstract methods
    public abstract void initialize();
}


