package com.example.bhsostek.fraudtek.engine.entity;

import com.example.bhsostek.fraudtek.engine.math.Vector3f;
import com.example.bhsostek.fraudtek.engine.models.ModelManager;

import org.json.JSONException;
import org.json.JSONObject;

public class Stone extends Entity{

    public Stone() {
        super();
    }

    public Stone(JSONObject saveData) {
        super();
        this.setModel(ModelManager.getInstance().loadModel("stone.obj"));
        this.setScale(0.75f);
        this.rotate(0, (float) (360.0f * Math.random()), 0);
        this.setType(EnumEntityType.STONE);
        initialize(saveData);
    }

    @Override
    public void initialize(JSONObject object){
//        try {
//            this.setPosition((float) object.getDouble("x"), 0, (float) object.getDouble("y"));
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
    }

    @Override
    public void update(double delta){
//        this.rotate(0, 1f, 0);
//        offset.setY(0.5f + (float)(0.25f * Math.sin(Math.toRadians(offsetTick))));
//        offsetTick+=2f;
//        offsetTick%=360f;
    }
}
