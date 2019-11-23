package com.example.bhsostek.fraudtek.engine.entity;

import com.example.bhsostek.fraudtek.engine.models.ModelManager;

import org.json.JSONObject;

public class Floor extends Entity{

    int x = 0;
    int y = 0;

    public Floor(int x, int y) {
        super();
        this.x = x;
        this.y = y;
        initialize(null);
    }

    @Override
    public void initialize(JSONObject object){
        this.setModel(ModelManager.getInstance().loadModel("cube2.obj"));
        this.setScale(0.5f);
        this.setType(EnumEntityType.FLOOR);
    }

    public int getX(){
        return x;
    }

    public int getY(){
        return y;
    }
}
