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
import com.amelithic.zorkgame.Main;
import com.amelithic.zorkgame.TrieAutocomplete;
import com.amelithic.zorkgame.characters.Alien;
import com.amelithic.zorkgame.characters.Player;
import com.amelithic.zorkgame.items.Item;
import com.amelithic.zorkgame.items.RequiredItem;
import com.amelithic.zorkgame.locations.OutdoorArea;
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
    private TextArea cardContent; // from FXML
    @FXML
    private TextArea outputConsole;
    @FXML
    private TextField inputConsole;
    @FXML
    private ImageView bg;
    @FXML
    private ImageView effects; //public for threads
    @FXML
    private ImageView inv1, inv2, inv3, inv4, inv5;
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
        cardContent.setEditable(false);
        cardContent.setWrapText(true);
        cardContent.setStyle("-fx-padding: 10px; -fx-font-size: 11px;");
        inputConsole.setPromptText("Enter your command here..."); //to set the hint text
        inputConsole.disableProperty().set(false);
        inputConsole.setEditable(true);

        health.setStyle("-fx-font-size: 14px;");
        oxygen.setStyle("-fx-font-size: 14px;");
        progress.setStyle("-fx-font-size: 14px;");

        try {
            String mapFileStr = Files.readString(Path.of("src\\main\\java\\com\\amelithic\\zorkgame\\config\\lore.json"));
            JsonNode lore = parse(mapFileStr);
            cardContent.setText(lore.get("mission1").asText());
        } catch (IOException e) {
            System.err.println("Exception when reading the JSON lore file...");
            e.printStackTrace();
        }

        //initial message
        outputConsole.appendText(gameState.getPlayer().displayInfo());
        outputConsole.appendText("\n"+gameState.getPlayer().getCurrentRoom().getLongDescription());
    
        //set bg image
        String roomId = player.getCurrentRoom().getId();
        String roomImgUrl = returnImageUrl(roomId);
        bg.setImage(new Image(roomImgUrl));

        //set effects image
        effects.setImage(new Image("/images/oxygenEffect.png"));

        //change to component listener
        outputConsole.textProperty().addListener((obs, oldText, newText) -> {
            outputConsole.setScrollTop(Double.MAX_VALUE);
        });

        // Create popup with a ListView
        autoCompletePopup = new Popup();
        autoCompletePopup.setHideOnEscape(true);
        autoCompletePopup.setAutoHide(true); //doesnt show if not focused`
        ListView<String> autoCompleteList = new ListView<>();
        autoCompleteList.setPrefHeight(120); //roughly 5 entries shown
        autoCompleteList.setId("autoList");
        autoCompletePopup.getContent().add(autoCompleteList);

        //listener to flip card on new content
        cardContent.textProperty().addListener((obs, oldText, newText) -> flipCard(card));

        //listener for text input for autocomplete
        inputConsole.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> obs, String oldText, String newText) {
                TrieAutocomplete trie = new TrieAutocomplete();
                //add all possible words given scenario
                for (Item playerItem : player.getInventory()) {
                    trie.insert(playerItem.getName());
                    trie.insert(playerItem.getId());
                }
                for (Item roomItem : (List<Item>) player.getCurrentRoom().getRoomItems()) {
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
                //other manually added words
                String[] manualWordStrings = {"to", "the", "in", "inv", "inventory", "items", "room", "around", "here", "rocket", "spacecraft", "spaceship", "power"};
                for (String string : manualWordStrings) trie.insert(string);

                newTextWords = newText.split(" ");
                String lastWord = newTextWords[newTextWords.length - 1];
                List<String> results = trie.search(lastWord); // your autocomplete trie
                
                //continue if new text entered and results > 1
                if ((!results.isEmpty()) && (!oldText.equals(newText))) {
                    autoCompleteList.getItems().setAll(results);
                    
                    Bounds bounds = inputConsole.localToScreen(inputConsole.getBoundsInLocal());
                    autoCompletePopup.show(inputConsole,bounds.getMinX(), bounds.getMinY() - autoCompleteList.getHeight()); // above the field
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
                for (String string : newTextWords) inputConsole.endOfNextWord();
                autoCompletePopup.hide();
            }
        });
        autoCompleteList.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.TAB) { 
                autoCompleteList.getSelectionModel().selectFirst(); //first in list by default
                String selected = autoCompleteList.getSelectionModel().getSelectedItem();
                if (selected != null) {
                    String newInput = ""; //don't override previous inputs, only append
                    for (int i=0; i < newTextWords.length-1; i++) newInput += newTextWords[i] + " ";
                    newInput += selected;

                    inputConsole.setText(newInput);
                    for (String string : newTextWords) inputConsole.endOfNextWord(); //skips cursor to last word out of words 
                    autoCompletePopup.hide();
                    event.consume(); //unfocus
                }
            } else if (event.getCode() == KeyCode.ENTER) {
                String selected = autoCompleteList.getSelectionModel().getSelectedItem();
                if (selected != null) {
                    String newInput = ""; //don't override previous inputs, only append
                    for (int i=0; i < newTextWords.length-1; i++) newInput += newTextWords[i] + " ";
                    newInput += selected;

                    inputConsole.setText(newInput);
                    for (String string : newTextWords) inputConsole.endOfNextWord(); //skips cursor to last word out of input words
                    autoCompletePopup.hide();
                    event.consume(); //unfocus
                }
            } else if (event.getCode() == KeyCode.UP) {
                autoCompleteList.requestFocus();
                autoCompleteList.getSelectionModel().selectPrevious();
                event.consume();
            } else if (event.getCode() == KeyCode.DOWN) {
                autoCompleteList.requestFocus();
                autoCompleteList.getSelectionModel().selectNext();
                event.consume();
            }

        });
        // Flip once on hover -> disabled as it was annoying :(
        /*card.setOnMouseEntered(event -> {
            flipCard(card);
        });*/ 

        //live stats thread
        Thread liveStats = new Thread(() -> {
            while (gameState.getGameRunning()) {
                //required to send the command to the UI thread, or else update UI is ignored :(
                Platform.runLater(() -> updateStats());
                //System.out.println("loop!");
                try {
                    Thread.sleep(1000); // avoid busy loop
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }

            if (gameState.getGameRunning()==false) {
                inputConsole.disableProperty().set(true);
                inputConsole.setEditable(false);
            }
        });
        liveStats.setDaemon(true); //thread ends when app closes
        liveStats.start();

        Thread oxyThread = new OxygenThread();
        oxyThread.setDaemon(true); //thread ends when app closes
        oxyThread.start();

        Thread attackedThread = new AlienAttackThread();
        attackedThread.setDaemon(true); //thread ends when app closes
        attackedThread.start();

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

            Optional<Command> cmdCheck = commandManager.parse(gameState, player, inputString);
            if (cmdCheck.isPresent()) {
                Command cmd = cmdCheck.get();
                if (cmd.getFailed() == true) {
                    outputConsole.appendText("\n"+cmd.getFailedResult());
                } else {
                    String result = cmd.execute();
                    outputConsole.appendText("\n"+result);
                }
            } else {
                outputConsole.appendText("\nI don't understand that command.");
            }

            //goal and death checker
            checkForGoals();
            checkForDeath();

            //change bg image if move to next room
            if (inputString.matches("^(go|move|walk|travel|win|chris).*")) {
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
            if (cmd.getFailed() == true) {
                outputConsole.appendText("\n"+cmd.getFailedResult());
            } else {
                String result = cmd.execute();
                outputConsole.appendText("\n"+result);
                String roomId = player.getCurrentRoom().getId();
                String roomImgUrl = returnImageUrl(roomId);
                bg.setImage(new Image(roomImgUrl));
            }
        } else {
            outputConsole.appendText("I don't understand that command.");
        }

        //goal checker
        checkForGoals();
        checkForDeath();
    }//end move

    @FXML //references button with #move in onAction property
    public void inventoryView(Event event){
        Popup helpPopup = new Popup();
        Label popupContent = new Label();
        popupContent.getStyleClass().add("darkMode");
        popupContent.getStyleClass().add("text");
        popupContent.setStyle("-fx-padding: 10px;");

        Optional<Command> cmdCheck = commandManager.parse(gameState, player, "show inv");
        if (cmdCheck.isPresent()) {
            Command cmd = cmdCheck.get();
            if (cmd.getFailed() == true) {
                popupContent.setText("\n"+cmd.getFailedResult());
            } else {
                String result = cmd.execute();
                popupContent.setText(result);
            }
        } else {
            popupContent.setText("Error loading inventory...");
        }
        helpPopup.getContent().add(popupContent);
        helpPopup.setHideOnEscape(true);
        helpPopup.setAutoHide(true); //doesnt show if not focused`
        helpPopup.show(((Node)event.getSource()).getScene().getWindow()); //show on screen from where its called from
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

    @FXML private void goal(ActionEvent event) {
            Popup goalPopup = new Popup();
            Label popupContent = new Label();
            popupContent.getStyleClass().add("darkMode");
            popupContent.getStyleClass().add("text");
            popupContent.setStyle("-fx-padding: 10px;");

            Optional<Command> cmdCheck = commandManager.parse(gameState, player, "goals");
            if (cmdCheck.isPresent()) {
                Command cmd = cmdCheck.get();
                String result = cmd.execute();
                popupContent.setText(result);
            } else {
                popupContent.setText("Error loading goals...");
            }
            goalPopup.getContent().add(popupContent);
            goalPopup.setHideOnEscape(true);
            goalPopup.setAutoHide(true); //doesnt show if not focused`
            goalPopup.show(((Node)event.getSource()).getScene().getWindow()); //show on screen from where its called from
    }

    @FXML private void map(ActionEvent event) {
            Popup goalPopup = new Popup();
            ImageView popupContent = new ImageView();
            popupContent.getStyleClass().add("darkMode");
            popupContent.setStyle("-fx-padding: 10px;");
            popupContent.setFitWidth(500);
            popupContent.setFitHeight(750);
            //popupContent.setFitHeight(100);

            Image mapImage = new Image(getClass().getResource("/images/gameMap.png").toExternalForm());

            popupContent.setImage(mapImage);
            goalPopup.getContent().add(popupContent);
            goalPopup.setHideOnEscape(true);
            goalPopup.setAutoHide(true); //doesnt show if not focused`
            goalPopup.show(((Node)event.getSource()).getScene().getWindow()); //show on screen from where its called from
    }

    // Call this from a button or event handler
    @FXML
    private void flipCard(Node card) {
        RotateTransition rotator = new RotateTransition(Duration.millis(1000), card);
        rotator.setAxis(Rotate.Y_AXIS);
        rotator.setFromAngle(0);
        rotator.setToAngle(360); //flips card
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

        if (player.getCurrentHealth() < player.getMaxHealth()){
            health.setStyle("-fx-text-fill: rgba(249, 83, 83, 1);");
        } else {
            health.setStyle("-fx-text-fill: rgb(226, 227, 238);");
        }

        //oxygen
        oxygen.setText("Oxygen: "+player.getOxygenLevel());
        
        //if low and outdoors without spacesuit, set effect visible
        if ((player.isOutdoor()) && !(player.getInventory().contains(gameState.getMap().getItemById("spacesuit")))) {
                //if no space suit and outdoors visual effects visible
                effects.setVisible(true);
        } else {
            effects.setVisible(false);
        }

        // show items gained out of 5
        List<Item> requiredItems = new ArrayList<>();
        for (Item item : player.getInventory()) {
            if (item instanceof RequiredItem) requiredItems.add(item);
        }
        progress.setText("Progress: "+requiredItems.size()+"/5");

        //oxygen or health death check
        if ((player.getOxygenLevel()<=0) || (player.getCurrentHealth()<=0)) {
            outputConsole.appendText("You died.");
            gameState.setGameRunning(false);
        }
    }

    public void checkForGoals() {
        try {
            String mapFileStr = Files.readString(Path.of("src\\main\\java\\com\\amelithic\\zorkgame\\config\\lore.json"));
            JsonNode lore = parse(mapFileStr);
            lore.get("startGame").asText();

            //check for 5 required items -> items by id check
            if (player.getInventory().contains(gameState.getMap().getItemById("thruster"))) {
                gameState.getMap().getGoalById(1).setSolved(true);
                inv1.setImage(new Image("/gui/resources/icons/star.png"));
            }
            if (player.getInventory().contains(gameState.getMap().getItemById("fuel"))) {
                gameState.getMap().getGoalById(2).setSolved(true);
                inv2.setImage(new Image("/gui/resources/icons/star.png"));
            }
            if (player.getInventory().contains(gameState.getMap().getItemById("chip"))) {
                gameState.getMap().getGoalById(3).setSolved(true);
                inv3.setImage(new Image("/gui/resources/icons/star.png"));
            }
            if (player.getInventory().contains(gameState.getMap().getItemById("idcard"))) {
                gameState.getMap().getGoalById(4).setSolved(true);
                inv4.setImage(new Image("/gui/resources/icons/star.png"));
            }
            if (player.getInventory().contains(gameState.getMap().getItemById("gearbox"))) {
                gameState.getMap().getGoalById(5).setSolved(true);
                inv5.setImage(new Image("/gui/resources/icons/star.png"));
            }

            //if no longer in base_quarters
            if (player.getCurrentRoom() != gameState.getMap().getRoomById("base_quarters")) {
                gameState.getMap().getGoalById(6).setSolved(true);
                cardContent.setText(lore.get("mission2").asText());
            }

            //if power solved 
            if (gameState.getMap().getRoomById("base_corridor").getRoomItems().contains(gameState.getMap().getItemById("workingpower"))) {
                gameState.getMap().getGoalById(7).setSolved(true);
                cardContent.setText(lore.get("mission3").asText());

            }

            //if enter outdoor area
            if (player.getCurrentRoom() instanceof OutdoorArea) {
                gameState.getMap().getGoalById(8).setSolved(true);
                cardContent.setText(lore.get("mission4").asText());

                if (player.getCurrentRoom().equals(gameState.getMap().getRoomById("outdoors_02"))) {
                    cardContent.setText(lore.get("mission5").asText());
                }
            }

            //if aliens defeated
            for (Alien alien : gameState.getMap().getAliens()) {
                String alienName = alien.getName();
                
                if (alien.getDefeated() && alienName.equalsIgnoreCase("Alien 1")) {
                    gameState.getMap().getGoalById(9).setSolved(true);
                } else if (alien.getDefeated() && alienName.equalsIgnoreCase("Alien 2")) {
                    gameState.getMap().getGoalById(10).setSolved(true);
                }
            }

            //if got all 5 pieces
            if ((player.getInventory().contains(gameState.getMap().getItemById("thruster")))
                && (player.getInventory().contains(gameState.getMap().getItemById("fuel")))
                && (player.getInventory().contains(gameState.getMap().getItemById("idcard")))
                && (player.getInventory().contains(gameState.getMap().getItemById("chip")))
                && (player.getInventory().contains(gameState.getMap().getItemById("gearbox")))
            ) gameState.getMap().getGoalById(11).setSolved(true);

            //if secret room reached 
            if (player.getCurrentRoom() == gameState.getMap().getRoomById("base_secret")) {
                gameState.getMap().getGoalById(13).setSolved(true);
            }
        } catch (IOException e) {
            System.err.println("Exception when reading the JSON lore file...");
            e.printStackTrace();
        }

    }//end check for goals

    public void checkForDeath() {
        boolean death = false;
        //death checker
        if (player.getCurrentRoom().equals(gameState.getMap().getRoomById("cliff"))) {
            outputConsole.appendText("\nYou slipped and fell off the cliff...");
            death = true;
        } else if (player.getCurrentRoom().equals(gameState.getMap().getRoomById("tunnel_03"))) {
            outputConsole.appendText("\nAs you crawled deeper into the tunnel, you heard a sudden crack and fell into the abyss...\nIn space no-one can hear you scream...");
            death = true;
        }

        if (death == true) {
            outputConsole.appendText("\nYou died.");
            gameState.setGameRunning(false);
        }
    }

}

