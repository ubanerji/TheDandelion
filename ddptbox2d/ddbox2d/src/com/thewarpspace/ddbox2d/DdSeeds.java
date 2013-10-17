package com.thewarpspace.ddbox2d;

import java.util.ArrayList;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

public class DdSeeds {
	int actorType;
	boolean toRemove = false; // flag to be used by ContactListener to mark possible removal
	// so later in the controller loops the actual removal can be done.
	
	public int getActorType() {
		return actorType;
	}

	public void setActorType(int actorType) {
		this.actorType = actorType;
	}

	public boolean isToRemove() {
		return toRemove;
	}

	public void setToRemove(boolean toRemove) {
		this.toRemove = toRemove;
	}

	public DdSeeds(ArrayList<Fixture> ddArray, Vector2 position, World world){
		actorType = 1;
		BodyDef ddBodyDef = new BodyDef();
		ddBodyDef.type = BodyType.DynamicBody;
	    ddBodyDef.position.set(position);
	    Body ddBody = world.createBody(ddBodyDef);
	    CircleShape ddCircle = new CircleShape();
	    ddCircle.setRadius(10.0f); // the diameter
	    FixtureDef fD = new FixtureDef();
	    fD.shape = ddCircle; // physical properties of ddseeds can be set here
	    fD.density = 0.1f;  
	    fD.friction = 0.0f;  
	    fD.restitution = 0.2f;  
	    Fixture ddFixture = ddBody.createFixture(fD);	
	    ddFixture.getBody().setUserData(this);
	    // because of the need to use userdata, I have to make DdSeeds a separate class
	    // because "this" will then be able to point to this instance
	    ddArray.add(ddFixture);
	    ddCircle.dispose();
	}
}
