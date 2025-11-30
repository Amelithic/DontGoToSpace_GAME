package com.amelithic.zorkgame.items;

interface Stackable {
    void increaseCount(int increase);
    void decreaseCount(int decrease);
}

public class Item implements Stackable {
    //fields
    protected String description;
    protected String name;
    protected String location;
    protected String id;
    protected boolean isPortable;
    protected int count; //for a stack of multiple languages

    //constructors
    public Item(String id, String name, String description) {
        this(id, name, description, true);
    }
    public Item(String id, String name, String description, boolean isPortable) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.isPortable = isPortable;
        this.count = 0;
    }

    //getters and setters
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }
    public void setLocation(String location) {
        this.location = location;
    }

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public boolean isPortable() {
        return isPortable;
    }
    public void setPortable(boolean isPortable) {
        this.isPortable = isPortable;
    }

    public int getCount() {
        return count;
    }

    //methods
    @Override
    public void increaseCount(int increase) {
        count++;
    }

    @Override
    public void decreaseCount(int decrease) {
        count--;
    }

}
