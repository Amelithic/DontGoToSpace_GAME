package com.amelithic.zorkgame;

import java.util.Optional;

public interface Command {
    Optional<Command> parse(String text); //parsing inputs
    void execute(Main game, Character player); //command logic

    //Output text
    String getName();
    String getDescription();
    String getSynonyms();
}

class TakeItemCommand implements Command {
    //fields
    private String item;

    //methods
    @Override
    public Optional<Command> parse(String text) {
        text = text.trim().toLowerCase(); //remove spaces and all lowercase
        if (text.startsWith("take ") || text.startsWith("pick up ")) {
            String item = text.replaceFirst("^(take|pick up)( the)? ", "");
            return Optional.of(this);
        }
        return Optional.empty();
    }

    @Override
    public void execute(Main game, Character player) {
        //game.takeItem(item);
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
        return "take";
    }
}

class GoCommand implements Command {
    private String direction;

    @Override
    public Optional<Command> parse(String text) {
        text = text.trim().toLowerCase();
        if (text.matches("^(go|move|walk|travel)\\s+\\w+")) {
            direction = text.replaceFirst("^(go|move|walk|travel)\\s+", "");
            return Optional.of(this);
        }
        return Optional.empty();
    }

    @Override
    public void execute(Main game, Character player) {
        // game.movePlayer(direction);
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
}

class QuitCommand implements Command {
    @Override
    public Optional<Command> parse(String text) {
        text = text.trim().toLowerCase();
        if (text.equals("quit") || text.equals("exit")) {
            return Optional.of(this);
        }
        return Optional.empty();
    }

    @Override
    public void execute(Main game, Character player) {
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
}

class HelpCommand implements Command {
    @Override
    public Optional<Command> parse(String text) {
        text = text.trim().toLowerCase();
        if (text.equals("help") || text.equals("commands")) {
            return Optional.of(this);
        }
        return Optional.empty();
    }

    @Override
    public void execute(Main game, Character player) {
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
}

class LookCommand implements Command {
    @Override
    public Optional<Command> parse(String text) {
        text = text.trim().toLowerCase();
        if (text.equals("look") || text.equals("examine") || text.equals("scan")) {
            return Optional.of(this);
        }
        return Optional.empty();
    }

    @Override
    public void execute(Main game, Character player) {
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
    private String item;

    @Override
    public Optional<Command> parse(String text) {
        text = text.trim().toLowerCase();
        if (text.startsWith("eat ") || text.startsWith("consume ") || text.startsWith("devour ") || text.startsWith("snack ")) {
            item = text.replaceFirst("^(eat|consume|devour|snack)\\s+", "");
            return Optional.of(this);
        }
        return Optional.empty();
    }

    @Override
    public void execute(Main game, Character player) {
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
}

class ShowCommand implements Command {
    private String target;

    @Override
    public Optional<Command> parse(String text) {
        text = text.trim().toLowerCase();
        if (text.matches("^(show|display)\\s+(inventory|inv|room|items)$")) {
            target = text.replaceFirst("^(show|display)\\s+", "");
            return Optional.of(this);
        }
        return Optional.empty();
    }

    @Override
    public void execute(Main game, Character player) {
        if (target.equals("inventory") || target.equals("inv")) {
            // game.showInventory();
        } else if (target.equals("room") || target.equals("items")) {
            // game.showRoomItems();
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

class DropCommand implements Command {
    private String item;

    @Override
    public Optional<Command> parse(String text) {
        text = text.trim().toLowerCase();
        if (text.startsWith("drop ") || text.startsWith("discard ") || text.startsWith("remove ")) {
            item = text.replaceFirst("^(drop|discard|remove)\\s+", "");
            return Optional.of(this);
        }
        return Optional.empty();
    }

    @Override
    public void execute(Main game, Character player) {
        // game.dropItem(item);
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
}

class SayCommand implements Command {
    private String message;

    @Override
    public Optional<Command> parse(String text) {
        text = text.trim().toLowerCase();
        if (text.startsWith("say ") || text.startsWith("speak ") || text.startsWith("tell ")) {
            message = text.replaceFirst("^(say|speak|tell)\\s+", "");
            return Optional.of(this);
        }
        return Optional.empty();
    }

    @Override
    public void execute(Main game, Character player) {
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

class DescribeCommand implements Command {
    private String item;

    @Override
    public Optional<Command> parse(String text) {
        text = text.trim().toLowerCase();
        if (text.startsWith("describe ") || text.startsWith("explain ") || text.startsWith("info ")) {
            item = text.replaceFirst("^(describe|explain|info)\\s+", "");
            return Optional.of(this);
        }
        return Optional.empty();
    }

    @Override
    public void execute(Main game, Character player) {
        // game.describeItem(item);
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
}