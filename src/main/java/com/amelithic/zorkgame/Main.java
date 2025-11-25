/* Don't Go To Space Game by Amelithic */


package com.amelithic.zorkgame;

import java.nio.file.Path;
import java.util.Optional;
import java.util.Scanner;

import com.amelithic.zorkgame.characters.Character;

public class Main {
    private static Character player;
    private static boolean gameRunning;
    private GameMap map;

    public Main() {
        Path fileNameMap = Path.of("src\\main\\java\\com\\amelithic\\zorkgame\\config\\map_default.json");
        map = new GameMap(fileNameMap);
        gameRunning = true;

        try {
            player = new Character("Amelie", map.getRooms().get(0));
            System.out.println(player.getName()+": "+player.getCurrentRoom().getName());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void printWelcome() {
        System.out.println();
        System.out.println("Welcome to the University adventure!");
        System.out.println("Type 'help' if you need help.");
        System.out.println();
    }

    private void printHelp() {
        System.out.println("You are lost. You are alone. You wander around the university.");
        System.out.print("Your command words are: ");
    }

    public GameMap getMap() {
        return map;
    }
    public void setGameRunning(boolean gameRunning) {
        Main.gameRunning = gameRunning;
    }
    

    public static void main(String[] args) {
        Main gameState = new Main();
        Scanner scanner = new Scanner(System.in);
        CommandManager commandManager = new CommandManager();

        while (gameRunning) {
            System.out.print("> ");
            String input = scanner.nextLine();

            Optional<Command> cmdCheck = commandManager.parse(gameState, player, input);
            if (cmdCheck.isPresent()) {
                Command cmd = cmdCheck.get();
                cmd.execute();
            } else {
                System.out.println("I don't understand that command.");
            }

        }
        System.out.println("Thank you for playing. Goodbye.");
    }
}