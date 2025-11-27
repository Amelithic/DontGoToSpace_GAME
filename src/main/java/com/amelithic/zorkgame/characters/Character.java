package com.amelithic.zorkgame.characters;

import com.amelithic.zorkgame.locations.Room;

public abstract class Character {
    protected String name;
    protected Room currentRoom;

    public Character(String name, Room startingRoom) {
        this.name = name;
        this.currentRoom = startingRoom;
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

    public abstract void move(String direction);
    public abstract String displayInfo();

}
