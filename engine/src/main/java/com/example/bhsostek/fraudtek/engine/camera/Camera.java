package com.example.bhsostek.fraudtek.engine.camera;

import android.opengl.Matrix;

import com.example.bhsostek.fraudtek.engine.math.MatrixUtils;
import com.example.bhsostek.fraudtek.engine.math.Vector2f;
import com.example.bhsostek.fraudtek.engine.math.Vector3f;

public class Camera {
    private Vector3f pos = new Vector3f(0f, 0f, 0f);
    private Vector3f rot = new Vector3f(0f, 0f, 0f);

    public Camera(){
        //Pass by reference or value
        System.out.println("Forward: "+ getForwardDir());
    }

    public Camera setPosition(Vector3f vec){
        pos = new Vector3f(vec);
        return this;
    }

    public Camera setRotation(Vector3f vec){
        rot = new Vector3f(vec);
        return this;
    }

    public Camera setPositionRef(Vector3f vec){
        pos = vec;
        return this;
    }

    public float[] getTransform(){
        float[] modelMatrix = new float[]{
            1, 0, 0,  pos.x(),
            0, 1, 0, -pos.y(),
            0, 0, 1, -pos.z(),
            0, 0, 0, 1
        };

        Matrix.rotateM(modelMatrix, 0,  rot.z(), 0f,0f, 1f);
        Matrix.rotateM(modelMatrix, 0,  rot.y(), 0f,1f, 0f);
        Matrix.rotateM(modelMatrix, 0,  rot.x(), 1f,0f, 0f);

        return modelMatrix;
    }

    public Vector3f getForwardDir(){
        float[] forward = {0, 0, -1, 1}; //Worldspace direction
        float[] out     = {0, 0, 0, 0};
        Matrix.multiplyMV(out, 0, getTransform(), 0, forward, 0);
        return new Vector3f(out[0] / out[3], out[1] / out[3], out[2] / out[3]);
    }

    public Vector3f getPosition() {
        return this.pos;
    }

    public Vector2f getPosition2D() {
        return new Vector2f(this.pos.x(), this.pos.y());
    }

    public Vector3f getRotation() {
        return this.rot;
    }

    public float[] getView() {
        float[] modelMatrix = MatrixUtils.getIdentityMatrix();

        Matrix.rotateM(modelMatrix, 0,  rot.z(), 0f,0f, 1f);
        Matrix.rotateM(modelMatrix, 0,  rot.y(), 0f,1f, 0f);
        Matrix.rotateM(modelMatrix, 0,  rot.x(), 1f,0f, 0f);

        return modelMatrix;
    }

    public float getX() {
        return this.pos.x();
    }

    public float getY(){
        return this.pos.y();
    }

    public void setPosition(Vector2f pos) {
        this.setPosition(new Vector3f(pos.x(), pos.y(), 0));
    }
}
