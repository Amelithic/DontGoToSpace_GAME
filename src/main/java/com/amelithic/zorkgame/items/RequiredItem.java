package com.amelithic.zorkgame.items;

public class RequiredItem extends Item {
    private String requiredFor;

    public RequiredItem(String id, String name, String description, String requiredFor) {
        super(id, name, description);
        this.requiredFor = requiredFor;
    }

    public String getRequiredFor() {
        return requiredFor;
    }

    public void setRequiredFor(String requiredFor) {
        this.requiredFor = requiredFor;
    }
    
}
