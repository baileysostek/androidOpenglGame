package com.example.bhsostek.fraudtek.engine.math;

import com.example.bhsostek.fraudtek.engine.Game;
import com.example.bhsostek.fraudtek.engine.math.Vector3f;

public class MatrixUtils {

    public static final float FOV = 70.0f;
    private static final float  NEAR_PLANE = 0.1f;
    private static final float  FAR_PLANE = 1024.0f;

    public static float[] getIdentityMatrix(){
        return new float[]{
            1, 0, 0, 0,
            0, 1, 0, 0,
            0, 0, 1, 0,
            0, 0, 0, 1
        };
    }

    //Translate matrix by an ammount.
    public static float[] translate(float[] mat4,  Vector3f offset){
        mat4[3]  += offset.x();
        mat4[7]  += offset.y();
        mat4[11] += offset.z();
        return mat4;
    }

    //Set translation in space
    public static float[] setTranslation(float[] mat4,  Vector3f offset){
        mat4[3]  = offset.x();
        mat4[7]  = offset.y();
        mat4[11] = offset.z();
        return mat4;
    }

    public static float[] createProjectionMatrix() {
        float aspectRatio = (float) Game.WIDTH / (float) Game.HEIGHT;
        float y_scale = (float) ((1f / Math.tan(Math.toRadians(FOV / 2f))));
        float x_scale = y_scale / aspectRatio;
        float frustum_length = FAR_PLANE - NEAR_PLANE;

        return new float[]{
            x_scale, 0, 0, 0,
            0, y_scale, 0, 0,
            0, 0, -((FAR_PLANE + NEAR_PLANE) / frustum_length), -1.0f,
            0, 0, -((2.0f * NEAR_PLANE * FAR_PLANE) / frustum_length), 0
        };
    }
}
