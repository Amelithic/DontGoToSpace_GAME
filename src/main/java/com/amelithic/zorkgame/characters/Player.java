package com.amelithic.zorkgame.characters;

import java.util.ArrayList;

import com.amelithic.zorkgame.items.Item;
import com.amelithic.zorkgame.locations.Room;

public class Player extends Character {
    private ArrayList<Item> inventory;
    private boolean isAdmin;

    public Player(String name, Room startingRoom) {
        super(name, startingRoom);
        this.inventory = new ArrayList<>();
        this.isAdmin = false;
    }

    public void setInventory(Item... items) {
        //Collections.addAll(inventory, items);
        for (Item item : items) {
            if (inventory.contains(item)) {
                item.increaseCount(1);
            } else {
                inventory.add(item);
            }
        }
    }
    public ArrayList<Item> getInventory() {
        return inventory;
    }
    public boolean removeFromInventory(Item item) {
        if (inventory.contains(item)) {
            Item itemInRoom = inventory.get(inventory.indexOf(item));
            if (itemInRoom.getCount() > 1) {
                itemInRoom.decreaseCount(1);
            } else {
                inventory.remove(item);
            }
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

    @Override
    public void move(String direction) {
        Room nextRoom = currentRoom.getExit(direction);
        if (nextRoom != null) {
            currentRoom = nextRoom;
            System.out.println("You moved to: " + currentRoom.getDescription());
        } else {
            System.out.println("You can't go that way!");
        }
    }

    @Override
    public String displayInfo() {
        return "Name: "+name+"\nRoom: "+currentRoom.getName()+"\n"+printInventory();
    }
}
