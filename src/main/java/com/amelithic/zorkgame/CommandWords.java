package com.amelithic.zorkgame;

import java.util.HashMap;
import java.util.Map;

public class CommandWords {
    private Map<String, String> validCommands;

    public CommandWords() {
        validCommands = new HashMap<>();
        validCommands.put("go", "Move to another room");
        validCommands.put("quit", "End the game");
        validCommands.put("help", "Show help");
        validCommands.put("look", "Look around");
        validCommands.put("eat", "Eat something");
        validCommands.put("show", "Display inventory or items in room");
        validCommands.put("take", "Takes item from room and adds to inventory");
        validCommands.put("drop", "Removes item from inventory");
        validCommands.put("say", "Repeats input from user to screen");
        validCommands.put("save", "Saves Character obj.");
        validCommands.put("describe", "Describes an item in the room or in inventory");
    }

    public boolean isCommand(String commandWord) {
        return validCommands.containsKey(commandWord);
    }

    public void showAll() {
        System.out.print("Valid commands are: ");
        for (String command : validCommands.keySet()) {
            System.out.print(command + " ");
        }
        System.out.println();
    }
}
