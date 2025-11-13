package com.amelithic.zorkgame.items;

import java.util.ArrayList;

public class StorageItem extends Item {
    private ArrayList<Item> inventory;

    public StorageItem(String name, String description) {
        super(name, description, true);
    }

    public ArrayList<Item> getInventory() {
        return inventory;
    }
}
