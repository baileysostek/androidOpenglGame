package com.example.bhsostek.fraudtek.engine.game;

import com.example.bhsostek.fraudtek.engine.util.AssetManager;

import org.json.JSONObject;

import java.util.LinkedList;

public class LevelSet {

    //Varaibales
    private LinkedList<Level> levels = new LinkedList<>();
    private JSONObject progress;

    //Constructor load levels and progress
    public LevelSet(String levelSet){
        AssetManager.getInstance().readFile("levels/"+levelSet+"/config.json");
    }

}
