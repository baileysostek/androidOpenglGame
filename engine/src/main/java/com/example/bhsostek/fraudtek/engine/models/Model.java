package com.example.bhsostek.fraudtek.engine.models;

import com.example.bhsostek.fraudtek.engine.math.Vector3f;
import com.example.bhsostek.fraudtek.engine.renderer.Handshake;

public class Model {

    //Metadata
    private String name;
    private int numIndicies = 0;
    private int id;

    private Vector3f[] aabb;

    //VAO
    private Handshake handshake;

    protected Model(int id, Handshake handshake, int numIndicies, Vector3f[] AABB){
//        this.name      = name;
        this.id = id;
        this.handshake = handshake;
        this.numIndicies = numIndicies;
        this.aabb = AABB;
    }

    public Handshake getHandshake(){
        return this.handshake;
    }

    public int getID(){
        return this.id;
    }

    public int getNumIndicies() {
        return this.numIndicies;
    }

    public Vector3f[] getAABB(){
        return this.aabb;
    }
}
