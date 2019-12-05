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
import com.example.bhsostek.fraudtek.engine.util.AssetManager;

import java.util.LinkedList;


public class Renderer implements GLSurfaceView.Renderer {

    private Context context;

    int shaderID=0;

    long last = System.nanoTime();

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
        ActionManager.initialize();
        GameManager.Initialize();

        SpriteManager.getInstance().genTexture();

        shaderID = ShaderManager.getInstance().loadShader("main");
        ShaderManager.getInstance().useShader(shaderID);

//        //Load some models
//        Model quad = ModelManager.getInstance().loadModel("quad2.obj");
//        Model avocado = ModelManager.getInstance().loadModel("avocado.obj");
//        Model onion = ModelManager.getInstance().loadModel("onion.obj");
//        Model pepper = ModelManager.getInstance().loadModel("pepper.obj");
//        Model mole = ModelManager.getInstance().loadModel("mole.obj");
//        Model sphere = ModelManager.getInstance().loadModel("sphere_smooth.obj");
//        EntityManager.getInstance().addEntity(new Entity().setModel(sphere).setPosition(0, 2, 0));

        //Load level
//        GameManager.getInstance().loadLevel("icePallace/1-01.txt");
        GameManager.getInstance().loadLevel("kitchen_1/1-01.json");
//        GameManager.getInstance().loadLevel("kitchen_1/1-06.json");


//        float size = 32;
//        for(int i = 0; i < 512; i++) {
//            EntityManager.getInstance().addEntity(new Entity(new Vector3f((float) (Math.random() * size) - (size / 2.0f), (float) (Math.random() * size) - (size / 2.0f), (float) (Math.random() * size) - (size / 2.0f)), models.get((int) Math.floor(models.size() * Math.random()))));
//        }
//
//        EntityManager.getInstance().addEntity(new Entity().setModel(mole).setPosition(new Vector3f(-4, 0, 4)).setScale(0.5f));

        GLES20.glUniformMatrix4fv(GLES20.glGetUniformLocation(shaderID, "perspective"), 1, false, MatrixUtils.createProjectionMatrix(), 0);
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
    }

    public void onDrawFrame(GL10 unused) {
        long now = System.nanoTime();
        double delta = ((double)((double)now - last) / 1000000000L);
        //UDPATE PART
        EntityManager.getInstance().update(delta);
        GameManager.getInstance().update(delta);
        //RENDER PART
        //Clear frame
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        Camera cam = CameraManager.getInstance().getActiveCamera();
//        cam.setRotation(cam.getRotation().add(new Vector3f(0, 1, 0)));
        if(VectorUtils.getDistance(new Vector3f(0, 0, 0), cam.getPosition()) < 5.5f) {
            cam.getPosition().add(cam.getForwardDir().inverse().mul(0.1f));
        }

        GLES20.glUniformMatrix4fv(GLES20.glGetUniformLocation(shaderID, "view"), 1, true, CameraManager.getInstance().getActiveCamera().getTransform(), 0);

        float[] out = CameraManager.getInstance().getActiveCamera().getForwardDir().toVec3N();
        out[0] *= -1.0f;
        out[1] *= -1.0f;
        out[2] *= -1.0f;
        GLES20.glUniform3fv(GLES20.glGetUniformLocation(shaderID, "inverseCamera"), 1, out, 0);

        //Render all entities
        int lastID = -1;
        for(Entity entity : EntityManager.getInstance().getEntities()){
            if(lastID != entity.getModel().getID()){
                ShaderManager.getInstance().loadHandshakeIntoShader(shaderID, entity.getModel().getHandshake());
                lastID = entity.getModel().getID();
//                System.out.println("Buffering new model:" + lastID);
            }
            //Mess with uniforms
            GLES20.glUniformMatrix4fv(GLES20.glGetUniformLocation(shaderID, "transformation"), 1, true, entity.getTransform(), 0);
            GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, entity.getModel().getNumIndicies());
        }

        //After all rendering calc new time
        last = now;

    }

    public void onSurfaceChanged(GL10 unused, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
        float ratio = (float) width / height;

        // this projection matrix is applied to object coordinates
        // in the onDrawFrame() method
//        Matrix.frustumM(projectionMatrix, 0, -ratio, ratio, -1, 1, 3, 7);
//        GLES20.glUniformMatrix4fv(GLES20.glGetUniformLocation(shaderID, "perspective"), 1, false, projectionMatrix, 0);
    }
}