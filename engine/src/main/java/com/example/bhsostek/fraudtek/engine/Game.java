package com.example.bhsostek.fraudtek.engine;

import android.annotation.SuppressLint;
import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.DisplayMetrics;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;

import com.example.bhsostek.fraudtek.engine.camera.Camera;
import com.example.bhsostek.fraudtek.engine.camera.CameraManager;
import com.example.bhsostek.fraudtek.engine.entity.EntityManager;
import com.example.bhsostek.fraudtek.engine.entity.EnumEntityType;
import com.example.bhsostek.fraudtek.engine.game.GameManager;
import com.example.bhsostek.fraudtek.engine.math.Vector3f;
import com.example.bhsostek.fraudtek.engine.models.ModelManager;
import com.example.bhsostek.fraudtek.engine.renderer.ScreenUtils;
import com.example.bhsostek.fraudtek.engine.renderer.ShaderManager;
import com.example.bhsostek.fraudtek.engine.util.AssetManager;

public class Game extends GLSurfaceView {

    private final com.example.bhsostek.fraudtek.engine.renderer.Renderer renderer;

    public static int WIDTH;
    public static int HEIGHT;

    @SuppressLint("ClickableViewAccessibility")
    public Game(Context context){
        super(context);
        // Create an OpenGL ES 2.0 context
        this.setEGLContextClientVersion(2);
        this.setPreserveEGLContextOnPause(true);
        renderer = new com.example.bhsostek.fraudtek.engine.renderer.Renderer(context);

        DisplayMetrics metrics = context.getResources().getDisplayMetrics();

        Game.WIDTH  = metrics.widthPixels;
        Game.HEIGHT = metrics.heightPixels;

        System.out.println("Device Resolution: " + Game.WIDTH + " , " + Game.HEIGHT);

        this.setOnDragListener(new OnDragListener() {
            @Override
            public boolean onDrag(View view, DragEvent dragEvent) {
                System.out.println("Drag" + dragEvent);
                return false;
            }
        });

        this.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()){
                    case (MotionEvent.ACTION_DOWN):{
                        GameManager.getInstance().onPress(motionEvent);
                        break;
                    }
                    case (MotionEvent.ACTION_MOVE):{
                        GameManager.getInstance().onMove(motionEvent);
                        break;
                    }
                    case (MotionEvent.ACTION_UP):{
                        GameManager.getInstance().onRelease(motionEvent);
                        break;
                    }
                }

                return true;
            }
        });

        initialize(context);

        // Set the Renderer for drawing on the GLSurfaceView
        setRenderer(renderer);
    }

    public void initialize(Context context){

    }

    public void pause(){

    }

    public void unPause(){

    }

    public void onShutdown(){
        ShaderManager.getInstance().shutdown();
    }
}