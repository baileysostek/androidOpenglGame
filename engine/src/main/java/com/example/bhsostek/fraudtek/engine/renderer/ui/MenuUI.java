package com.example.bhsostek.fraudtek.engine.renderer.ui;

import com.example.bhsostek.fraudtek.engine.game.GameManager;
import com.example.bhsostek.fraudtek.engine.renderer.SpriteManager;
import com.example.bhsostek.fraudtek.engine.scene.MenuScene;
import com.example.bhsostek.fraudtek.engine.scene.SceneManager;

public class MenuUI extends UI {
    public MenuUI() {
        super(SpriteManager.getInstance().loadTexture("destroy.png"));
        super.setAlignment(EnumAlignment.CENTER_LEFT);
        super.setScale(0.25f);
    }

    @Override
    public void onPress(){
        SceneManager.getInstance().setScene(new MenuScene());
    }
}
