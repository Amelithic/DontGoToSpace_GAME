package com.amelithic.zorkgame.items;

interface Consumable {
    String getConsumptionMessage();
}

public class FoodItem extends Item implements Consumable {
    private boolean isEdible;
    private String consumptionMessage;

    public FoodItem(String id, String name, String description, String consumptionMessage) {
        super(id, name, description);
        this.isEdible = true;
        this.consumptionMessage = consumptionMessage;
    }

    public boolean getEdible() {
        return isEdible;
    }

    public void setEdible(boolean isEdible) {
        this.isEdible = isEdible;
    }

    @Override
    public String getConsumptionMessage() {
        return consumptionMessage;
    }
    
}
