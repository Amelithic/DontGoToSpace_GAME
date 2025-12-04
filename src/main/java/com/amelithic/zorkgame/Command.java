package com.amelithic.zorkgame;

import java.io.FileInputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Properties;

import com.amelithic.zorkgame.GameMap.ExitDirection;
import com.amelithic.zorkgame.characters.Alien;
import com.amelithic.zorkgame.characters.Player;
import com.amelithic.zorkgame.items.FoodItem;
import com.amelithic.zorkgame.items.Item;
import com.amelithic.zorkgame.items.RequiredItem;
import com.amelithic.zorkgame.items.StorageItem;
import com.amelithic.zorkgame.items.Usable;
import com.amelithic.zorkgame.locations.Room;

public interface Command {
    Optional<Command> parse(Main game, Player player, String text); //parsing inputs
    String execute(); //command logic

    //Output text
    String getName();
    String getDescription();
    String getSynonyms();
    boolean getFailed();
    String getFailedResult();
}

class TakeCommand implements Command {
    //fields
    private String itemInString;
    private Player player; //author of command (whos running it)
    private Main game; //game instance
    private Item takeItem;
    private String modText;
    private boolean failedCmd;

    //methods
    @Override
    public Optional<Command> parse(Main game, Player player, String text) {
        this.game = game;
        this.player = player;
        failedCmd = false;

        text = text.trim().toLowerCase(); //remove spaces and all lowercase
        if (text.matches("^(take|pick up|grab)\\s+.+$")) {
            itemInString = text.replaceFirst("^(take|pick up|grab)( the)?\\s+", "");
            return Optional.of(this);
        } else if (text.matches("^(take|pick up|grab)\\s*$")) {
            modText = text.substring(0,1).toUpperCase() + text.substring(1); //capitalise first letter
            modText += " what?";
            failedCmd = true;
            return Optional.of(this);
        }
        return Optional.empty();
    }

    @Override
    public String execute() {
        //find item in room
        for (int i=0; i < player.getCurrentRoom().getRoomItems().size(); i++) {
            Item item = (Item) player.getCurrentRoom().getRoomItems().get(i);

            if (item.getName().equalsIgnoreCase(itemInString) || item.getId().equalsIgnoreCase(itemInString)) {
                takeItem = item;
                break;
            } else {
                continue;
            }
        }

        //verify it exists
        if (takeItem != null) {
        //check room contains object + add to inventory + remove from room
            if ((player.getCurrentRoom().getRoomItems().contains(takeItem)) && (takeItem.isPortable())) {
                player.setInventory(takeItem);
                if (player.getCurrentRoom().removeRoomItem(takeItem)) {
                    return String.format("Added %s to inventory!\n", takeItem.getName());
                } else {
                    return "Cannot add item";
                }

            } else {
                return "This item is not portable, or there is no item of that type in this room.";
            }
        }
        return "Invalid item, please try again.";
    }

    //getters
    @Override
    public String getName() {
        return "take";
    }

    @Override
    public String getDescription() {
        return "Pick up an item";
    }

    @Override
    public String getSynonyms() {
        return "take, pick up, grab";
    }

    @Override
    public String getFailedResult() {
        return modText;
    }

    @Override
    public boolean getFailed() {
        return failedCmd;
    }
} //end Take

class DropCommand implements Command {
    private String itemInString;
    private Player player; //author of command (whos running it)
    private Main game; //game instance
    private Item removeItem;
    private String modText;
    private boolean failedCmd;

    @Override
    public Optional<Command> parse(Main game, Player player, String text) {
        this.player = player;
        this.game = game;
        failedCmd = false;

        text = text.trim().toLowerCase();
        if (text.matches("^(drop|discard|remove)\\s+.+$")) {
            itemInString = text.replaceFirst("^(drop|discard|remove)\\s+", "");
            return Optional.of(this);
        } else if (text.matches("^(drop|discard|remove)\\s*$")) {
            modText = text.substring(0,1).toUpperCase() + text.substring(1); //capitalise first letter
            modText += " what?";
            failedCmd = true;
            return Optional.of(this);
        }
        return Optional.empty();
    }

