package com.example.bhsostek.fraudtek.engine.entity;

import com.example.bhsostek.fraudtek.engine.actions.Action;
import com.example.bhsostek.fraudtek.engine.math.Vector3f;

import java.util.LinkedList;

public interface WorldObject {
    //World position stuff
    int getFloorX();
    int getFloorY();
    void setFloorX(int x);
    void setFloorY(int y);
    Vector3f getWorldOffset();

    //Starting pos
    int getStartX();
    int getStartY();
    void setStartX(int x);
    void setStartY(int y);

    //Gameplay
    void setIsSeethrough(boolean isSeethrough);
    boolean isSeethrough();
    void isSatisfied(boolean isSatisfied);
    boolean isSatisfied();

    //Action management stuff
    void setActions(LinkedList<Action> actions);
    void addAction(Action action);
    LinkedList<Action> getActions();
    void spliceAction(int index);
    void performAction(Action action);

    void onStep(Entity other);

    boolean isAnimating();
}
