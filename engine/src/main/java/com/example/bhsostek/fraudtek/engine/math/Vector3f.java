package com.example.bhsostek.fraudtek.engine.math;

public class Vector3f {

    private float x = 0.0f;
    private float y = 0.0f;
    private float z = 0.0f;

    //Create identity vector with all zeros.
    public Vector3f(){}

    public Vector3f(float x, float y, float z){
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector3f(float w){
        this.x = w;
        this.y = w;
        this.z = w;
    }

    public Vector3f(Vector3f copy){
        this.x = copy.x;
        this.y = copy.y;
        this.z = copy.z;
    }

    //Create normaized vec3
    public Vector3f(float[] vec4){
        this.x = vec4[0] / vec4[3];
        this.y = vec4[1] / vec4[3];
        this.z = vec4[2] / vec4[3];
    }

    //Basic maths
    public Vector3f add(Vector3f copy){
        this.x += copy.x;
        this.y += copy.y;
        this.z += copy.z;
        return this;
    }

    public Vector3f sub(Vector3f copy){
        this.x -= copy.x;
        this.y -= copy.y;
        this.z -= copy.z;
        return this;
    }

    public Vector3f div(Vector3f copy){
        this.x /= copy.x;
        this.y /= copy.y;
        this.z /= copy.z;
        return this;
    }

    public Vector3f div(float scale){
        this.x /= scale;
        this.y /= scale;
        this.z /= scale;
        return this;
    }

    public Vector3f mul(Vector3f copy){
        this.x *= copy.x;
        this.y *= copy.y;
        this.z *= copy.z;
        return this;
    }

    public Vector3f mul(float scale){
        this.x *= scale;
        this.y *= scale;
        this.z *= scale;
        return this;
    }

    //Getters
    public float x(){
        return this.x;
    }

    public float y(){
        return this.y;
    }

    public float z(){
        return this.z;
    }

    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }

    public void setZ(float z) {
        this.z = z;
    }

    public Vector3f inverse() {
        this.x *= -1f;
        this.y *= -1f;
        this.z *= -1f;
        return this;
    }

    public float[] toVec4() {
        return new float[]{
            x,
            y,
            z,
            1
        };
    }

    public float getMagnitude(){
        return (float) Math.sqrt((x * x) + (y * y) + (z * z));
    }

    public float[] toVec3N() {
        float mag = getMagnitude();
        return new float[]{
            x / mag,
            y / mag,
            z / mag,
        };
    }

    //Override toString
    @Override
    public String toString(){
        return "{x:" + VectorUtils.format(x) +", y:" + VectorUtils.format(y) +" z:"+VectorUtils.format(z)+"}";
    }
}
