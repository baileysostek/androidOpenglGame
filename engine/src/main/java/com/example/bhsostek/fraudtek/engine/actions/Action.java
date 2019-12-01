package com.example.bhsostek.fraudtek.engine.actions;

import com.example.bhsostek.fraudtek.engine.entity.EnumEntityType;

import java.util.Comparator;

public class Action{
    private EnumActionType type;
    public Action(EnumActionType type){
        this.type = type;
    }

    public EnumActionType getType(){
        return type;
    }
}
