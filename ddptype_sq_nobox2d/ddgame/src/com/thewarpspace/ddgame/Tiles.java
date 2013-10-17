package com.thewarpspace.ddgame;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Tiles {
	Vector2 position = new Vector2();
	Rectangle bounds = new Rectangle();
	Rectangle forceBox = new Rectangle();
	int type;	
	public static final float SIZE = 1.0f;
	
	public Vector2 getPosition() {
		return position;
	}

	public void setPosition(Vector2 position) {
		this.position = position;
	}

	public Rectangle getBounds() {
		return bounds;
	}

	public void setBounds(Rectangle bounds) {
		this.bounds = bounds;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public Rectangle getForceBox() {
		return forceBox;
	}

	public Tiles(Vector2 position, int type){
		this.position = position;
		this.bounds.setX(position.x);
		this.bounds.setY(position.y);
		this.bounds.setHeight(SIZE);
		this.bounds.setWidth(SIZE);
		this.forceBox.setX(position.x-SIZE/2.0f);
		this.forceBox.setY(position.y-SIZE/2.0f);
		this.forceBox.width=SIZE*2.0f;
		this.forceBox.height=SIZE*2.0f;
		this.type = type;
	}
}
