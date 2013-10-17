package com.thewarpspace.ddgame.screens;

import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.math.Vector2;
import com.thewarpspace.ddgame.Ddseeds;
import com.thewarpspace.ddgame.Worlds;
import com.thewarpspace.ddgame.controllers.WorldController;
import com.thewarpspace.ddgame.renderers.WorldRenderer;

public class GameScreen implements Screen, InputProcessor {

	private Worlds world;
	private WorldRenderer renderer;
	int width, height;
	private int elapsedTime = 0;
	private WorldController controller;
	private Random r = new Random();

	@Override
	public void render(float delta) {
		// TODO Auto-generated method stub
		Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		controller.update(delta, elapsedTime);
		renderer.render();
		elapsedTime += 1; // count elapsedTime for further control
	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub
		renderer.setSize(width, height);
		this.width = width;
		this.height = height;
	}

	@Override
	public void show() {
		// TODO Auto-generated method stub
		world = new Worlds();
		renderer = new WorldRenderer(world, false);
		controller = new WorldController(world);
		Gdx.input.setInputProcessor(this);
	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean keyDown(int keycode) {
		// TODO Auto-generated method stub
		if (keycode == Keys.D){
			if(renderer.isDebug() == false){
				renderer.setDebug(true);
			} else {
				renderer.setDebug(false);
			}
		}
		if (keycode == Keys.Z){
			Ddseeds dd = new Ddseeds(new Vector2(3,6));
			world.getDdarray().add(dd);
		}
		if (keycode == Keys.X){			
			world.getWind().setWinddir(new Vector2(r.nextInt(20)-10, r.nextInt(20)-10));
			System.out.println(world.getWind().getWinddir());
		}
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		// TODO Auto-generated method stub
		return false;
	}

}
