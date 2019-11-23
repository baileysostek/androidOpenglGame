package com.example.bhsostek.fraudtek.engine.entity;

import com.example.bhsostek.fraudtek.engine.models.ModelManager;

import org.json.JSONObject;

public class Ice extends Entity{

    public Ice() {
        super();
    }

    public Ice(JSONObject saveData) {
        super();
        this.setModel(ModelManager.getInstance().loadModel("ice.obj"));
        this.setScale(0.75f);
        this.setType(EnumEntityType.ICE);
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
}
