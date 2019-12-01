package com.example.bhsostek.fraudtek.engine.game;

import android.view.MotionEvent;

import com.example.bhsostek.fraudtek.engine.actions.Action;
import com.example.bhsostek.fraudtek.engine.actions.ActionComparison;
import com.example.bhsostek.fraudtek.engine.actions.ActionManager;
import com.example.bhsostek.fraudtek.engine.actions.Consume;
import com.example.bhsostek.fraudtek.engine.actions.EnumActionType;
import com.example.bhsostek.fraudtek.engine.actions.EnumDirection;
import com.example.bhsostek.fraudtek.engine.camera.CameraManager;
import com.example.bhsostek.fraudtek.engine.entity.Avocado;
import com.example.bhsostek.fraudtek.engine.entity.Entity;
import com.example.bhsostek.fraudtek.engine.entity.EntityManager;
import com.example.bhsostek.fraudtek.engine.entity.EnumEntityType;
import com.example.bhsostek.fraudtek.engine.entity.Floor;
import com.example.bhsostek.fraudtek.engine.entity.Mole;
import com.example.bhsostek.fraudtek.engine.entity.WorldObject;
import com.example.bhsostek.fraudtek.engine.math.Vector3f;
import com.example.bhsostek.fraudtek.engine.renderer.ScreenUtils;
import com.example.bhsostek.fraudtek.engine.util.AssetManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;

public class GameManager {

    private static GameManager gameManager;

    private Level    loadedLevel = null;
    private Entity[] grid;
    private Entity[] worldObjects;

    private boolean pressed = false;
    private float screenX = 0;
    private float screenY = 0;

    private HashMap<Entity, Action> frameActions = new HashMap<>();
    private LinkedList<Entity> blackList = new LinkedList<>();

    //TODO move to inventory class
    //Inventory stuff
    Entity ingridient;

    //Perform one action every x unit of time
    float timeScale = 0.25f;
    double time = 0;

    int index = 1;

    private GameManager(){

    }

