package com.thewarpspace.ddbox2d;

import java.util.ArrayList;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.thewarpspace.ddbox2d.screens.GameScreen;

public class HedgeHog {
	int actorType;
	boolean toRemove = false;
	// everything similar to DdSeeds class, refer there
	Fixture hhFixture;
	
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

	public Fixture getHhFixture() {
		return hhFixture;
	}

	public HedgeHog(Vector2 position, World world){	
		actorType = 3;
		BodyDef hhBodyDef = new BodyDef();
		hhBodyDef.position.set(position);
        Body hhBody = world.createBody(hhBodyDef);
        CircleShape hhCircle = new CircleShape();
        hhCircle.setRadius(GameScreen.SCALEVIEW*30.0f);
        FixtureDef fD = new FixtureDef();
        fD.shape = hhCircle;
        fD.isSensor = true; // This is important as HedgeHog will not trigger collision response,
        // ideal for eating stars and more fancy stuff
        hhFixture = hhBody.createFixture(fD);	
        hhFixture.getBody().setUserData(this); // points to this instance
        hhCircle.dispose();
	}
}
