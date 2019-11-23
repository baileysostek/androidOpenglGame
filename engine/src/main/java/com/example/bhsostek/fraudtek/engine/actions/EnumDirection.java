package com.example.bhsostek.fraudtek.engine.actions;

public enum EnumDirection {

    //For Game axises
    //Top corner of game is 0,0
    //negX is screen left, posX is screenRight
    //negY -s screen up, posY is screenDown

    //Directions
    UP(0, -1),
    DOWN(1, 1),
    LEFT(2, -1),
    RIGHT(3, 1);

    protected int index = -1;
    protected int offsetDir = 0;

    EnumDirection(int dir, int offsetDir){
        this.index = dir;
        this.offsetDir = offsetDir;
    }

    public int getIndex(){
        return index;
    }

}
