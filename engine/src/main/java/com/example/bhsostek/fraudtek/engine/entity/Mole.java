package com.example.bhsostek.fraudtek.engine.entity;

import com.example.bhsostek.fraudtek.engine.actions.Action;
import com.example.bhsostek.fraudtek.engine.actions.Consume;
import com.example.bhsostek.fraudtek.engine.actions.Move;
import com.example.bhsostek.fraudtek.engine.game.GameManager;
import com.example.bhsostek.fraudtek.engine.math.Vector3f;
import com.example.bhsostek.fraudtek.engine.models.ModelManager;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.LinkedList;

public class Mole extends Entity implements WorldObject{

    private HashMap<Integer, Action> memory = new HashMap<>();
    private LinkedList<Action> actions = new LinkedList<>();

    private Entity holding = null;


    int floorX = 0;
    int floorY = 0;

    int startX = -1;
    int startY = -1;

    boolean isSeethrough = false;
    boolean isSatisfied  = false;

    public Mole() {
        super();
    }

    public Mole(JSONObject saveData) {
        super();
        this.setModel(ModelManager.getInstance().loadModel("mole.obj"));
        this.setScale(0.75f);
        this.setType(EnumEntityType.MOLE);
        initialize(saveData);
    }

    @Override
    public void initialize(JSONObject object){
//        MoleHole hole = new MoleHole();
//        hole.setPosition(new Vector3f(this.getPosition()));
//        EntityManager.getInstance().addEntity(hole);
    }

    @Override
    public void setIsSeethrough(boolean isSeethrough){
        this.isSeethrough = isSeethrough;
    }

    @Override
    public boolean isSeethrough(){
        return this.isSeethrough;
    }

    @Override
    public void isSatisfied(boolean isSatisfied) {
        this.isSatisfied = isSatisfied;
    }

    @Override
    public boolean isSatisfied() {
        return this.isSatisfied;
    }


    @Override
    public int getFloorX() {
        return this.floorX;
    }

    @Override
    public int getFloorY() {
        return this.floorY;
    }

    @Override
    public void setFloorX(int x) {
        this.floorX = x;
    }

    @Override
    public void setFloorY(int y) {
        this.floorY = y;
    }

    @Override
    public int getStartX() {
        return this.startX;
    }

    @Override
    public int getStartY() {
        return this.startY;
    }

    @Override
    public void setStartX(int x) {
        if(this.startX == -1){
            this.startX = x;
        }
    }

    @Override
    public void setStartY(int y) {
        if(this.startY == -1){
            this.startY = y;
        }
    }

    @Override
    public void setActions(LinkedList<Action> actions) {
        this.actions = actions;
    }

    @Override
    public void addAction(Action action) {
        this.actions.add(action);
    }

    @Override
    public LinkedList<Action> getActions() {
        return this.actions;
    }

    @Override
    public void spliceAction(int index) {
        this.actions.remove(index);
    }

    @Override
    public void performAction(Action action) {
        switch (action.getType()){
            case MOVE:{
                Move move = (Move)action;

                GameManager.getInstance().moveWorldObject(GameManager.getInstance().toGridCoords(this.floorX, this.floorY), GameManager.getInstance().toGridCoords(this.floorX + move.getxDir(), this.floorY + move.getyDir()));

                this.floorX += move.getxDir();
                this.floorY += move.getyDir();
                this.memory.put(GameManager.getInstance().toGridCoords(this.floorX, this.floorY), new Move(move.getxDir() * -1, move.getyDir() * -1));

                this.getPosition().add(new Vector3f(move.getxDir() * 1f, 0, move.getyDir() * 1f));
                break;
            }
            case CONSUME:{
                Consume consume = (Consume)action;
                Entity toEat = consume.getToEat();
                if(toEat != null){
                    if(toEat instanceof WorldObject){
                        //Find all other moles who want to eat this
                        for(Entity e : EntityManager.getInstance().getEntitiesOfType(EnumEntityType.MOLE)){
                            Mole mole = ((Mole) e );
                            if(mole != this){
                                //Check to see if this mole has a consume action
                                for(Action otherActorAction : mole.getActions()){
                                    if(otherActorAction instanceof Consume){
                                        Consume otherConsume = ((Consume)otherActorAction);
                                        if(otherConsume.getToEat().equals(toEat)){ // This mole also wants this food
                                            mole.getActions().clear();//TODO if on this frame, all actors take a step back
                                        }
                                    }
                                }
                            }
                        }
                        //Actual eating animation
                        WorldObject edible = ((WorldObject) toEat);
                        GameManager.getInstance().removeWorldObjectAtIndex(GameManager.getInstance().toGridCoords(edible.getFloorX(), edible.getFloorY()));
                        EntityManager.getInstance().removeEntity(toEat);
                        this.holding = new Entity().setModel(toEat.getModel()).setScale(toEat.getScale());
                        this.holding.setParent(this);
                        this.holding.setPosition(0, 1.0f, 0);
                        EntityManager.getInstance().addEntity(this.holding);

                        //Now that we have picked up our item, we need to move home
                        int trackBackX = this.floorX;
                        int trackBackY = this.floorY;
                        int index = GameManager.getInstance().toGridCoords(trackBackX, trackBackY);
                        while(this.memory.containsKey(index)){
                            //If we have reached our starting tile, stop moving
                            if(index == GameManager.getInstance().toGridCoords(this.getStartX(), this.getStartY())){
                                break;
                            }
                            //add backward traversal
                            Move move = (Move)this.memory.get(index);
                            trackBackX += move.getxDir();
                            trackBackY += move.getyDir();
                            index = GameManager.getInstance().toGridCoords(trackBackX, trackBackY);
                            this.getActions().addLast(move);
                        }
                    }
                }
                break;
            }
        }
    }
}














































