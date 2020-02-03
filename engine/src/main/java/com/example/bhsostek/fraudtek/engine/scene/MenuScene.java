package com.example.bhsostek.fraudtek.engine.scene;

import android.opengl.GLES20;
import android.view.MotionEvent;

import com.example.bhsostek.fraudtek.engine.animation.EnumInterpolation;
import com.example.bhsostek.fraudtek.engine.animation.EnumLoop;
import com.example.bhsostek.fraudtek.engine.animation.Timeline;
import com.example.bhsostek.fraudtek.engine.math.Vector2f;
import com.example.bhsostek.fraudtek.engine.renderer.ScreenUtils;
import com.example.bhsostek.fraudtek.engine.renderer.SpriteManager;
import com.example.bhsostek.fraudtek.engine.renderer.ui.BackgroundUI;
import com.example.bhsostek.fraudtek.engine.renderer.ui.EnumAlignment;
import com.example.bhsostek.fraudtek.engine.renderer.ui.UI;
import com.example.bhsostek.fraudtek.engine.renderer.ui.UIManager;
import com.example.bhsostek.fraudtek.engine.util.Callback;

public class MenuScene extends Scene{

    UI logo = null;
    UI play = null;
    UI background = null;
    Timeline fadeIn = new Timeline();

    private boolean loadGame = false;

    public MenuScene(){
        //LOGO
        logo = new UI(SpriteManager.getInstance().loadTexture("guac-a-mole.png"));
        logo.setScale(0.9f);
        logo.setAlignment(EnumAlignment.CUSTOM);
        logo.setPosition(new Vector2f(0, 0.2f));
        fadeIn.setDuration(5);

        fadeIn.addKeyFrame("alpha", 0, 0);
        fadeIn.addKeyFrame("alpha", 0.5f, 0);
        fadeIn.addKeyFrame("alpha", 1.5f, 1);

        background = new BackgroundUI("space.png");

        fadeIn.addCallback(2.0f, new Callback() {
            @Override
            public Object callback(Object... objects) {
            play = new UI(SpriteManager.getInstance().loadTexture("play.png", GLES20.GL_LINEAR));
            play.setScale(0.9f);
            play.setAlignment(EnumAlignment.CUSTOM);
            play.setPosition(new Vector2f(0, -0.75f));
            play.setScale(new Vector2f(0.5f, 0.35f));
            play.setCallback(new Callback() {
                @Override
                public Object callback(Object... objects) {
                SceneManager.getInstance().setScene(new OverworldMap());
                return null;
                }
            });
            UIManager.getInstance().addUI(play);
            return null;
            }
        });

        fadeIn.setLoop(EnumLoop.STOP_LAST_VALUE);

    }

    @Override
    public void onLoad() {
        UIManager.getInstance().addUI(background);
        UIManager.getInstance().addUI(logo);
        fadeIn.start();
    }

    @Override
    public void onUnload() {
        UIManager.getInstance().removeUI(logo);
        UIManager.getInstance().removeUI(play);
        UIManager.getInstance().removeUI(background);
    }

    @Override
    public void update(double delta) {
        if(loadGame){
            play.onPress();
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
        float screenX = event.getX();
        float screenY = event.getY();

        Vector2f screenPos = new Vector2f(ScreenUtils.screenToGL(screenX, screenY));
        screenPos.mulY(-1f);

        if(play != null) {
            if (play.pointInside(screenPos)) {
                this.loadGame = true;
            }
        }
    }
}
