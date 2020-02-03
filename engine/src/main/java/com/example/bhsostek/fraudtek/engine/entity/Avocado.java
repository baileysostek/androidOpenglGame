package com.example.bhsostek.fraudtek.engine.entity;

import com.example.bhsostek.fraudtek.engine.actions.Action;
import com.example.bhsostek.fraudtek.engine.math.MatrixUtils;
import com.example.bhsostek.fraudtek.engine.math.Vector3f;
import com.example.bhsostek.fraudtek.engine.models.ModelManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;

public class Avocado extends Entity implements WorldObject{

    private Vector3f offset = new Vector3f();
    private int offsetTick = 0;

    private int worldX = 0;
    private int worldY = 0;
    private int startX = 0;
    private int startY = 0;

    public Avocado() {
        super();
        this.setTexture("avocado.png");
        this.setType(EnumEntityType.AVOCADO);
    }

    public Avocado(JSONObject saveData) {
        super();
        this.setModel(ModelManager.getInstance().loadModel("avocado.obj"));
        this.setTexture("avocado.png");
//        this.setScale(0.3f);
        this.setType(EnumEntityType.AVOCADO);
        initialize(saveData);
    }

    @Override
    public void update(double delta){
        this.rotate(0, 1f, 0);
        offset.setY(0.5f + (float)(0.25f * Math.sin(Math.toRadians(offsetTick))));
//        offset.setZ(0.5f + (float)(0.25f * Math.sin(Math.toRadians(offsetTick))));
        offsetTick+=2f;
        offsetTick%=360f;
    }

    @Override
    public float[] getTransform(){
        float[] transform = super.getTransform();
        MatrixUtils.translate(transform, offset);
        return transform;
    }

    @Override
    public void initialize(JSONObject object){

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
    public Vector3f getWorldOffset() {
        return new Vector3f();
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
    public void setIsSeethrough(boolean isSeethrough) {
        return;
    }

    @Override
    public boolean isSeethrough() {
        return false;
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
}
