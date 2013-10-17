package com.thewarpspace.ddbox2d;

import java.util.ArrayList;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.World;


public class Actors {
	// Manages the record of arrays of dandelion seeds and hedgehog(s)
	// Creation of dandelion and hedgehog goes through here
	ArrayList<Fixture> ddArray = new ArrayList<Fixture>();
	ArrayList<Fixture> hhArray = new ArrayList<Fixture>();
	
	public Actors(){		
	}
	
	public ArrayList<Fixture> getDdArray() {
		return ddArray;
	}

	public ArrayList<Fixture> getHhArray() {
		return hhArray;
	}	
	
	public void addDdSeeds(Vector2 position, World world){
		DdSeeds dd = new DdSeeds(ddArray, position, world);
	}
	
	public void addHedgeHog(Vector2 position, World world){
		HedgeHog hh = new HedgeHog(hhArray, position, world);
	}

}
