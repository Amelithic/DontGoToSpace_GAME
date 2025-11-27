package com.amelithic.zorkgame.gui;

import java.util.Optional;

import com.amelithic.zorkgame.Command;
import com.amelithic.zorkgame.CommandManager;
import com.amelithic.zorkgame.Main;
import com.amelithic.zorkgame.characters.Player;

import javafx.animation.Interpolator;
import javafx.animation.RotateTransition;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;
import javafx.util.Duration;


public class GUIController {
    private final Main gameState;
    private final Player player;
    private final CommandManager commandManager;

    public GUIController(Main gameState, Player player, CommandManager commandManager) {
        this.gameState = gameState;
        this.player = player;
        this.commandManager = commandManager;
    }

    @FXML
    private StackPane card; // from FXML

    @FXML
    public void initialize() {
        // Flip once on hover
        card.setOnMouseEntered(event -> {
            flipCard(card);
        });

    }


    @FXML //references button with #move in onAction property
    public void move(Event event){
        String idButtonPressed = ((Button) event.getSource()).getId();
        System.err.println(idButtonPressed);

        Optional<Command> cmdCheck = commandManager.parse(gameState, player, "go "+idButtonPressed);
        if (cmdCheck.isPresent()) {
            Command cmd = cmdCheck.get();
            cmd.execute();
        } else {
            System.out.println("I don't understand that command.");
        }
    }

    @FXML //references button with #move in onAction property
    public void inventoryView(Event event){
        Optional<Command> cmdCheck = commandManager.parse(gameState, player, "show inv");
        if (cmdCheck.isPresent()) {
            Command cmd = cmdCheck.get();
            cmd.execute();
        } else {
            System.out.println("I don't understand that command.");
        }
    }

    @FXML
    public void exit(Event event) {
        Optional<Command> cmdCheck = commandManager.parse(gameState, player, "exit");
        if (cmdCheck.isPresent()) {
            Command cmd = cmdCheck.get();
            cmd.execute();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.close();
        } else {
            System.out.println("I don't understand that command.");
        }
    }

    // Call this from a button or event handler
    @FXML
    private void flipCard(Node card) {
        RotateTransition rotator = new RotateTransition(Duration.millis(1000), card);
        rotator.setAxis(Rotate.Y_AXIS);
        rotator.setFromAngle(0);
        rotator.setToAngle(180); //flips card
        rotator.setInterpolator(Interpolator.LINEAR);
        rotator.setCycleCount(1);

        rotator.play();
    }


}
