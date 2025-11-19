/* Don't Go To Space Game by Amelithic */


package com.amelithic.zorkgame;

import java.nio.file.Path;
import java.util.Optional;
import java.util.Scanner;

import com.amelithic.zorkgame.characters.Character;

public class Main {
    private Character player;
    private GameMap map;

    public Main() {
        Path fileNameMap = Path.of("src\\main\\java\\com\\amelithic\\zorkgame\\config\\map_default.json");
        map = new GameMap(fileNameMap);

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

    public static void main(String[] args) {
        Main gameState = new Main();
        Scanner scanner = new Scanner(System.in);
        CommandManager commandManager = new CommandManager();

        boolean gameRunning = true;
        while (gameRunning) {
            System.out.print("> ");
            String input = scanner.nextLine();

            boolean matched = false;
            Optional<Command> cmd = commandManager.parse(input);
            if (cmd.isPresent()) {
                //cmd.get().execute(gameState, player);
                matched = true;
                break;
            }

            if (!matched) {
                System.out.println("I don't understand that command.");
            }

        }
        System.out.println("Thank you for playing. Goodbye.");
    }
}