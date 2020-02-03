package com.example.bhsostek.fraudtek.engine.renderer.ui;

import com.example.bhsostek.fraudtek.engine.animation.EnumLoop;
import com.example.bhsostek.fraudtek.engine.animation.Timeline;
import com.example.bhsostek.fraudtek.engine.game.GameManager;
import com.example.bhsostek.fraudtek.engine.renderer.SpriteManager;
import com.example.bhsostek.fraudtek.engine.scene.GameplayScene;
import com.example.bhsostek.fraudtek.engine.scene.MenuScene;
import com.example.bhsostek.fraudtek.engine.scene.SceneManager;
import com.example.bhsostek.fraudtek.engine.util.Callback;

import java.sql.Time;

public class LevelInformation extends UI {

    private final String levelName;
    private Timeline fadeIn;
    private Timeline fadeOut;
    private final float scale = 0.95f;

    public LevelInformation(String levelName) {
        super(SpriteManager.getInstance().loadTexture("9slice.png"));
        super.setAlignment(EnumAlignment.CENTER_CENTER);
        super.setScale(0);
        //Higher than level UI
        super.setzIndex(2);
        this.levelName = levelName;

        fadeIn = new Timeline();
        fadeIn.setDuration(0.25f);
        fadeIn.addKeyFrame("scale", 0, 0);
        fadeIn.addKeyFrame("scale", 0.15f, 1);
        fadeIn.addKeyFrame("scale", 0.20f, 1.1f);
        fadeIn.addKeyFrame("scale", 0.25f, 1.0f);

        fadeIn.setLoop(EnumLoop.STOP_LAST_VALUE);

        fadeOut = new Timeline();
        fadeOut.setDuration(0.1f);
        fadeOut.addKeyFrame("scale", 0, 1);
        fadeOut.addKeyFrame("scale", 0.1f, 0);

        final UI that = this;

        fadeOut.addCallback(0.1f, new Callback() {
            @Override
            public Object callback(Object... objects) {
                UIManager.getInstance().removeUI(that);
                return null;
            }
        });

        fadeOut.setLoop(EnumLoop.STOP_LAST_VALUE);

    }

    public void close(){
        fadeOut.start();
    }

    @Override
    public void onAdd(){
        fadeIn.start();
    }

    @Override
    public void update(double delta){
        fadeIn.update(delta);
        fadeOut.update(delta);
        super.setScale(scale * fadeIn.getValueOf("scale") * fadeOut.getValueOf("scale"));
    }

    @Override
    public void onPress(){
//        GameManager.getInstance().cleanup();
        GameManager.getInstance().loadLevel(this.levelName);
        SceneManager.getInstance().setScene(new GameplayScene());
    }
}
