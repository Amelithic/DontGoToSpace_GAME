package com.amelithic.zorkgame;

public class Goal {
    //fields
    private int id;
    private String goalName;
    private boolean isSolved;

    //constructor
    public Goal(int id, String goalName, boolean isSolved) {
        this.id = id;
        this.goalName = goalName;
        this.isSolved = isSolved;
    }

    //getters and setters
    public int getId() {
        return id;
    }

    public String getName() {
        return goalName;
    }


    public boolean getSolved() {
        return isSolved;
    }
    public void setSolved(boolean isSolved) {
        this.isSolved = isSolved;
    }
}
