package com.example.bhsostek.fraudtek.engine.entity;

import com.example.bhsostek.fraudtek.engine.models.ModelManager;

import org.json.JSONObject;

public class MoleHole extends Entity{


    public MoleHole() {
        super();
        this.setModel(ModelManager.getInstance().loadModel("tnt.obj"));
//        this.setScale(0.75f);
        this.setType(EnumEntityType.HOLE);

    }


    @Override
    public void initialize(JSONObject object){

    }
}
