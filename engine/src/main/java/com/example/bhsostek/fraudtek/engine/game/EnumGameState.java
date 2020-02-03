package com.example.bhsostek.fraudtek.engine.game;

public enum  EnumGameState {
    //IN level
    IDLE(),
    CHECK_FOR_ACTIONS(),
    PERFORMING_ACTIONS(),
    WIN(),
    LOSE(),

    //In overworld (REfactor to scenes)
    LOADING(),
    MENU(),
    LEVEL_SELECT(),

    PLAY(),
    EDIT(),

}
