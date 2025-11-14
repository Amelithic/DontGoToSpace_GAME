package com.amelithic.zorkgame.items;

import java.util.ArrayList;
import java.util.Collections;

public class StorageItem extends Item {
    private ArrayList<Item> inventory;

    public StorageItem(String id, String name, String description) {
        super(id, name, description);
        inventory = new ArrayList<>();
    }

    public ArrayList<Item> getInventory() {
        return inventory;
    }

    public void setInventory(Item... items) {
        Collections.addAll(inventory, items);
    }

    public String showInventory() {
        if (!inventory.isEmpty()) {
            String itemString = "Inventory items in: "+name;
            for (Item item : inventory) {
                itemString += "\n\t- "+ item.getName();
            }
            return itemString;
        } else {
            return "No items in this item's inventory.";
        }
    }
    public boolean removeRoomItem(Item item) {
        if (inventory.contains(item)) {
            inventory.remove(item);
            return true;
        } else {
            return false; //no item
        }
    }
}
