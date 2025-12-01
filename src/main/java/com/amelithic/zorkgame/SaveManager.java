package com.amelithic.zorkgame;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import com.fasterxml.jackson.databind.ObjectMapper;

public class SaveManager {
    //fields
    String saveDir;
    private ArrayList<Path> saveFilePaths;

    //constructor
    public SaveManager() {
        saveDir = "./saves/";
        saveFilePaths = new ArrayList<>();

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

    public ArrayList<Path> getSaves() {
        return saveFilePaths;
    }

    public void load(Path savePath) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            SaveFile saveFile = mapper.readValue(savePath.toFile(), SaveFile.class);

            //Setting loaded values into Main
            /*Main.setPlayer(saveFile.getPlayer());
            Main.setMap(saveFile.getMap());*/

            //Manual parse
            GameMap map = saveFile.getMap();

            //items in rooms
            //map.getItems() {}

            System.out.println("Loaded save: " + savePath);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //SEE THIS FROM GAMEMAP
        /*
        try {
            String mapFileStr = Files.readString(mapFile);
            //System.out.println(mapFileStr); //debug
            JsonNode map = parse(mapFileStr);

            //set name field to "name": "{name of map}" at top of JSON
            this.name = map.get("name").asText();
            this.description = map.get("description").asText();

            //initialise all goals + add to array
            goals = new ArrayList<>();
            ArrayNode goalsArrayFromFile = (ArrayNode) map.get("goals");
            for (int i=0; i < goalsArrayFromFile.size(); i++) {
                int goalId = goalsArrayFromFile.get(i).get("goalId").asInt();
                String goalName = goalsArrayFromFile.get(i).get("goalName").asText();
                boolean isSolved = goalsArrayFromFile.get(i).get("isSolved").asBoolean();

                Goal goal = new Goal(goalId, goalName, isSolved);
                goals.add(goal);
            }


            //initialise all items + add to array
            items = new ArrayList<>();
            ArrayNode itemsArrayFromFile = (ArrayNode) map.get("items");
            for (int i=0; i < itemsArrayFromFile.size(); i++) {
                String itemId = itemsArrayFromFile.get(i).get("id").asText();
                String itemName = itemsArrayFromFile.get(i).get("name").asText();
                String itemDesc = itemsArrayFromFile.get(i).get("description").asText();
                String itemType = itemsArrayFromFile.get(i).get("type").asText();

                Item item;
                switch (itemType) {
                    case "none":
                        boolean isPortable = itemsArrayFromFile.get(i).get("isPortable").asBoolean();
                        item = new Item(itemId,itemName,itemDesc, isPortable);
                        break;
                    case "food": //always portable = true
                        String foodConsumeMessage = itemsArrayFromFile.get(i).get("consumeMessage").asText();
                        item = new FoodItem(itemId, itemName, itemDesc, (foodConsumeMessage != null && foodConsumeMessage.length() > 0)? foodConsumeMessage : "");
                        break;
                    case "required": //always portable = true
                        item = new RequiredItem(itemId, itemName, itemDesc, itemType);
                        break;
                    case "storage":
                        isPortable = itemsArrayFromFile.get(i).get("isPortable").asBoolean();
                        item = new StorageItem(itemId, itemName, itemDesc, isPortable);
                        break;
                    case "info":
                        isPortable = itemsArrayFromFile.get(i).get("isPortable").asBoolean();
                        String infoFile = "src\\main\\java\\com\\amelithic\\zorkgame\\config\\info_json\\"+itemsArrayFromFile.get(i).get("infoFile").asText();
                        Path infoFilePath = Path.of(infoFile);
                        item = new InfoItem(itemId, itemName, itemType, isPortable, infoFilePath);
                        break;
                    default:
                        isPortable = itemsArrayFromFile.get(i).get("isPortable").asBoolean();
                        item = new Item(itemId,itemName,itemDesc, isPortable);
                        break;
                }
                items.add(item);
            }

            //initialise all rooms, using items + add to array
            rooms = new ArrayList<>();
            ArrayNode roomsArrayFromFile = (ArrayNode) map.get("rooms");
            for (int i=0; i < roomsArrayFromFile.size(); i++) {
                String roomId = roomsArrayFromFile.get(i).get("id").asText();
                String roomName = roomsArrayFromFile.get(i).get("name").asText();
                String roomDesc = roomsArrayFromFile.get(i).get("description").asText();
                String roomType = roomsArrayFromFile.get(i).get("type").asText();

                Room<ExitDirection> room;
                switch (roomType) {
                    case "indoor":
                        room = new IndoorArea<>(roomId, roomName, roomDesc);
                        break;
                    case "outdoor":
                        room = new OutdoorArea<>(roomId, roomName, roomDesc);
                        break;
                    case "tunnel":
                        room = new TunnelArea<>(roomId, roomName, roomDesc);
                        break;
                    default:
                        room = new IndoorArea<>(roomId, roomName, roomDesc);
                        break;
                }
                rooms.add(room);

                //items in room
                ArrayNode roomsItemArray = (ArrayNode) roomsArrayFromFile.get(i).get("items");
                if (roomsItemArray != null && roomsItemArray.isArray()) {
                    //it exists, now check if its not empty...
                    if (roomsItemArray.size() > 0) {
                        //for each array item
                        for (int item = 0; item < roomsItemArray.size(); item++) {
                            String roomItemId = roomsItemArray.get(item).asText(); //id of item in room

                            //check if item exists in items array
                            for (Item mapItemId : items) {
                                if (mapItemId.getId().equals(roomItemId)) {
                                    //add if matching ids -> item exists
                                    Item roomItem = items.get(items.indexOf(mapItemId));
                                    room.setRoomItems(roomItem);
                                }
                            }

                        }
                    }
                }
                //System.out.println(room.printRoomItems()); //debug                
            }


            //adding exits after all rooms exist
            for (int i=0; i < roomsArrayFromFile.size(); i++) {
                String roomId = roomsArrayFromFile.get(i).get("id").asText();

                JsonNode roomExitsArray = roomsArrayFromFile.get(i).get("exits");
                if (roomExitsArray != null && roomExitsArray.isContainerNode()) {
                    Iterator<Map.Entry<String, JsonNode>> fields = roomExitsArray.fields();
                    while (fields.hasNext()) {
                        Map.Entry<String, JsonNode> entry = fields.next();

                        String direction = entry.getKey().toLowerCase();
                        ExitDirection enumDirection = mapDirection(direction);

                        String destinationId = entry.getValue().asText();
                        Room<ExitDirection> targetRoom = findRoomById(destinationId, rooms);

                        if (enumDirection != null && targetRoom != null) {
                            Room<ExitDirection> room = null;
                            for (Room<ExitDirection> possibleRoom : rooms) {
                                if (possibleRoom.getId().equalsIgnoreCase(roomId)) {
                                    room = possibleRoom;
                                }
                            }

                            if (room != null) {
                                room.setExit(enumDirection, targetRoom);
                                //System.out.println(room.getName() + ": " + enumDirection + " -> " + destinationId);
                            }
                        }
                    }
                }
            }

        } catch (IOException e) {
            System.err.println("Exception when reading the JSON map file...");
            e.printStackTrace();
        } */
    }
    
    public String save(Main gameState) {
        //get current date (for file name)
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        String formattedDate = today.format(formatter);
        String saveFileName = saveDir+"save"+formattedDate+".json";

        //serialisation with Jackson
        ObjectMapper mapper = new ObjectMapper();

        //SaveFile class used because Main fields are static (not serialisable...)
        SaveFile saveFile = new SaveFile();
        saveFile.setPlayer(Main.getPlayer());
        saveFile.setMap(Main.getMap());

        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(new File(saveFileName), saveFile);
        } catch (IOException e) {
            e.printStackTrace();
            return "Save failed!";
        }
        
        return "Saved! Save file created: "+saveFileName;
    }
}
