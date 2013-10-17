package com.thewarpspace.ddgame;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class Worlds {
	private Levels level;
	private Winds wind;
	private Winds ranWind;
	Array<Ddseeds> ddarray = new Array<Ddseeds>();
	private Random r = new Random();
	
	public Levels getLevel() {
		return level;
	}	
	
	public Winds getWind() {
		return wind;
	}

	public Winds getRanWind() {
		return ranWind;
	}

	public void setRanWind(Winds ranWind) {
		this.ranWind = ranWind;
	}

	public Array<Ddseeds> getDdarray() {
		return ddarray;
	}	

	public Worlds(){
		createNewWorld();
	}

	private void createNewWorld() {
		// TODO Auto-generated method stub
		level = new Levels();
		wind = new Winds(new Vector2(10,0));
		ranWind = new Winds(new Vector2(r.nextInt(6)-3, r.nextInt(6)-3 ));
	}
}
