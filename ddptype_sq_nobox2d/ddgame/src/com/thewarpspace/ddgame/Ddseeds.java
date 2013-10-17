package com.thewarpspace.ddgame;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class Ddseeds {
	
	Vector2 position = new Vector2();
	Vector2 velocity = new Vector2();
	Vector2 acceleration = new Vector2();
	Rectangle bounds = new Rectangle();
	public static final float SIZE = 5f;
	Array<Rectangle> collisionRects = new Array<Rectangle>();
	private boolean stopX;
	private boolean stopY; // used for collision and set vel to 0
	
	public boolean isStopX() {
		return stopX;
	}

	public void setStopX(boolean stopX) {
		this.stopX = stopX;
	}

	public boolean isStopY() {
		return stopY;
	}

	public void setStopY(boolean stopY) {
		this.stopY = stopY;
	}

	public Vector2 getPosition() {
		return position;
	}

	public void setPosition(Vector2 position) {
		this.position = position;
		this.bounds.setX(position.x);
		this.bounds.setY(position.y);
	}

	public Vector2 getVelocity() {
		return velocity;
	}

	public void setVelocity(Vector2 velocity) {
		this.velocity = velocity;
	}

	public Vector2 getAcceleration() {
		return acceleration;
	}
	
	public Array<Rectangle> getCollisionRects() {
		return collisionRects;
	}

	public void setAcceleration(Vector2 acceleration) {
		this.acceleration = acceleration;
	}

	public Rectangle getBounds() {
		return bounds;
	}
	
	public void update(float delta){
		
	}

	public Ddseeds(Vector2 position){
		this.position = position;
		this.bounds.setX(position.x);
		this.bounds.setY(position.y);
		this.bounds.setWidth(SIZE);
		this.bounds.setHeight(SIZE);
		this.setVelocity(new Vector2(0,0));
	}
}
