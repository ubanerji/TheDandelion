package com.thewarpspace.ddbox2d;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.thewarpspace.ddbox2d.screens.GameScreen;

public class DdBox2d extends Game {
    Box2DDebugRenderer debugRenderer;  
    OrthographicCamera camera;  
    GameScreen gamescreen = new GameScreen(); 
    
	@Override
	public void create() {		
		setScreen(gamescreen);
		// Main creation, setup of canvas, parameters, initialization, etc.
	}

	@Override
	public void dispose() {

	}

	@Override
	public void render() {		
		super.render();
		// Appears to go to GameScreen.render somehow. But not sure why
	}

	@Override
	public void resize(int width, int height) {
		gamescreen.resize(width, height);
		// Not 100% clear how the convertion between world, box and pixel coord should be done
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}
}
