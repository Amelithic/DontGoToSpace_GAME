package com.amelithic.zorkgame.locations;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.amelithic.zorkgame.items.Item;

public class Room implements Serializable {
    protected String description;
    protected Map<String, Room> exits; // Map direction to neighboring Room
    protected ArrayList<Item> roomItems;

    public Room(String description) {
        this.description = description;
        exits = new HashMap<>();
        roomItems = new ArrayList<>();
    }

    public String getDescription() {
        return description;
    }

    public void setExit(String direction, Room neighbor) {
        exits.put(direction, neighbor);
    }

    public Room getExit(String direction) {
        return exits.get(direction);
    }

    public String getExitString() {
        StringBuilder sb = new StringBuilder();
        for (String direction : exits.keySet()) {
            sb.append(direction).append(" ");
        }
        return sb.toString().trim();
    }

    public void setRoomItems(Item... items) {
        Collections.addAll(roomItems, items);
    }
    public ArrayList<Item> getRoomItems() {
        return roomItems;
    }
    public String printRoomItems() {
        if (!roomItems.isEmpty()) {
            String itemString = "Objects in the room: ";
            for (Item item : roomItems) {
                itemString += "\n\t- "+ item.getName();
            }
            return itemString;
        } else {
            return "No items in this room.";
        }
    }
    public boolean removeRoomItem(Item item) {
        if (roomItems.contains(item)) {
            roomItems.remove(item);
            return true;
        } else {
            return false; //no item
        }
    }

    public String getLongDescription() {
        return "You are " + description + ".\nExits: " + getExitString();
    }
}
