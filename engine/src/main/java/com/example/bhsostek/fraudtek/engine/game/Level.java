package com.example.bhsostek.fraudtek.engine.game;

public class Level {

    private String name;
    private int width  = 0;
    private int height = 0;

    public Level(String name, int width, int height){
        this.name   = name;
        this.width  = width;
        this.height = height;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight(){
        return this.height;
    }
}
