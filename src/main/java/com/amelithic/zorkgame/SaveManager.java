package com.amelithic.zorkgame;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.amelithic.zorkgame.characters.Alien;
import com.amelithic.zorkgame.characters.Player;
import com.amelithic.zorkgame.items.Item;
import com.amelithic.zorkgame.locations.Room;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

public class SaveManager {
    //fields
    String saveDir;
    private List<Path> saveFilePaths;

    //constructor
    public SaveManager() {
        saveDir = "./saves/";
        saveFilePaths = new ArrayList<>();

        updateSavesArray();
    }

    public List<Path> getSaves() {
        return saveFilePaths;
    }

    public void updateSavesArray() {
        saveFilePaths.clear(); //clear on update

        File dir = new File(saveDir);
        if (!dir.exists() || !dir.isDirectory()) {
            boolean created = dir.mkdir(); // creates directory if none
            if (created) {
                System.out.println("Directory created: " + dir.getAbsolutePath());
            } else {
                System.out.println("Failed to create directory: " + dir.getAbsolutePath());
            }

        }

        //anonymous class to filter only JSON files, then add to saveFilePaths array
        File[] filesInDir = dir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File d, String name) {
                return name.toLowerCase().endsWith(".json");
            }
        });
        if (filesInDir != null) {
            for (File saveFile : filesInDir) {
                saveFilePaths.add(saveFile.toPath());
            }
        }
    }

    //take in gamestate, load file, return player object instead of create new
    //or could have empty person object with setters
    public Optional<Player> load(Path savePath) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            //SaveFile saveFile = mapper.readValue(savePath.toFile(), SaveFile.class);
            JsonNode saveFile = mapper.readTree(savePath.toFile());

            //Manual parse
            JsonNode map = saveFile.get("map");

            //items in rooms
            for (Room<GameMap.ExitDirection> room : Main.getMap().getRooms()) {
                //wipe existing inventory
                room.clearRoomItems();

                //fill room inventory with save items
                ArrayNode saveRoomArray = (ArrayNode) map.get("rooms");
                for (int saveRoomIndex = 0; saveRoomIndex < saveRoomArray.size(); saveRoomIndex++) {
                    String saveRoomId = saveRoomArray.get(saveRoomIndex).get("id").asText();

                    //find saved item ids in saved room, then find equivalent from Main.getMap()
                    List<Item> itemsToLoad = new ArrayList<>();
                    ArrayNode saveItemArray = (ArrayNode) saveRoomArray.get(saveRoomIndex).get("roomItems");
                    if (room.getId().equalsIgnoreCase(saveRoomId)) {
                        for (int saveRoomItemIndex = 0; saveRoomItemIndex < saveItemArray.size(); saveRoomItemIndex++) {
                            String saveRoomItemId = saveItemArray.get(saveRoomItemIndex).get("id").asText();

                            Item itemToLoad = null;
                            for (Item item : Main.getMap().getItems()) {
                                if (item.getId().equalsIgnoreCase(saveRoomItemId)) {
                                    itemToLoad = item;
                                }
                            }

                            if (itemToLoad != null) itemsToLoad.add(itemToLoad);
                        }
                    }

                    //save all to Main.getMap() room
                    for (Item itemToLoad : itemsToLoad) room.setRoomItems(itemToLoad);
                }
            }//end items in room

            //aliens rooms
            for (Alien alien : Main.getMap().getAliens()) {
                //set current room, set is defeated

                //for each saved alien
                ArrayNode saveAliensArray = (ArrayNode) map.get("aliens");
                for (int saveAlienIndex = 0; saveAlienIndex < saveAliensArray.size(); saveAlienIndex++) {
                    //if names match
                    String saveAlienName = saveAliensArray.get(saveAlienIndex).get("name").asText();
                    if (alien.getName().equalsIgnoreCase(saveAlienName)) {
                        String saveAlienRoomId = saveAliensArray.get(saveAlienIndex).get("currentRoom").get("id").asText();
                        boolean saveAlienDefeated = saveAliensArray.get(saveAlienIndex).get("defeated").asBoolean();

                        Room<GameMap.ExitDirection> saveAlienRoom = null;
                        for (Room<GameMap.ExitDirection> room : Main.getMap().getRooms()) {
                            if (room.getId().equalsIgnoreCase(saveAlienRoomId)) saveAlienRoom = room;
                        }

                        alien.setDefeated(saveAlienDefeated);
                        if (saveAlienRoom != null) alien.setCurrentRoom(saveAlienRoom);
                    }
                }
            }//end get aliens

            //goals
            for (Goal goal : Main.getMap().getGoals()) {
                ArrayNode saveGoalArray = (ArrayNode) map.get("goals");
                for (int saveGoalArrayIndex=0; saveGoalArrayIndex < saveGoalArray.size(); saveGoalArrayIndex++) {
                    int saveGoalId = saveGoalArray.get(saveGoalArrayIndex).get("id").asInt();
                    boolean saveGoalSolved = saveGoalArray.get(saveGoalArrayIndex).get("solved").asBoolean();

                    if (goal.getId() == saveGoalId) goal.setSolved(saveGoalSolved);
                }
            }

            //player data and inventory
            JsonNode player = saveFile.get("player");
            String playerName = player.get("name").asText();
            String playerRoomId = player.get("currentRoom").get("id").asText();
            int playerHealthMax = player.get("maxHealth").asInt();
            int playerHealthCurrent = player.get("currentHealth").asInt();
            int playerAttackDamage = player.get("attackDamage").asInt();

            //find room from playerRoomId
            Room<GameMap.ExitDirection> playerCurrentRoom = Main.getMap().getRoomById(playerRoomId);

            Player newPlayer = null;
            if (playerCurrentRoom != null) {
                newPlayer = new Player(playerName, playerCurrentRoom, playerHealthMax, playerHealthCurrent, playerAttackDamage);

                //load inventory
                ArrayNode playerInv = (ArrayNode) player.get("inventory");
                for (int i=0; i < playerInv.size(); i++) {
                    String loadItemId = playerInv.get(i).get("id").asText();
                    Item itemInv = Main.getMap().getItemById(loadItemId);
                    if (itemInv != null) newPlayer.setInventory(itemInv);
                }

                System.out.println("Loaded save successfully: " + savePath);
                return Optional.of(newPlayer);
            }

            return Optional.of(newPlayer);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }
    
    public String save(Main gameState) {
        //serialisation with Jackson
        ObjectMapper mapper = new ObjectMapper();

        //SaveFile class used because Main fields are static (not serialisable...)
        SaveFile saveFile = new SaveFile();
        saveFile.setPlayer(Main.getPlayer());
        saveFile.setMap(Main.getMap());

        //update saves array
        updateSavesArray();
        String saveFileName = fileNameGenerator();

        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(new File(saveFileName), saveFile);
        } catch (IOException e) {
            e.printStackTrace();
            return "Save failed!";
        }
        
        return "Saved! Save file created: "+saveFileName;
    }

    public String fileNameGenerator() {
        LocalDateTime today = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH-mm-dd-MM-yyyy");
        String formattedDate = today.format(formatter);

        String fileName = "save" + formattedDate;
        String fileNameChecked = fileNameCheck(fileName, 1);

        //directory and JSON extension added at end
        return saveDir+fileNameChecked+".json";
    }

    //recursive function
    public String fileNameCheck(String fileName, int nextNum) {
        String newFileName = fileName;

        for (Path path : saveFilePaths) {
            String pathName = path.getFileName().toString().trim();

            System.out.println("path: "+pathName);
            System.out.println("filename: "+fileName);

            //remove JSON file extension
            if (pathName.endsWith(".json")) {
                pathName = pathName.substring(0, pathName.length() - 5); // remove last 5 chars: ".json"
            }

            //for each path check if name match
            if (pathName.equalsIgnoreCase(newFileName)) {

                System.out.println("path equals: "+newFileName);
                //CASE 1: filename already has (n)
                if (newFileName.matches(".*\\(\\d+\\)$")) {
                    nextNum ++; 
                    // (Anything) followed by '(number)'
                    newFileName = newFileName.replaceAll("\\(\\d+\\)$", "");
                    newFileName += "("+nextNum+")";
                    System.out.println("1 new name equals: "+newFileName);
                    return newFileName = fileNameCheck(newFileName, nextNum);                
                }

                //CASE 2: filename exists but has no number
                newFileName += "("+nextNum+")";
                System.out.println("2 new name equals: "+newFileName);
                return fileNameCheck(newFileName, nextNum);
            }
        }
        return newFileName;
    }
}


//SaveFile
class SaveFile {
    private Player player;
    private GameMap map;

    public Player getPlayer() {
        return player;
    }
    public void setPlayer(Player player) {
        this.player = player;
    }

    public GameMap getMap() {
        return map;
    }
    public void setMap(GameMap map) {
        this.map = map;
    }
}