package com.dandelion.app.Framework;

import com.dandelion.app.Framework.Game;

/**
 * Created by ubanerji on 7/27/13.
 */
public abstract class Screen {
    protected final Game game;

    public Screen(Game game) {
        this.game = game;
    }

    public abstract void update(float deltaTime);

    public abstract void paint(float deltaTime);

    public abstract void pause();

    public abstract void resume();

    public abstract void dispose();

    public abstract void backButton();
}
