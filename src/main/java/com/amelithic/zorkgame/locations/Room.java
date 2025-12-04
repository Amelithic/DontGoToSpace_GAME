package com.amelithic.zorkgame.locations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.amelithic.zorkgame.items.Item;

public abstract class Room<T> {
    //fields
    protected String id;
    protected String name;
    protected String description;
    protected Map<T, Room> exits; // Map direction to neighboring Room
    protected List<Item> roomItems;

    //constructors
    private Room() {};
    public Room(String id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
        exits = new HashMap<>();
        roomItems = new ArrayList<>();
    }

    //getters + setters
    public String getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public String getDescription() {
        return description;
    }
    public String getLongDescription() {
        return description + "\nExits: " + getExitString() + "\nObjects in room: " + printRoomItems();
    }
    

    public void setExit(T direction, Room neighbor) {
        exits.put(direction, neighbor);
    }
    public Room getExit(T direction) {
        return exits.get(direction);
    }
    public boolean hasExit(T direction) {
        if (exits.containsKey(direction)) {
            return true;
        } else {
            return false;
        }
    }
    public String getExitString() {
        StringBuilder sb = new StringBuilder();
        for (T direction : exits.keySet()) {
            sb.append(direction).append(" ");
        }
        return sb.toString().trim();
    }

    public void setRoomItems(Item... items) {
        //Collections.addAll(roomItems, items);
        for (Item item : items) {
            if (roomItems.contains(item)) {
                item.increaseCount(1);
            } else {
                roomItems.add(item);
            }
        }
    }
    public List<Item> getRoomItems() {
        return roomItems;
    }
    public String printRoomItems() {
        if (!roomItems.isEmpty()) {
            String itemString = "";
            for (Item item : roomItems) {
                itemString += "\n\t- "+ item.getName();

                if (item.getCount() > 1) itemString += " x "+item.getCount();
            }
            return itemString;
        } else {
            return "No items in this room.";
        }
    }
    public boolean removeRoomItem(Item item) {
        if (roomItems.contains(item)) {
            Item itemInRoom = roomItems.get(roomItems.indexOf(item));
            if (itemInRoom.getCount() > 1) {
                itemInRoom.decreaseCount(1);
            } else {
                roomItems.remove(item);
            }
            return true;
        } else {
            return false; //no item
        }
    }
    public void clearRoomItems() {
        roomItems.clear();
    }
}
