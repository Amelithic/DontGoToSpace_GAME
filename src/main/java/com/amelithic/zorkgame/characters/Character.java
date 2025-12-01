package com.amelithic.zorkgame.characters;

import com.amelithic.zorkgame.locations.Room;

public abstract class Character {
    protected String name;
    protected Room currentRoom;
    protected int maxHealth;
    protected int currentHealth;
    private int attackDamage;

    public Character(String name, Room startingRoom, int maxHealth, int attackDamage) {
        this.name = name;
        this.currentRoom = startingRoom;
        this.maxHealth = maxHealth;
        this.currentHealth = maxHealth; //full health at start
        this.attackDamage = attackDamage;
    }

    public String getName() {
        return name;
    }

    public Room getCurrentRoom() {
        return currentRoom;
    }
    public void setCurrentRoom(Room room) {
        this.currentRoom = room;
    }

    public void setMaxHealth(int maxHealth) {
        this.maxHealth = maxHealth;
    }
    public int getMaxHealth() {
        return maxHealth;
    }
    public String printHealth() {
        return "Health for "+name+": "+currentHealth+"/"+maxHealth;
    }

    public void setCurrentHealth(int currentHealth) {
        this.currentHealth = currentHealth;
    }
    public int getCurrentHealth() {
        return currentHealth;
    }

    public void setAttackDamage(int attackDamage) {
        this.attackDamage = attackDamage;
    }
    public int getAttackDamage() {
        return attackDamage;
    }

    public abstract void move(String direction);
    public abstract String displayInfo();

}
