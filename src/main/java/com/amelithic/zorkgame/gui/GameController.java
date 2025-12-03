package com.amelithic.zorkgame.gui;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

import com.amelithic.zorkgame.Command;
import com.amelithic.zorkgame.CommandManager;
import com.amelithic.zorkgame.GUI;
import com.amelithic.zorkgame.GameMap;
import com.amelithic.zorkgame.Main;
import com.amelithic.zorkgame.TrieAutocomplete;
import com.amelithic.zorkgame.characters.Player;
import com.amelithic.zorkgame.items.Item;
import com.amelithic.zorkgame.items.RequiredItem;
import com.amelithic.zorkgame.locations.Room;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import javafx.animation.Interpolator;
import javafx.animation.RotateTransition;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
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
import javafx.util.Duration;

public class GameController extends GUIController {
    //fields
    private String[] newTextWords;
    private Popup autoCompletePopup;
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
    public GameController(GUI gui, Main gameState, Player player, CommandManager commandManager) {
        super(gui, gameState, player, commandManager);
    }

    //FXML components
    @FXML
    private StackPane card; // from FXML
    @FXML
    private TextArea outputConsole;
    @FXML
    private TextField inputConsole;
    @FXML
    private ImageView bg;
    @FXML
    private Label health;
    @FXML
    private Label oxygen;
    @FXML
    private Label progress;

