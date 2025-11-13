package com.amelithic.zorkgame.items;

public class RequiredItem extends Item {
    private String requiredFor;

    public RequiredItem(String name, String description, String requiredFor) {
        super(name, description);
        this.requiredFor = requiredFor;
    }

    public String getRequiredFor() {
        return requiredFor;
    }

    public void setRequiredFor(String requiredFor) {
        this.requiredFor = requiredFor;
    }
    
}
