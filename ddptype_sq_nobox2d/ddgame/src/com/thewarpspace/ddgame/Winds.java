package com.thewarpspace.ddgame;

import com.badlogic.gdx.math.Vector2;

public class Winds {
	
	private Vector2 winddir;

	public Vector2 getWinddir() {
		return winddir;
	}

	public void setWinddir(Vector2 winddir) {
		this.winddir = winddir;
	}

	public Winds(Vector2 winddir){
		this.setWinddir(winddir);
	}
}