    @Override
    public String execute() {
        //find item in inventory
        for (Item item : player.getInventory()) {
            if (item.getName().equalsIgnoreCase(itemInString) || item.getId().equalsIgnoreCase(itemInString)) {
                removeItem = item;
                break;
            } else {
                continue;
            }
        }

        //verify it exists
        if (removeItem != null) {
        //check its in inventory + remove from inventory + add to room
            if (player.getInventory().contains(removeItem)) {

                player.getCurrentRoom().setRoomItems(removeItem);
                if (player.removeFromInventory(removeItem)) {
                    return String.format("Dropped %s!\n", removeItem.getName());
                } else {
                    return "Cannot drop item";
                }
            }
        } else {
            return "There is no item of that type in your inventory.";
        }
        return "Invalid item, please try again.";
    }

    @Override
    public String getName() {
        return "drop";
    }

    @Override
    public String getDescription() {
        return "Removes item from inventory";
    }

    @Override
    public String getSynonyms() {
        return "drop, discard, remove";
    }

    @Override
    public String getFailedResult() {
        return modText;
    }

    @Override
    public boolean getFailed() {
        return failedCmd;
    }
} //end Drop

class DescribeCommand implements Command {
    private Player player; //author of command (whos running it)
    private Main game; //game instance
    private String itemInString;
    private Item describeItem;
    private String modText;
    private boolean failedCmd;

    @Override
    public Optional<Command> parse(Main game, Player player, String text) {
        this.player = player;
        this.game = game;
        failedCmd = false;

        text = text.trim().toLowerCase();
        if (text.matches("^(describe|explain|info)\\s+.+$")) {
            itemInString = text.replaceFirst("^(describe|explain|info)\\s+", "");
            return Optional.of(this);
        } else if (text.matches("^(describe|explain|info)\\s*$")) {
            modText = text.substring(0,1).toUpperCase() + text.substring(1); //capitalise first letter
            modText += " what?";
            failedCmd = true;
            return Optional.of(this);
        }
        return Optional.empty();
    }

    @Override
    public String execute() {
        //find item in room or in inventory
        for (Item item : player.getInventory()) {
            if (item.getName().equalsIgnoreCase(itemInString) || item.getId().equalsIgnoreCase(itemInString)) {
                describeItem = item;
                break;
            } else {
                continue;
            }
        }
        for (int i=0; i < player.getCurrentRoom().getRoomItems().size(); i++) {
            Item item = (Item) player.getCurrentRoom().getRoomItems().get(i);
            if (item.getName().equalsIgnoreCase(itemInString)) {
                describeItem = item;
                break;
            } else {
                continue;
            }
        }

        if (describeItem != null) {
            return describeItem.getName()+": "+describeItem.getDescription();
        } else {
            return "There is no item of that type in this room or inventory.";
        }
    }

    @Override
    public String getName() {
        return "describe";
    }

    @Override
    public String getDescription() {
        return "Describes an item in the room or in inventory";
    }

    @Override
    public String getSynonyms() {
        return "describe, explain, info";
    }

    @Override
    public String getFailedResult() {
        return modText;
    }

    @Override
    public boolean getFailed() {
        return failedCmd;
    }
} //end Describe

class UseCommand implements Command {
    private Player player; //author of command (whos running it)
    private Main game; //game instance
    private String itemInString;
    private Item useItem;
    private String modText;
    private boolean failedCmd;

    @Override
    public Optional<Command> parse(Main game, Player player, String text) {
        this.player = player;
        this.game = game;
        failedCmd = false;

        text = text.trim().toLowerCase();
        if (text.matches("^(use|interact)\\s+.+$")) {
            itemInString = text.replaceFirst("^(use|interact)\\s+", "");
            return Optional.of(this);
        } else if (text.matches("^(use|interact)\\s*$")) {
            modText = text.substring(0,1).toUpperCase() + text.substring(1); //capitalise first letter
            modText += " what?";
            failedCmd = true;
            return Optional.of(this);
        }
        return Optional.empty();
    }

