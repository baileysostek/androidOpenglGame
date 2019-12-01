package com.example.bhsostek.fraudtek.engine.actions;

public enum EnumActionType {
    CONSUME(1),
    MOVE(0);

    protected int moveIndex = 0;

    EnumActionType(int index){
        this.moveIndex = index;
    }

    public int getMoveIndex(){
        return this.moveIndex;
    }

}
