package com.example.bhsostek.fraudtek.engine.scene;

import android.view.MotionEvent;

public abstract class Scene {

    public abstract void onLoad();
    public abstract void onUnload();
    public abstract void update(double delta);
    public abstract void render();

    //Input methods
    public abstract void onPress(MotionEvent event);
    public abstract void onMove(MotionEvent event);
    public abstract void onRelease(MotionEvent event);
}