    @Override
    public String execute() {
        //find item in room or in inventory
        for (Item item : player.getInventory()) {
            if (item.getName().equalsIgnoreCase(itemInString) || item.getId().equalsIgnoreCase(itemInString)) {
                useItem = item;
                break;
            }
        }
        for (int i=0; i < player.getCurrentRoom().getRoomItems().size(); i++) {
            Item item = (Item) player.getCurrentRoom().getRoomItems().get(i);
            if (item.getName().equalsIgnoreCase(itemInString)) {
                useItem = item;
                break;
            }
        }

        if (useItem != null) {
            if ((useItem instanceof Usable)) {
                //item type specific logic
                String useString = ((Usable)useItem).use();
                if (useItem instanceof StorageItem useItemStorage) {
                    for (Item storedItem : useItemStorage.getInventory()) {
                        player.getCurrentRoom().setRoomItems(game.getMap().getItemById(storedItem.getId()));
                    }
                    useItemStorage.clearInventory();
                }
                
                //general return string
                return useString;
            } else {
                return "This item cannot be used";
            }
        } else {
            return "There is no item of that type in this room or inventory.";
        }
    }

    @Override
    public String getName() {
        return "use";
    }

    @Override
    public String getDescription() {
        return "Uses a consumable or interactable item";
    }

    @Override
    public String getSynonyms() {
        return "interact";
    }

    @Override
    public String getFailedResult() {
        return modText;
    }

    @Override
    public boolean getFailed() {
        return failedCmd;
    }
} //end Use

class FixCommand implements Command {
    private Player player; //author of command (whos running it)
    private Main game; //game instance
    private String itemInString;
    private Item fixItem;
    private String modText;
    private boolean failedCmd;

    @Override
    public Optional<Command> parse(Main game, Player player, String text) {
        this.player = player;
        this.game = game;
        failedCmd = false;

        text = text.trim().toLowerCase();
        if (text.matches("^(fix|repair)\\s+.+$")) {
            itemInString = text.replaceFirst("^(fix|repair)\\s+", "");
            return Optional.of(this);
        } else if (text.matches("^(fix|repair)\\s*$")) {
            modText = text.substring(0,1).toUpperCase() + text.substring(1); //capitalise first letter
            modText += " what?";
            failedCmd = true;
            return Optional.of(this);
        }
        return Optional.empty();
    }

    @Override
    public String execute() {
        //find item in room
        for (int i=0; i < player.getCurrentRoom().getRoomItems().size(); i++) {
            Item item = (Item) player.getCurrentRoom().getRoomItems().get(i);
            if (item.getId().equalsIgnoreCase(itemInString)) {
                fixItem = item;
                break;
            }
        }

        if (fixItem != null) {
            if ((fixItem.getId().equalsIgnoreCase("brokenpower"))) {
                player.getCurrentRoom().removeRoomItem(fixItem);
                player.getCurrentRoom().setRoomItems(game.getMap().getItemById("workingpower"));
                return "Power unit fixed!";
            } else {
                return "This item cannot be fixed.";
            }
        } else if (itemInString.matches("^(rocket|spacecraft|space craft|spaceship)\\s*")) {
            if ((player.getInventory().contains(game.getMap().getItemById("thruster")))
                && (player.getInventory().contains(game.getMap().getItemById("fuel")))
                && (player.getInventory().contains(game.getMap().getItemById("idcard")))
                && (player.getInventory().contains(game.getMap().getItemById("chip")))
                && (player.getInventory().contains(game.getMap().getItemById("gearbox")))
            ) {
                game.setGameRunning(false);
                game.getMap().getGoalById(13).setSolved(true);
                return "You won the game and got to space!";
            }
            return "You do not have all the necessary pieces to repair the rocket!";
        } else {
            return "There is no item of that type in this room or inventory.";
        }
    }

    @Override
    public String getName() {
        return "fix";
    }

    @Override
    public String getDescription() {
        return "Fixes a broken object";
    }

    @Override
    public String getSynonyms() {
        return "repair";
    }

    @Override
    public String getFailedResult() {
        return modText;
    }

    @Override
    public boolean getFailed() {
        return failedCmd;
    }
} //end Fix

class GoCommand implements Command {
    private String direction;
    private Player player; //author of command (whos running it)
    private Main game; //game instance
    private String modText;
    private boolean failedCmd;

    @Override
    public Optional<Command> parse(Main game, Player player, String text) {
        this.player = player;
        this.game = game;
        failedCmd = false;

        //TODO: fix 'travel to/to the' without args must return "...where?"

        text = text.trim().toLowerCase();
        //.+$ -> better than w+, can capture multi-word input
        if (text.matches("^(go|move|walk|travel)( to| to the)?\\s+.+$")) {
            direction = text.replaceFirst("^(go|move|walk|travel)( to| to the)?\\s+", "");
            return Optional.of(this);
        } else if (text.matches("^(go|move|walk|travel)")) {
            modText = text.substring(0,1).toUpperCase() + text.substring(1); //capitalise first letter
            modText += " where?";
            failedCmd = true;
            return Optional.of(this);
        }
        return Optional.empty();
    }

