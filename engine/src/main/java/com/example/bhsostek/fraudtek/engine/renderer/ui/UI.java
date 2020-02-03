package com.example.bhsostek.fraudtek.engine.renderer.ui;

import com.example.bhsostek.fraudtek.engine.camera.Camera;
import com.example.bhsostek.fraudtek.engine.math.Vector2f;
import com.example.bhsostek.fraudtek.engine.math.Vector3f;
import com.example.bhsostek.fraudtek.engine.util.Callback;

public class UI {
    private int textureID;

    //UI uinique uniforms
    private Vector2f position = new Vector2f(0, 0);
    private Vector2f scale = new Vector2f(1);
    private float alpha = 1.0f;

    //Zindex
    private int zIndex = 0;

    private EnumAlignment alignment = EnumAlignment.CUSTOM;

    private Callback callback = null;
    private Camera camera = null;

    //NOTE no model information needs to be stored within a UI, this is because all of the models are quads.

    public UI(int textureID){
        this.textureID = textureID;
    }

    public int getTextureID(){
        return this.textureID;
    }

    public float getAlpha(){
        return this.alpha;
    }

    public void setAlpha(float alpha) {
        this.alpha = alpha;
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public void setCamera(Camera camera){
        this.camera = camera;
    }

    public float[] getScale(){
        return this.scale.toArray();
    }

    public boolean pointInside(Vector2f position){
        float[] posBuffer = getPosition();
        float xMin = posBuffer[0] - getRelativeScale().x() / 2f;
        float xMax = posBuffer[0] + getRelativeScale().x() / 2f;
        float yMin = posBuffer[1] - getRelativeScale().y() / 2f;
        float yMax = posBuffer[1] + getRelativeScale().y() / 2f;
        return (position.x() >= xMin && position.x() <= xMax) && (position.y() >= yMin && position.y() <= yMax);
    }

    //On Added to scene.
    public void onAdd(){
        return;
    }

    //When this UI is unloaded, call this function. This is used mainly to allow for ui's to remove their children.
    public void onRemove(){
        return;
    }

    public void update(double delta){
        return;
    }

    public void onPress(){
        if(this.callback != null){
            callback.callback();
            System.out.println("Callback");
        }
    }

    private Vector2f getRelativeScale(){
        Vector2f relScale = new Vector2f(1);
        relScale.mul(scale);
        relScale.divX(UIManager.getInstance().getAR());
        relScale.mul(2);
        return relScale;
    }

    public float[] getPosition() {
        float[] out = getRelativeScale().mul(position).toArray();
        switch (alignment){
            case CUSTOM:{
                break;
            }
            case CENTER_CENTER:{
                out = new Vector2f(0, 0).toArray();
                break;
            }
            case TOP_LEFT:{
                out = new Vector2f(-1, 1).add(getRelativeScale().mul(new Vector2f(1, -1)).div(2f)).toArray();
                break;
            }
            case CENTER_LEFT:{
                out = new Vector2f(-1, 0).add(getRelativeScale().mul(new Vector2f(1, 0)).div(2f)).toArray();
                break;
            }
            case BOTTOM_LEFT:{
                out = new Vector2f(-1, -1).add(getRelativeScale().div(2f)).toArray();
                break;
            }
            case TOP_CENTER:{
                out = new Vector2f(0, 1).add(getRelativeScale().mul(new Vector2f(0, -1)).div(2f)).toArray();
                break;
            }
            case BOTTOM_CENTER:{
                out = new Vector2f(0, -1).add(getRelativeScale().mul(new Vector2f(0, 1)).div(2f)).toArray();
                break;
            }
            case TOP_RIGHT:{
                out = new Vector2f(1, 1).sub(getRelativeScale().mul(new Vector2f(1, 1)).div(2f)).toArray();
                break;
            }
            case CENTER_RIGHT:{
                out = new Vector2f(1, 0).sub(getRelativeScale().mul(new Vector2f(1, 0)).div(2f)).toArray();
                break;
            }
            case BOTTOM_RIGHT:{
                out = new Vector2f(1, -1).sub(getRelativeScale().mul(new Vector2f(1, -1)).div(2f)).toArray();
                break;
            }
        }

        if(this.camera != null) {
            //Transform by camera
            out[0] -= camera.getX();
            out[1] -= camera.getY();
        }

        return out;
    }

    public void setPosition(Vector2f vector2f) {
        this.position = vector2f;
    }

    public void setScale(float v) {
        this.scale = new Vector2f(v);
    }

    public void setScale(Vector2f vector) {
        this.scale = new Vector2f(vector);
    }

    public void setAlignment(EnumAlignment type) {
        this.alignment = type;
    }

    protected float calculatedHeight() {
       return getRelativeScale().y();
    }

    protected float calculatedWidth() {
        return getRelativeScale().x();
    }

    public void setzIndex(int zIndex){
        this.zIndex = zIndex;
    }

    public int getZIndex(){
        return this.zIndex;
    }
}
