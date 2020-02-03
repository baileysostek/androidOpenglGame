package com.example.bhsostek.fraudtek.engine.scene;

import android.opengl.GLES20;
import android.view.MotionEvent;

import com.example.bhsostek.fraudtek.engine.camera.Camera;
import com.example.bhsostek.fraudtek.engine.camera.CameraManager;
import com.example.bhsostek.fraudtek.engine.entity.Entity;
import com.example.bhsostek.fraudtek.engine.entity.EntityManager;
import com.example.bhsostek.fraudtek.engine.game.GameManager;
import com.example.bhsostek.fraudtek.engine.math.MatrixUtils;
import com.example.bhsostek.fraudtek.engine.math.Vector2f;
import com.example.bhsostek.fraudtek.engine.math.Vector3f;
import com.example.bhsostek.fraudtek.engine.math.VectorUtils;
import com.example.bhsostek.fraudtek.engine.renderer.ScreenUtils;
import com.example.bhsostek.fraudtek.engine.renderer.ShaderManager;
import com.example.bhsostek.fraudtek.engine.renderer.ui.EditUI;
import com.example.bhsostek.fraudtek.engine.renderer.ui.IncrementIngridientUI;
import com.example.bhsostek.fraudtek.engine.renderer.ui.Ingridients;
import com.example.bhsostek.fraudtek.engine.renderer.ui.MapUI;
import com.example.bhsostek.fraudtek.engine.renderer.ui.MenuUI;
import com.example.bhsostek.fraudtek.engine.renderer.ui.RestartUI;
import com.example.bhsostek.fraudtek.engine.renderer.ui.UI;
import com.example.bhsostek.fraudtek.engine.renderer.ui.UIManager;

import java.util.LinkedList;

public class GameplayScene extends Scene{

    int shaderID = 0;
    float[] projection;

    private LinkedList<UI> uiElements = new LinkedList<>();

    private Ingridients ingridients;

    public GameplayScene(){
        shaderID = ShaderManager.getInstance().loadShader("main");
        ShaderManager.getInstance().useShader(shaderID);

        this.projection = MatrixUtils.createProjectionMatrix();
        GLES20.glUniformMatrix4fv(GLES20.glGetUniformLocation(shaderID, "perspective"), 1, false, this.projection, 0);

        uiElements.add(new RestartUI());
        uiElements.add(new MapUI());

    }

    @Override
    public void onLoad() {
        for(UI ui : uiElements){
            UIManager.getInstance().addUI(ui);
        }
    }

    @Override
    public void onUnload() {
        for(UI ui : uiElements){
            UIManager.getInstance().removeUI(ui);
        }
        EntityManager.getInstance().clearEntities();
    }

    @Override
    public void update(double delta) {
        //UDPATE PART
        EntityManager.getInstance().update(delta);
        GameManager.getInstance().update(delta);
    }

    @Override
    public void render() {
        ShaderManager.getInstance().useShader(shaderID);
        GLES20.glUniformMatrix4fv(GLES20.glGetUniformLocation(shaderID, "perspective"), 1, false, this.projection, 0);
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);

        Camera cam = CameraManager.getInstance().getActiveCamera();
//        cam.setRotation(cam.getRotation().add(new Vector3f(0, 1, 0)));
        if(VectorUtils.getDistance(new Vector3f(0, 0, 0), cam.getPosition()) < 12f) {
            cam.getPosition().add(cam.getForwardDir().inverse().mul(1.0f));
        }

        GLES20.glUniformMatrix4fv(GLES20.glGetUniformLocation(shaderID, "view"), 1, true, CameraManager.getInstance().getActiveCamera().getTransform(), 0);

        float[] out = CameraManager.getInstance().getActiveCamera().getForwardDir().toVec3N();
        out[0] *= -1.0f;
        out[1] *= -1.0f;
        out[2] *= -1.0f;
        GLES20.glUniform3fv(GLES20.glGetUniformLocation(shaderID, "inverseCamera"), 1, out, 0);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);

        //Render all entities
        int lastID = -1;
        int lastTexture = -1;
        for(Entity entity : EntityManager.getInstance().getEntities()){
            if(lastID != entity.getModel().getID()){
                ShaderManager.getInstance().loadHandshakeIntoShader(shaderID, entity.getModel().getHandshake());
                lastID = entity.getModel().getID();
//                System.out.println("Buffering new model:" + lastID);
            }

            if(lastTexture != entity.getTextureID()) {
                GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, entity.getTextureID());
                GLES20.glUniform1i(GLES20.glGetUniformLocation(shaderID, "textureID"), GLES20.GL_TEXTURE0);
                lastTexture = entity.getTextureID();
            }

            //Mess with uniforms
            GLES20.glUniformMatrix4fv(GLES20.glGetUniformLocation(shaderID, "transformation"), 1, true, entity.getTransform(), 0);
            GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, entity.getModel().getNumIndicies());
        }


        GLES20.glDisable(GLES20.GL_DEPTH_TEST);

        //Clear frame
        GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT);
        UIManager.getInstance().render();
    }

    @Override
    public void onPress(MotionEvent event) {
        float screenX = event.getX();
        float screenY = event.getY();

        Vector2f screenPos = new Vector2f(ScreenUtils.screenToGL(screenX, screenY));
        screenPos.mulY(-1f);

        UI hit = UIManager.getInstance().getOverlap(screenPos);
        if(hit != null){
            System.out.println("hit:"+hit);
        }

        GameManager.getInstance().onPress(event);
    }

    @Override
    public void onMove(MotionEvent event) {
        GameManager.getInstance().onMove(event);
    }

    @Override
    public void onRelease(MotionEvent event) {
        GameManager.getInstance().onRelease(event);
    }


}