    @Override
    public String execute() {
        //if valid enum direction && valid direction for room exits
        ExitDirection enumDirection;
        switch (direction) {
            case "n", "north", "up":
                enumDirection = ExitDirection.NORTH;
                break;
            case "e", "east", "right":
                enumDirection = ExitDirection.EAST;
                break;
            case "w", "west", "left":
                enumDirection = ExitDirection.WEST;
                break;
            case "s", "south", "down":
                enumDirection = ExitDirection.SOUTH;
                break;
            case "ne", "northeast", "north-east", "north east":
                enumDirection = ExitDirection.NORTH_EAST;
                break;
            case "nw", "northwest", "north-west", "north west":
                enumDirection = ExitDirection.NORTH_WEST;
                break;
            case "se", "southeast", "south-east", "south east":
                enumDirection = ExitDirection.SOUTH_EAST;
                break;
            case "sw", "southwest", "south-west", "south west":
                enumDirection = ExitDirection.SOUTH_WEST;
                break;
            default:
                enumDirection = null;
                break;
        }

        if (enumDirection != null) {
            System.out.println(enumDirection);

            if (player.getCurrentRoom().hasExit(enumDirection)) {
                Room targetRoom = player.getCurrentRoom().getExit(enumDirection);

                if (game.getMap().getRooms().contains(targetRoom)) {
                    int roomIndexInMap = game.getMap().getRooms().indexOf(targetRoom);
                    Room roomInMap = game.getMap().getRooms().get(roomIndexInMap);

                    //check that power is working to exit airlock
                    if (roomInMap.getId().equalsIgnoreCase("airlock")) {
                        if (!(game.getMap().getRoomById("base_corridor").getRoomItems().contains(game.getMap().getItemById("workingpower")))) {
                            return "Airlock cannot be used while the power is out!";
                        }
                    }

                    player.setCurrentRoom(roomInMap);
                    return roomInMap.getName()+": "+roomInMap.getDescription()+"\nExits: "+roomInMap.getExitString()+"\nItems: "+roomInMap.printRoomItems();
                }
            } else {
                return "There is no exit '"+enumDirection+"' from this location, please try again.";
            }
        } else {
            return "Not a valid direction, please try again.";
        }

        return "Invalid direction, please try again.";
    }

    @Override
    public String getName() {
        return "go";
    }

    @Override
    public String getDescription() {
        return "Move to another room";
    }

    @Override
    public String getSynonyms() {
        return "go, move, walk, travel";
    }

    @Override
    public String getFailedResult() {
        return modText;
    }
    
    @Override
    public boolean getFailed() {
        return failedCmd;
    }
} //end Go

class SaveCommand implements Command {
    private Player player; //author of command (whos running it)
    private Main game; //game instance

    @Override
    public Optional<Command> parse(Main game, Player player, String text) {
        this.player = player;
        this.game = game;

        text = text.trim().toLowerCase();
        if (text.equals("save")) {
            return Optional.of(this);
        }
        return Optional.empty();
    }

    @Override
    public String execute() {
        return game.getSaveManager().save(game);
    }

    @Override
    public String getName() {
        return "save";
    }

    @Override
    public String getDescription() {
        return "Save the game";
    }

    @Override
    public String getSynonyms() {
        return "";
    }

    @Override
    public String getFailedResult() {
        return "";
    }

    @Override
    public boolean getFailed() {
        return false;
    }
} //end Save

class LoadCommand implements Command {
    private Player player; //author of command (whos running it)
    private Main game; //game instance
    private String pathToSaveFile;
    private String modText;
    private boolean failedCmd;

    @Override
    public Optional<Command> parse(Main game, Player player, String text) {
        this.player = player;
        this.game = game;
        failedCmd = false;

        text = text.trim().toLowerCase();
        if (text.matches("^(load)?\\s+.+$")) {
            pathToSaveFile = text.replaceFirst("^(load)\\s+", "");
            return Optional.of(this);
        } else if (text.equalsIgnoreCase("load")) {
            modText = text.substring(0,1).toUpperCase() + text.substring(1); //capitalise first letter
            modText += " what?";
            failedCmd = true;
            return Optional.of(this);
        }
        return Optional.empty();
    }

