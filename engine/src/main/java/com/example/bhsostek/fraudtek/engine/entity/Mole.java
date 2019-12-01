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

                //New pos
                this.floorX += move.getxDir();
                this.floorY += move.getyDir();

                //Only add to memory if not in memory.
                if(!this.memory.containsKey(GameManager.getInstance().toGridCoords(this.floorX, this.floorY))){
                    this.memory.put(GameManager.getInstance().toGridCoords(this.floorX, this.floorY), new Move(move.getxDir() * -1, move.getyDir() * -1));
                }

                this.getPosition().add(new Vector3f(move.getxDir() * 1f, 0, move.getyDir() * 1f));

                //Check to see if we have consumed something and are home
                if(holding != null) {
                    if (this.floorX == startX && this.floorY == startY) {
                        this.isSeethrough = true;
                        this.isSatisfied  = true;
                    }
                }
                break;
            }
            case CONSUME:{
                Consume consume = (Consume)action;
                Entity toEat = consume.getToEat();
                if(toEat != null){
                    if(toEat instanceof WorldObject){
                        //If we can eat this ingridient. This is true unless another mole would like to eat this same ingridient.
                        boolean canEat = true;

                        // Linked list contianing all other actors who would eat this ingredient.
                        LinkedList<Mole> otherEaters = new LinkedList<>();

                        //Find all other moles who want to eat this
                        for(Entity e : EntityManager.getInstance().getEntitiesOfType(EnumEntityType.MOLE)){
                            Mole mole = ((Mole) e );
                            if(mole != this){
                                //Check to see if this mole has a consume action
                                System.out.println("Looking through other actions:" +  mole.getActions());
                                for(Action otherActorAction : mole.getActions()){
                                    System.out.println("Action:"+otherActorAction);
                                    if(otherActorAction instanceof Consume){
                                        System.out.println("This action is a Consume:"+otherActorAction);
                                        Consume otherConsume = ((Consume)otherActorAction);
                                        System.out.println("To Eat chheck:"+toEat+" other "+otherConsume.getToEat());
                                        if(otherConsume.getToEat().equals(toEat)){ // This mole also wants this food
                                            mole.getActions().clear(); //TODO if on this frame, all actors take a step back
                                            otherEaters.add(mole);
                                        }
                                    }
                                }
                            }
                        }

                        //Loop through actions to be executed this frame
                        for(Entity entity : GameManager.getInstance().getFrameActions().keySet()){
                            if(entity != this){
                                if(GameManager.getInstance().getFrameActions().get(entity) instanceof Consume){
                                    if(((Consume)GameManager.getInstance().getFrameActions().get(entity)).getToEat() == toEat){
                                        ((Mole)entity).getActions().clear();
                                        otherEaters.add(((Mole)entity));
                                    }
                                }
                            }
                        }

                        //Loop through the world actions that are supposed to happen this frame, search for a mole on the otherEaters list.
                        for(Mole mole : otherEaters){
                            //Check to see if this actor has an action this frame.
                            if(GameManager.getInstance().getFrameActions().containsKey(mole)){
                                //Check for other consume
                                if(GameManager.getInstance().getFrameActions().get(mole) instanceof Consume) {
                                    Consume otherConsume = ((Consume) GameManager.getInstance().getFrameActions().get(mole));
                                    System.out.println(otherConsume.getToEat());
                                    System.out.println(consume.getToEat());
                                    if (otherConsume.getToEat() == consume.getToEat()) {
                                        System.out.println("We got another one");
                                        canEat = false;
                                    }
                                    //Blacklist this mole so we dont preform this action.

                                    GameManager.getInstance().addToBlackList(mole);
                                    //This is where moles get mad
                                }
                            }
                        }

                        if(canEat) {
                            //Actual eating animation
                            WorldObject edible = ((WorldObject) toEat);
                            GameManager.getInstance().removeWorldObjectAtIndex(GameManager.getInstance().toGridCoords(edible.getFloorX(), edible.getFloorY()));
                            EntityManager.getInstance().removeEntity(toEat);
                            this.holding = new Entity().setModel(toEat.getModel()).setScale(toEat.getScale());
                            this.holding.setParent(this);
                            this.holding.setPosition(0, 1.0f, 0);
                            EntityManager.getInstance().addEntity(this.holding);

                            //We have now animated and picked up this consumable, we are now sethrough when we get home.

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
                        }else{
                            //Look through other eaters and move them all back one unit
                            LinkedList<Mole> allEaters = new LinkedList<>(otherEaters);
                            allEaters.add(this);
                            for(Mole mole : allEaters){
                                mole.addAction(mole.memory.get(GameManager.getInstance().toGridCoords(mole.getFloorX(), mole.getFloorY())));
                            }
                        }
                    }
                }
                break;
            }
        }
    }
}














































