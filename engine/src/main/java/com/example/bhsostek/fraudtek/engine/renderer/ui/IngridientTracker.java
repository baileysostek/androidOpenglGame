package com.example.bhsostek.fraudtek.engine.renderer.ui;

import com.example.bhsostek.fraudtek.engine.game.GameManager;
import com.example.bhsostek.fraudtek.engine.math.Vector2f;
import com.example.bhsostek.fraudtek.engine.renderer.SpriteManager;
import com.example.bhsostek.fraudtek.engine.util.AssetManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;

public class IngridientTracker extends UI {

    private final String[] INGRIDIENTS = new String[]{"AVOCADO", "ONION", "PEPPER", "LIME", "BEAN", "LEVELS", "DEATHS"};
    private LinkedList<UI> ingridients = new LinkedList<>();

    private final float scale = 0.075f;

    public IngridientTracker() {
        super(-1);
        super.setScale(0);
        super.setAlpha(0);

        //Load the save file
        String saveText     = AssetManager.getInstance().readFile("saves/save.json");
        try {
            JSONObject saveData = new JSONObject(saveText);
            int index = 0;

            for(String ingridient : INGRIDIENTS){
                UI ui = new UI(SpriteManager.getInstance().loadTexture(saveData.getJSONObject(ingridient).getString("icon")));
                ui.setScale(scale);
                ui.setAlignment(EnumAlignment.CUSTOM);
                ui.setzIndex(1);
                ui.setPosition(new Vector2f((-(1 / ui.calculatedWidth()) + 0.5f) + (index * ((((1 / ui.calculatedWidth()) * 2))) / (Math.max(INGRIDIENTS.length , 1))) + 0.5f, (1 / ui.calculatedHeight()) - (0.75f)));
                UIManager.getInstance().addUI(ui);
                ingridients.add(ui);
                index++;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onAdd() {

    }

    @Override
    public void onRemove() {
        for(UI ui : ingridients){
            UIManager.getInstance().removeUI(ui);
        }
    }


    @Override
    public void onPress(){
        GameManager.getInstance().promptReload();
    }
}