    @Override
    public String execute() {
        SaveManager saveManager = game.getSaveManager();
        player = saveManager.load(Path.of(pathToSaveFile)).get();
        game.setPlayer(player);
        return "Loaded save successfully";
    }

    @Override
    public String getName() {
        return "load";
    }

    @Override
    public String getDescription() {
        return "Load a saved state of the game";
    }

    @Override
    public String getSynonyms() {
        return "";
    }

    @Override
    public String getFailedResult() {
        return modText;
    }

    @Override
    public boolean getFailed() {
        return failedCmd;
    }
} //end Load

class QuitCommand implements Command {
    private Player player; //author of command (whos running it)
    private Main game; //game instance

    @Override
    public Optional<Command> parse(Main game, Player player, String text) {
        this.player = player;
        this.game = game;

        text = text.trim().toLowerCase();
        if (text.equals("quit") || text.equals("exit")) {
            return Optional.of(this);
        }
        return Optional.empty();
    }

    @Override
    public String execute() {
        game.setGameRunning(false);
        return "Thank you for playing, goodbye!";
    }

    @Override
    public String getName() {
        return "quit";
    }

    @Override
    public String getDescription() {
        return "End the game";
    }

    @Override
    public String getSynonyms() {
        return "quit, exit";
    }

    @Override
    public String getFailedResult() {
        return "";
    }
    
    @Override
    public boolean getFailed() {
        return false;
    }
} //end 

class AttackCommand implements Command {
    private Player player; //author of command (whos running it)
    private Main game; //game instance

    @Override
    public Optional<Command> parse(Main game, Player player, String text) {
        this.player = player;
        this.game = game;

        text = text.trim().toLowerCase();
        if (text.matches("^(attack|hit|punch)\\s*$")) {
            return Optional.of(this);
        }
        return Optional.empty();
    }

    @Override
    public String execute() {
        //find if player current room has alien
        Room playerRoom  = player.getCurrentRoom();

        Alien attackee = null;
        for (Alien alien : game.getMap().getAliens()) {
            if (alien.getCurrentRoom().equals(playerRoom)) {
                attackee = alien;
                break; //get first alien
            }
        }

        Item alienDrop1 = null;
        Item alienDrop2 = null;
        for (Item item : game.getMap().getItems()) {
            if (item.getId().equals("fuel")) alienDrop1 = item;
            if (item.getId().equals("thruster")) alienDrop2= item;
        }

        //attack
        if (attackee != null) {
            if (attackee.getDefeated()==false) {
                //if not defeated, attack
                attackee.setCurrentHealth(attackee.getCurrentHealth() - player.getAttackDamage());
                if (attackee.getCurrentHealth() <= 0) {
                    attackee.setDefeated(true);
                    switch (attackee.getName()) {
                        case "Alien 1":
                            player.setInventory(alienDrop1);
                            game.getMap().getGoalById(9).setSolved(true);
                            break;
                        case "Alien 2":
                            player.setInventory(alienDrop2);
                            game.getMap().getGoalById(10).setSolved(true);
                            break;
                    }
                    return "Alien defeated! Check inventory for acquired items!";
                }
                return "Alien attacked! Health remaining: "+attackee.getCurrentHealth();
            } else {
                return "Alien already defeated!";
            }
        }
        return "Attacked nothing...";
    }

    @Override
    public String getName() {
        return "attack";
    }

    @Override
    public String getDescription() {
        return "Attacks if alien in the room";
    }

    @Override
    public String getSynonyms() {
        return "hit, punch";
    }

    @Override
    public String getFailedResult() {
        return "";
    }

    @Override
    public boolean getFailed() {
        return false;
    }
} //end Attack

class HelpCommand implements Command {
    private Player player; //author of command (whos running it)
    private Main game; //game instance
    private String itemInString;

    @Override
    public Optional<Command> parse(Main game, Player player, String text) {
        this.player = player;
        this.game = game;

        text = text.trim().toLowerCase();
        if (text.matches("^(help|commands)\\s+.+$")) {
            itemInString = text.replaceFirst("^(help|commands)\\s+", "");
            return Optional.of(this);
        } else if (text.matches("^(help|commands)\\s*$")) {
            itemInString = "";
            return Optional.of(this);
        }
        return Optional.empty();
    }

