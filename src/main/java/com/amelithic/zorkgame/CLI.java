package com.amelithic.zorkgame;

import java.util.Optional;
import java.util.Scanner;

public class CLI {
    public static void run(Main game) {
        Scanner scanner = new Scanner(System.in);
        CommandManager commandManager = new CommandManager();

        while (game.getGameRunning()) {
            System.out.print("> ");
            String input = scanner.nextLine().trim();
            Optional<Command> cmdCheck = commandManager.parse(game, game.getPlayer(), input);
            if (cmdCheck.isPresent()) {
                System.out.println(cmdCheck.get().execute());
            } else {
                System.out.println("I don't understand that command.");
            }
        }
        System.out.println("Thank you for playing. Goodbye.");
    }
}
