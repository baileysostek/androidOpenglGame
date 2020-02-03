package com.example.bhsostek.fraudtek.engine.scene;

import android.opengl.GLES20;
import android.view.MotionEvent;

import com.example.bhsostek.fraudtek.engine.animation.Timeline;
import com.example.bhsostek.fraudtek.engine.renderer.SpriteManager;
import com.example.bhsostek.fraudtek.engine.renderer.ui.UI;
import com.example.bhsostek.fraudtek.engine.renderer.ui.UIManager;
import com.example.bhsostek.fraudtek.engine.util.Callback;

public class LogoScene extends Scene{

    UI logo = null;
    Timeline fadeIn = new Timeline();

    private boolean requestClose = false;

    public LogoScene(){
        //LOGO
        logo = new UI(SpriteManager.getInstance().loadTexture("cadmium_logo_pixel.png", GLES20.GL_LINEAR));
        logo.setScale(0.9f);

        fadeIn.setDuration(5f);

        fadeIn.addKeyFrame("alpha", 0, 0);
        fadeIn.addKeyFrame("alpha", 1, 0);
        fadeIn.addKeyFrame("alpha", 1.5f, 1);
        fadeIn.addKeyFrame("alpha", 3.5f, 1);
        fadeIn.addKeyFrame("alpha", 4f, 0);

        fadeIn.addCallback(fadeIn.getDuration(), new Callback() {
            @Override
            public Object callback(Object... objects) {
            SceneManager.getInstance().setScene(new MenuScene());
            return null;
            }
        });
    }

    @Override
    public void onLoad() {
        UIManager.getInstance().addUI(logo);
        fadeIn.start();
    }

    @Override
    public void onUnload() {
        UIManager.getInstance().removeUI(logo);
    }

    @Override
    public void update(double delta) {
        if(this.requestClose){
            fadeIn.stop();
            SceneManager.getInstance().setScene(new MenuScene());
            return;
        }
        fadeIn.update(delta);
        logo.setAlpha(fadeIn.getValueOf("alpha"));
    }

    @Override
    public void render() {
        //Clear frame
        GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT);
        UIManager.getInstance().render();
    }

    @Override
    public void onPress(MotionEvent event) {

    }

    @Override
    public void onMove(MotionEvent event) {

    }

    @Override
    public void onRelease(MotionEvent event) {
        this.requestClose = true;
    }
}
