package com.example.bhsostek.fraudtek.engine.scene;

import android.opengl.GLES20;
import android.view.MotionEvent;

import com.example.bhsostek.fraudtek.engine.Game;
import com.example.bhsostek.fraudtek.engine.animation.EnumLoop;
import com.example.bhsostek.fraudtek.engine.animation.Timeline;
import com.example.bhsostek.fraudtek.engine.camera.Camera;
import com.example.bhsostek.fraudtek.engine.game.GameManager;
import com.example.bhsostek.fraudtek.engine.math.Vector2f;
import com.example.bhsostek.fraudtek.engine.math.Vector3f;
import com.example.bhsostek.fraudtek.engine.renderer.Renderer;
import com.example.bhsostek.fraudtek.engine.renderer.ScreenUtils;
import com.example.bhsostek.fraudtek.engine.renderer.SpriteManager;
import com.example.bhsostek.fraudtek.engine.renderer.ui.BackgroundUI;
import com.example.bhsostek.fraudtek.engine.renderer.ui.EnumAlignment;
import com.example.bhsostek.fraudtek.engine.renderer.ui.IngridientTracker;
import com.example.bhsostek.fraudtek.engine.renderer.ui.LevelInformation;
import com.example.bhsostek.fraudtek.engine.renderer.ui.UI;
import com.example.bhsostek.fraudtek.engine.renderer.ui.UIManager;
import com.example.bhsostek.fraudtek.engine.util.AssetManager;
import com.example.bhsostek.fraudtek.engine.util.Callback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;

public class OverworldMap extends Scene{

    private Timeline moveToSelected = new Timeline();

    private LinkedList<UI> uiElements = new LinkedList<>();
    private UI hit = null;
    private LevelInformation levelInfo = null;

    private final float GRID_SCALE = 1.25f;

    private boolean scrollLock = false;

    //Movement stuff
    private boolean canProcessTouch = true;

    private Camera offsetCamera;
    private Vector2f startPos = new Vector2f();
    private Vector2f screenStart = new Vector2f();

    public OverworldMap(){
        //Init camera
        offsetCamera = new Camera();
        offsetCamera.setPosition(new Vector3f());

        //Move to selected
        moveToSelected.setDuration(3f);
        moveToSelected.addKeyFrame("distance", 0, 0);
        moveToSelected.addKeyFrame("distance", moveToSelected.getDuration(), 1);
        moveToSelected.setLoop(EnumLoop.STOP_LAST_VALUE);

        //Add other elements
        uiElements.addFirst(new IngridientTracker());

        uiElements.addFirst(new BackgroundUI("map.png"));

        //Load the map data
        try {
            String levelText = AssetManager.getInstance().readFile("maps/overworld.json");
            JSONObject levelSetData = new JSONObject(levelText);
            JSONArray levelsArray = levelSetData.getJSONArray("levels");
            for(int i = 0; i < levelsArray.length(); i++){
                final JSONObject levelMeta = levelsArray.getJSONObject(i);
                int x = levelMeta.getInt("x");
                int y = levelMeta.getInt("y");
                final String levelName = levelMeta.getString("name");
                UI set = new UI(SpriteManager.getInstance().loadTexture("avocado.png"));
                set.setScale(new Vector2f(0.15f, 0.15f));
                set.setAlignment(EnumAlignment.CUSTOM);
                set.setPosition(new Vector2f( x * GRID_SCALE , y * GRID_SCALE));
                set.setCamera(offsetCamera);
                set.setCallback(new Callback() {
                    @Override
                    public Object callback(Object... objects) {
                    levelInfo = new LevelInformation(levelName);
                    System.out.println(levelMeta);
                    UIManager.getInstance().addUI(levelInfo);
                    uiElements.add(levelInfo);
                    scrollLock = true;
                    return null;
                    }
                });
                uiElements.add(set);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onLoad() {
        for(UI ui : uiElements){
            UIManager.getInstance().addUI(ui);
        }
        moveToSelected.start();
    }

    @Override
    public void onUnload() {
        for(UI ui : uiElements){
            UIManager.getInstance().removeUI(ui);
        }
    }

    @Override
    public void update(double delta) {
        if(hit != null){
            hit.onPress();
            hit = null;
            return;
        }
        moveToSelected.update(delta);
    }

    @Override
    public void render() {
        //Clear frame
        GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT);
        UIManager.getInstance().render();
    }

    @Override
    public void onPress(MotionEvent event) {
        //Make sure that we can process touch still.
        if(canProcessTouch) {
            float screenX = event.getX();
            float screenY = event.getY();

            Vector2f screenPos = new Vector2f(ScreenUtils.screenToGL(screenX, screenY));
            screenPos.mulY(-1f);

            hit = null;
            for (UI ui : uiElements) {
                if (ui.pointInside(screenPos)) {
                    this.hit = ui;
                }
            }

            //If we hit nothing, we are in scroll mode
            if (hit == null && !scrollLock) {
                startPos = offsetCamera.getPosition2D();
                screenStart = new Vector2f(screenX, screenY);
            } else {
                //Check to see if we hit the level info
                if (hit != null && hit.equals(levelInfo)) {
                    //We hit the level info
                    hit.onPress();
                } else {
                    //If we are in a scroll lock, we want to disable scroll lock and remove the hit ui
                    scrollLock = false;
                    //Remove our UI elements
                    if(levelInfo != null) {
                        levelInfo.close();
                    }
                    //Set the touch point.
                    startPos = offsetCamera.getPosition2D();
                    screenStart = new Vector2f(screenX, screenY);

                    //Disable touch input for the rest of this transaction
//                    canProcessTouch = false;
                }
            }
        }
    }

    @Override
    public void onMove(MotionEvent event) {
        if(!this.scrollLock && canProcessTouch) {
            float screenX = event.getX();
            float screenY = event.getY();
            Vector2f offsetPos = new Vector2f(screenX, screenY);
            Vector2f delta = offsetPos.sub(screenStart).mul(new Vector2f(-2, 2));
            offsetCamera.setPosition(new Vector2f(startPos).add(new Vector2f(delta).div(new Vector2f(Game.WIDTH, Game.HEIGHT))));
        }
    }

    @Override
    public void onRelease(MotionEvent event) {
        canProcessTouch = true;
    }
}
