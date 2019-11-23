package com.example.bhsostek.fraudtek.engine.renderer;

import android.opengl.Matrix;

import com.example.bhsostek.fraudtek.engine.Game;
import com.example.bhsostek.fraudtek.engine.camera.Camera;
import com.example.bhsostek.fraudtek.engine.camera.CameraManager;
import com.example.bhsostek.fraudtek.engine.math.MatrixUtils;
import com.example.bhsostek.fraudtek.engine.math.Vector3f;

public class ScreenUtils {
    public static float[] screenToGL(float x, float y){
        float percentX = x / (float)Game.WIDTH;
        float percentY = y / (float)Game.HEIGHT;
        percentX *= 2f;
        percentY *= 2f;
        percentX -= 1f;
        percentY -= 1f;
        return new float[]{percentX, percentY};
    }

    public static float[] glToWorld(float[] screen, Camera camera){
        float[] forward = {0, 0, -1, 1}; //Worldspace direction

        float aspectRatio = (float) Game.WIDTH / (float) Game.HEIGHT;
        float y_scale = (float) ((1f / Math.tan(Math.toRadians(MatrixUtils.FOV / 2f))));
        float x_scale = y_scale / aspectRatio;

        float[] touchMatrix = MatrixUtils.getIdentityMatrix();
        System.out.println("xScale:" + x_scale + " yScale:"+y_scale + " ar:"+aspectRatio);
        Matrix.rotateM(touchMatrix, 0, (screen[0] - (0.16f)) * -1f * (MatrixUtils.FOV / 0.95f), 0f,0f, 1f); //X
        Matrix.rotateM(touchMatrix, 0, screen[1] * -1f * (MatrixUtils.FOV / aspectRatio), 1f,0f, 0f); //Y

        float[] out     = {0, 0, 0, 0};
        Matrix.multiplyMV(out, 0, camera.getTransform(), 0, forward, 0);
        Matrix.multiplyMV(out, 0, touchMatrix, 0, out, 0);

        //Invert X axis
        out[0] *= -1.0f;

        return out;
    }
}
