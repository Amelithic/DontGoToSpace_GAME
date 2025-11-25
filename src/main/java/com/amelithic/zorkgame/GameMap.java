package com.amelithic.zorkgame;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.amelithic.zorkgame.items.FoodItem;
import com.amelithic.zorkgame.items.Item;
import com.amelithic.zorkgame.items.RequiredItem;
import com.amelithic.zorkgame.items.StorageItem;
import com.amelithic.zorkgame.locations.IndoorArea;
import com.amelithic.zorkgame.locations.OutdoorArea;
import com.amelithic.zorkgame.locations.Room;
import com.amelithic.zorkgame.locations.TunnelArea;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

public class GameMap {
    //fields
    private String name;
    private String description;
    private ArrayList<Item> items; //all initialised Item objects in the Map (used in Rooms)
    private ArrayList<Room<ExitDirection>> rooms; //all initialised Room objects in the Map
    private static ObjectMapper objmap = getDefaultObjectMapper(); //for JSON parsing

    private static ObjectMapper getDefaultObjectMapper() {
        ObjectMapper defaultObjectMapper = new ObjectMapper();
        //config ...
        return defaultObjectMapper;
    }

    public static JsonNode parse(String src) throws IOException {
        return objmap.readTree(src);
    }

    public enum ExitDirection {
        NORTH,
        EAST,
        SOUTH,
        WEST,
        NORTH_EAST,
        NORTH_WEST,
        SOUTH_EAST,
        SOUTH_WEST
    }

    //constructors
    private GameMap() {}
    public GameMap(Path mapFile) {
        try {
            String mapFileStr = Files.readString(mapFile);
            //System.out.println(mapFileStr); //debug
            JsonNode map = parse(mapFileStr);

            //set name field to "name": "{name of map}" at top of JSON
            this.name = map.get("name").asText();
            this.description = map.get("description").asText();

            //initialise all items + add to array"
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
                        item = new Item(itemId,itemName,itemDesc);
                        break;
                    case "food":
                        item = new FoodItem(itemId, itemName, itemType);
                        break;
                    case "required":
                        item = new RequiredItem(itemId, itemName, itemDesc, itemType);
                        break;
                    case "storage":
                        item = new StorageItem(itemId, itemName, itemType);
                        break;
                    default:
                        item = new Item(itemId,itemName,itemDesc);
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
        }
    }

    //getters (no need for setters?)
    public String getName() {
        return name;
    }
    public String getDescription() {
        return description;
    }
    public ArrayList<Item> getItems() {
        return items;
    }
    public ArrayList<Room<ExitDirection>> getRooms() {
        return rooms;
    }

    //useful methods
    private ExitDirection mapDirection(String direction) {
        switch (direction.toLowerCase()) {
            case "north":      return ExitDirection.NORTH;
            case "east":       return ExitDirection.EAST;
            case "west":       return ExitDirection.WEST;
            case "south":      return ExitDirection.SOUTH;
            case "northeast":  return ExitDirection.NORTH_EAST;
            case "northwest":  return ExitDirection.NORTH_WEST;
            case "southeast":  return ExitDirection.SOUTH_EAST;
            case "southwest":  return ExitDirection.SOUTH_WEST;
            default:           return null;
        }
    }
    private Room<ExitDirection> findRoomById(String id, List<Room<ExitDirection>> rooms) {
        for (Room<ExitDirection> room : rooms) {
            if (room.getId().equals(id)) {
                return room;
            }
        }
        return null;
    }
}
