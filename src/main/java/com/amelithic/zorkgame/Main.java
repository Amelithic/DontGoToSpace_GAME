/* Don't Go To Space Game by Amelithic */


package com.amelithic.zorkgame;

import java.io.FileInputStream;
import java.nio.file.Path;
import java.util.Properties;

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
        Path fileNameMap = Path.of("config/map_default.json");
        this.map = new GameMap(fileNameMap);
        this.saveManager = new SaveManager();
        this.properties = new Properties();
        this.gameRunning = true;

        try {
            properties.load(new FileInputStream("config/config.properties"));
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
    public void setMap(GameMap map) {
        this.map = map;
    }

    public static Player getPlayer() {
        return player;
    }
    public void setPlayer(Player player) {
        this.player = player;
    }

    public static SaveManager getSaveManager() {
        return saveManager;
    }

    public boolean getGameRunning() {
        return gameRunning;
    }
    public void setGameRunning(boolean gameRunning) {
        this.gameRunning = gameRunning;
    }

    public static Properties getProperties() {
        return properties;
    }
    
    //main
    public static void main(String[] args) {
        Main game = new Main(); // initialise everything once

        String uiMode = game.getProperties().getProperty("ui.ui_mode", "cli").trim();
        switch (uiMode) {
            case "gui":
                System.out.println("GUI SELECTED");
                Application.launch(GUI.class, args);
                break;
            case "cli":
                System.out.println("CLI SELECTED");
                CLI.run(game);
                break;
            default:
                System.err.println("Unknown UI mode: " + uiMode);
        }
    }
}