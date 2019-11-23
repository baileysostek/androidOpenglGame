package com.example.bhsostek.fraudtek.engine.game;

import com.example.bhsostek.fraudtek.engine.entity.Entity;

public class DistancePair {
    private int distance  = -1;
    private Entity entity =  null;
    private int floorX = 0;
    private int floorY = 0;

    public DistancePair(int distance, Entity entity, int floorX, int floorY){
        this.distance = distance;
        this.entity = entity;
        this.floorX = floorX;
        this.floorY = floorY;
    }

    public int getDistance(){
        return this.distance;
    }

    public Entity getEntity(){
        return this.entity;
    }

    @Override
    public String toString(){
        return "{ entity: " + entity.toString() + " , distance: " + distance + " }";
    }

    public int getFloorX(){
        return this.floorX;
    }

    public int getFloorY(){
        return this.floorY;
    }
}
