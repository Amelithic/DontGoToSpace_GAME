package com.amelithic.zorkgame.characters;

import com.amelithic.zorkgame.locations.Room;

public class Alien extends Character {
    private boolean isDefeated;

    public Alien(String name, Room startingRoom, int maxHealth, int attackDamage) {
        super(name, startingRoom, maxHealth, attackDamage);
        isDefeated = false;
    }
    
    public void setCurrentRoom(Room room) {
        this.currentRoom = room;
    }

    public void setDefeated(boolean isDefeated) {
        this.isDefeated = isDefeated;
    }
    public boolean getDefeated() {
        return isDefeated;
    }

    @Override
    public void move(String direction) {
        Room nextRoom = currentRoom.getExit(direction);
        if (nextRoom != null) {
            currentRoom = nextRoom;
            //System.out.println("You moved to: " + currentRoom.getDescription());
        }
    }
    
    @Override
    public String displayInfo() {
        return "Name: "+name+"\nRoom: "+currentRoom.getName()+"\nHealth: "+currentHealth;
    }
}
