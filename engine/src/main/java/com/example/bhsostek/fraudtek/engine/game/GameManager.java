package com.example.bhsostek.fraudtek.engine.game;

import android.view.MotionEvent;

import com.example.bhsostek.fraudtek.engine.actions.Action;
import com.example.bhsostek.fraudtek.engine.actions.ActionComparison;
import com.example.bhsostek.fraudtek.engine.actions.ActionManager;
import com.example.bhsostek.fraudtek.engine.actions.Consume;
import com.example.bhsostek.fraudtek.engine.actions.EnumDirection;
import com.example.bhsostek.fraudtek.engine.animation.EnumLoop;
import com.example.bhsostek.fraudtek.engine.animation.Timeline;
import com.example.bhsostek.fraudtek.engine.camera.CameraManager;
import com.example.bhsostek.fraudtek.engine.entity.Entity;
import com.example.bhsostek.fraudtek.engine.entity.EntityManager;
import com.example.bhsostek.fraudtek.engine.entity.EnumEntityType;
import com.example.bhsostek.fraudtek.engine.entity.Floor;
import com.example.bhsostek.fraudtek.engine.entity.Mole;
import com.example.bhsostek.fraudtek.engine.entity.Water;
import com.example.bhsostek.fraudtek.engine.entity.WorldObject;
import com.example.bhsostek.fraudtek.engine.math.Vector2f;
import com.example.bhsostek.fraudtek.engine.math.Vector3f;
import com.example.bhsostek.fraudtek.engine.renderer.ScreenUtils;
import com.example.bhsostek.fraudtek.engine.renderer.ui.BackgroundUI;
import com.example.bhsostek.fraudtek.engine.renderer.ui.Ingridients;
import com.example.bhsostek.fraudtek.engine.renderer.ui.UIManager;
import com.example.bhsostek.fraudtek.engine.util.AssetManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;

public class GameManager {

    private static GameManager gameManager;

    //Game State
    private Level    loadedLevel = null;
    private Entity[] grid;
    private Entity[] worldObjects;

    private LinkedList<EnumEntityType> ingridients = new LinkedList<>();
    private Ingridients ingridients_ui;

    private boolean pressed = false;
    private float screenX = 0;
    private float screenY = 0;

    private boolean failed = false;
    private boolean canPlace = true;

    final int MAX_CLOSEST = 128;

    //Used for editor
    private EnumGameState state = EnumGameState.PLAY;
    private int placeIndex = 0;

    //Sometimes we have entities which overlap oneanother, this happens for instance when a mole moves over an ingridient, or two moles try to eat the same avocado
    private LinkedList<Entity> frameOverlap = new LinkedList<>();
    private HashMap<Entity, Action> frameActions = new HashMap<>();
    private LinkedList<Entity> blackList = new LinkedList<>();

    //TODO move to inventory class
    //Inventory stuff
    Entity ingridient;
    private int lastValidX = -1;
    private int lastValidY = -1;
    private Floor lastFloor = null;

    //Perform one action every x unit of time
    float TIME_SCALE = 0.33f;
    double time = 0;
    private boolean shouldReload = false;

    //Space Timeline
    private Timeline toSpace;
    private Vector3f baseCamPos = new Vector3f();
    private BackgroundUI space_bg;

    private GameManager(){
        toSpace = new Timeline();
        toSpace.setDuration(1);
        toSpace.setLoop(EnumLoop.STOP_LAST_VALUE);

        space_bg = new BackgroundUI("space.png");
        space_bg.setzIndex(1);
        space_bg.setAlpha(0);
        space_bg.setScale(new Vector2f(UIManager.getInstance().getAR(), 1));
        UIManager.getInstance().addUI(space_bg);

        toSpace.addKeyFrame("camera", 0f, 0f);
        toSpace.addKeyFrame("camera", 0.5f, 1f);
        toSpace.addKeyFrame("alpha", 0f, 0f);
        toSpace.addKeyFrame("alpha", 0.5f, 1f);

    }