/* THREADS */
class OxygenThread extends Thread {
    @Override
    public void run() {
        Player player = GameController.player;

        Item spaceSuit = GameController.gameState.getMap().getItemById("spacesuit");

        while (GameController.gameState.getGameRunning()) {
            //System.err.println("oxy loop!");
            if ((player.isOutdoor()) && !(player.getInventory().contains(spaceSuit))) {
                //if no space suit and outdoors

                //decrease oxygen
                player.decreaseOxygen((int) (Math.random()*8)); //decrease by random amount from 1-8
                System.out.println("Decreased oxygen. Current oxygen level: "+player.getOxygenLevel());

                //wait
                try {
                    OxygenThread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                player.setOxygenLevel(100); //set to max if not outdoors
            }
        }
    }
}// OxygenThread

class AlienAttackThread extends Thread {
    @Override
    public void run() {
        Player player = GameController.player;

        while (GameController.gameState.getGameRunning()) {
            //attacked if in room with Alien
            for (Alien alien : GameController.gameState.getMap().getAliens()) {
                if ((player.getCurrentRoom().equals(alien.getCurrentRoom())) && (alien.getDefeated() != true)) {
                    //attack amount ranges from 5 to (max+5);
                    int randomAttackRange = (int) (Math.random()*alien.getAttackDamage()) + 5;
                    player.setCurrentHealth(player.getCurrentHealth()-randomAttackRange);
                    System.out.println("Player attacked, damage inflicted: "+randomAttackRange);
                } else {
                    //slowly regain health if not in room with undefeated alien
                    player.setCurrentHealth(player.getCurrentHealth()+5);
                }

            }

            try {
                OxygenThread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}//end AlienAttackDamage