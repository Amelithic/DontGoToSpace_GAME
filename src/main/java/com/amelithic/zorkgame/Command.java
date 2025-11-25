package com.amelithic.zorkgame;

import java.util.Optional;

import com.amelithic.zorkgame.GameMap.ExitDirection;
import com.amelithic.zorkgame.characters.Character;
import com.amelithic.zorkgame.items.Item;
import com.amelithic.zorkgame.locations.Room;

//TODO: Complete all command logic before commmit

public interface Command {
    Optional<Command> parse(Main game, Character player, String text); //parsing inputs
    void execute(); //command logic

    //Output text
    String getName();
    String getDescription();
    String getSynonyms();
}


class TakeItemCommand implements Command {
    //fields
    private String itemInString;
    private Character player; //author of command (whos running it)
    private Main game; //game instance
    private Item takeItem;

    //methods
    @Override
    public Optional<Command> parse(Main game, Character player, String text) {
        this.game = game;
        this.player = player;

        text = text.trim().toLowerCase(); //remove spaces and all lowercase
        if (text.matches("^(take|pick up|grab)\\s+.+$")) {
            String itemInString = text.replaceFirst("^(take|pick up|grab)( the)? ", "");
            if (!itemInString.isEmpty()) {
                return Optional.of(this);
            } else {
                return Optional.empty();
            }
        } else if (text.matches("^(take|pick up|grab)\\s*$")) {
            String modText = text.substring(0,1).toUpperCase() + text.substring(1); //capitalise first letter
            System.out.println(modText+" what?");
        }
        return Optional.empty();
    }

    @Override
    public void execute() {
        //find item in room
        for (int i=0; i < player.getCurrentRoom().getRoomItems().size(); i++) {
            Item item = (Item) player.getCurrentRoom().getRoomItems().get(i);

            if (item.getName().equalsIgnoreCase(itemInString)) {
                takeItem = item;
                break;
            } else {
                continue;
            }
        }

        //verify it exists
        if (takeItem != null) {
        //check room contains object + add to inventory + remove from room
            if (player.getCurrentRoom().getRoomItems().contains(takeItem)) {
                player.setInventory(takeItem);
                if (player.getCurrentRoom().removeRoomItem(takeItem)) {
                    System.out.printf("Added %s to inventory!\n", takeItem.getName());
                } else {
                    System.out.println("Cannot add item");
                }

            }
        }
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
} //end Take

class DropCommand implements Command {
    private String itemInString;
    private Character player; //author of command (whos running it)
    private Main game; //game instance
    private Item removeItem;

    @Override
    public Optional<Command> parse(Main game, Character player, String text) {
        this.player = player;
        this.game = game;

        text = text.trim().toLowerCase();
        if (text.matches("^(drop|discard|remove)\\s+.+$")) {
            itemInString = text.replaceFirst("^(drop|discard|remove)\\s+", "");
            return Optional.of(this);
        } else if (text.matches("^(drop|discard|remove)\\s*$")) {
            String modText = text.substring(0,1).toUpperCase() + text.substring(1); //capitalise first letter
            System.out.println(modText+" what?");
        }
        return Optional.empty();
    }

    @Override
    public void execute() {
        //find item in inventory
        for (Item item : player.getInventory()) {
            if (item.getName().equalsIgnoreCase(itemInString)) {
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
                    System.out.printf("Dropped %s!\n", removeItem.getName());
                } else {
                    System.out.println("Cannot drop item");
                }
            }
        } else {
            System.out.println("There is no item of that type in this room.");
        }
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
} //end Drop

class DescribeCommand implements Command {
    private Character player; //author of command (whos running it)
    private Main game; //game instance
    private String itemInString;
    private Item describeItem;

    @Override
    public Optional<Command> parse(Main game, Character player, String text) {
        this.player = player;
        this.game = game;

        text = text.trim().toLowerCase();
        if (text.matches("^(describe|explain|info)\\s+.+$")) {
            itemInString = text.replaceFirst("^(describe|explain|info)\\s+", "");
            return Optional.of(this);
        } else if (text.matches("^(describe|explain|info)\\s*$")) {
            String modText = text.substring(0,1).toUpperCase() + text.substring(1); //capitalise first letter
            System.out.println(modText+" what?");
        }
        return Optional.empty();
    }

