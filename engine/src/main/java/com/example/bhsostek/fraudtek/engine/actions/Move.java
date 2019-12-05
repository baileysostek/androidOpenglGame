package com.example.bhsostek.fraudtek.engine.actions;

public class Move extends Action{

    private int xDir = 0;
    private int yDir = 0;

    public Move(int x, int y) {
        super(EnumActionType.MOVE);
        xDir = x;
        yDir = y;
    }

    public int getxDir(){
        return this.xDir;
    }

    public int getyDir(){
        return this.yDir;
    }

    public Move inverse(){
        return new Move(this.xDir * -1, this.yDir * -1);
    }

    public String toString(){
        return "{ x:"+ xDir +" , dy:"+yDir+" }";
    }
}
