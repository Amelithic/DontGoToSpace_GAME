package com.amelithic.zorkgame;

import java.util.Optional;
import java.util.Scanner;

public class CLI {
    public static void run(Main game) {
        Scanner scanner = new Scanner(System.in);
        CommandManager commandManager = new CommandManager();

        System.out.println(titleText());
        System.out.println("Welcome...\nWhat is your name?");
        game.getPlayer().setName(scanner.nextLine());
        System.out.println(game.getPlayer().displayInfo());
        System.out.println("\n"+game.getPlayer().getCurrentRoom().getLongDescription());

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

    public static String titleText() {
        return """
               ________                 /\\  __        ________            ___________           _________                              \r
               \\______ \\    ____    ____\\(_/  |_     /  _____/   ____     \\__    ___/____      /   _____/______ _____     ____   ____  \r
                |    |  \\  /  _ \\  /    \\ \\   __\\   /   \\  ___  /  _ \\      |    |  /  _ \\     \\_____  \\ \\____ \\\\__  \\  _/ ___\\_/ __ \\ \r
                |    `   \\(  <_> )|   |  \\ |  |     \\    \\_\\  \\(  <_> )     |    | (  <_> )    /        \\|  |_> >/ __ \\_\\  \\___\\  ___/ \r
               /_______  / \\____/ |___|  / |__|      \\______  / \\____/      |____|  \\____/    /_______  /|   __/(____  / \\___  >\\___  >\r
                       \\/              \\/                   \\/                                        \\/ |__|        \\/      \\/     \\/ """;
    }
}
