package com.example.bhsostek.fraudtek.engine.entity;

import com.example.bhsostek.fraudtek.engine.actions.Action;
import com.example.bhsostek.fraudtek.engine.actions.ActionManager;
import com.example.bhsostek.fraudtek.engine.actions.Consume;
import com.example.bhsostek.fraudtek.engine.actions.EnumDirection;
import com.example.bhsostek.fraudtek.engine.actions.Move;
import com.example.bhsostek.fraudtek.engine.animation.EnumLoop;
import com.example.bhsostek.fraudtek.engine.animation.Timeline;
import com.example.bhsostek.fraudtek.engine.game.GameManager;
import com.example.bhsostek.fraudtek.engine.math.Vector3f;
import com.example.bhsostek.fraudtek.engine.models.ModelManager;
import com.example.bhsostek.fraudtek.engine.util.Callback;

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

    //Max level width is 128
    int closest = GameManager.getInstance().getMaxDistance();

    boolean isSeethrough = false;
    boolean isSatisfied  = false;
    boolean animating    = false;
    private EnumEntityType healdType = EnumEntityType.UNKNOWN;

    //Used for movement lerping
    private Timeline movement = new Timeline();
    private Vector3f startPos = new Vector3f();
    private Vector3f endPos   = new Vector3f();

    public Mole() {
        super();
    }

    public Mole(JSONObject saveData) {
        super();
        this.setModel(ModelManager.getInstance().loadModel("mole.obj"));
        this.setScale(0.5f);
        this.setType(EnumEntityType.MOLE);
        this.setTexture("mole.png");
        initialize(saveData);
    }

    @Override
    public void initialize(JSONObject object){
        movement.setDuration(GameManager.getInstance().getTimeScale());

        movement.addCallback(0, new Callback() {
            @Override
            public Object callback(Object... objects) {
                animating = true;
                return null;
            }
        });

        movement.addCallback(GameManager.getInstance().getTimeScale(), new Callback() {
            @Override
            public Object callback(Object... objects) {
                animating = false;
                return null;
            }
        });

        movement.addKeyFrame("lerp", 0f, 0f);
        movement.addKeyFrame("lerp", GameManager.getInstance().getTimeScale(), 1f);

        movement.setLoop(EnumLoop.STOP_LAST_VALUE);

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

    public int getClosest() {
        return this.closest;
    }

    public void setClosest(int distance) {
        this.closest = distance;
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
    public String toString(){
        return "Type:"+super.getType()+"\n"+"Satisfied:"+this.isSatisfied+"\n"+"Seethrough:"+this.isSeethrough;
    }

    @Override
    public void update(double delta){
        if(movement.isRunning()) {
            movement.update(delta);
            this.setPosition(new Vector3f(startPos).add(new Vector3f(endPos).mul(movement.getValueOf("lerp"))));
        }
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
                    this.memory.put(GameManager.getInstance().toGridCoords(this.floorX, this.floorY), move.inverse());
                }

                this.setLookAngle(ActionManager.getInstance().determineDirection(move));
                startPos = new Vector3f(this.getPosition());
                endPos = new Vector3f(new Vector3f(move.getxDir() * 1f, 0, move.getyDir() * 1f));
                movement.start();

//                this.getPosition().add(endPos);

                //Check to see if we have consumed something and are home
                if(holding != null) {
                    checkIsHome();
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
                        LinkedList<Mole> frameEaters = new LinkedList<>();

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
                                            GameManager.getInstance().addToBlackList(mole);
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
                                        frameEaters.add(((Mole)entity));
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
                            GameManager.getInstance().removeWorldObject(toEat);
                            this.holding = new Entity().setModel(toEat.getModel()).setScale(toEat.getScale());
                            this.healdType = toEat.getType();
                            this.holding.setParent(this);
                            this.holding.setTexture(toEat.getTextureID());
                            this.holding.setPosition(0, 1.0f, 0);
                            EntityManager.getInstance().addEntity(this.holding);

                            //We have now animated and picked up this consumable, we are now sethrough when we get home.

                            //Now that we have picked up our item, we need to move home
                            int trackBackX = this.floorX;
                            int trackBackY = this.floorY;
                            int index = GameManager.getInstance().toGridCoords(trackBackX, trackBackY);

                            //If we home, then break.
                            if(checkIsHome()){
                                break;
                            }

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
                            //Remove world object
                            GameManager.getInstance().removeWorldObject(toEat);

                            //Look through other eaters and move them all back one unit
                            LinkedList<Mole> allEaters = new LinkedList<>(frameEaters);
                            allEaters.add(this);
                            for(Mole mole : allEaters){
                                Move move = ((Move)mole.memory.get(GameManager.getInstance().toGridCoords(mole.getFloorX(), mole.getFloorY())));
                                mole.performAction(move);
                            }
                        }
                    }
                }
                break;
            }
        }
    }

    @Override
    public void onStep(Entity other) {
        switch(other.getType()) {
            case WATER: {
                GameManager.getInstance().triggerFail();
            }
        }
    }

    @Override
    public boolean isAnimating() {
        return animating;
    }

    public boolean checkIsHome(){
        if (this.floorX == startX && this.floorY == startY) {
            System.out.println("I am home! With food too!");
            this.isSeethrough = true;
            this.isSatisfied  = true;
            //Remove self
            GameManager.getInstance().removeWorldObjectAtIndex(GameManager.getInstance().toGridCoords(this.floorX, this.floorY));
            //If onion, plug up hole
            switch(this.healdType){
                case ONION:{
                    OnionBlocker blocker = new OnionBlocker(null);
                    blocker.setPosition(GameManager.getInstance().toWorldSpace(this.floorX, this.floorY));
                    EntityManager.getInstance().addEntity(blocker);
                    GameManager.getInstance().getWorldObjects()[GameManager.getInstance().toGridCoords(this.floorX, this.floorY)] = blocker;
                    break;
                }
            }
            //Move into hole
            super.getPosition().add(new Vector3f(0, -1, 0));
            return true;
        }
        return false;
    }

    @Override
    public Vector3f getWorldOffset() {
        return new Vector3f(0);
    }

    public void setLookAngle(EnumDirection determineDirection) {
        switch (determineDirection){
            case UP:{
                super.setRotation(0, 180, 0);
                break;
            }
            case DOWN:{
                super.setRotation(0, 0, 0);
                break;
            }
            case LEFT:{
                super.setRotation(0, 90, 0);
                break;
            }
            case RIGHT:{
                super.setRotation(0, 270, 0);
                break;
            }
        }
    }
}














































