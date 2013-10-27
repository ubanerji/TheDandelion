package com.thewarpspace.ddbox2d.controllers;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.World;
import com.thewarpspace.ddbox2d.DdSeeds;
import com.thewarpspace.ddbox2d.HedgeHog;
import com.thewarpspace.ddbox2d.TileEdges;
import com.thewarpspace.ddbox2d.Tiles;

public class HedgeContactListener implements ContactListener {
	
	public HedgeContactListener(){
	}
	
	@Override
	public void beginContact(Contact contact) {
		// TODO Auto-generated method stub
		// This part is really cumbersome, cost me one entire day to figure out 
		// how to play with the getUserData(). This setup over here appears to be standard practice
		// people also try to use eventDispatcher, which relies on threads, which I know nothing
		 if(contact.getFixtureA().getBody().getUserData() instanceof HedgeHog &&
			contact.getFixtureB().getBody().getUserData() instanceof DdSeeds) {
			DdSeeds dd = (DdSeeds) contact.getFixtureB().getBody().getUserData();
			// has to force the generic object to DdSeeds class
			// the userdata is an instance of either DdSeeds or HedgeHog
			// once assigned to dd, can then operate normally 
			dd.setToRemove(true);
			// set a flag for later removal of this seed in Controller
			// can not remove over here, otherwise the corresponding ddArray is not updated and will cause crash
			// System.out.println("DD collides with HH");
		 }
		 if(contact.getFixtureB().getBody().getUserData() instanceof HedgeHog &&
			contact.getFixtureA().getBody().getUserData() instanceof DdSeeds) {
			DdSeeds dd = (DdSeeds) contact.getFixtureA().getBody().getUserData();
			dd.setToRemove(true);
			// System.out.println("DD collides with HH");
		 }

	}

	@Override
	public void endContact(Contact contact) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void preSolve(Contact contact, Manifold oldManifold) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) {
		// TODO Auto-generated method stub
		
	}

}
