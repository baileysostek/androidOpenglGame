package com.example.bhsostek.fraudtek.engine.actions;

import com.example.bhsostek.fraudtek.engine.entity.Entity;

public class Consume extends Action{

    private Entity toEat;

    public Consume(Entity toEat) {
        super(EnumActionType.CONSUME);
        this.toEat = toEat;
    }

    public Entity getToEat(){
        return this.toEat;
    }

}
