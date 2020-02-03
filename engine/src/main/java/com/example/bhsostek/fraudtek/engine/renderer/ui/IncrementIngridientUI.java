package com.example.bhsostek.fraudtek.engine.renderer.ui;

import com.example.bhsostek.fraudtek.engine.game.GameManager;
import com.example.bhsostek.fraudtek.engine.renderer.SpriteManager;

public class IncrementIngridientUI extends UI {
    public IncrementIngridientUI() {
        super(SpriteManager.getInstance().loadTexture("water.png"));
        super.setAlignment(EnumAlignment.BOTTOM_RIGHT);
        super.setScale(0.25f);
    }

    @Override
    public void onPress(){
        GameManager.getInstance().incrementPlaceIndex();
    }
}
