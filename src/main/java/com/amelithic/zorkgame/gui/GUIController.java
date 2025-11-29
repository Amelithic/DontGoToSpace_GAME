package com.amelithic.zorkgame.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.amelithic.zorkgame.Command;
import com.amelithic.zorkgame.CommandManager;
import com.amelithic.zorkgame.Main;
import com.amelithic.zorkgame.TrieAutocomplete;
import com.amelithic.zorkgame.characters.Player;
import com.amelithic.zorkgame.items.Item;

import javafx.animation.Interpolator;
import javafx.animation.RotateTransition;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.transform.Rotate;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.util.Duration;


public class GUIController {
    private final Main gameState;
    private final Player player;
    private final CommandManager commandManager;
    private String[] newTextWords;
    private Popup autoCompletePopup;

    public GUIController(Main gameState, Player player, CommandManager commandManager) {
        this.gameState = gameState;
        this.player = player;
        this.commandManager = commandManager;
    }

    @FXML
    private StackPane card; // from FXML
    @FXML
    private TextArea outputConsole;
    @FXML
    private TextField inputConsole;
    @FXML
    private ImageView bg;

    @FXML
    public void initialize() {
        outputConsole.setEditable(false);
        outputConsole.setWrapText(true);
        inputConsole.setPromptText("Enter your command here..."); //to set the hint text
    

        //outputConsole.setDisable(true); //cannot mouse select

        //set bg image
        String roomId = player.getCurrentRoom().getId();
        String roomImgUrl = returnImageUrl(roomId);
        bg.setImage(new Image(roomImgUrl));

        //change to component listener
        outputConsole.textProperty().addListener((obs, oldText, newText) -> {
            outputConsole.setScrollTop(Double.MAX_VALUE);
            //TODO: fix -> eat table breaks scroll
        });

        // Create popup with a ListView
        autoCompletePopup = new Popup();
        autoCompletePopup.setHideOnEscape(true);
        autoCompletePopup.setAutoHide(true); //doesnt show if not focused`
        ListView<String> autoCompleteList = new ListView<>();
        autoCompleteList.setPrefHeight(120); //roughly 5 entries shown
        autoCompleteList.setId("autoList");
        autoCompletePopup.getContent().add(autoCompleteList);

        inputConsole.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> obs, String oldText, String newText) {
                TrieAutocomplete trie = new TrieAutocomplete();
                //add all possible words given scenario
                for (Item playerItem : player.getInventory()) {
                    trie.insert(playerItem.getName());
                    trie.insert(playerItem.getId());
                }
                for (Item roomItem : (ArrayList<Item>) player.getCurrentRoom().getRoomItems()) {
                    //dunno why issue? 
                    //TODO: fix required cast
                    trie.insert(roomItem.getName());
                    trie.insert(roomItem.getId());
                }
                String[] playerRoomExits = player.getCurrentRoom().getExitString().split(" ");
                for (String roomExit : playerRoomExits) {
                    trie.insert(roomExit);
                }
                for (Command command : commandManager.getAllCommands()) {
                    trie.insert(command.getName());
                    String[] synonyms = command.getSynonyms().split(", ");
                    for (String string : synonyms) trie.insert(string);
                }
                
                newTextWords = newText.split(" ");
                String lastWord = newTextWords[newTextWords.length - 1];
                List<String> results = trie.search(lastWord); // your autocomplete trie
                
                //continue if new text entered and results > 1
                if ((!results.isEmpty()) && (!oldText.equals(newText))) {
                    autoCompleteList.getItems().setAll(results);
                    
                    // Position popup ABOVE the input field
                    Bounds bounds = inputConsole.localToScreen(inputConsole.getBoundsInLocal());
                    autoCompletePopup.show(inputConsole,
                            bounds.getMinX(),
                            bounds.getMinY() - autoCompleteList.getHeight()); // above the field
                    //TODO: fix dimensions of popup
                } else {
                    autoCompletePopup.hide();
                }
            }
        });
        // Handle selection
        autoCompleteList.setOnMouseClicked(event -> {
            String selected = autoCompleteList.getSelectionModel().getSelectedItem();
            if (selected != null) {
                String newInput = "";
                for (int i=0; i < newTextWords.length-1; i++) newInput += newTextWords[i] + " ";
                newInput += selected;
                inputConsole.setText(newInput);
                inputConsole.endOfNextWord();
                autoCompletePopup.hide();
            }
        });
        autoCompleteList.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.TAB) { 
                autoCompleteList.getSelectionModel().selectFirst(); //first in list by default
                String selected = autoCompleteList.getSelectionModel().getSelectedItem();
                if (selected != null) {
                    String newInput = "";
                    for (int i=0; i < newTextWords.length-1; i++) newInput += newTextWords[i] + " ";
                    newInput += selected;
                    inputConsole.setText(newInput);
                    //TODO: moves to end of new words, not previous
                    inputConsole.endOfNextWord();
                    autoCompletePopup.hide();
                    event.consume(); // prevent default focus traversal
                } else if (event.getCode() == KeyCode.UP) {
                    autoCompleteList.requestFocus();
                    autoCompleteList.getSelectionModel().selectPrevious();
                    event.consume();
                } else if (event.getCode() == KeyCode.DOWN) {
                    autoCompleteList.requestFocus();
                    autoCompleteList.getSelectionModel().selectNext();
                    event.consume();
                }

            }
        });



        // Flip once on hover
        card.setOnMouseEntered(event -> {
            flipCard(card);
        });

    }

    @FXML
    public void inputToTextField(ActionEvent event){ 
        //System.out.println(event.getSource().getClass());
        TextField inputField;

        if (event.getSource() instanceof TextField textField) {
            inputField = textField;
            String inputString = inputField.getText();
            System.out.println(inputString);
            inputField.setText("");
            autoCompletePopup.hide();

            Optional<Command> cmdCheck = commandManager.parse(gameState, player, inputString);
            if (cmdCheck.isPresent()) {
                Command cmd = cmdCheck.get();
                String result = cmd.execute();
                outputConsole.appendText(result);
            } else {
                outputConsole.appendText("I don't understand that command.\n");
            }

            //outputConsole.appendText(inputString);
        }
    }

    @FXML //references button with #move in onAction property
    public void move(Event event){
        String idButtonPressed = ((Button) event.getSource()).getId();
        System.err.println(idButtonPressed);

        Optional<Command> cmdCheck = commandManager.parse(gameState, player, "go "+idButtonPressed);
        if (cmdCheck.isPresent()) {
            Command cmd = cmdCheck.get();
            String result = cmd.execute();
            outputConsole.appendText(result+"\n");
            String roomId = player.getCurrentRoom().getId();
            String roomImgUrl = returnImageUrl(roomId);
            bg.setImage(new Image(roomImgUrl));
        } else {
            outputConsole.appendText("I don't understand that command.\n");
        }
    }

    @FXML //references button with #move in onAction property
    public void inventoryView(Event event){
        Optional<Command> cmdCheck = commandManager.parse(gameState, player, "show inv");
        if (cmdCheck.isPresent()) {
            Command cmd = cmdCheck.get();
            String result = cmd.execute();
            outputConsole.appendText(result+"\n");
        } else {
            outputConsole.appendText("I don't understand that command.\n");
        }
    }

    @FXML
    public void exit(Event event) {
        Optional<Command> cmdCheck = commandManager.parse(gameState, player, "exit");
        if (cmdCheck.isPresent()) {
            Command cmd = cmdCheck.get();
            String result = cmd.execute();
            outputConsole.appendText(result+"\n");
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.close();
        } else {
            System.out.println("I don't understand that command.\n");
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

    private String returnImageUrl(String roomId) {
        switch (roomId) {
            case "base_quarters":
                return "/images/quarters.png";
            case "base_main":
                return "/images/mainroom.png";
            case "base_corridor":
                return "/images/corridor.png";
            case "base_kitchen":
                return "/images/kitchen.png";
            case "base_bathrooms":
                return "/images/bathrooms.png";
            case "base_storage":
                return "/images/storageroom.png";
            case "tunnel_01", "tunnel_02", "tunnel_03":
                return "/images/tunnel.png";
            case "base_secret":
                return "/images/secretroom.png";
            case "airlock":
                return "/images/airlock.png";
            case "outdoors_01":
                return "/images/outside.png";
            case "cliff":
                return "/images/cliff.png";
            case "outdoors_02":
                return "/images/dustlands.png";
            case "broken_spacecraft":
                return "/images/brokenship.png";
            case "crater":
                return "/images/crater.png";
            case "cave":
                return "/images/cave.png";
            case "old_spacecraft":
                return "/images/oldship.png";
            default:
                return "/images/galaxy2.png";
        }
    }
}