    @Override
    public void execute() {
        //find item in room or in inventory
        for (Item item : player.getInventory()) {
            if (item.getName().equalsIgnoreCase(itemInString)) {
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
            System.out.println(describeItem.getName()+": "+describeItem.getDescription());
        } else {
            System.out.println("There is no item of that type in this room or inventory.");
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
} //end Describe

class GoCommand implements Command {
    private String direction;
    private Character player; //author of command (whos running it)
    private Main game; //game instance

    @Override
    public Optional<Command> parse(Main game, Character player, String text) {
        this.player = player;
        this.game = game;

        //TODO: fix 'travel to/to the' without args must return "...where?"

        text = text.trim().toLowerCase();
        //.+$ -> better than w+, can capture multi-word input
        if (text.matches("^(go|move|walk|travel)( to| to the)?\\s+.+$")) {
            direction = text.replaceFirst("^(go|move|walk|travel)( to| to the)?\\s+", "");
            return Optional.of(this);
        } else if (text.matches("^(go|move|walk|travel)( to| to the)?")) {
            String modText = text.substring(0,1).toUpperCase() + text.substring(1); //capitalise first letter
            System.out.println(modText+" where?");
        }
        return Optional.empty();
    }

    @Override
    public void execute() {
        //if valid enum direction && valid direction for room exits
        ExitDirection enumDirection;
        switch (direction) {
            case "north", "up":
                enumDirection = ExitDirection.NORTH;
                break;
            case "east", "right":
                enumDirection = ExitDirection.EAST;
                break;
            case "west", "left":
                enumDirection = ExitDirection.WEST;
                break;
            case "south", "down":
                enumDirection = ExitDirection.SOUTH;
                break;
            case "northeast", "north-east", "north east":
                enumDirection = ExitDirection.NORTH_EAST;
                break;
            case "northwest", "north-west", "north west":
                enumDirection = ExitDirection.NORTH_WEST;
                break;
            case "southeast", "south-east", "south east":
                enumDirection = ExitDirection.SOUTH_EAST;
                break;
            case "southwest", "south-west", "south west":
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
                    player.setCurrentRoom(roomInMap);
                    System.out.println(roomInMap.getName()+": "+roomInMap.getDescription()+"\nExits: "+roomInMap.getExitString());
                }
            } else {
                System.out.println("There is no exit '"+enumDirection+"' from this location, please try again.");
            }
        } else {
            System.out.println("Not a valid direction, please try again.");
        }
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
} //end Go

class QuitCommand implements Command {
    private Character player; //author of command (whos running it)
    private Main game; //game instance

    @Override
    public Optional<Command> parse(Main game, Character player, String text) {
        this.player = player;
        this.game = game;

        text = text.trim().toLowerCase();
        if (text.equals("quit") || text.equals("exit")) {
            return Optional.of(this);
        }
        return Optional.empty();
    }

    @Override
    public void execute() {
        //TODO: logic
        // game.quit();
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
} //end Quit

class HelpCommand implements Command {
    private Character player; //author of command (whos running it)
    private Main game; //game instance

    @Override
    public Optional<Command> parse(Main game, Character player, String text) {
        this.player = player;
        this.game = game;

        text = text.trim().toLowerCase();
        if (text.matches("^(help|commands)\\s+.+$")) {
            return Optional.of(this);
        }
        return Optional.empty();
    }

    @Override
    public void execute() {
        //TODO: logic
        // game.showHelp();
    }

    @Override
    public String getName() {
        return "help";
    }

    @Override
    public String getDescription() {
        return "Show help";
    }

    @Override
    public String getSynonyms() {
        return "help, commands";
    }
} //end Help

class LookCommand implements Command {
    private Character player; //author of command (whos running it)
    private Main game; //game instance

    @Override
    public Optional<Command> parse(Main game, Character player, String text) {
        this.player = player;
        this.game = game;

        text = text.trim().toLowerCase();
        if (text.equals("look") || text.equals("examine") || text.equals("scan")) {
            return Optional.of(this);
        }
        return Optional.empty();
    }

    @Override
    public void execute() {
        //TODO: logic
        // game.lookAround();
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
}

class EatCommand implements Command {
    private Character player; //author of command (whos running it)
    private Main game; //game instance
    private String item;

    @Override
    public Optional<Command> parse(Main game, Character player, String text) {
        this.player = player;
        this.game = game;

        text = text.trim().toLowerCase();
        if (text.matches("^(eat|consume|devour|snack)\\s+.+$")) {
            item = text.replaceFirst("^(eat|consume|devour|snack)\\s+", "");
            return Optional.of(this);
        } else if (text.matches("^(eat|consume|devour|snack)")) {
            String modText = text.substring(0,1).toUpperCase() + text.substring(1); //capitalise first letter
            System.out.println(modText+" what?");
        }
        return Optional.empty();
    }

    @Override
    public void execute() {
        //TODO: logic
        // game.eatItem(item);
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
} //end Eat

class ShowCommand implements Command {
    private Character player; //author of command (whos running it)
    private Main game; //game instance
    private String target;

    @Override
    public Optional<Command> parse(Main game, Character player, String text) {
        this.player = player;
        this.game = game;

        text = text.trim().toLowerCase();
        if (text.matches("^(show|display)\\s+(inventory|inv|room|items)$")) {
            //TODO: fix
            // System.err.println("here");
            target = text.replaceFirst("^(show|display)\\s+", "");
            return Optional.of(this);
        } else if (text.matches("^(inventory|inv)")) {
            target = "inventory";
            return Optional.of(this);
        } else if (text.matches("^(show|display)")) {
            String modText = text.substring(0,1).toUpperCase() + text.substring(1); //capitalise first letter
            System.out.println(modText+" what?");
        }
        return Optional.empty();
    }

    @Override
    public void execute() {
        if (target.equals("inventory") || target.equals("inv")) {
            player.printInventory();
        } else if (target.equals("room") || target.equals("items")) {
            player.getCurrentRoom().printRoomItems();
        }
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
}

class SayCommand implements Command {
    private Character author; //author of command (whos running it)
    private Character recipient; //author of command (whos running it)
    private Main game; //game instance
    private String message;

    @Override
    public Optional<Command> parse(Main game, Character player, String text) {
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
    public void execute() {
        //TODO: logic
        System.out.println(message);
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
}