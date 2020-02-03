package com.example.bhsostek.fraudtek.engine.entity;

import com.example.bhsostek.fraudtek.engine.actions.Action;
import com.example.bhsostek.fraudtek.engine.math.Vector3f;
import com.example.bhsostek.fraudtek.engine.models.ModelManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;

public class Water extends Entity implements WorldObject{

    public Water() {
        super();
    }

    private int worldX = 0;
    private int worldY = 0;
    private int startX = 0;
    private int startY = 0;

    public Water(JSONObject saveData) {
        super();
        this.setModel(ModelManager.getInstance().loadModel("quad.obj"));
        this.setScale(0.5f);
        this.setType(EnumEntityType.WATER);
        this.setTexture("water.png");
        initialize(saveData);
    }

    @Override
    public void initialize(JSONObject object){
        try {
            int x = object.getInt("x");
            int y = object.getInt("y");

            this.worldX = x;
            this.startX = x;

            this.worldY = y;
            this.startY = y;

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(double delta){
//        this.rotate(0, 1f, 0);
//        offset.setY(0.5f + (float)(0.25f * Math.sin(Math.toRadians(offsetTick))));
//        offsetTick+=2f;
//        offsetTick%=360f;
    }

    @Override
    public int getFloorX() {
        return worldX;
    }

    @Override
    public int getFloorY() {
        return worldY;
    }

    @Override
    public void setFloorX(int x) {
        this.worldX = x;
    }

    @Override
    public void setFloorY(int y) {
        this.worldY = y;
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
        this.startX = x;
    }

    @Override
    public void setStartY(int y) {
        this.startY = y;
    }

    @Override
    public void setIsSeethrough(boolean isSeethrough) {
        return;
    }

    @Override
    public boolean isSeethrough() {
        return true;
    }

    @Override
    public void isSatisfied(boolean isSatisfied) {
        return;
    }

    @Override
    public boolean isSatisfied() {
        return true;
    }

    @Override
    public void setActions(LinkedList<Action> actions) {

    }

    @Override
    public void addAction(Action action) {

    }

    @Override
    public LinkedList<Action> getActions() {
        return null;
    }

    @Override
    public void spliceAction(int index) {

    }

    @Override
    public void performAction(Action action) {

    }

    @Override
    public void onStep(Entity other) {

    }

    @Override
    public boolean isAnimating() {
        return false;
    }

    @Override
    public Vector3f getWorldOffset() {
        return new Vector3f();
    }
}