    @Override
    public String execute() {
        CommandManager cmdManager = new CommandManager();
        if (!itemInString.isEmpty() || !itemInString.equals("")) {
            for (Command cmd : cmdManager.getAllCommands()) {
                if (cmd.getName().equalsIgnoreCase(itemInString)) {
                    return cmd.getName()+ "\n" + cmd.getDescription() + "\nSynonyms: " + cmd.getSynonyms();
                }
            }
            //if no matching command to arg
            return "Command now found, please try again.";
        } else {
            String possibleCommands = "Possible commands: ";
            for (Command cmd : cmdManager.getAllCommands()) possibleCommands += "\n\t" + cmd.getName();
            return possibleCommands;
        }
    }

    @Override
    public String getName() {
        return "help";
    }

    @Override
    public String getDescription() {
        return "Shows help for specific commands or all possible commands";
    }

    @Override
    public String getSynonyms() {
        return "help, commands";
    }

    @Override
    public String getFailedResult() {
        return "";
    }

    @Override
    public boolean getFailed() {
        return false;
    }
} //end Help

class LookCommand implements Command {
    private Player player; //author of command (whos running it)
    private Main game; //game instance
    private String itemInString;
    private String modText;
    private boolean failedCmd;

    @Override
    public Optional<Command> parse(Main game, Player player, String text) {
        this.player = player;
        this.game = game;
        failedCmd = false;

        text = text.trim().toLowerCase();
        if (text.matches("^(look|examine|scan)\\s+.+$")) {
            itemInString = text.replaceFirst("^(look|examine|scan)\\s+", "");
            return Optional.of(this);
        } else if (text.matches("^(look|examine|scan)")) {
            modText = text.substring(0,1).toUpperCase() + text.substring(1); //capitalise first letter
            modText += " what?";
            failedCmd = true;
            return Optional.of(this);
        }
        return Optional.empty();
    }

    @Override
    public String execute() {
        //possible matches
        ArrayList<String> itemSearchArray = new ArrayList<>(); //array of all item ids and names
        for (int i=0; i < player.getCurrentRoom().getRoomItems().size(); i++) {
            Item item = (Item) player.getCurrentRoom().getRoomItems().get(i);
            itemSearchArray.add(item.getId());
            itemSearchArray.add(item.getName());
        }

        if (itemInString.matches("^(around|room|here)")) {
            return player.getCurrentRoom().getLongDescription(); //look around room
        } else {
            //look at items
            if (!itemSearchArray.isEmpty()) for (String itemPossibleString : itemSearchArray) {
                if (itemPossibleString.equalsIgnoreCase(itemInString)) {
                    //if string matches, find corresponding item

                    for (Item item : (ArrayList<Item>) player.getCurrentRoom().getRoomItems()) {
                        String itemReturn = "";
                        if ((itemPossibleString.equalsIgnoreCase(item.getId())) || (itemPossibleString.equalsIgnoreCase(item.getName()))) {
                            itemReturn += item.getDescription();
                        }

                        //storage items
                        if (item instanceof StorageItem storageItem) {
                            itemReturn += "\n" + storageItem.showInventory();
                        }

                        return itemReturn;
                    }
                }
            }
        }
       return "Nothing to look at, please try again.";
    }

    @Override
    public String getName() {
        return "look";
    }

    @Override
    public String getDescription() {
        return "Look around";
    }

    @Override
    public String getSynonyms() {
        return "look, examine, scan";
    }

    @Override
    public String getFailedResult() {
        return modText;
    }

    @Override
    public boolean getFailed() {
        return failedCmd;
    }
}

class EatCommand implements Command {
    private Player player; //author of command (whos running it)
    private Main game; //game instance
    private String itemInString;
    private FoodItem foodItem;
    private String modText;
    private boolean failedCmd;

    @Override
    public Optional<Command> parse(Main game, Player player, String text) {
        this.player = player;
        this.game = game;
        failedCmd = false;

        text = text.trim().toLowerCase();
        if (text.matches("^(eat|consume|devour|snack)\\s+.+$")) {
            itemInString = text.replaceFirst("^(eat|consume|devour|snack)\\s+", "");
            return Optional.of(this);
        } else if (text.matches("^(eat|consume|devour|snack)")) {
            modText = text.substring(0,1).toUpperCase() + text.substring(1); //capitalise first letter
            modText += " what?";
            failedCmd = true;
            return Optional.of(this);
        }
        return Optional.empty();
    }

