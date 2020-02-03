package com.example.bhsostek.fraudtek.engine.renderer.ui;

import com.example.bhsostek.fraudtek.engine.game.GameManager;
import com.example.bhsostek.fraudtek.engine.renderer.SpriteManager;

public class EditUI extends UI {
    public EditUI() {
        super(SpriteManager.getInstance().loadTexture("tree.png"));
        super.setAlignment(EnumAlignment.TOP_RIGHT);
        super.setScale(0.25f);
    }

    @Override
    public void onPress(){
        GameManager.getInstance().enterEdit();
    }
}
