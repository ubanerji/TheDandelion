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

public class Tiles {	
	Float RotAngle;
	boolean toRemove = false;
	Fixture tileFixture;
	
	public Float getRotAngle() {
		return RotAngle;
	}

	public void setRotAngle(Float rotAngle) {
		RotAngle = rotAngle;
	}

	public boolean isToRemove() {
		return toRemove;
	}

	public void setToRemove(boolean toRemove) {
		this.toRemove = toRemove;
	}

	public Fixture getTileFixture() {
		return tileFixture;
	}

	public Tiles(Vector2 position, World world, Float rot, float f){
		this.RotAngle = rot;
		BodyDef tileBodyDef = new BodyDef();
		tileBodyDef.position.set(position);
        Body tileBody = world.createBody(tileBodyDef);
		CircleShape tileCircle = new CircleShape();
		tileCircle.setRadius(GameScreen.SCALEVIEW *f);
        FixtureDef fD = new FixtureDef();
        fD.shape = tileCircle;
        fD.density = 1.0f;  
        fD.friction = 0.5f;  
        fD.restitution = 0.1f;  
        tileFixture = tileBody.createFixture(fD);
        tileFixture.getBody().setUserData(this);
        tileCircle.dispose();
	}
}
