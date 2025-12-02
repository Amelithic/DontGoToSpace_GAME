/* Don't Go To Space Game by Amelithic */


package com.amelithic.zorkgame;

import java.io.FileInputStream;
import java.nio.file.Path;
import java.util.Optional;
import java.util.Properties;
import java.util.Scanner;

import com.amelithic.zorkgame.characters.Player;
import com.amelithic.zorkgame.locations.Room;

import javafx.application.Application;

public class Main {
    //fields
    private static Player player;
    private static boolean gameRunning;
    private static GameMap map;
    private static SaveManager saveManager;
    private static Properties properties;

    //constructors
    public Main() {
        Path fileNameMap = Path.of("src\\main\\java\\com\\amelithic\\zorkgame\\config\\map_default.json");
        map = new GameMap(fileNameMap);
        saveManager = new SaveManager();
        gameRunning = true;

        properties = new Properties();

        try {
            properties.load(new FileInputStream("src\\main\\java\\com\\amelithic\\zorkgame\\config\\config.properties"));
            String startRoomId = properties.getProperty("engine.start_room").trim();
            Room startRoom = null;
            for (Room room : map.getRooms()) {
                if (room.getId().equals(startRoomId)) startRoom = room;
            }
            //TODO: move player to main() -> ask for userninput for name
            if (startRoom != null) {
                player = new Player("Unnamed", map.getRooms().get(map.getRooms().indexOf(startRoom)),100,10);
                System.out.println(player.displayInfo());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //getters
    public static GameMap getMap() {
        return map;
    }
    public static void setMap(GameMap map) {
        Main.map = map;
    }

    public static Player getPlayer() {
        return player;
    }
    public static void setPlayer(Player player) {
        Main.player = player;
    }

    public static SaveManager getSaveManager() {
        return saveManager;
    }
    public void setGameRunning(boolean gameRunning) {
        Main.gameRunning = gameRunning;
    }
    
    //main
    public static void main(String[] args) {
        Main gameState = new Main();
        Scanner scanner = new Scanner(System.in);
        CommandManager commandManager = new CommandManager();

        try {
            String uiMode = properties.getProperty("ui.ui_mode").trim();
            System.out.println(uiMode);
            if (uiMode != null) {
                switch (uiMode) {
                    case "gui":
                        System.err.println("GUI SELECTED");
                        Application.launch(GUI.class, args);
                        break;
                    case "cli":
                        System.err.println("CLI SELECTED");
                        break;
                    default:
                        System.err.println("property not found");
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        while (gameRunning) {
            System.out.print("> ");
            String input = scanner.nextLine();

            Optional<Command> cmdCheck = commandManager.parse(gameState, player, input);
            if (cmdCheck.isPresent()) {
                Command cmd = cmdCheck.get();
                System.out.println(cmd.execute());
            } else {
                System.out.println("I don't understand that command.");
            }

        }
        System.out.println("Thank you for playing. Goodbye.");
    }
}