/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.bhsostek.fraudtek.engine.util;


import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

/**
 *
 * @author Bailey
 */
public abstract class DynamicCollection <type> implements Iterable<type>{

    private ArrayList<type>  collection = new ArrayList<type>();
    private LinkedList<type> toAdd = new LinkedList<type>();
    private LinkedList<type> toRemove = new LinkedList<type>();

    @Override
    public Iterator<type> iterator() {
        return collection.iterator();
    }

    private void updateCollection(){
        //only run if need to
        if(toRemove.size()>0 || toAdd.size()>0){
            LinkedList<type> newCollection = new LinkedList<type>();
            //remove all lights flagged to remove
            for(int i = 0; i < this.collection.size(); i++){
                if(toRemove.contains(this.collection.get(i))){
                    //Remove that body
                    onRemove((type)this.collection.get(i));
                }else{
                    newCollection.add((type)this.collection.get(i));
                }
            }
            //clear the buffer
            toRemove.clear();

            //add all new lights
            for (type object : toAdd) {
                newCollection.add(object);
                onAdd(object);
            }
            //clear that buffer
            toAdd.clear();
            //build new light array
            ArrayList<type> out = new ArrayList<type>();
            for(int i = 0; i < newCollection.size(); i++){
                out.set(i , newCollection.get(i));
            }
            //set it
            this.collection = out;
        }
    }

    public void synch(){
        updateCollection();
    }

    public void remove(type object){
        toRemove.add(object);
    }

    public void add(type object){
        if(object!=null){
            toAdd.add(object);
        }
    }

    public void clear() {
        for(type t : collection){
            remove(t);
        }
    }

    public int getLength(){
        return this.collection.size();
    }

    public type[] getCollection(Class<type> c){
        @SuppressWarnings("unchecked")
        type[] a = (type[]) Array.newInstance(c, this.getLength());

        for(int i = 0; i < a.length; i++){
            a[i] = (type)this.collection.get(i);
        }

        return a;
    }

    public boolean contains(type object){
        return this.collection.contains(object);
    }

    public void remove(int index){
        if(index >= 0 && index < collection.size()) {
            this.toRemove.add((type) this.collection.get(index));
        }
    }

    public type get(int index){
        return this.collection.get(index);
    }

    public abstract void onAdd(type object);
    public abstract void onRemove(type object);
}
