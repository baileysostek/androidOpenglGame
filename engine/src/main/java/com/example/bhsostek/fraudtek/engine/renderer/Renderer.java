package com.example.bhsostek.fraudtek.engine.renderer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import com.example.bhsostek.fraudtek.engine.actions.ActionManager;
import com.example.bhsostek.fraudtek.engine.camera.Camera;
import com.example.bhsostek.fraudtek.engine.camera.CameraManager;
import com.example.bhsostek.fraudtek.engine.entity.Entity;
import com.example.bhsostek.fraudtek.engine.entity.EntityManager;
import com.example.bhsostek.fraudtek.engine.entity.EnumEntityType;
import com.example.bhsostek.fraudtek.engine.game.GameManager;
import com.example.bhsostek.fraudtek.engine.math.Vector3f;
import com.example.bhsostek.fraudtek.engine.math.MatrixUtils;
import com.example.bhsostek.fraudtek.engine.math.VectorUtils;
import com.example.bhsostek.fraudtek.engine.models.Model;
import com.example.bhsostek.fraudtek.engine.models.ModelManager;
import com.example.bhsostek.fraudtek.engine.renderer.ui.UIManager;
import com.example.bhsostek.fraudtek.engine.scene.SceneManager;
import com.example.bhsostek.fraudtek.engine.util.AssetManager;

import java.util.LinkedList;


public class Renderer implements GLSurfaceView.Renderer {

    private Context context;

    long last = System.nanoTime();

    private float[] projection;

    public Renderer(Context context){
        this.context = context;
    }

    public void onSurfaceCreated(GL10 unused, EGLConfig config) {
        // Set the background frame color
        GLES20.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);

        //Initialize all managers
        AssetManager.initialize(context);
        SpriteManager.initialize();
        ShaderManager.initialize();
        ModelManager.initialize();
        CameraManager.initialize();
        EntityManager.initialize();
        UIManager.initialize();
        ActionManager.initialize();
        GameManager.Initialize();
        SceneManager.initialize();

        //Preload images
        //TODO remove and have async loading screens
        SpriteManager.getInstance().loadTexture("map_icon.png");
        SpriteManager.getInstance().loadTexture("restart.png");
        SpriteManager.getInstance().loadTexture("mole.png");
        SpriteManager.getInstance().loadTexture("stone.png");
        SpriteManager.getInstance().loadTexture("avocado.png");
        SpriteManager.getInstance().loadTexture("onion.png");
        SpriteManager.getInstance().loadTexture("white_normal.png");
        SpriteManager.getInstance().loadTexture("grass.png");
        SpriteManager.getInstance().loadTexture("sand.png");

    }

    public void onDrawFrame(GL10 unused) {
        long now = System.nanoTime();
        double delta = ((double)((double)now - last) / 1000000000L);

        //TICK
        SceneManager.getInstance().update(delta);
        UIManager.getInstance().update(delta);

        //Render
        SceneManager.getInstance().render();


        //After all rendering calc new time
        last = now;

    }

    public void onSurfaceChanged(GL10 unused, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
        float ratio = (float) width / height;
        UIManager.getInstance().setAR(ratio);

        // this projection matrix is applied to object coordinates
        // in the onDrawFrame() method
//        Matrix.frustumM(projectionMatrix, 0, -ratio, ratio, -1, 1, 3, 7);
//        GLES20.glUniformMatrix4fv(GLES20.glGetUniformLocation(shaderID, "perspective"), 1, false, projectionMatrix, 0);
    }
}