    public void update(double delta){
        time += delta;
        if(time > timeScale){
            time = ((double) time - timeScale);
            //Every time scale
            if(actorsHaveActions()) {
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
//                    System.out.println("We Won, next level please");
                    unloadLevel();
                }
                if(!satisified){
                    //Shoot out rays from ingrieints
                    //TODO all ingridients
//                    for(Entity ingridient : EntityManager.getInstance().getEntitiesOfType(EnumEntityType.AVOCADO)){
//                        WorldObject worldObject = ((WorldObject)ingridient);
//                        DistancePair[] adjasent = getAdjacent(worldObject.getFloorX(), worldObject.getFloorY());
//                        for(DistancePair dp : adjasent){
//                            if(dp != null) {
//                                if(dp.getEntity() != null) {
//                                    if (dp.getEntity().getType().equals(EnumEntityType.MOLE)) {
//                                        ((WorldObject) dp.getEntity()).setActions(ActionManager.getInstance().genMoveActions(dp.getFloorX(), dp.getFloorY(), worldObject.getFloorX(), worldObject.getFloorY()));
//                                        ((WorldObject) dp.getEntity()).addAction(new Consume(ingridient));
//                                    }
//                                }
//                            }
//                        }
//                    }

                    //Check
                }
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
            loadedLevel = level;

            //Create the floor grid
            for (int j = 0; j < height; j++) {
                for(int i = 0; i < width; i++) {
                    Entity floorTile = new Floor(i, j).setPosition(new Vector3f((float) i - (((float)level.getWidth()) / 2f), -0.5f, (float)j - (((float)level.getHeight()) / 2f)));
                    EntityManager.getInstance().addEntity(floorTile);
                    grid[i + (j * width)] = floorTile;
                }
            }

            //Load all entities for this level
            JSONArray entityData = levelData.getJSONArray("entities");
            System.out.println(entityData);
            for(int i = 0; i < entityData.length(); i++){
                JSONObject object = entityData.getJSONObject(i);
                int x = object.getInt("x");
                int y = object.getInt("y");
                Entity worldObject = EntityManager.getInstance().createEntityFromRegistry(object);
                worldObject.setPosition(x - ((float)width / 2f), 0, y - ((float)height / 2f));
                if(worldObject instanceof WorldObject){
                    WorldObject wo = ((WorldObject) worldObject);
                    wo.setFloorX(x);
                    wo.setFloorY(y);
                    wo.setStartX(x);
                    wo.setStartY(y);
                }
                worldObjects[x + (y * width)] = worldObject;
                System.out.println("Adding:"+ worldObject+" x:"+x+" y:"+y);
                EntityManager.getInstance().addEntity(worldObject);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void unloadLevel(){
        //Remove world objects.
        for(int j = 0; j < loadedLevel.getHeight(); j++){
            for(int i = 0; i < loadedLevel.getWidth(); i++){
                Entity e = worldObjects[toGridCoords(i, j)];
                if(e != null) {
                    System.out.println(e);
                    EntityManager.getInstance().removeEntity(e);
                }
                //Free
                e = null;
                worldObjects[toGridCoords(i, j)] = null;

            }
        }
        //Remove floor
        for(EnumEntityType type : EnumEntityType.values()) {
            for (Entity e : EntityManager.getInstance().getEntitiesOfType(type)) {
                EntityManager.getInstance().removeEntity(e);
            }
        }
        index++;
        loadLevel("kitchen_1/1-0"+index+".json");

//        System.out.println("Remaing entities:" + EntityManager.getInstance().getEntities().size());

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

        int index = 0;
        for(DistancePair dp : out){
            System.out.println(EnumDirection.values()[index] + ":" + dp);
            index++;
        }

        return out;
    }

    public int toGridCoords(int x, int y){
        return x + (y * loadedLevel.getWidth());
    }

    public Entity getFloorSpace(int x, int y){
        int index = (x + (y * loadedLevel.getWidth()));
        if(index < grid.length && index >= 0){
            return grid[index];
        }
        return null;
    }

    public void moveWorldObject(int oldPos, int newPos){
        worldObjects[newPos] = worldObjects[oldPos];
        worldObjects[oldPos] = null;
    }

    //Interaction Callbacks
    public void onPress(MotionEvent motionEvent) {
        pressed = true;
        screenX = motionEvent.getX();
        screenY = motionEvent.getY();

        ingridient = new Avocado(null);
        EntityManager.getInstance().addEntity(ingridient);

    }

    public void onMove(MotionEvent motionEvent) {
        screenX = motionEvent.getX();
        screenY = motionEvent.getY();

        float[] dir = ScreenUtils.glToWorld(ScreenUtils.screenToGL(GameManager.getInstance().getScreenX(), GameManager.getInstance().getScreenY()), CameraManager.getInstance().getActiveCamera());
        LinkedList<Entity> hit = EntityManager.getInstance().getHitEntities(CameraManager.getInstance().getActiveCamera().getPosition(), new Vector3f(dir), EnumEntityType.FLOOR);

        if(hit.size() > 0){
            Entity base = hit.getFirst();
            ingridient.setPosition(new Vector3f(base.getPosition()).add(new Vector3f(0f, 1f, 0f)));
        }

    }

    public void onRelease(MotionEvent motionEvent) {
        float[] dir = ScreenUtils.glToWorld(ScreenUtils.screenToGL(screenX, screenY), CameraManager.getInstance().getActiveCamera());
        LinkedList<Entity> hit = EntityManager.getInstance().getHitEntities(CameraManager.getInstance().getActiveCamera().getPosition(), new Vector3f(dir), EnumEntityType.FLOOR);
        if(hit.size() > 0){
            Floor floor = (Floor) hit.getFirst();
            System.out.println("Clicked: "+ floor.getX() + " , " + floor.getY());
            if(worldObjects[toGridCoords(floor.getX(), floor.getY())] == null){
                worldObjects[toGridCoords(floor.getX(), floor.getY())] = ingridient;
                DistancePair[] adjasent = getAdjacent(floor.getX(), floor.getY());
                for(DistancePair dp : adjasent){
                    if(dp != null) {
                        if(dp.getEntity() != null) {
                            if (dp.getEntity().getType().equals(EnumEntityType.MOLE)) {
                                ((WorldObject) dp.getEntity()).setActions(ActionManager.getInstance().genMoveActions(dp.getFloorX(), dp.getFloorY(), floor.getX(), floor.getY()));
                                ((WorldObject) dp.getEntity()).addAction(new Consume(ingridient));
                                System.out.println("Adding consume action.");
                            }
                        }
                    }
                }
            }else{
                //TODO some cool particle effect
                EntityManager.getInstance().removeEntity(ingridient);
            }
        }

        screenX = 0;
        screenY = 0;
        pressed = false;
        System.out.println("release");
        ingridient = null;
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
}
