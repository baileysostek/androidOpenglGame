package com.example.bhsostek.fraudtek.engine.renderer.ui;

import com.example.bhsostek.fraudtek.engine.math.Vector2f;
import com.example.bhsostek.fraudtek.engine.renderer.SpriteManager;

public class BackgroundUI extends UI {
    public BackgroundUI(String texture) {
        super(SpriteManager.getInstance().loadTexture(texture));
        super.setAlignment(EnumAlignment.CENTER_CENTER);
        super.setScale(new Vector2f(UIManager.getInstance().getAR(), 1));
    }

    @Override
    public void onPress(){

    }
}
