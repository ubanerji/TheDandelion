package com.thewarpspace.ddbox2d.controllers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactFilter;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.JointEdge;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.World;
import com.thewarpspace.ddbox2d.Actors;
import com.thewarpspace.ddbox2d.DdSeeds;
import com.thewarpspace.ddbox2d.Levels;
import com.thewarpspace.ddbox2d.TileEdges;
import com.thewarpspace.ddbox2d.Tiles;
import com.thewarpspace.ddbox2d.renderers.WorldRenderer;
import com.thewarpspace.ddbox2d.screens.GameScreen;

public class WorldController{
	
	ArrayList<Fixture> tileRemoverArray = new ArrayList<Fixture>();
	
	public ArrayList<Fixture> getTileRemoverArray() {
		return tileRemoverArray;
	}

	public WorldController(World world){
		
	}
	
	public int update(World world, Actors actor, int points){
		return addPoint(world, actor, points);				
	}
	
	public int addPoint(World world, Actors actor, int points){
		//for (Iterator<Fixture> iterh = actor.getHhArray().iterator(); iterh.hasNext();) {
		//	Fixture fth = iterh.next();
		    for (Iterator<Fixture> iterd = actor.getDdArray().iterator(); iterd.hasNext();) {
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
		//}
		return points;
	}
	
	public void removeEdge(World world, Levels level, Vector2 position){
		
		for (Iterator<Fixture> iter = level.getTileEdgesPresetModifiable().iterator(); iter.hasNext();) {
	    	Fixture ft = iter.next();
	        EdgeShape es = (EdgeShape) ft.getShape();
	        Vector2 vet1 = new Vector2();
	        Vector2 vet2 = new Vector2();
	        es.getVertex1(vet1);
	        es.getVertex2(vet2);
	        Vector2 edgeStart = new Vector2(ft.getBody().getPosition().x + vet1.x, 
	        								ft.getBody().getPosition().y + vet1.y); // recover global coord of vertices
	        Vector2 edgeEnd = new Vector2(ft.getBody().getPosition().x + vet2.x, 
	        							  ft.getBody().getPosition().y + vet2.y); 
	        Vector2 intersect1 = new Vector2();
	        Vector2 intersect2 = new Vector2();
	        int whichToRemove = circleCrossEdge(position, edgeStart, edgeEnd, intersect1, intersect2);
			if( whichToRemove != -1) {
	        	// if the remover cross with edge, need to break this edge
	        	TileEdges te = (TileEdges) ft.getBody().getUserData();
    			te.setToRemove(true); // mark the affected edges to be removed
    			float s1 = (float) Math.sqrt((intersect1.x - edgeStart.x) * (intersect1.x - edgeStart.x) 
    					+ (intersect1.y - edgeStart.y) * (intersect1.y - edgeStart.y));
    			float s2 = (float) Math.sqrt((intersect2.x - edgeStart.x) * (intersect2.x - edgeStart.x) 
    					+ (intersect2.y - edgeStart.y) * (intersect2.y - edgeStart.y));
    			float e1 = (float) Math.sqrt((intersect1.x -edgeEnd.x) * (intersect1.x - edgeEnd.x) 
    					+ (intersect1.y - edgeEnd.y) * (intersect1.y - edgeEnd.y));
    			float e2 = (float) Math.sqrt((intersect2.x -edgeEnd.x) * (intersect2.x - edgeEnd.x) 
    					+ (intersect2.y - edgeEnd.y) * (intersect2.y - edgeEnd.y));
	        	if (whichToRemove == 3) { // entire edge enclosed in circle, nothing to add
	        		
	        	} else if (whichToRemove == 0) { // keep both. So build two new edges
	        		if (s1 < s2){ // if intersect1 closer to pt_start
	        			level.addTileEdgeBuffered(edgeStart, intersect1, world);
	        			level.addTileEdgeBuffered(intersect2, edgeEnd, world);
	        		} else {
	        			level.addTileEdgeBuffered(edgeStart, intersect2, world);
	        			level.addTileEdgeBuffered(intersect1, edgeEnd, world);
	        		}	        		
	        	} else if (whichToRemove == 1) { // remove pt_start
	        		if (e1 < e2) { // if intersect1 closer to pt_end
	        			level.addTileEdgeBuffered(intersect1, edgeEnd, world);
	        		} else {
	        			level.addTileEdgeBuffered(intersect2, edgeEnd, world);
	        		}
	        	} else if (whichToRemove == 2) { // remove pt_end
	        		if (s1 < s2) { // if intersect1 closer to pt_start
	        			level.addTileEdgeBuffered(edgeStart, intersect1, world);
	        		} else {
	        			level.addTileEdgeBuffered(edgeStart, intersect2, world);
	        		}
	        	}
	        }
	    }
		
		for (Iterator<Fixture> iter = level.getTileEdgesPresetModifiable().iterator(); iter.hasNext();) {
			Fixture ft = iter.next();
	    	TileEdges te = (TileEdges) (ft.getBody().getUserData());
	    	// retrieve the data possibly changed during contactlistener
	    	if (te.isToRemove()) {	    		
	    		// System.out.println("Tile Edge removed");
	    		removeBodySafely(world, ft.getBody());
	    		iter.remove();
	    		// remove the affected edges that are crossed by the remover circle
	    	}
		}
		for (Iterator<Fixture> iter1 = level.getTileEdgesBuffered().iterator(); iter1.hasNext();) {
			Fixture ft = iter1.next(); // add the buffered new edges to the already modified edge array
			level.getTileEdgesPresetModifiable().add(ft);
		}
		level.getTileEdgesBuffered().clear(); // clear the buffer array
	}	
	
	private int circleCrossEdge(Vector2 position, Vector2 edgeStart, Vector2 edgeEnd, Vector2 intersect1, Vector2 intersect2) {
		// TODO Auto-generated method stub			
		//                   x  p
		//              v1 <-    -> v2
		//          1 x              v3 ->   x 2
		//		
		Vector2 v1 = new Vector2(edgeStart.x - position.x, edgeStart.y - position.y);
		Vector2 v2 = new Vector2(edgeEnd.x - position.x, edgeEnd.y - position.y);
		Vector2 v3 = new Vector2(edgeEnd.x - edgeStart.x, edgeEnd.y - edgeStart.y);
		int whichToRemove;
		float dx = v2.x - v1.x;
		float dy = v2.y - v1.y;
		float dr = (float) Math.sqrt(dx * dx + dy * dy);
		float dd = v1.x * v2.y - v2.x * v1.y;
		float rad = GameScreen.CLEANRANGE;
		float discriminant = rad  *rad * dr * dr - dd * dd;
		
		if (discriminant > 0) {			
			// within the parellel lines with R distance from the edge (v2)
			// tangent case not considered for simplicity
			if (v1.x * v3.x + v1.y * v3.y > 0) {
				// if the point is to the left of start, needs to stay within a radius from start
				if (Math.sqrt(v1.x * v1.x + v1.y * v1.y) >= rad) {
					return -1; // if beyond radius of start to the left
				}
			}
			if (v2.x * v3.x + v2.y * v3.y < 0) {
				// if the point is to the right of end, needs to stay within a radius from end
				if (Math.sqrt(v2.x * v2.x + v2.y * v2.y) >= rad) {
					return -1; // if beyond radius of end to the right
				}
			}
			// these are the intersect points
			List<Vector2> intersects = getCircleLineIntersectionPoint(edgeStart, edgeEnd, position, rad);
			if (intersects.size() != 2) {
				System.out.println("Found less than 2 cross points, bug! ");
			}
			intersect1.set(intersects.get(0));
			intersect2.set(intersects.get(1));
			float d1 = (float) Math.sqrt(v1.x * v1.x + v1.y * v1.y);
			float d2 = (float) Math.sqrt(v2.x * v2.x + v2.y * v2.y);
			if (d1 < rad && d2 < rad) { // both points within radius
				whichToRemove = 3;
			} else if (d1 < rad && d2 > rad) { // only pt1 within radius (toberemoved)
				whichToRemove = 1;				
			} else if (d1 > rad && d2 < rad) { // only pt2 within radius (toberemoved)
				whichToRemove = 2;
			} else { // neither point enclosed in circle
				whichToRemove = 0;
			}
			return whichToRemove; // otherwise cross
		}		
		return -1;		
	}
	
	public static List<Vector2> getCircleLineIntersectionPoint(Vector2 pointA,
			Vector2 pointB, Vector2 center, float radius) {
		float baX = pointB.x - pointA.x;
		float baY = pointB.y - pointA.y;
		float caX = center.x - pointA.x;
		float caY = center.y - pointA.y;

		float a = baX * baX + baY * baY;
        float bBy2 = baX * caX + baY * caY;
        float c = caX * caX + caY * caY - radius * radius;

        float pBy2 = bBy2 / a;
        float q = c / a;

        float disc = pBy2 * pBy2 - q;
        if (disc < 0) {
            return Collections.emptyList();
        }
        // if disc == 0 ... dealt with later
        float tmpSqrt = (float) Math.sqrt(disc);
        float abScalingFactor1 = -pBy2 + tmpSqrt;
        float abScalingFactor2 = -pBy2 - tmpSqrt;

        Vector2 p1 = new Vector2(pointA.x - baX * abScalingFactor1, 
        						 pointA.y - baY * abScalingFactor1);
        if (disc == 0) { // abScalingFactor1 == abScalingFactor2
            return Collections.singletonList(p1);
        }
        Vector2 p2 = new Vector2(pointA.x - baX * abScalingFactor2, 
        					 	 pointA.y - baY * abScalingFactor2);
        return Arrays.asList(p1, p2);
    }

	public void removeTiles(World world, Levels level, WorldRenderer renderer, Vector2 position){
		int counter;
		for (Iterator<Fixture> iter = level.getTileArrayPresetModifiable().iterator(); iter.hasNext();) {
			Fixture ft = iter.next();
		    if (Math.sqrt((ft.getBody().getPosition().x - position.x) * (ft.getBody().getPosition().x - position.x)
		    		 + (ft.getBody().getPosition().y - position.y) * (ft.getBody().getPosition().y - position.y)) 
		    	< GameScreen.CLEANRANGE) {
		    	 removeBodySafely(world, ft.getBody());
		    	 // need to call to remove everything related to body safely
		    	 iter.remove();
		    	 // need to remove this guy out of the array to keep good track
		    }			     
		}
		for (int i = (int) position.x- (int) (GameScreen.CLEANRANGE*1.5);
			 i < (int) position.x +(int) (GameScreen.CLEANRANGE*1.5); 
			 i++){
			for (int j = (int)(renderer.getCamera().viewportHeight - position.y) - (int) (GameScreen.CLEANRANGE * 1.5);
				 j < (int)(renderer.getCamera().viewportHeight - position.y) + (int) (GameScreen.CLEANRANGE * 1.5);
				 j++){
				if (renderer.getPixmap().getPixel(i, j) != -256) {
					counter =0;
					for (int ix = -5; ix < 5; ix++) {
						for (int iy = -5; iy < 5; iy++) {
							if (renderer.getPixmap().getPixel(i + ix, j + iy) != -256) {								
							} else {
								counter++;
							}
						}
					}
					if (counter < 10 && counter > 0) {
						Vector2 posCur = new Vector2(i, renderer.getCamera().viewportHeight -j);
						level.addTile(posCur, world, 0f, 4);
					}
				}			
			}
		}
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
