package com.amelithic.zorkgame.items;

import java.util.ArrayList;
import java.util.Collections;

public class StorageItem extends Item implements Usable{
    private ArrayList<Item> inventory;

    public StorageItem(String id, String name, String description, boolean isPortable) {
        super(id, name, description, isPortable);
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
    public boolean removeFromInventory(Item item) {
        if (inventory.contains(item)) {
            inventory.remove(item);
            return true;
        } else {
            return false; //no item
        }
    }

    @Override
    public String use() {
        String result = "Items found in "+name+": ";
        for (Item item : inventory) {
            result += "\t\n"+item.getName();
        }
        return result + (inventory.isEmpty()? "None.":"");
    }
}
