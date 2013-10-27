package com.thewarpspace.ddbox2d;

import java.util.ArrayList;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.thewarpspace.ddbox2d.screens.GameScreen;

public class TileEdges {
	boolean toRemove = false;
	Fixture edgeFixture;
	
	public boolean isToRemove() {
		return toRemove;
	}

	public void setToRemove(boolean toRemove) {
		this.toRemove = toRemove;
	}

	public Fixture getEdgeFixture() {
		return edgeFixture;
	}

	public TileEdges(Vector2 edgeStart, Vector2 edgeEnd, World world){
		Vector2 center = new Vector2((edgeStart.x+edgeEnd.x)*0.5f,(edgeStart.y + edgeEnd.y)*0.5f);
		Vector2 reledgeStart = new Vector2 (edgeStart.x - center.x, edgeStart.y - center.y);
		Vector2 reledgeEnd = new Vector2 (edgeEnd.x - center.x, edgeEnd.y - center.y);
		BodyDef edgeBodyDef = new BodyDef();
		edgeBodyDef.position.set(center.x,center.y);
        Body edgeBody = world.createBody(edgeBodyDef);
        EdgeShape edge = new EdgeShape();
		edge.set(reledgeStart, reledgeEnd);
        FixtureDef fD = new FixtureDef();
        fD.shape = edge;
        fD.friction = 1;
        edgeFixture = edgeBody.createFixture(fD);	
        edgeFixture.getBody().setUserData(this);        
        edge.dispose();      
	}
}
