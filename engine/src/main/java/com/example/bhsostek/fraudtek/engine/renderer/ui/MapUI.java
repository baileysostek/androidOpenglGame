package com.example.bhsostek.fraudtek.engine.renderer.ui;

import com.example.bhsostek.fraudtek.engine.game.GameManager;
import com.example.bhsostek.fraudtek.engine.renderer.SpriteManager;
import com.example.bhsostek.fraudtek.engine.scene.OverworldMap;
import com.example.bhsostek.fraudtek.engine.scene.SceneManager;

public class MapUI extends UI {
    public MapUI() {
        super(SpriteManager.getInstance().loadTexture("map_icon.png"));
        super.setAlignment(EnumAlignment.BOTTOM_RIGHT);
        super.setScale(0.25f);
    }

    @Override
    public void onPress(){
        GameManager.getInstance().cleanup();
        SceneManager.getInstance().setScene(new OverworldMap());
    }
}
