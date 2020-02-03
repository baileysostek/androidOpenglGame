package com.example.bhsostek.fraudtek.engine.renderer.ui;

import com.example.bhsostek.fraudtek.engine.game.GameManager;
import com.example.bhsostek.fraudtek.engine.renderer.SpriteManager;

public class RestartUI extends UI {
    public RestartUI() {
        super(SpriteManager.getInstance().loadTexture("restart.png"));
        super.setAlignment(EnumAlignment.TOP_RIGHT);
        super.setScale(0.25f);
    }

    @Override
    public void onPress(){
        GameManager.getInstance().promptReload();
    }
}
