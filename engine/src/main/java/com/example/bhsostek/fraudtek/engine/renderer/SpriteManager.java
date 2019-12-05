package com.example.bhsostek.fraudtek.engine.renderer;

import android.opengl.GLES20;

import com.example.bhsostek.fraudtek.engine.util.AssetManager;

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

    public void loadTexture(String texture){
        if(!loadedImages.containsKey(texture)){
            int textureID = genTexture();

            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureID);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR_MIPMAP_LINEAR);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
//            GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_UNSIGNED_BYTE, width, height, 0, GLES20.GL_UNSIGNED_BYTE, GLES20.GL_UNSIGNED_BYTE, pixels);
            GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D);

            //Clear out our texture
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        }
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
