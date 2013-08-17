package com.dandelion.app;

import com.dandelion.app.Framework.Game;
import com.dandelion.app.Framework.Graphics;
import com.dandelion.app.Framework.Screen;
import com.dandelion.app.Framework.Graphics.ImageFormat;

/**
 * Created by ubanerji on 8/11/13.
 */
public class LoadingScreen extends Screen {
    public LoadingScreen(Game game) {
        super(game);
    }


    @Override
    public void update(float deltaTime) {
        Graphics g = game.getGraphics();
        Assets.menu = g.newImage("menu.jpg", Graphics.ImageFormat.RGB565);
        Assets.click = game.getAudio().createSound("explode.ogg");



        game.setScreen(new MainMenuScreen(game));


    }


    @Override
    public void paint(float deltaTime) {


    }


    @Override
    public void pause() {


    }


    @Override
    public void resume() {


    }


    @Override
    public void dispose() {


    }


    @Override
    public void backButton() {


    }
}
