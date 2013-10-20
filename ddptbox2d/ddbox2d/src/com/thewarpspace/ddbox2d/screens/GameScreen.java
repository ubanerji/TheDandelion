package com.thewarpspace.ddbox2d.screens;

import java.util.Iterator;
import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.World;
import com.thewarpspace.ddbox2d.Actors;
import com.thewarpspace.ddbox2d.DdSeeds;
import com.thewarpspace.ddbox2d.Levels;
import com.thewarpspace.ddbox2d.controllers.HedgeContactListener;
import com.thewarpspace.ddbox2d.controllers.WorldController;
import com.thewarpspace.ddbox2d.renderers.WorldRenderer;

public class GameScreen implements Screen, InputProcessor {

	World world;
	private Levels level;
	private WorldRenderer renderer;
	private Random r = new Random();
	private int width;
	private int height;
	private WorldController controller;
	private Actors actor;
    int points, points_current;
	
	@Override
	public void render(float delta) {
		// TODO Auto-generated method stub
		points = controller.update(world, actor, points);
		// checks if dandelion seeds reach the hedgehog, and add points accordingly
		if(points_current != points) {
			points_current= points;
			System.out.printf("Player points: %d \n", points_current);
		}
		renderer.render(points_current); // main renderer and world steps in here. automatically 
		//called everytime from super.render in the DdBox2d class
		
	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub		
		this.width = width;
		this.height = height;
	}

	@Override
	public void show() {
		// TODO Auto-generated method stub
		points = 0;
		points_current = 0; // Count player score
		world = new World(new Vector2(0, -10), true); // Create new world
		HedgeContactListener listener = new HedgeContactListener();		
		world.setContactListener(listener); 
		// Create listener, which is automatically called to check whether collision happens
		actor = new Actors(); // Empty construction
		level = new Levels(world); // Need to pass camera dimensions through renderer		
		renderer = new WorldRenderer(world, actor, level);		
		controller = new WorldController(world); // Empty construction
		actor.addHedgeHog(new Vector2(300, 100), world); // Add HedgeHog in designated place
		Gdx.input.setInputProcessor(this); // Initialing hardware input listener
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
		// when Z pressed, add one dandelion seed
			actor.addDdSeeds(new Vector2(50,300), world);
		}
		if (keycode == Keys.X){			
		// when X pressed, change the direction of gravity
			Vector2 gravity = new Vector2(r.nextInt(20)-10, r.nextInt(20)-10);
			System.out.printf("Changed gravity to %f %f\n", gravity.x, gravity.y);
			world.setGravity( gravity );
			// Need to wake all bodies once gravity field changes
			for(Iterator<Fixture> iterd = actor.getDdArray().iterator(); iterd.hasNext();) {
		    	Fixture ftd = iterd.next();
		    	ftd.getBody().setAwake(true);		    	
		    }
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
		// mouse left button to add one tile 
		// mouse right button to remove one added-tile
		// mouse middle button to remove pre-defined tiles
		// no effects on anything other than added tiles
		if(button == Input.Buttons.LEFT){
			Vector2 position = new Vector2(screenX *(renderer.getCamera().viewportWidth*1.0f/width), (height -screenY)*(renderer.getCamera().viewportHeight*1.0f/height));
			System.out.println(position);
			// need to scale, otherwise gamebox units inconsistent with screen display pixels
			if( position.x > 430 && position.y > 270) {
				actor.addDdSeeds(new Vector2(50,300), world);
			} else if (position.x > 430 && position.y < 50) {				
				Vector2 gravity = new Vector2(r.nextInt(20)-10, r.nextInt(20)-10);
				System.out.printf("Changed gravity to %f %f\n", gravity.x, gravity.y);
				world.setGravity( gravity );
				// Need to wake all bodies once gravity field changes
				for(Iterator<Fixture> iterd = actor.getDdArray().iterator(); iterd.hasNext();) {
			    	Fixture ftd = iterd.next();
			    	ftd.getBody().setAwake(true);		    	
			    }
			}	else{
				level.addTile(position, world);
			}
		} else if(button == Input.Buttons.RIGHT) {
			Vector2 position = new Vector2(screenX *(renderer.getCamera().viewportWidth*1.0f/width), (height -screenY)*(renderer.getCamera().viewportHeight*1.0f/height));
			for (Iterator<Fixture> iter = level.getTileArrayModifiable().iterator(); iter.hasNext();) {
				Fixture ft = iter.next();
			     if (Math.sqrt((ft.getBody().getPosition().x - position.x)*(ft.getBody().getPosition().x - position.x) +(ft.getBody().getPosition().y - position.y)*(ft.getBody().getPosition().y - position.y)) < 3.0f) {
			    	 controller.removeBodySafely(world, ft.getBody());
			    	 // need to call to remove everything related to body safely
			    	 iter.remove();
			    	 // need to remove this guy out of the array to keep good track
			     }
			     
			}
		} else if(button == Input.Buttons.MIDDLE) {
			Vector2 position = new Vector2(screenX *(renderer.getCamera().viewportWidth*1.0f/width), (height -screenY)*(renderer.getCamera().viewportHeight*1.0f/height));
			for (Iterator<Fixture> iter = level.getTileArrayPreset().iterator(); iter.hasNext();) {
			    Fixture ft = iter.next();
				if (Math.sqrt((ft.getBody().getPosition().x - position.x)*(ft.getBody().getPosition().x - position.x) +(ft.getBody().getPosition().y - position.y)*(ft.getBody().getPosition().y - position.y)) < 3.0f) {
			    	 controller.removeBodySafely(world, ft.getBody());
			    	 iter.remove();
			     }
			}
		}
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