    @Override
    public String execute() {
        //find item in inventory
        for (Item item : player.getInventory()) {
            if ((item.getName().equalsIgnoreCase(itemInString) || item.getId().equalsIgnoreCase(itemInString)) && item instanceof FoodItem) {
                foodItem = (FoodItem) item;
                break;
            }
        }

        //verify it exists
        if (foodItem != null) {
        //check its in inventory + remove from inventory + add to room
            if (player.getInventory().contains(foodItem)) {

                if (foodItem.getId().equalsIgnoreCase("blackmold")) {
                    game.setGameRunning(false);
                    return String.format("Consumed %s!\n", foodItem.getName())+foodItem.getConsumptionMessage()+"\nIt poisons your digestive system.\nYou died.";
                }

                if (player.removeFromInventory(foodItem)) {
                    return String.format("Consumed %s!\n", foodItem.getName())+foodItem.getConsumptionMessage();
                } else {
                    return String.format("Cannot eat %s", foodItem.getName());
                }
            }
        } else {
            return "There is no item of that type in your inventory.";
        }
        return "Invalid item, please try again.";
    }

    @Override
    public String getName() {
        return "eat";
    }

    @Override
    public String getDescription() {
        return "Eat something";
    }

    @Override
    public String getSynonyms() {
        return "eat, consume, devour, snack";
    }

    @Override
    public String getFailedResult() {
        return modText;
    }

    @Override
    public boolean getFailed() {
        return failedCmd;
    }
} //end Eat

class ShowCommand implements Command {
    private Player player; //author of command (whos running it)
    private Main game; //game instance
    private String target;
    private String modText;
    private boolean failedCmd;

    @Override
    public Optional<Command> parse(Main game, Player player, String text) {
        this.player = player;
        this.game = game;
        failedCmd = false;

        text = text.trim().toLowerCase();
        if (text.matches("^(show|display)\\s+(inventory|inv|room|items)$")) {
            //TODO: fix -> ??? dont remember what to fix here? it works tho?
            target = text.replaceFirst("^(show|display)\\s+", "");
            return Optional.of(this);
        } else if (text.matches("^(inventory|inv)")) {
            target = "inventory";
            return Optional.of(this);
        } else if (text.matches("^(show|display)")) {
            modText = text.substring(0,1).toUpperCase() + text.substring(1); //capitalise first letter
            modText += " what?";
            failedCmd = true;
            return Optional.of(this);
        }
        return Optional.empty();
    }

    @Override
    public String execute() {
        if (target.equals("inventory") || target.equals("inv")) {
            return player.printInventory();
        } else if (target.equals("room") || target.equals("items")) {
            return player.getCurrentRoom().getLongDescription();
        } 
        return "Invalid option, please try again.";
    }

    @Override
    public String getName() {
        return "show";
    }

    @Override
    public String getDescription() {
        return "Display inventory or items in room";
    }

    @Override
    public String getSynonyms() {
        return "show, display";
    }
    
    @Override
    public String getFailedResult() {
        return modText;
    }

    @Override
    public boolean getFailed() {
        return failedCmd;
    }
} //end show

class GiveCommand implements Command {
    private Player player; //author of command (whos running it)
    private Main game; //game instance
    private String target;
    private String modText;
    private boolean failedCmd;

    @Override
    public Optional<Command> parse(Main game, Player player, String text) {
        this.player = player;
        this.game = game;
        failedCmd = false;

        text = text.trim().toLowerCase();
        if (text.matches("^(give)\\s+.+$")) {
            target = text.replaceFirst("^(give)\\s+", "");
            return Optional.of(this);
        } else if (text.matches("^(give)")) {
            modText = text.substring(0,1).toUpperCase() + text.substring(1); //capitalise first letter
            modText += " what?";
            failedCmd = true;
            return Optional.of(this);
        }
        return Optional.empty();
    }

