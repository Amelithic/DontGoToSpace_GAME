package com.amelithic.zorkgame.items;

public class FoodItem extends Item {
    private boolean isEdible;

    public FoodItem(String id, String name, String description) {
        super(id, name, description);
        this.isEdible = true;
    }

    public boolean getEdible() {
        return isEdible;
    }

    public void setEdible(boolean isEdible) {
        this.isEdible = isEdible;
    }
    
}
