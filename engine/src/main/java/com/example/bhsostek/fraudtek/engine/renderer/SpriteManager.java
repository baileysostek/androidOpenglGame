package com.example.bhsostek.fraudtek.engine.renderer;

import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLUtils;

import com.example.bhsostek.fraudtek.engine.entity.Entity;
import com.example.bhsostek.fraudtek.engine.util.AssetManager;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class SpriteManager {
    //Singleton instance
    private static SpriteManager spriteManager;

    private LinkedList<Integer> textureIDs = new LinkedList<>();
    private HashMap<String, Integer> loadedImages = new HashMap<String, Integer>();
    private LinkedList<Integer> toGenerate = new LinkedList<>();

    //Lock for locking our entity set
    private Lock lock;

    //Private constructor because singleton.
    private SpriteManager(){
        lock = new ReentrantLock();
    }

    private void update(){
        lock.lock();
        try {
            for(int i : toGenerate){
                int check = genTexture();
                if(i != check){
                    System.err.println("Error: texture id mismatch");
                }
            }
            toGenerate.clear();
        } finally {
            lock.unlock();
        }
    }

    //GenTextures
    public int genTexture(){
        int[] textureIndex = new int[1];
        GLES20.glGenTextures(1, textureIndex, 0);
        System.out.println("Texture:" + textureIndex[0]);
        textureIDs.push(textureIndex[0]);
        return textureIndex[0];
    }

    public int loadTexture(String texture){
        return loadTexture(texture, GLES20.GL_NEAREST);
    }

    public int loadTexture(String texture, int filter){
        if(!loadedImages.containsKey(texture)){
            //Try to load our bitmap
            System.out.print("Loading texture:"+texture);
            Bitmap bmp = AssetManager.getInstance().readImage(texture);
            if(bmp != null) {
                System.out.println(" success.");
                int textureID = genTexture();

                GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureID);

                GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, filter);
                GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, filter);

                GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bmp, 0);

                bmp.recycle();
//
//                int[] pixels = new int[bmp.getWidth() * bmp.getHeight()];
//                bmp.getPixels(pixels, 0, bmp.getWidth(), 0, 0, bmp.getWidth(), bmp.getHeight());
//
//                GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, bmp.getWidth(), bmp.getHeight(), 0, GLES20.GL_RGB, GLES20.GL_UNSIGNED_BYTE, BufferUtils.bufferData(pixels));
//                GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D);

                //Clear out our texture
                GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
                loadedImages.put(texture, textureID);

                return textureID;
            }else{
                System.out.println(" fail.");
                return -1;
            }
        }else{
            return loadedImages.get(texture);
        }
    }

    public int putTexture(String texture, Bitmap bmp){
        lock.lock();
        try {
            if(!loadedImages.containsKey(texture)){
                //Try to load our bitmap
                System.out.print("Loading texture:"+texture);
                if(bmp != null) {
                    System.out.println(" success.");
                    int textureID = this.textureIDs.size();
                    this.toGenerate.push(textureID);

                    GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureID);

                    GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
                    GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);

                    GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bmp, 0);

                    bmp.recycle();

                    //Clear out our texture
                    GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
                    loadedImages.put(texture, textureID);

                    return textureID;
                }else{
                    System.out.println(" fail.");
                    return -1;
                }
            }else{
                return loadedImages.get(texture);
            }
        } finally {
            lock.unlock();
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