    @Override
    public String execute() {
        Item targetItem = game.getMap().getItemById(target);

        try {
            Properties properties = game.getProperties();
            properties.load(new FileInputStream("src\\main\\java\\com\\amelithic\\zorkgame\\config\\config.properties"));
            String adminEnabled = properties.getProperty("engine.admin_commands").trim();

            if (targetItem != null) {
                if (adminEnabled.equalsIgnoreCase("true")) {
                    player.setInventory(targetItem);
                    return targetItem.getName()+" added to inventory!";
                }
                return "Cannot add item as admin_commands is false.";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        return "Invalid option, please try again.";
    }

    @Override
    public String getName() {
        return "give";
    }

    @Override
    public String getDescription() {
        return "Give player any item in game! (Admin)";
    }

    @Override
    public String getSynonyms() {
        return "";
    }
    
    @Override
    public String getFailedResult() {
        return modText;
    }

    @Override
    public boolean getFailed() {
        return failedCmd;
    }
} //end give

class WinCommand implements Command {
    private Player player; //author of command (whos running it)
    private Main game; //game instance
    private String target;

    @Override
    public Optional<Command> parse(Main game, Player player, String text) {
        this.player = player;
        this.game = game;

        text = text.trim().toLowerCase();
        if (text.equalsIgnoreCase("win") || text.equalsIgnoreCase("chris")) {
            return Optional.of(this);
        }
        return Optional.empty();
    }

    @Override
    public String execute() {
        try {
            Properties properties = game.getProperties();
            properties.load(new FileInputStream("src\\main\\java\\com\\amelithic\\zorkgame\\config\\config.properties"));
            String adminEnabled = properties.getProperty("engine.admin_commands").trim();

            if (adminEnabled.equalsIgnoreCase("true")) {
                    for (Item item : game.getMap().getItems()) {
                        if (item instanceof RequiredItem) player.setInventory(item);
                    }
                    for (Room<GameMap.ExitDirection> room : game.getMap().getRooms()) {
                        if (room.getId().equals("broken_spacecraft")) player.setCurrentRoom(room);
                    }
                
                    return "Win conditions manually added.";
            }
            return "Cannot run command as admin_commands is false.";
            
        } catch (Exception e) {
            e.printStackTrace();
        }


        return "Invalid option, please try again.";
    }

    @Override
    public String getName() {
        return "win";
    }

    @Override
    public String getDescription() {
        return "Automatically creates win condition! (Admin)";
    }

    @Override
    public String getSynonyms() {
        return "chris";
    }
    
    @Override
    public String getFailedResult() {
        return "";
    }

    @Override
    public boolean getFailed() {
        return false;
    }
} //end win

class SayCommand implements Command {
    //for future use: LAN multiplayer option
    private Player author; //author of command (whos running it)
    private Player recipient; //author of command (whos running it)
    private Main game; //game instance
    private String message;

    @Override
    public Optional<Command> parse(Main game, Player player, String text) {
        this.author = player;
        this.game = game;

        text = text.trim().toLowerCase();
        if (text.startsWith("say ") || text.startsWith("speak ") || text.startsWith("tell ")) {
            message = text.replaceFirst("^(say|speak|tell)\\s+", "");
            return Optional.of(this);
        }
        return Optional.empty();
    }

    @Override
    public String execute() {
        //TODO: logic
        return author.getName()+"> "+message;
    }

    @Override
    public String getName() {
        return "say";
    }

    @Override
    public String getDescription() {
        return "Repeats input from user to screen";
    }

    @Override
    public String getSynonyms() {
        return "say, speak, tell";
    }

    @Override
    public String getFailedResult() {
        return "";
    }

    @Override
    public boolean getFailed() {
        return false;
    }
}

class GoalsCommand implements Command {
    private Player player; //author of command (whos running it)
    private Main game; //game instance

    @Override
    public Optional<Command> parse(Main game, Player player, String text) {
        this.player = player;
        this.game = game;

        text = text.trim().toLowerCase();
        if (text.equals("goals")) {
            return Optional.of(this);
        }
        return Optional.empty();
    }

    @Override
    public String execute() {
        String goalsRemaining = "Goals: ";
        for (Goal goal : game.getMap().getGoals()) {
            if (goal.getSolved() != true) goalsRemaining += "\n\t" + goal.getName();
        }
        return goalsRemaining + ((goalsRemaining.equals("Goals: "))? "No goals remaining!" : "");
    }

    @Override
    public String getName() {
        return "goals";
    }

    @Override
    public String getDescription() {
        return "Show remaining goals";
    }

    @Override
    public String getSynonyms() {
        return "";
    }

    @Override
    public String getFailedResult() {
        return "";
    }

    @Override
    public boolean getFailed() {
        return false;
    }
} //end Goals