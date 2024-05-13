package com.gamecodeschool.snake;

public class Player {
    private final int score;
    private final String name;

    Player(String name, int score){
        this.name=name;
        this.score=score;
    }
    public String format(){
        return (name + Integer.toString(score));
    }
}
