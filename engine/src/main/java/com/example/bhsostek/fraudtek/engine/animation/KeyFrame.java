package com.example.bhsostek.fraudtek.engine.animation;

public class KeyFrame {
    private float value    = 0f;
    private float position = 0f;
    private KeyFrame next  = null;
    private EnumInterpolation interpolation = EnumInterpolation.LERP;

    public KeyFrame(float value, float position){
        this.value = value;
        this.position = position;
    }

    public KeyFrame setNext(KeyFrame frame){
        this.next = frame;
        return this;
    }

    public KeyFrame setInterpolation(EnumInterpolation interpolation){
        this.interpolation = interpolation;
        return this;
    }

    public float getValue(float position){
        //If keyframe is less than this, return this
        if(this.next == null || position < this.position){
            return value;
        }

        //If keyframe is greater than next, getValue of next
        if(position >= this.next.position){
            return this.next.getValue(position);
        }

        //Get the deltas
        float delta = (next.position - this.position); // Delta between frames
        float index = (position - this.position);      // Delta from start


        //Calc interpolation
        switch (interpolation){
            case LERP:{
                //Linear interpolation
                float diff = (index / delta);
                float diff_prime = (1.0f - diff);
                return (diff_prime * this.value) + (diff * next.value);
            }
        }

        return value;
    }

    public KeyFrame getNext() {
        return this.next;
    }
}