    public void update(double delta){
        toSpace.update(delta);
        if(toSpace.isRunning()){
            System.out.println("Running:"+new Vector3f(baseCamPos).add(new Vector3f(0, 1, 0).mul(128 * toSpace.getValueOf("camera"))));
            CameraManager.getInstance().getActiveCamera().setPosition(new Vector3f(baseCamPos).add(new Vector3f(0, 128f, 0).mul(toSpace.getValueOf("camera"))));
            space_bg.setAlpha(toSpace.getValueOf("alpha"));
        }

        if(shouldReload){
            shouldReload = false;
            restartLevel();
            return;
        }

        if(!state.equals(EnumGameState.PLAY)){
            return;
        }

        if(failed){
            System.out.println("Failure state");
            return;
        }

        if(pressed){
            if(canPlace) {
                return;
            }
        }

        //Update all world objects
        boolean actorsAnimating = false;
        for(Entity e : worldObjects){
            if(e != null){
                e.update(delta);
                if(e instanceof WorldObject){
                    if(((WorldObject)e).isAnimating()){
                        actorsAnimating = true;
                    }
                }
            }
        }

        //If actors are animating we dont want to continue to process action until they are done.
        if(actorsAnimating){
            return;
        }


        time += delta;
        if(time > TIME_SCALE){
            time = ((double) time - TIME_SCALE);
            //Every time scale
            if(actorsHaveActions()) {
                canPlace = false;
                frameActions.clear();
                blackList.clear();
                HashMap<Entity, Action> actionSet = genActionSet();
                this.setFrameActions(actionSet);

                //Sort the actions to be preformed
                LinkedList<Action> actions = new LinkedList<Action>(actionSet.values());
                Collections.sort(actions, new ActionComparison());

                for(Action action : actions){
                    //Find this entity
                    Entity entity = null;
                    for(Entity check : actionSet.keySet()){
                        if(actionSet.get(check) == action){
                            entity = check;
                            break;
                        }
                    }
                    if(entity != null) {
                        if (!blackList.contains(entity)) {
                            ((WorldObject) entity).performAction(actionSet.get(entity));
                        }
                    }
                }
            }else{
                canPlace = true;
                //Check to see if we are satisifed
                boolean satisified = true;
                loop:{
                    for(Entity e : EntityManager.getInstance().getEntitiesOfType(EnumEntityType.MOLE)){
                        Mole mole = (Mole) e;
                        if(!mole.isSatisfied()){
                            satisified = false;
                            break loop;
                        }
                    }
                    //WIN
                    if(!state.equals(EnumGameState.WIN)){
                        state = EnumGameState.WIN;
                        baseCamPos = CameraManager.getInstance().getActiveCamera().getPosition();
//                        toSpace.start();
                    }
                }
                if(!satisified){
                    //Reset Moles
                    for(Entity e : EntityManager.getInstance().getEntitiesOfType(EnumEntityType.MOLE)){
                        Mole mole = (Mole)e;
                        //Reset closest on anger.
                        mole.setClosest(MAX_CLOSEST);
                    }
                    //Shoot out rays from ingrieints
                    //TODO all ingredients
                    for(Entity ingredient : EntityManager.getInstance().getEntitiesOfType(EnumEntityType.AVOCADO, EnumEntityType.ONION)){
                        WorldObject worldObject = ((WorldObject)ingredient);
                        DistancePair[] adjasent = getAdjacent(worldObject.getFloorX(), worldObject.getFloorY());
                        for(DistancePair dp : adjasent){
                            if(dp != null) {
                                if(dp.getEntity() != null) {
                                    if (dp.getEntity().getType().equals(EnumEntityType.MOLE)) {
                                        //Check to see if closest
                                        Mole mole = (Mole) dp.getEntity();
                                        if(dp.getDistance() < mole.getClosest()){
                                            mole.setClosest(dp.getDistance());
                                            ((WorldObject) dp.getEntity()).setActions(ActionManager.getInstance().genMoveActions(dp.getFloorX(), dp.getFloorY(), worldObject.getFloorX(), worldObject.getFloorY()));
                                            ((WorldObject) dp.getEntity()).addAction(new Consume(ingredient));
                                            mole.setLookAngle(ActionManager.getInstance().determineDirection(dp.getFloorX(), dp.getFloorY(), worldObject.getFloorX(), worldObject.getFloorY()));
                                        }else if(dp.getDistance() == mole.getClosest()){
                                            //We confused and equidistant
                                            mole.getActions().clear();
                                        }
                                    }
                                }
                            }
                        }
                    }

                    //Check
                }
            }
            //Try to move our entities back into the world.
            if(frameOverlap.size() > 0) {
                LinkedList<Entity> frameOverlapBuffer = new LinkedList<>();
                for (Entity e : frameOverlap) {
                    WorldObject wo = ((WorldObject) e);
                    if (worldObjects[toGridCoords(wo.getFloorX(), wo.getFloorY())] == null) {
                        worldObjects[toGridCoords(wo.getFloorX(), wo.getFloorY())] = e;
                        frameOverlapBuffer.add(e);
                    }
                }
                for(Entity e : frameOverlapBuffer){
                    frameOverlap.remove(e);
                }
                frameOverlapBuffer.clear();
            }
        }
    }

