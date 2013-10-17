package com.thewarpspace.ddgame.controllers;

import java.util.Random;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.thewarpspace.ddgame.Ddseeds;
import com.thewarpspace.ddgame.Tiles;
import com.thewarpspace.ddgame.Worlds;


public class WorldController {	
	
	private Worlds world;
	public static final float STRENGTH = 10f;
	private static final float DAMP 			= 0.90f;
	private static final float MAX_VEL 			= 10f;
	private static final float LOCSTR = 0.2f;
	private Random r = new Random();

	private Pool<Rectangle> rectPool = new Pool<Rectangle>(){
		@Override
		protected Rectangle newObject(){
			return new Rectangle();
		}
	};
	
	private Array<Tiles> collidable = new Array<Tiles>();
	private int aa;
	private int bb;
	private Vector2 globalWind;
	
	public WorldController(Worlds world){
		this.world = world;
	}
	
	public void update(float delta, int elapsedTime){
		if(elapsedTime % 10 == 0) {
			world.getRanWind().setWinddir(new Vector2(r.nextInt(4)-2, r.nextInt(4)-2));
		}
		//System.out.println(delta);
		
		if(delta < 0.05){
			// has to enforce this, otherwise during screen resizing delta changes hugely and character will randomly disappear
			// processInput();	
			for(Ddseeds dd : world.getDdarray()){
				globalWind = new Vector2(world.getWind().getWinddir().x*STRENGTH +world.getRanWind().getWinddir().x*STRENGTH, world.getWind().getWinddir().y*STRENGTH +world.getRanWind().getWinddir().y*STRENGTH);
				dd.getAcceleration().set(globalWind);						
				addLocalAcceleration(dd, globalWind, delta);
				dd.getAcceleration().mul(delta);
				dd.getVelocity().add(dd.getAcceleration().x, dd.getAcceleration().y);
				checkCollisionWithTiles(dd, delta);	
				if(dd.getVelocity().x > MAX_VEL) {
					dd.getVelocity().x = MAX_VEL;
				}
				if(dd.getVelocity().x < -MAX_VEL) {
					dd.getVelocity().x = -MAX_VEL;
				}	
				if(dd.getVelocity().y > MAX_VEL) {
					dd.getVelocity().y = MAX_VEL;
				}
				if(dd.getVelocity().y < -MAX_VEL) {
					dd.getVelocity().y = -MAX_VEL;
				}	
				dd.update(delta);
			}
			
		}
	}
	
	private void addLocalAcceleration(Ddseeds dd, Vector2 globalWind2, float delta) {
		// TODO Auto-generated method stub
		dd.getVelocity().mul(delta);
		Rectangle ddRect = rectPool.obtain();
		ddRect.set(dd.getBounds().x, dd.getBounds().y, dd.getBounds().width, dd.getBounds().height);
		// System.out.printf("ddRect: %f, %f, %f, %f \n", ddRect.x, ddRect.y, ddRect.width, ddRect.height);
		int startX, endX;
		int startY = (int) dd.getBounds().y;
		int endY = (int) (dd.getBounds().y + dd.getBounds().height);
		// System.out.printf("startY end Y: %d, %d \n", startY, endY);
		if(dd.getVelocity().x < 0) {
			startX = endX = (int) Math.floor(dd.getBounds().x + dd.getVelocity().x);
		} else {
			startX = endX = (int) Math.floor(dd.getBounds().x + dd.getBounds().width + dd.getVelocity().x);
		}
		populateCollidableTiles(startX, startY, endX, endY);
		ddRect.x += dd.getVelocity().x;
		dd.getCollisionRects().clear();
		for(Tiles tile : collidable) {
			// System.out.println("inside x collision check");
			if(ddRect.overlaps(tile.getForceBox())){ // apply force around tiles
				if(ddRect.x + ddRect.width <= tile.getBounds().x){// if dd is to the left, apply -x force
					dd.getAcceleration().add(new Vector2(Math.abs(globalWind2.x)*LOCSTR,0));
				} 
				if(ddRect.x >= tile.getBounds().x + tile.getBounds().width){// if dd is to the right, apply +x force
					dd.getAcceleration().add(new Vector2(-Math.abs(globalWind2.x)*LOCSTR,0));
				} 
			}
		}
		ddRect.x = dd.getPosition().x;
		startX = (int) dd.getBounds().x;
		endX = (int) (dd.getBounds().x + dd.getBounds().width);
		if(dd.getVelocity().y < 0) {
			startY = endY = (int) Math.floor(dd.getBounds().y + dd.getVelocity().y);
		} else {
			startY = endY = (int) Math.floor(dd.getBounds().y + dd.getBounds().height + dd.getVelocity().y);
		}
		// System.out.printf("startY endY %d %d, velocity %f\n", startY, endY, dd.getVelocity().y);
		populateCollidableTiles(startX, startY, endX, endY);
		ddRect.y += dd.getVelocity().y;
		for(Tiles tile : collidable) {
			// System.out.println("inside y collision check");
			if(ddRect.overlaps(tile.getForceBox())){ // apply force around tiles
				if(ddRect.y + ddRect.height <= tile.getBounds().y){
					dd.getAcceleration().add(new Vector2(0,Math.abs(globalWind2.y)*LOCSTR));
				} 
				if(ddRect.y >= tile.getBounds().y + tile.getBounds().height){
					dd.getAcceleration().add(new Vector2(0,-Math.abs(globalWind2.y)*LOCSTR));
				}
			}
		}
		dd.getVelocity().mul(1/delta);
	}

