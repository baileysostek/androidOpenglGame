package com.example.bhsostek.fraudtek.engine.math;

public class Vector2f {

    private float x = 0.0f;
    private float y = 0.0f;

    //Create identity vector with all zeros.
    public Vector2f(){}

    public Vector2f(float x, float y){
        this.x = x;
        this.y = y;
    }

    public Vector2f(float w){
        this.x = w;
        this.y = w;
    }

    public Vector2f(Vector2f copy){
        this.x = copy.x;
        this.y = copy.y;
    }

    //Create normaized vec2
    public Vector2f(float[] vec4){
        this.x = vec4[0] / vec4[3];
        this.y = vec4[1] / vec4[3];
    }

    //Basic maths
    public Vector2f add(Vector2f copy){
        this.x += copy.x;
        this.y += copy.y;
        return this;
    }

    public Vector2f sub(Vector2f copy){
        this.x -= copy.x;
        this.y -= copy.y;
        return this;
    }

    public Vector2f div(Vector2f copy){
        this.x /= copy.x;
        this.y /= copy.y;
        return this;
    }

    public Vector2f div(float scale){
        this.x /= scale;
        this.y /= scale;
        return this;
    }

    public Vector2f mul(Vector2f copy){
        this.x *= copy.x;
        this.y *= copy.y;
        return this;
    }

    public Vector2f mul(float scale){
        this.x *= scale;
        this.y *= scale;
        return this;
    }

    //Getters
    public float x(){
        return this.x;
    }

    public float y(){
        return this.y;
    }

    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }

    public Vector2f inverse() {
        this.x *= -1f;
        this.y *= -1f;
        return this;
    }

    public float[] toVec4() {
        return new float[]{
                x,
                y,
                0,
                1
        };
    }

    public float getMagnitude(){
        return (float) Math.sqrt((x * x) + (y * y));
    }

    public float[] toVec3N() {
        float mag = getMagnitude();
        return new float[]{
                x / mag,
                y / mag,
        };
    }

    public Vector3f toVec3(){
        return new Vector3f(this.x(), this.y, 0);
    }

    //Override toString
    @Override
    public String toString(){
        return "{x:" + VectorUtils.format(x) +", y:" + VectorUtils.format(y) +"}";
    }
}
