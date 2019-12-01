package com.example.bhsostek.fraudtek.engine.game;

public enum  EnumGameState {
    //IN level
    IDLE(),
    CHECK_FOR_ACTIONS(),
    PERFORMING_ACTIONS(),
    WIN(),
    LOSE(),

    //In overworld
    LOADING(),
    MENU(),
    LEVEL_SELECT(),

}
