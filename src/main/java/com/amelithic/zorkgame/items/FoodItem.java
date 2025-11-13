package com.amelithic.zorkgame.items;

import com.amelithic.zorkgame.items.*;

public class FoodItem extends Item {
    private int nutritionValue;

    public FoodItem(String name, String description, int nutritionValue) {
        super(name, description, true);
        this.nutritionValue = nutritionValue;
    }

    public int getNutritionValue() {
        return nutritionValue;
    }

    public void setNutritionValue(int nutritionValue) {
        this.nutritionValue = nutritionValue;
    }
    
}
