package com.amelithic.zorkgame.characters;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;

import com.amelithic.zorkgame.items.Item;
import com.amelithic.zorkgame.locations.Room;

public class Character implements Serializable {
    private String name;
    private Room currentRoom;
    private ArrayList<Item> inventory;

    public Character(String name, Room startingRoom) {
        this.name = name;
        this.currentRoom = startingRoom;
        this.inventory = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public Room getCurrentRoom() {
        return currentRoom;
    }

    public void setCurrentRoom(Room room) {
        this.currentRoom = room;
    }

    public void move(String direction) {
        Room nextRoom = currentRoom.getExit(direction);
        if (nextRoom != null) {
            currentRoom = nextRoom;
            System.out.println("You moved to: " + currentRoom.getDescription());
        } else {
            System.out.println("You can't go that way!");
        }
    }

    public void setInventory(Item... items) {
        Collections.addAll(inventory, items);
    }
    public ArrayList<Item> getInventory() {
        return inventory;
    }
    public boolean removeFromInventory(Item item) {
        if (inventory.contains(item)) {
            inventory.remove(item);
            return true;
        } else {
            return false; //no item
        }
    }
    public String printInventory() {
        if (!inventory.isEmpty()) {
            String itemString = "Current inventory: ";
            for (Item item : inventory) {
                itemString += "\n\t- "+ item.getName();
            }
            return itemString;
        } else {
            return "There are no items in your inventory.";
        }
    }

    public void printChar() {
        System.out.println("Name: "+name);
        System.out.println("Room: "+currentRoom.getDescription());
        System.out.println(printInventory());
    }

}
