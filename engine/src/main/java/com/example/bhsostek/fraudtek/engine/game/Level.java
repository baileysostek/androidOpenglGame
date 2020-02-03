package com.example.bhsostek.fraudtek.engine.game;

import org.json.JSONArray;

public class Level {

    private String name;
    private int width  = 0;
    private int height = 0;

    private JSONArray entities;
    private JSONArray ingidients;

    public Level(String name, int width, int height){
        this.name   = name;
        this.width  = width;
        this.height = height;
    }

    public String getName() { return this.name;}

    public int getWidth() {
        return this.width;
    }

    public int getHeight(){
        return this.height;
    }


    //Manage entities.
    public void setEntities(JSONArray entities) {
        this.entities = entities;
    }

    public JSONArray getEntities(){
        return this.entities;
    }

    public void setIngridients(JSONArray ingridients) {
        this.ingidients = ingridients;
    }

    public JSONArray getIngidients(){
        return this.ingidients;
    }
}
