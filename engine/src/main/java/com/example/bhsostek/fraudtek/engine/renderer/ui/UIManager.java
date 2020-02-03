package com.example.bhsostek.fraudtek.engine.renderer.ui;

import android.annotation.SuppressLint;
import android.opengl.GLES20;
import android.opengl.Matrix;

import com.example.bhsostek.fraudtek.engine.camera.Camera;
import com.example.bhsostek.fraudtek.engine.math.MatrixUtils;
import com.example.bhsostek.fraudtek.engine.math.Vector2f;
import com.example.bhsostek.fraudtek.engine.math.Vector3f;
import com.example.bhsostek.fraudtek.engine.models.Model;
import com.example.bhsostek.fraudtek.engine.models.ModelManager;
import com.example.bhsostek.fraudtek.engine.renderer.ShaderManager;
import com.example.bhsostek.fraudtek.engine.renderer.SpriteManager;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class UIManager {

    private static UIManager manager;
    private LinkedList<UI> interfaces = new LinkedList<>();
    private LinkedList<UI> toAdd      = new LinkedList<>();
    private LinkedList<UI> toRemove   = new LinkedList<>();
    private int shaderID;

    private float ar = 1f;

    private Model quad;

    private float[] transform = MatrixUtils.getIdentityMatrix();

    //Lock for locking our entity set
    private Lock lock;

    private Camera camera = new Camera();

    //Private constructor because this is a singleton thing.
    private UIManager() {
        //Setup lock
        lock = new ReentrantLock();

        //Load our Shader and object for quads
        Matrix.rotateM(transform, 0, 90, 0, 0, 1);
        Matrix.rotateM(transform, 0, 90, 0, 1, 0);
        quad = ModelManager.getInstance().loadModel("quad.obj");
        shaderID = ShaderManager.getInstance().loadShader("ui");
    }

    public static void initialize() {
        if (manager == null) {
            manager = new UIManager();
        }
    }

    public void addUI(UI ui) {
        lock.lock();
        try {
            this.toAdd.add(ui);
        } finally {
            lock.unlock();
        }
    }

    public void removeUI(UI ui) {
        lock.lock();
        try {
            this.toRemove.add(ui);
        } finally {
            lock.unlock();
        }
    }

    public void update(double delta) {
        lock.lock();
        try {
            //Clear remove buffer
            LinkedList<UI> removeBuffer = new LinkedList<>(toRemove);
            toRemove.clear();
            for(UI ui : removeBuffer){
                ui.onRemove();
                interfaces.remove(ui);
            }

            //Clear add buffer
            for (UI ui : toAdd) {
                interfaces.add(ui);
                ui.onAdd();
            }
            toAdd.clear();

            //Clear sync buffer
            for (UI ui : interfaces) {
                ui.update(delta);
            }
        } finally {
            lock.unlock();
        }
    }

    public static UIManager getInstance() {
        return manager;
    }

    //Draw all of our Scenes
    @SuppressLint("NewApi")
    public void render() {
        lock.lock();
        try {
            //Load our shader into the currently active program slot
            ShaderManager.getInstance().useShader(shaderID);
            ShaderManager.getInstance().loadHandshakeIntoShader(shaderID, quad.getHandshake());

            GLES20.glEnable(GLES20.GL_BLEND);
            GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);

            //set the aspect ratio
            GLES20.glUniform1f(GLES20.glGetUniformLocation(shaderID, "ar"), ar);
            GLES20.glUniformMatrix4fv(GLES20.glGetUniformLocation(shaderID, "transformation"), 1, true, transform, 0);
            int lastTexture = -1;

            //TODO may allocate too much memory per frame, maybe keep instance of toSort or sort on insert.
            ArrayList<UI> toSort = new ArrayList<UI>(interfaces);
            toSort.sort(new Comparator() {
                @Override
                public int compare(Object ui1, Object ui2) {
                    return ((UI) ui1).getZIndex() - ((UI) ui2).getZIndex();
                }
            });

            for (UI ui : toSort) {
                if (lastTexture != ui.getTextureID()) {
                    GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, ui.getTextureID());
                    GLES20.glUniform1i(GLES20.glGetUniformLocation(shaderID, "textureID"), GLES20.GL_TEXTURE0);
                    lastTexture = ui.getTextureID();
                }
                //Mess with uniforms
                GLES20.glUniform2fv(GLES20.glGetUniformLocation(shaderID, "scale"), 1, ui.getScale(), 0);
                GLES20.glUniform1f(GLES20.glGetUniformLocation(shaderID, "alpha"), ui.getAlpha());
                GLES20.glUniform2fv(GLES20.glGetUniformLocation(shaderID, "position"), 1, ui.getPosition(), 0);
                GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, quad.getNumIndicies());
            }

            //Cleanup
            GLES20.glDisable(GLES20.GL_BLEND);
        } finally {
            lock.unlock();
        }
    }

    public UI getOverlap(Vector2f pos) {
        lock.lock();

        UI buffer = null;

        try {
            for (UI ui : this.interfaces) {
                if (ui.pointInside(pos)) {
                    ui.onPress();
                    buffer = ui;
                    break;
                }
            }
        } finally {
            lock.unlock();
        }

        return buffer;
    }

    public void setAR(float ratio) {
        this.ar = ratio;
    }

    public float getAR() {
        return this.ar;
    }
}
