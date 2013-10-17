package com.thewarpspace.ddbox2d.controllers;

import java.util.ArrayList;
import java.util.Iterator;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactFilter;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.JointEdge;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.World;
import com.thewarpspace.ddbox2d.Actors;
import com.thewarpspace.ddbox2d.DdSeeds;

public class WorldController{
	
	public WorldController(World world){
		
	}
	
	public int update(World world, Actors actor, int points){
		return addPoint(world, actor, points);		
	}
	
	public int addPoint(World world, Actors actor, int points){
		for (Iterator<Fixture> iterh = actor.getHhArray().iterator(); iterh.hasNext();) {
			Fixture fth = iterh.next();
		    for(Iterator<Fixture> iterd = actor.getDdArray().iterator(); iterd.hasNext();) {
		    	Fixture ftd = iterd.next();
		    	DdSeeds dd = (DdSeeds) (ftd.getBody().getUserData());
		    	// retrieve the data possibly changed during contactlistener
		    	if (dd.isToRemove()) {
		    		points ++;
		    		System.out.println("Dandelion Seed removed");
		    		removeBodySafely(world, ftd.getBody());
		    		iterd.remove();
		    		// remove the dandelion seeds once they hit the HedgeHog
		    	}
		    }
		}
		return points;
	}
	
	public void removeBodySafely(World world, Body body) {
	    //to prevent some obscure c assertion that happened randomly once in a blue moon
	    final ArrayList<JointEdge> list = body.getJointList();
	    while (list.size() > 0) {
	        world.destroyJoint(list.get(0).joint);
	    }
	    // actual remove
	    world.destroyBody(body);
	}

//	@Override
//	public boolean shouldCollide(Fixture fixtureA, Fixture fixtureB) {
//		// TODO Auto-generated method stub
//		Filter filterA = fixtureA.getFilterData();
//		Filter filterB = fixtureB.getFilterData();
//		
//		if (filterA.groupIndex == filterB.groupIndex && filterA.groupIndex != 0){
//			return filterA.groupIndex > 0;
//			// if positive, then bool=1, negative (never collide) bool = 0
//		}
//		
//		// when belongs to different groups
//		boolean collision = ((filterA.maskBits & filterB.categoryBits) != 0 && (filterB.maskBits & filterA.categoryBits) !=0);
//		return collision;
//	}


}