    private void setFrameActions(HashMap<Entity, Action> actionSet) {
        this.frameActions = actionSet;
    }

    public boolean actorsHaveActions(){
        for(Entity e : EntityManager.getInstance().getEntitiesOfType(EnumEntityType.MOLE)){
           if(e instanceof WorldObject){
               if(((WorldObject) e).getActions().size() > 0){
                   return true;
               }
           }
        }
        return false;
    }

    public HashMap<Entity, Action> genActionSet(){
        HashMap<Entity, Action> actionSet = new HashMap<Entity, Action>();
        for(Entity e : EntityManager.getInstance().getEntitiesOfType(EnumEntityType.MOLE)){
            if(e instanceof WorldObject){
                if(((WorldObject) e).getActions().size() > 0){
                    actionSet.put(e, ((WorldObject) e).getActions().getFirst());
                    ((WorldObject) e).spliceAction(0);
                }
            }
        }
        return actionSet;
    }

    public Entity[] getWorldObjects(){
        return this.worldObjects;
    }

    public void loadLevel(String levelPath){
        try {
            String levelText = AssetManager.getInstance().readFile("levels/" + levelPath);
            JSONObject levelData = new JSONObject(levelText);
            System.out.println(levelData);

            //Sizing
            int width  = levelData.getInt("width");
            int height = levelData.getInt("height");

            //Init grid
            grid         = new Entity[width * height];
            worldObjects = new Entity[width * height];

            //Create level object
            Level level = new Level(levelData.getString("name"), width, height);
            level.setEntities(levelData.getJSONArray("entities"));
            level.setIngridients(levelData.getJSONArray("inventory"));

            loadedLevel = level;

            initializeLevel(level);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void initializeLevel(Level level){
        try {
            //First we clear the level
            unloadLevel();

            //Set the Game state to play
            this.state = EnumGameState.PLAY;

            //Load Ingridients
            String[] ingrdientString = new String[level.getIngidients().length()];
            for(int i = 0; i < level.getIngidients().length(); i++){
                String ingridient = level.getIngidients().getString(i).toUpperCase();
                this.ingridients.push(EnumEntityType.valueOf(ingridient));
                ingrdientString[i] = ingridient;
            }

            ingridients_ui = new Ingridients(ingrdientString);

            UIManager.getInstance().addUI(ingridients_ui);

            //Create the floor grid
            for (int j = 0; j < level.getHeight(); j++) {
                for(int i = 0; i < level.getWidth(); i++) {
                    Entity floorTile = new Floor(i, j).setPosition(new Vector3f((float) i - (((float)level.getWidth()) / 2f), -0.5f, (float)j - (((float)level.getHeight()) / 2f)));
                    EntityManager.getInstance().addEntity(floorTile);
                    grid[i + (j * level.getWidth())] = floorTile;
                }
            }

            //Load all entities for this level
            for(int i = 0; i < level.getEntities().length(); i++){
                JSONObject object = level.getEntities().getJSONObject(i);
                int x = object.getInt("x");
                int y = object.getInt("y");
                Entity worldObject = EntityManager.getInstance().createEntityFromRegistry(object);
                worldObject.setPosition(x - ((float)level.getWidth() / 2f), 0, y - ((float)level.getHeight() / 2f));
                //TODO move to constuctor or initialize somehow
                if(worldObject instanceof Water){
                    worldObject.getPosition().add(new Vector3f(0, 0.1f, 0));
                }
                if(worldObject instanceof WorldObject){
                    WorldObject wo = ((WorldObject) worldObject);
                    wo.setFloorX(x);
                    wo.setFloorY(y);
                    wo.setStartX(x);
                    wo.setStartY(y);
                    //Offset starting pos
                    worldObject.getPosition().add(wo.getWorldOffset());
                }
                worldObjects[x + (y * level.getWidth())] = worldObject;
                System.out.println("Adding:"+ worldObject+" x:"+x+" y:"+y);
                EntityManager.getInstance().addEntity(worldObject);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void unloadLevel(){
        this.failed = false;
        this.canPlace = true;
        this.ingridients.clear();

        if(this.ingridients_ui != null) {
            UIManager.getInstance().removeUI(this.ingridients_ui);
        }

        //Remove world objects.
        int index = 0;
        for(Entity e : worldObjects){
            if(e != null) {
                EntityManager.getInstance().removeEntity(e);
            }
            worldObjects[index] = null;
            index++;
        }
        //Remove floor
        for(EnumEntityType type : EnumEntityType.values()) {
            for (Entity e : EntityManager.getInstance().getEntitiesOfType(type)) {
                EntityManager.getInstance().removeEntity(e);
            }
        }
    }

    public DistancePair[] getAdjacent(int x, int y){
        DistancePair[] out = new DistancePair[4];
        //0 = up;
        int up = y;
        while(up > 0){
            up--;
            if(this.toGridCoords(x , up) < worldObjects.length) {
                Entity upEntity = worldObjects[this.toGridCoords(x , up)];
                if(upEntity != null){
                    if(!((WorldObject)upEntity).isSeethrough()){
                        out[EnumDirection.UP.getIndex()] = new DistancePair(y - up, upEntity, x, up);
                        break;
                    }
                }
            }else{
                out[EnumDirection.UP.getIndex()] = null;
                break;
            }
        }
        //1 = down;
        int down = y;
        while(down < loadedLevel.getHeight()-1){
            down++;
            if(this.toGridCoords(x, down) < worldObjects.length) {
                Entity downEntity = worldObjects[this.toGridCoords(x, down)];
                if (downEntity != null) {
                    if (!((WorldObject)downEntity).isSeethrough()) {
                        out[EnumDirection.DOWN.getIndex()] = new DistancePair(down - y, downEntity, x, down);
                        break;
                    }
                }
            }else{
                out[EnumDirection.DOWN.getIndex()] = null;
                break;
            }
        }
        //2 = left;
        int left = x;
        while(left > 0){
            left--;
            if(this.toGridCoords(left, y) < worldObjects.length) {
                Entity leftEntity = worldObjects[this.toGridCoords(left, y)];
                if (leftEntity != null) {
                    if (!((WorldObject)leftEntity).isSeethrough()) {
                        out[EnumDirection.LEFT.getIndex()] = new DistancePair(x - left, leftEntity, left, y);
                        break;
                    }
                }
            }else{
                out[EnumDirection.LEFT.getIndex()] = null;
                break;
            }
        }
        //3 = right;
        int right = x;
        while(right < loadedLevel.getWidth()-1){
            right++;
            if(this.toGridCoords(right , y) < worldObjects.length) {
                Entity rightEntity = worldObjects[this.toGridCoords(right, y)];
                if (rightEntity != null) {
                    if (!((WorldObject)rightEntity).isSeethrough()) {
                        out[EnumDirection.RIGHT.getIndex()] = new DistancePair(right - x, rightEntity, right, y);
                        break;
                    }
                }
            }else{
                out[EnumDirection.RIGHT.getIndex()] = null;
                break;
            }
        }

//        //Print
//        int index = 0;
//        for(DistancePair dp : out){
//            System.out.println(EnumDirection.values()[index] + ":" + dp);
//            index++;
//        }

        return out;
    }

    public int toGridCoords(int x, int y){
        return x + (y * loadedLevel.getWidth());
    }

    public Vector2f fromGridCoords(int xy){
        return new Vector2f(xy % loadedLevel.getWidth(), (int)Math.floor(xy / loadedLevel.getWidth()));
    }

    public Entity getFloorSpace(int x, int y){
        int index = (x + (y * loadedLevel.getWidth()));
        if(index < grid.length && index >= 0){
            return grid[index];
        }
        return null;
    }

    public void moveWorldObject(int oldPos, int newPos){
        if(worldObjects[newPos] != null){
            System.out.println("We are moving onto an actor...");
            frameOverlap.push(worldObjects[newPos]);
            ((WorldObject)worldObjects[oldPos]).onStep(worldObjects[newPos]);
            worldObjects[newPos] = null;
        }
        worldObjects[newPos] = worldObjects[oldPos];
        worldObjects[oldPos] = null;
    }

    public LinkedList<Entity> getFrameOverlap(){
        return this.frameOverlap;
    }

    public Vector3f toWorldSpace(int x, int y){
        return new Vector3f(x - ((float)loadedLevel.getWidth() / 2f), 0, y - ((float)loadedLevel.getHeight() / 2f));
    }

    public void removeWorldObject(Entity object){
        if(object instanceof WorldObject) {
            WorldObject edible = ((WorldObject) object);
            GameManager.getInstance().removeWorldObjectAtIndex(GameManager.getInstance().toGridCoords(edible.getFloorX(), edible.getFloorY()));
            EntityManager.getInstance().removeEntity(object);
        }
    }

    //Interaction Callbacks
    public void onPress(MotionEvent motionEvent) {
        pressed = true;
        screenX = motionEvent.getX();
        screenY = motionEvent.getY();

        if(!canPlace){
            return;
        }

        JSONObject initializationData = new JSONObject();
        try {
            initializationData.put("x", 0);
            initializationData.put("y", 0);

            //Otherwise we will load a selection
            if(state.equals(EnumGameState.PLAY)) {
                if (ingridients.size() > 0) {
                    ingridient = EntityManager.getInstance().generateEntityOfType(ingridients.pop(), initializationData);
                    EntityManager.getInstance().addEntity(ingridient);
                } else {
                    //Out of ingridients
                }
            }else if(state.equals(EnumGameState.EDIT)){
                System.out.println("Try place:"+EnumEntityType.values()[placeIndex]);
                ingridient = EntityManager.getInstance().generateEntityOfType(EnumEntityType.values()[placeIndex], initializationData);
                System.out.println("ingridient:"+ingridient);
                if(ingridient != null){
                    EntityManager.getInstance().addEntity(ingridient);
                }else{
                    ingridient = EntityManager.getInstance().generateEntityOfType(EnumEntityType.AVOCADO, initializationData);
                    EntityManager.getInstance().addEntity(ingridient);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //Thread safe
    public void promptReload(){
        this.shouldReload = true;
    }

    public void enterEdit(){
        this.state = EnumGameState.EDIT;
    }

    public void incrementPlaceIndex(){
        this.placeIndex+=1;
        this.placeIndex%=EnumEntityType.values().length;
    }

    public void onMove(MotionEvent motionEvent) {
        screenX = motionEvent.getX();
        screenY = motionEvent.getY();

        if(!canPlace){
            return;
        }

        float[] dir = ScreenUtils.glToWorld(ScreenUtils.screenToGL(GameManager.getInstance().getScreenX(), GameManager.getInstance().getScreenY()), CameraManager.getInstance().getActiveCamera());
        LinkedList<Entity> hit = EntityManager.getInstance().getHitEntities(CameraManager.getInstance().getActiveCamera().getPosition(), new Vector3f(dir), EnumEntityType.FLOOR);

        if(ingridient != null) {
            if (hit.size() > 0) {
                Entity base = hit.getFirst();
                Floor floor = (Floor) base;
                if(lastFloor != null){
                    lastFloor.returnToInitialTexture();
                }
                floor.setTexture(0);
                lastFloor = floor;
                lastValidX = floor.getX();
                lastValidY = floor.getY();
                ingridient.setPosition(new Vector3f(base.getPosition()).add(new Vector3f(0f, 1f, 0f)));
            }
        }

    }

    public void onRelease(MotionEvent motionEvent) {
        if(!canPlace){
            return;
        }
        if(ingridient != null) {
            float[] dir = ScreenUtils.glToWorld(ScreenUtils.screenToGL(screenX, screenY), CameraManager.getInstance().getActiveCamera());
            LinkedList<Entity> hit = EntityManager.getInstance().getHitEntities(CameraManager.getInstance().getActiveCamera().getPosition(), new Vector3f(dir), EnumEntityType.FLOOR);
            if (hit.size() > 0) {
                Floor floor = (Floor) hit.getFirst();
                System.out.println("Clicked: " + floor.getX() + " , " + floor.getY());
                if (worldObjects[toGridCoords(floor.getX(), floor.getY())] == null) {
                    worldObjects[toGridCoords(floor.getX(), floor.getY())] = ingridient;
                    DistancePair[] adjasent = getAdjacent(floor.getX(), floor.getY());
                    for (DistancePair dp : adjasent) {
                        if (dp != null) {
                            if (dp.getEntity() != null) {
                                if (dp.getEntity().getType().equals(EnumEntityType.MOLE)) {
                                    Mole mole = (Mole) dp.getEntity();
                                    if (dp.getDistance() < mole.getClosest()) {
                                        mole.setClosest(dp.getDistance());
                                        ((WorldObject) dp.getEntity()).setActions(ActionManager.getInstance().genMoveActions(dp.getFloorX(), dp.getFloorY(), floor.getX(), floor.getY()));
                                        ((WorldObject) dp.getEntity()).addAction(new Consume(ingridient));
                                        mole.setLookAngle(ActionManager.getInstance().determineDirection(dp.getFloorX(), dp.getFloorY(), floor.getX(), floor.getY()));
                                        System.out.println("Adding consume action.");
                                    } else if (dp.getDistance() == mole.getClosest()) {
                                        //We confused and equidistant
                                        mole.getActions().clear();
                                    }
                                }
                            }
                        }
                        System.out.println("Distance Pair:" + dp);
                    }
                } else {
                    //This is when an ingridient is destroyed
                    //TODO some cool particle effect
                    ingridients.push(ingridient.getType());
                    EntityManager.getInstance().removeEntity(ingridient);
                }
            }else{

                System.out.println("We hit nothing on release.");
                int worldObjectIndex = toGridCoords(lastValidX, lastValidY);
                if (worldObjectIndex >= 0 && worldObjects[worldObjectIndex] == null) {
                    worldObjects[toGridCoords(lastValidX, lastValidY)] = ingridient;
                    DistancePair[] adjasent = getAdjacent(lastValidX, lastValidY);
                    for (DistancePair dp : adjasent) {
                        if (dp != null) {
                            if (dp.getEntity() != null) {
                                if (dp.getEntity().getType().equals(EnumEntityType.MOLE)) {
                                    Mole mole = (Mole) dp.getEntity();
                                    if (dp.getDistance() < mole.getClosest()) {
                                        mole.setClosest(dp.getDistance());
                                        ((WorldObject) dp.getEntity()).setActions(ActionManager.getInstance().genMoveActions(dp.getFloorX(), dp.getFloorY(), lastValidX, lastValidY));
                                        ((WorldObject) dp.getEntity()).addAction(new Consume(ingridient));
                                        mole.setLookAngle(ActionManager.getInstance().determineDirection(dp.getFloorX(), dp.getFloorY(), lastValidX, lastValidY));
                                        System.out.println("Adding consume action.");
                                    } else if (dp.getDistance() == mole.getClosest()) {
                                        //We confused and equidistant
                                        mole.getActions().clear();
                                    }
                                }
                            }
                        }
                        System.out.println("Distance Pair:" + dp);
                    }
                } else {
                    //This is when an ingridient is destroyed
                    //TODO some cool particle effect
                    ingridients.push(ingridient.getType());
                    EntityManager.getInstance().removeEntity(ingridient);
                }

            }

            lastValidX = -1;
            lastValidY = -1;
            screenX = 0;
            screenY = 0;
            pressed = false;
            System.out.println("release");
            printWorldObjects();
            ingridient = null;
        }
    }


    //Singleton access methods
    public static void Initialize(){
        if(AssetManager.getInstance() == null){
            System.err.println("The AssetManager needs to be initialized before the GameManager can be initialized.");
            return;
        }
        if(gameManager == null){
            gameManager = new GameManager();
        }
    }

    public static GameManager getInstance(){
        return gameManager;
    }

    public boolean isPressed(){
        return this.pressed;
    }

    //TODO move to input
    public float getScreenX() {
        return this.screenX;
    }

    public float getScreenY() {
        return this.screenY;
    }

    public void removeWorldObjectAtIndex(int toGridCoords) {
        this.worldObjects[toGridCoords] = null;
    }

    public HashMap<Entity, Action> getFrameActions() {
        return this.frameActions;
    }

    public void addToBlackList(Entity entity){
        blackList.push(entity);
    }

    public void printWorldObjects() {
        System.out.println("------ World Objects ------");
        int index = 0;
        for(Entity e : GameManager.getInstance().getWorldObjects()){
            if(e != null){
                System.out.println(e);
                System.out.println("Located at:"+GameManager.getInstance().fromGridCoords(index));
                System.out.println("--------------");
            }
            index++;
        }
        System.out.println("------ Overlap Buffer ------");
        for(Entity e : frameOverlap){
            if(e != null){
                System.out.println(e);
                WorldObject wo = ((WorldObject) e);
                System.out.println("Located at:"+new Vector2f(wo.getFloorX(), wo.getFloorY()));
                System.out.println("--------------");
            }
            index++;
        }
    }

    public int getMaxDistance() {
        return this.MAX_CLOSEST;
    }

    public void triggerFail() {
        this.failed = true;
    }

    public void restartLevel(){
        if(loadedLevel != null){
            CameraManager.getInstance().getActiveCamera().setPosition(baseCamPos);
            initializeLevel(loadedLevel);
        }
    }

    public float getTimeScale(){
        return this.TIME_SCALE;
    }


    public void cleanup() {
        this.loadedLevel = null;
        worldObjects = new Entity[]{};
        for(Entity e : EntityManager.getInstance().getEntities()){
            EntityManager.getInstance().removeEntity(e);
        }

        if(this.ingridients_ui != null) {
            UIManager.getInstance().removeUI(this.ingridients_ui);
        }

        this.ingridient = null;
        this.ingridients.clear();
        this.space_bg.setAlpha(0);
    }
}
