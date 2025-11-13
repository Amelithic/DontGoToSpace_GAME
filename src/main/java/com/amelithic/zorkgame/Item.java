package main.java;

public class Item {
    private String description;
    private String name;
    private String location;
    private int id;
    private boolean isVisible;
    private boolean isEdible;

    public Item(String name, String description) {
        this(name, description, false);
    }
    public Item(String name, String description, boolean isEdible) {
        this.name = name;
        this.description = description;
        this.isVisible = true;
        this.isEdible = isEdible;
    }

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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public void setVisible(boolean visible) {
        isVisible = visible;
    }

    public boolean isEdible() {
        return isEdible;
    }

}