	private void checkCollisionWithTiles(Ddseeds dd, float delta){
		dd.getVelocity().mul(delta);
		Rectangle ddRect = rectPool.obtain();
		ddRect.set(dd.getBounds().x, dd.getBounds().y, dd.getBounds().width, dd.getBounds().height);
		// System.out.printf("ddRect: %f, %f, %f, %f \n", ddRect.x, ddRect.y, ddRect.width, ddRect.height);
		int startX, endX;
		int startY = (int) dd.getBounds().y;
		int endY = (int) (dd.getBounds().y + dd.getBounds().height);
		// System.out.printf("startY end Y: %d, %d \n", startY, endY);
		if(dd.getVelocity().x < 0) {
			startX = endX = (int) Math.floor(dd.getBounds().x + dd.getVelocity().x);
		} else {
			startX = endX = (int) Math.floor(dd.getBounds().x + dd.getBounds().width + dd.getVelocity().x);
		}
		populateCollidableTiles(startX, startY, endX, endY);
		ddRect.x += dd.getVelocity().x;
		dd.getCollisionRects().clear();
		for(Tiles tile : collidable) {
			// System.out.println("inside x collision check");
			if(tile == null) continue ;
			if(ddRect.overlaps(tile.getBounds())){
				// System.out.println("x collision found");
				dd.getVelocity().x = 0;
				dd.getCollisionRects().add(tile.getBounds());
				break;
			}
		}
		ddRect.x = dd.getPosition().x;
		startX = (int) dd.getBounds().x;
		endX = (int) (dd.getBounds().x + dd.getBounds().width);
		if(dd.getVelocity().y < 0) {
			startY = endY = (int) Math.floor(dd.getBounds().y + dd.getVelocity().y);
		} else {
			startY = endY = (int) Math.floor(dd.getBounds().y + dd.getBounds().height + dd.getVelocity().y);
		}
		// System.out.printf("startY endY %d %d, velocity %f\n", startY, endY, dd.getVelocity().y);
		populateCollidableTiles(startX, startY, endX, endY);
		ddRect.y += dd.getVelocity().y;
		for(Tiles tile : collidable) {
			// System.out.println("inside y collision check");
			if(tile == null) continue ;
			if(ddRect.overlaps(tile.getBounds())){
				// System.out.println("y collision found");
				dd.getVelocity().y = 0;
				dd.getCollisionRects().add(tile.getBounds());
				break;
			}
		}
		ddRect.y = dd.getPosition().y;
		dd.getPosition().add(dd.getVelocity());
		dd.getBounds().x = dd.getPosition().x;
		dd.getBounds().y = dd.getPosition().y;
		dd.getVelocity().mul(1/delta);
	}	

	private void populateCollidableTiles(int startX, int startY, int endX,
			int endY) {
		// TODO Auto-generated method stub		
		Rectangle bufferBox = new Rectangle();
		bufferBox.set(startX, startY, endX-startX, endY-startY);
		collidable.clear();
		for(Tiles tile : world.getLevel().getTilearray()){
			if(bufferBox.overlaps(tile.getBounds())){
				collidable.add(tile);
			}
		}
	}
	
	private void processInput() {
		// TODO Auto-generated method stub
		
	}
}
