package com.amelithic.zorkgame.gui;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Optional;

import com.amelithic.zorkgame.Command;
import com.amelithic.zorkgame.CommandManager;
import com.amelithic.zorkgame.GUI;
import com.amelithic.zorkgame.Main;
import com.amelithic.zorkgame.characters.Player;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;

public class LoadSaveController extends GUIController {
    //fields
    private File[] filesInDir;

    //constructor
    public LoadSaveController(GUI gui, Main gameState, Player player, CommandManager commandManager) {
        super(gui, gameState, player, commandManager);
    }

    //FXML Components
    @FXML
    private Label titleText;
    @FXML
    private VBox saves;
    //Button layoutX="-1.0" layoutY="-2.0" mnemonicParsing="false" prefHeight="54.0" prefWidth="318.0" text="item" />



    @FXML
    public void initialize() {
        titleText.setStyle("-fx-font-size: 28px;");

        String saveDir = "./saves/";
        File dir = new File(saveDir);
        filesInDir = dir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File d, String name) {
                return name.toLowerCase().endsWith(".json");
            }
        }); //only checks for JSON files in dir

        if (filesInDir != null) {
            for (int i=0; i < filesInDir.length; i++) {
                String saveFileName = filesInDir[i].getName();

                //remove JSON file extension
                if (saveFileName.endsWith(".json")) {
                    saveFileName = saveFileName.substring(0, saveFileName.length() - 5); // remove last 5 chars: ".json"
                }

                Button linkToSave = new Button();
                linkToSave.setText(saveFileName);
                linkToSave.setId(""+i); //not quite sure why not auto-cast?
                linkToSave.setPrefHeight(46.0);
                linkToSave.setMaxWidth(Double.MAX_VALUE); //fills space horizontally
                linkToSave.setOnAction(event -> {
                    try {
                        loadSave(event);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });

                saves.getChildren().add(linkToSave);
            }
        } //adds valid paths

    }//end initialize

    @FXML
    public void exit(ActionEvent event) throws IOException {
        switchToTitle(event);
    }//end exit

    @FXML
    public void loadSave(ActionEvent event) throws IOException {
        String idButtonPressed = ((Button) event.getSource()).getId();
        int idButtonPressedInt = Integer.parseInt(idButtonPressed); //change to usable integer
        System.out.println("load save: "+ idButtonPressed);

        //matches idButtonPressed to filesInDir by index
        try {
            String saveFile = filesInDir[idButtonPressedInt].getPath(); //save path

            Optional<Command> cmdCheck = commandManager.parse(gameState, player, "load "+saveFile);
            if (cmdCheck.isPresent()) {
                Command cmd = cmdCheck.get();
                String result = cmd.execute();
                gameState.setGameRunning(true);
                switchToGame(event);
            } else {
                Popup errorLoadPopup = new Popup();
                Label popupContent = new Label();
                popupContent.setText("Error with loading save file...");
                errorLoadPopup.getContent().add(popupContent);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
