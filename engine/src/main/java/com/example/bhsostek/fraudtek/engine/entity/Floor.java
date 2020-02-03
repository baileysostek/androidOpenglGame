package com.example.bhsostek.fraudtek.engine.entity;

import com.example.bhsostek.fraudtek.engine.models.ModelManager;
import com.example.bhsostek.fraudtek.engine.renderer.SpriteManager;

import org.json.JSONObject;

public class Floor extends Entity{

    int x = 0;
    int y = 0;

    int initialTexture = 0;

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
        if((this.x + this.y) % 2 == 0){
            initialTexture = SpriteManager.getInstance().loadTexture("sand.png");
        }else{
            initialTexture = SpriteManager.getInstance().loadTexture("grass.png");
        }
        this.setTexture(initialTexture);
    }

    public int getX(){
        return x;
    }

    public int getY(){
        return y;
    }

    public void overrideTexture(int id){
        this.setTexture(id);
    }

    public void returnToInitialTexture(){
        this.setTexture(initialTexture);
    }
}
