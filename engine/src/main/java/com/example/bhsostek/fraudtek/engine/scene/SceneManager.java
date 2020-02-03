package com.example.bhsostek.fraudtek.engine.scene;

import android.opengl.GLES20;
import android.view.MotionEvent;

import java.util.LinkedList;

public class SceneManager {
    private static SceneManager manager;

    private Scene loadedScene = null;
    private LinkedList<Scene> scenes = new LinkedList<Scene>();

    private SceneManager(){
        this.setScene(new LogoScene());
//        this.setScene(new OverworldMap());
    }

    public void update(double delta){
        if(this.loadedScene != null){
            loadedScene.update(delta);
        }
    }

    public void onPress(MotionEvent motionEvent) {
        if(this.loadedScene != null){
            this.loadedScene.onPress(motionEvent);
        }
    }

    public void onMove(MotionEvent motionEvent) {
        if(this.loadedScene != null){
            this.loadedScene.onMove(motionEvent);
        }
    }

    public void onRelease(MotionEvent motionEvent) {
        if(this.loadedScene != null){
            this.loadedScene.onRelease(motionEvent);
        }
    }

    public void render(){
        //Clear frame
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        if(this.loadedScene != null){
            try {
                loadedScene.render();
            }catch(NullPointerException e){

            }
        }
    }

    public void setScene(Scene scene){
        if(scene != this.loadedScene){
            if(this.loadedScene != null) {
                this.loadedScene.onUnload();
            }
        }
        this.loadedScene = scene;
        scene.onLoad();
    }

    //Singleton
    public static void initialize(){
        if(manager == null){
            manager = new SceneManager();
        }
    }

    public static SceneManager getInstance(){
        return manager;
    }
}
