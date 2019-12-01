package com.example.bhsostek.fraudtek.engine.actions;

import java.util.Comparator;

public class ActionComparison implements Comparator<Action> {
    @Override
    public int compare(Action t0, Action t1) {
        return t1.getType().getMoveIndex() - t0.getType().getMoveIndex();
    }
}
