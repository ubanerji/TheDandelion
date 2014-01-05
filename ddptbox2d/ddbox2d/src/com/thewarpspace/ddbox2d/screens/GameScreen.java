package com.thewarpspace.ddbox2d.screens;

import java.util.Iterator;
import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.thewarpspace.ddbox2d.Actors;
import com.thewarpspace.ddbox2d.DdSeeds;
import com.thewarpspace.ddbox2d.Levels;
import com.thewarpspace.ddbox2d.controllers.HedgeContactListener;
import com.thewarpspace.ddbox2d.controllers.WorldController;
import com.thewarpspace.ddbox2d.renderers.WorldRenderer;

public class GameScreen implements Screen, InputProcessor {
	public static final float CAMERAVIEWWIDTH=480.0f; // This is the actual resolution of on-screen display
	public static final float CAMERAVIEWHEIGHT=320.0f;  
	public static final float SCALEVIEW= CAMERAVIEWHEIGHT/320.0f; // Scale everything when resolution changes
	private static final float SEPARATION = 10.0f *SCALEVIEW;
	public static final float CLEANRANGE = 10.0f *SCALEVIEW;
	World world;
	private Levels level;
	private WorldRenderer renderer;
	private Random r = new Random();
	private int width;
	private int height;
	private WorldController controller;
	private Actors actor;
    int points, points_current;
	private Vector2 ddSpawnPos = new Vector2();
	private Vector2 hhSpawnPos = new Vector2();
	private float dragAccumulator;
	Vector2 dragOld = new Vector2(); // used to record drag path
	private int dragOnFlag;
	Random rand = new Random();
	private Vector2 actualSpawnPos= new Vector2();
	
	
	@Override
	public void render(float delta) {
		// TODO Auto-generated method stub
		points = controller.update(world, actor, points);
		// checks if dandelion seeds reach the hedgehog, and add points accordingly
		if(points_current != points) {
			points_current = points;
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
		world = new World(new Vector2(0, -(int)(10 * SCALEVIEW * SCALEVIEW)), true); // Create new world
		// Create listener, which is automatically called to check whether collision happens
		HedgeContactListener listener = new HedgeContactListener();		
		world.setContactListener(listener); 
		
		actor = new Actors(); // Empty construction
		level = new Levels(world, ddSpawnPos, hhSpawnPos); // Need to pass camera dimensions through renderer, now just directly set to value. 
		renderer = new WorldRenderer(world, actor, level);	
		controller = new WorldController(world); // Empty construction
		
		System.out.println(hhSpawnPos);
		
		actor.addHedgeHog(hhSpawnPos, world); // Add HedgeHog in designated place
		Gdx.input.setInputProcessor(this); // Initialing hardware input listener
		dragAccumulator = 0f;
		dragOnFlag = 0;		
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
		if (keycode == Keys.D) {
			if (renderer.isDebug() == false) {
				renderer.setDebug(true);
			} else {
				renderer.setDebug(false);
			}
		}
		if (keycode == Keys.Z) {
		// when Z pressed, add one dandelion seed
			actualSpawnPos.x = ddSpawnPos.x - rand.nextFloat() * 10.0f * SCALEVIEW;
			actualSpawnPos.y = ddSpawnPos.y - rand.nextFloat() * 10.0f * SCALEVIEW;
			actor.addDdSeeds(actualSpawnPos, world);
		}
		if (keycode == Keys.X) {
		// when X pressed, change the direction of gravity
			Vector2 gravity = new Vector2((int) ((r.nextInt(20) - 10) * SCALEVIEW * SCALEVIEW),
										  (int) ((r.nextInt(20) - 10) * SCALEVIEW * SCALEVIEW));
			System.out.printf("Changed gravity to %f %f\n", gravity.x, gravity.y);
			world.setGravity(gravity);
			// Need to wake all bodies once gravity field changes
			for (Iterator<Fixture> iterd = actor.getDdArray().iterator(); iterd.hasNext();) {
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
		if (button == Input.Buttons.LEFT) {
			Vector2 position = new Vector2(screenX * (renderer.getCamera().viewportWidth * 1.0f / width), 
										   (height - screenY) * (renderer.getCamera().viewportHeight * 1.0f / height));
			System.out.println(position);
			// need to scale, otherwise gamebox units inconsistent with screen display pixels
			if (position.x > CAMERAVIEWWIDTH*0.9f && position.y > CAMERAVIEWHEIGHT*0.9f) {
				actualSpawnPos.x = ddSpawnPos.x - rand.nextFloat()*10.0f*SCALEVIEW;
				actualSpawnPos.y = ddSpawnPos.y - rand.nextFloat()*10.0f*SCALEVIEW;
				actor.addDdSeeds(actualSpawnPos, world);
			} else if (position.x > CAMERAVIEWWIDTH*0.9f && position.y < CAMERAVIEWHEIGHT*0.1f) {				
				Vector2 gravity = new Vector2((int)((r.nextInt(20) - 10) * SCALEVIEW * SCALEVIEW),
						                            (int)((r.nextInt(20) - 10) * SCALEVIEW * SCALEVIEW));
				
				System.out.printf("Changed gravity to %f %f\n", gravity.x, gravity.y);
				world.setGravity(gravity);
				// Need to wake all bodies once gravity field changes
				for (Iterator<Fixture> iterd = actor.getDdArray().iterator(); iterd.hasNext();) {
			    	Fixture ftd = iterd.next();
			    	ftd.getBody().setAwake(true);		    	
			    }
			}	
			else if (position.x < CAMERAVIEWWIDTH * 0.1f && position.y < CAMERAVIEWHEIGHT * 0.1f) {				
				if (!renderer.isTileReset()) renderer.setTileReset(true);
			}	
			else{
				System.out.printf("Remover at position to %f %f\n", 
						screenX * (renderer.getCamera().viewportWidth * 1.0f / width), 
						screenY * (renderer.getCamera().viewportHeight * 1.0f / height));
				controller.removeEdge(world, level, position);
				renderer.removeTileRender(position); // This MUST be after removeEdge. Otherwise the newly created edges will be removed!				
			}
		} 
		if (button == Input.Buttons.RIGHT) {
			Vector2 position = new Vector2(screenX * (renderer.getCamera().viewportWidth * 1.0f / width), 
										   (height - screenY) * (renderer.getCamera().viewportHeight * 1.0f / height));
			for (Iterator<Fixture> iter = level.getTileArrayModifiable().iterator(); iter.hasNext();) {
				Fixture ft = iter.next();
			    if (Math.sqrt((ft.getBody().getPosition().x - position.x) * (ft.getBody().getPosition().x - position.x) 
			    		+ (ft.getBody().getPosition().y - position.y) * (ft.getBody().getPosition().y - position.y)) 
			    	< CLEANRANGE) {
					controller.removeBodySafely(world, ft.getBody());
					// need to call to remove everything related to body safely
					iter.remove();
					// need to remove this guy out of the array to keep good
					// track
				}
			}
		} else if(button == Input.Buttons.MIDDLE) {
			Vector2 position = new Vector2(screenX * (renderer.getCamera().viewportWidth * 1.0f / width),
										   (height - screenY) * (renderer.getCamera().viewportHeight * 1.0f / height));
			for (Iterator<Fixture> iter = level.getTileArrayPreset().iterator(); iter.hasNext();) {
			    Fixture ft = iter.next();
				if (Math.sqrt((ft.getBody().getPosition().x - position.x) * (ft.getBody().getPosition().x - position.x) 
						+ (ft.getBody().getPosition().y - position.y) * (ft.getBody().getPosition().y - position.y)) 
					< CLEANRANGE) {
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
		dragOnFlag = 0; // Clear flag at end of drag
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		// TODO Auto-generated method stub
		Vector2 position = new Vector2(screenX * (renderer.getCamera().viewportWidth * 1.0f / width),
									   (height - screenY) * (renderer.getCamera().viewportHeight * 1.0f / height));
		Vector2 distVec2 = new Vector2();
		// need to scale, otherwise gamebox units inconsistent with screen display pixels
		if (dragOnFlag == 0) { // when drag first happens
			dragOld.x = position.x;
			dragOld.y = position.y;
			dragAccumulator = 0;
			dragOnFlag = 1;
			if( position.x > CAMERAVIEWWIDTH*0.9f && position.y > CAMERAVIEWHEIGHT*0.9f) {
			} else if (position.x > CAMERAVIEWWIDTH*0.9f && position.y < CAMERAVIEWHEIGHT*0.1f) {	
			} else {
				level.addGrass(position, world, rand.nextFloat() * 360f, 4);
			}
						
		} else { // when drag continues
			dragAccumulator += position.dst(dragOld) ;	
			if (dragAccumulator > SEPARATION) {
				distVec2.x = position.x; // caution not to use distVec2 = position
				distVec2.y = position.y;
				distVec2 = distVec2.sub(dragOld);
				int nSegs = (int) (dragAccumulator / SEPARATION); // to make sure the added grass tiles look continuous
				// this is useful when the player drag across screen real fast
				distVec2.div((float)nSegs); // get the vector for each segment
				for (int i = 0; i< nSegs; i++) {							
					if (dragOld.x > CAMERAVIEWWIDTH * 0.9f && dragOld.y > CAMERAVIEWHEIGHT * 0.9f) {
					} else if (dragOld.x > CAMERAVIEWWIDTH * 0.9f && dragOld.y < CAMERAVIEWHEIGHT * 0.1f) {
					} else{
						level.addGrass(dragOld, world, rand.nextFloat() * 360f, 4); // give grass random rotation						
					}
					dragOld.add(distVec2); // dragOld added towards current position
				}
				dragAccumulator = 0; // ready for next round
			}
			dragOld.x = position.x;
			dragOld.y = position.y;
		} 		
		
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