    @FXML
    public void initialize() {
        outputConsole.setEditable(false);
        outputConsole.setWrapText(true);
        inputConsole.setPromptText("Enter your command here..."); //to set the hint text
    
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
                    for (String string : newTextWords) inputConsole.endOfNextWord();
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

        //live stats thread
        Thread liveStats = new Thread(() -> {
            while (gameState.getGameRunning()) {
                //required to send the command to the UI thread, or else update UI is ignored :(
                Platform.runLater(() -> updateStats());
                System.out.println("loop!");
                try {
                    Thread.sleep(1000); // avoid busy loop
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });
        liveStats.setDaemon(true); //thread ends when app closes

        Thread oxyThread = new OxygenThread();
        oxyThread.setDaemon(true); //thread ends when app closes

        liveStats.start();
        oxyThread.start();
    }//end initialize

    @FXML
    public void inputToTextField(ActionEvent event){ 
        //System.out.println(event.getSource().getClass());
        TextField inputField;

        if (event.getSource() instanceof TextField textField) {
            inputField = textField;
            String inputString = inputField.getText().trim().toLowerCase();
            //System.out.println(inputString);
            inputField.setText("");
            autoCompletePopup.hide();

            if (inputString.equalsIgnoreCase("win")) {
                for (Item item : gameState.getMap().getItems()) {
                    if (item instanceof RequiredItem) player.setInventory(item);
                }
                for (Room<GameMap.ExitDirection> room : gameState.getMap().getRooms()) {
                    if (room.getId().equals("broken_spacecraft")) player.setCurrentRoom(room);
                }
            }

            Optional<Command> cmdCheck = commandManager.parse(gameState, player, inputString);
            if (cmdCheck.isPresent()) {
                Command cmd = cmdCheck.get();
                String result = cmd.execute();
                outputConsole.appendText("\n"+result);
            } else {
                outputConsole.appendText("\nI don't understand that command.");
            }

            //change bg image if move to next room
            if (inputString.matches("^(go|move|walk|travel).*")) {
                String roomId = player.getCurrentRoom().getId();
                String roomImgUrl = returnImageUrl(roomId);
                bg.setImage(new Image(roomImgUrl));
            }
        }
    }//end inputToTextField

    @FXML //references button with #move in onAction property
    public void move(Event event){
        String idButtonPressed = ((Button) event.getSource()).getId();
        //System.err.println(idButtonPressed);

        Optional<Command> cmdCheck = commandManager.parse(gameState, player, "go "+idButtonPressed);
        if (cmdCheck.isPresent()) {
            Command cmd = cmdCheck.get();
            String result = cmd.execute();
            outputConsole.appendText(result+"\n");
            String roomId = player.getCurrentRoom().getId();
            String roomImgUrl = returnImageUrl(roomId);
            bg.setImage(new Image(roomImgUrl));
        } else {
            outputConsole.appendText("\nI don't understand that command.");
        }
    }//end move

    @FXML //references button with #move in onAction property
    public void inventoryView(Event event){
        Optional<Command> cmdCheck = commandManager.parse(gameState, player, "show inv");
        if (cmdCheck.isPresent()) {
            Command cmd = cmdCheck.get();
            String result = cmd.execute();
            outputConsole.appendText("\n"+result);
        } else {
            outputConsole.appendText("\nI don't understand that command.");
        }
    }//end inventoryView

    @FXML
    public void exit(ActionEvent event) throws IOException {
        //only saves if auto-save = true
        try {
            Properties properties = Main.getProperties();
            properties.load(new FileInputStream("src\\main\\java\\com\\amelithic\\zorkgame\\config\\config.properties"));
            String autoSave = properties.getProperty("engine.auto_save").trim();

            if (autoSave.equals("true")) {
                Optional<Command> cmdCheck = commandManager.parse(gameState, player, "save");
                if (cmdCheck.isPresent()) {
                    Command cmd = cmdCheck.get();
                    cmd.execute();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        switchToTitle(event);
    }//end exit

    @FXML private void save(ActionEvent event) {
            Optional<Command> cmdCheck = commandManager.parse(gameState, player, "save");
            if (cmdCheck.isPresent()) {
                Command cmd = cmdCheck.get();
                String result = cmd.execute();
                outputConsole.appendText("\n"+result);
            } else {
                outputConsole.appendText("\nI don't understand that command.");
            }
    }

    @FXML private void help(ActionEvent event) {
            Popup helpPopup = new Popup();
            Label popupContent = new Label();
            popupContent.getStyleClass().add("darkMode");
            popupContent.getStyleClass().add("text");
            popupContent.setStyle("-fx-padding: 10px;");

            Optional<Command> cmdCheck = commandManager.parse(gameState, player, "help");
            if (cmdCheck.isPresent()) {
                Command cmd = cmdCheck.get();
                String result = cmd.execute();
                popupContent.setText(result);
            } else {
                popupContent.setText("Error loading commands...");
            }
            helpPopup.getContent().add(popupContent);
            helpPopup.setHideOnEscape(true);
            helpPopup.setAutoHide(true); //doesnt show if not focused`
            helpPopup.show(((Node)event.getSource()).getScene().getWindow()); //show on screen from where its called from
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
    }//end flipCard

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
    }//end returnImageUrl

    public void updateStats() {
        health.setText("Health: "+player.getCurrentHealth()+"/"+player.getMaxHealth());
        oxygen.setText("Oxygen: "+player.getOxygenLevel());

        // show items gained out of 5
        ArrayList<Item> requiredItems = new ArrayList<>();
        for (Item item : player.getInventory()) {
            if (item instanceof RequiredItem) requiredItems.add(item);
        }
        progress.setText("Progress: "+requiredItems.size()+"/5");

        //goal checker
        try {
            String mapFileStr = Files.readString(Path.of("src\\main\\java\\com\\amelithic\\zorkgame\\config\\lore.json"));
            JsonNode lore = parse(mapFileStr);
            lore.get("startGame").asText();

            //check for 5 required items -> items by id check
            if (player.getInventory().contains(gameState.getMap().getItemById("thruster"))) {
                gameState.getMap().getGoalById(1).setSolved(true);
            } else if (player.getInventory().contains(gameState.getMap().getItemById("fuel"))) {
                gameState.getMap().getGoalById(2).setSolved(true);
            } else if (player.getInventory().contains(gameState.getMap().getItemById("chip"))) {
                gameState.getMap().getGoalById(3).setSolved(true);
            } else if (player.getInventory().contains(gameState.getMap().getItemById("idcard"))) {
                gameState.getMap().getGoalById(4).setSolved(true);
            }else if (player.getInventory().contains(gameState.getMap().getItemById("gearbox"))) {
                gameState.getMap().getGoalById(5).setSolved(true);
            }
        } catch (IOException e) {
            System.err.println("Exception when reading the JSON lore file...");
            e.printStackTrace();
        }

        //win checker
        if ((requiredItems.size() >= 5) && (player.getCurrentRoom().getId().equals("broken_spacecraft"))) {
            win();
        }
    }

    public void win() {
        gameState.setGameRunning(false);
        outputConsole.appendText("\nYou won the game and got to space!");
        //switchToWin(event);
    }

}

class OxygenThread extends Thread {
    @Override
    public void run() {
        Player player = GameController.player;

        Item spaceSuit = null;
        for (Item item : GameController.gameState.getMap().getItems()) {
            if (item.getId().equals("spacesuit")) spaceSuit = item;
        }

        while (GameController.gameState.getGameRunning()) {
            if (player.isOutdoor() && !player.getInventory().contains(spaceSuit)) {
                //if no space suit and outdoors
                player.decreaseOxygen((int) (Math.random()*3)); //decrease by random amount from 1-3
                try {
                    OxygenThread.sleep(1000);
                } catch (InterruptedException ex) {
                }
            } else {
                player.setOxygenLevel(100); //set to max if not outdoors
            }
        }
    }
}