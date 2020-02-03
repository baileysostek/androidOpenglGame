package com.example.bhsostek.fraudtek.engine.entity;

import android.os.Build;
import android.support.annotation.NonNull;

import com.example.bhsostek.fraudtek.engine.math.Vector3f;

import org.joml.Intersectionf;
import org.joml.Vector2f;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class EntityManager {

    private static EntityManager entityManager;

    //This is the list of all entities in the scene.
    private LinkedList<Entity> entities = new LinkedList<Entity>();
    private LinkedList<Entity> toAdd    = new LinkedList<Entity>();
    private LinkedList<Entity> toRemove = new LinkedList<Entity>();
    private HashMap<EnumEntityType, LinkedList<Entity>> typedEntities = new HashMap<EnumEntityType, LinkedList<Entity>>();

    //On remove see if a registered parent was removed, if it was, remove all children.
    private HashMap<Entity, LinkedList<Entity>> links = new HashMap<>();

    //Lock for locking our entity set
    private Lock lock;

    //Singleton design pattern
    private EntityManager(){
        lock = new ReentrantLock();

        for(EnumEntityType type : EnumEntityType.values()){
            typedEntities.put(type, new LinkedList<Entity>());
        }
    }

    public void addEntity(Entity entity){
        lock.lock();
        try {
            this.toAdd.add(entity);
        } finally {
            lock.unlock();
        }
    }

    public void clearEntities(){
        this.entities.clear();
        for(LinkedList<Entity> typeList : typedEntities.values()){
            typeList.clear();
        }
    }

    public LinkedList<Entity> getEntities(){
        return this.entities;
    }

    public synchronized LinkedList<Entity> getEntitiesOfType(EnumEntityType type, EnumEntityType ... types){
        if(types.length == 0) {
            return this.typedEntities.get(type);
        }else{
            LinkedList<Entity> entities = new LinkedList<>(this.typedEntities.get(type));
            for(EnumEntityType additionalType : types){
                entities.addAll(this.typedEntities.get(additionalType));
            }
            return entities;
        }
    }

    //Once per insert
    private void sortEntities() {
        Collections.sort(entities, new Comparator<Entity>() {
            @Override
            public int compare(Entity e1, Entity e2) {
            return e1.getModel().getID() - e2.getModel().getID();
            }
        });
    }

    //Update all entities
    public void update(double delta){
        sync();
        for(Entity e : entities){
            e.update(delta);
        }
    }

    //Singleton functions
    public static void initialize(){
        if(entityManager == null){
            entityManager = new EntityManager();
        }
    }

    public static EntityManager getInstance(){
        return entityManager;
    }

    public LinkedList<Entity> getHitEntities(Vector3f pos, Vector3f dir, EnumEntityType ... types){
        LinkedList<Entity> hits = new LinkedList<Entity>();

        if(types.length == 0){
            for(Entity e : entities){
                Vector3f[] aabb = e.getAABB();
                Vector3f mutablePos = new Vector3f(pos);
                Vector3f mutableDir = new Vector3f(dir);
                Vector2f hit = new Vector2f();
                boolean hitCheck = Intersectionf.intersectRayAab(mutablePos.x(), mutablePos.y(), mutablePos.z(), mutableDir.x(), mutableDir.y(), mutableDir.z(), aabb[0].x(), aabb[0].y() - 1, aabb[0].z(), aabb[1].x(), aabb[1].y() - 1, aabb[1].z(), hit);
                if(hitCheck){
                    hits.push(e);
                }
            }
        }else{
            for(EnumEntityType type : types){
                for(Entity e : getEntitiesOfType(type)){
                    Vector3f[] aabb = e.getAABB();
                    Vector3f mutablePos = new Vector3f(pos);
                    Vector3f mutableDir = new Vector3f(dir);
                    Vector2f hit = new Vector2f();
                    boolean hitCheck = Intersectionf.intersectRayAab(mutablePos.x(), mutablePos.y(), mutablePos.z(), mutableDir.x(), mutableDir.y(), mutableDir.z(), aabb[0].x(), aabb[0].y(), aabb[0].z(), aabb[1].x(), aabb[1].y(), aabb[1].z(), hit);
                    if(hitCheck){
                        hits.push(e);
                    }
                }
            }
        }

        return hits;
    }

    public Entity createEntityFromRegistry(JSONObject saveData) {
        try {
            EnumEntityType type = EnumEntityType.valueOf(saveData.getString("type").toUpperCase());
            return generateEntityOfType(type, saveData);
        }catch (IllegalArgumentException e) {
            try {
                System.out.println("Unknown entity type:" + saveData.getString("type"));
            } catch (JSONException ex) {
                ex.printStackTrace();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Entity generateEntityOfType(EnumEntityType type, JSONObject saveData){
        switch (type){
            case MOLE:{
                return new Mole(saveData);
            }
            case AVOCADO:{
                return new Avocado(saveData);
            }
            case AVACADO:{
                return new Avocado(saveData);
            }
            case ONION:{
                return new Onion(saveData);
            }
            case STONE:{
                return new Stone(saveData);
            }
            case ICE:{
                return new Ice(saveData);
            }
            case WATER:{
                return new Water(saveData);
            }
            default:{
                System.out.println("Undefined case for type:" + type);
//                System.exit(1);
            }
        }
        return null;
    }

    public void sync() {
        if(toAdd.size() > 0 || toRemove.size() > 0) {
            lock.lock();
            try {
                for (Entity e : toAdd) {
                    this.entities.add(e);
                    e.onAdd();
                    this.typedEntities.get(e.getType()).add(e);
                }
                toAdd.clear();
                for (Entity e : toRemove) {
                    this.entities.remove(e);
                    this.typedEntities.get(e.getType()).remove(e);
                }
                toRemove.clear();
                sortEntities();
            } finally {
                lock.unlock();
            }
        }
    }

    public void removeEntity(Entity toRemove) {
        lock.lock();
        try {
            if(this.entities.contains(toRemove)) {
                parentRemoveHelper(toRemove);
            }
        } finally {
            lock.unlock();
        }
    }

    private void parentRemoveHelper(Entity parent){
        this.toRemove.add(parent);
        if(this.links.containsKey(parent)){
            LinkedList<Entity> links = this.links.get(parent);
            for(Entity child : links){
                parentRemoveHelper(child);
            }
            this.links.remove(links);
        }
    }

    public void link(Entity parent, Entity child) {
        if(!this.links.containsKey(parent)){
            this.links.put(parent, new LinkedList<Entity>());
        }
        this.links.get(parent).push(child);
    }
}
