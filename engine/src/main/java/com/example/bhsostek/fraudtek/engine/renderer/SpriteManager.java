package com.example.bhsostek.fraudtek.engine.renderer;

import android.opengl.GLES20;

import java.util.HashMap;
import java.util.LinkedList;

public class SpriteManager {
    //Singleton instance
    private static SpriteManager spriteManager;

    private LinkedList<Integer> textureIDs = new LinkedList<>();
    private HashMap<String, Integer> loadedImages = new HashMap<String, Integer>();

    //Private constructor because singleton.
    private SpriteManager(){

    }

    //GenTextures
    public int genTexture(){
        int[] textureIndex = new int[1];
        GLES20.glGenTextures(1, textureIndex, 0);
        System.out.println("Texture:" + textureIndex[0]);
        textureIDs.push(textureIndex[0]);
        return textureIndex[0];
    }

    //Singleton instances
    public static void initialize(){
        if(spriteManager == null){
            spriteManager = new SpriteManager();
        }
    }

    public static SpriteManager getInstance(){
        return spriteManager;
    }

    public void onShutdown(){
        for(int texture: textureIDs){

        }
    }

}
