package com.amelithic.zorkgame;

import com.amelithic.zorkgame.characters.Player;

public class SaveFile {
    private Player player;
    private GameMap map;

    public Player getPlayer() {
        return player;
    }
    public void setPlayer(Player player) {
        this.player = player;
    }

    public GameMap getMap() {
        return map;
    }
    public void setMap(GameMap map) {
        this.map = map;
    }
}