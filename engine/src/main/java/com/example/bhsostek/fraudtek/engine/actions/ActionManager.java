package com.example.bhsostek.fraudtek.engine.actions;

import java.util.LinkedList;

public class ActionManager{

    private static ActionManager actionManager;

    private ActionManager(){

    }

    public LinkedList<Action> genMoveActions(int x, int y, int xx, int yy){
        LinkedList<Action> out = new LinkedList<>();
        //Determine if this is an X delta or a Y delta
        if(x == xx){
            int delta = yy - y;
            EnumDirection dir;
            if(y < yy){
                dir = EnumDirection.DOWN;
            }else{
                dir = EnumDirection.UP;
            }
            for(int i = 0; i < Math.abs(delta); i++){
                out.push( new Move(0, dir.offsetDir));
            }
        }
        if(y == yy){
            int delta = xx - x;
            EnumDirection dir;
            if(x < xx){
                dir = EnumDirection.RIGHT;
            }else{
                dir = EnumDirection.LEFT;
            }
            for(int i = 0; i < Math.abs(delta); i++){
                out.push( new Move(dir.offsetDir, 0));
            }
        }

        for(Action a : out){
            System.out.println((Move)a);
        }

        return out;
    }

    public EnumDirection determineDirection(int x, int y, int xx, int yy){
        LinkedList<Action> out = new LinkedList<>();
        //Determine if this is an X delta or a Y delta
        EnumDirection dir = null;
        if(x == xx){
            if(y < yy){
                dir = EnumDirection.DOWN;
            }else{
                dir = EnumDirection.UP;
            }
        }
        if(y == yy){
            if(x < xx){
                dir = EnumDirection.RIGHT;
            }else{
                dir = EnumDirection.LEFT;
            }
        }
        return dir;
    }




    public static void initialize() {
        if(actionManager == null){
            actionManager = new ActionManager();
        }
    }

    public static ActionManager getInstance(){
        return actionManager;
    }

    public void onShutdown() {

    }

    public EnumDirection determineDirection(Move move) {
        if(move.getxDir() != 0 && move.getxDir() < 0){
            return EnumDirection.LEFT;
        }
        if(move.getxDir() != 0 && move.getxDir() > 0){
            return EnumDirection.RIGHT;
        }
        if(move.getyDir() != 0 && move.getyDir() < 0){
            return EnumDirection.UP;
        }
        if(move.getyDir() != 0 && move.getyDir() > 0){
            return EnumDirection.DOWN;
        }
        return null;
    }
}
