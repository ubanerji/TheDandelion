package com.thewarpspace.ddbox2d.trashcan;
//
//import java.util.ArrayList;
//
//import com.badlogic.gdx.math.Vector2;
//import com.badlogic.gdx.physics.box2d.Body;
//import com.badlogic.gdx.physics.box2d.BodyDef;
//import com.badlogic.gdx.physics.box2d.FixtureDef;
//import com.badlogic.gdx.physics.box2d.PolygonShape;
//
public class Unused {
//	private ArrayList<Vector2> thePoly;
//	private ArrayList<Integer> discardedPoints = new ArrayList<Integer>();
//	
//	
//private void drawTriangle(int a,int b, int c) {
//		
//		
//		Vector2 vert[] = new Vector2[3];
//		Vector2 vertOd1[]  = new Vector2[3];
//		Vector2 vertOd[]  = new Vector2[3];
//		for(int i=0;i<3;i++){
//			vert[i]= new Vector2();
//			vertOd1[i]= new Vector2();
//			vertOd[i]= new Vector2();
//		}
//		vert[0]=thePoly.get(a);		
//		vert[1]=thePoly.get(b);
//		vert[2]=thePoly.get(c);
//		float res[] = new float [3];
//		
//		Vector2 vertAvg = new Vector2((vert[0].x + vert[1].x + vert[2].x), (vert[0].y + vert[1].y + vert[2].y));
//		for(int i=0;i<3;i++){
//			res[i] = (float ) (Math.atan2(vert[i].y- vertAvg.y, vert[i].x- vertAvg.x));
//		}
//		float maximum = Math.max(res[0], Math.max(res[1],res[2]));
//		int i;
//		for(i=0;i<3;i++){
//			if(res[i] == maximum) {
//				vertOd1[0] = vert[i];
//				break;
//			}			
//		}
//		maximum = Math.max(res[(i+1)%3],res[(i+2)%3]);
//		if(maximum == res[(i+1)%3]) {
//			vertOd1[1] = vert[(i+1)%3];
//			vertOd1[2] = vert[(i+2)%3];
//		} else{
//			vertOd1[1] = vert[(i+2)%3];
//			vertOd1[2] = vert[(i+1)%3];
//		}
//		
//		float area = 0.5f *((vertOd1[1].x-vertOd1[0].x)*(vertOd1[2].y - vertOd1[0].y) - (vertOd1[1].y-vertOd1[0].y)*(vertOd1[2].x - vertOd1[0].x));
//		if (area > 0.0001f) {
//			vertOd[0].x = vertOd1[2].x;
//			vertOd[0].y = camera.viewportHeight -vertOd1[2].y ;
//			vertOd[1].x = vertOd1[1].x;
//			vertOd[1].y = camera.viewportHeight -vertOd1[1].y ;
//			vertOd[2].x = vertOd1[0].x;
//			vertOd[2].y = camera.viewportHeight -vertOd1[0].y ;
//		} else {
//			vertOd[0].x = vertOd1[0].x;
//			vertOd[0].y = camera.viewportHeight -vertOd1[0].y ;
//			vertOd[1].x = vertOd1[1].x;
//			vertOd[1].y = camera.viewportHeight -vertOd1[1].y ;
//			vertOd[2].x = vertOd1[2].x;
//			vertOd[2].y = camera.viewportHeight -vertOd1[2].y ;			
//		}
//		if(Math.abs(area) > 0.00001f){
//			BodyDef hhBodyDef = new BodyDef();
//			hhBodyDef.position.set(0,0);
//	        Body hhBody = world.createBody(hhBodyDef);
//	        PolygonShape polyShape = new PolygonShape();
//	        polyShape.set(vertOd);
//	        FixtureDef fD = new FixtureDef();
//	        fD.shape = polyShape;
//	        hhBody.createFixture(fD);	
//	        polyShape.dispose();
//		} else{
//			System.out.println("Warning: three points in a line, triangulation fails.");
//			System.out.println(vert[0]);
//			System.out.println(vert[1]);
//			System.out.println(vert[2]);
//		}        		
//	}
//	
//	private void triangulate() {
//		// success is a Boolean variable which will say if we found a valid triangle
//		boolean success = true;
//		// triangleA is the leftmost vertex of the polygon, according to discarded points
//		int triangleA = leftmostPoint();
//		// triangleB is next vertex
//		int triangleB = (triangleA + 1) % thePoly.size();
//		// triangleC is previous vertex so in the end we have a triangle
//		int triangleC = (triangleA - 1);
//		if (triangleC<0) {
//			triangleC = thePoly.size() - 1;
//		}
//		// now it's time to see if any of the remaining vertices is inside the triangle
//		for (int i=0; i<thePoly.size(); i++) {
//			if (i!=triangleA && i!=triangleB && i!=triangleC) {
//				if (isInsideTriangle(thePoly.get(triangleA),thePoly.get(triangleB),thePoly.get(triangleC),thePoly.get(i))) {
//					// if one vertex is inside the triangle, we discard the leftmost point just found
//					discardedPoints.add(triangleA);
//					// then we set success variable to false
//					success = false;
//					break;
//				}
//			}
//		}
//		if (success) {
//			// if we have just found a valid triangle, we draw it
//			drawTriangle(triangleA,triangleB,triangleC);
//			// then we remove the leftmost point found from the polygon, obtaining a smaller polygon
//			thePoly.remove(triangleA);
//			// we also clear the vector of discarded points
//			discardedPoints=new ArrayList<Integer>();
//		}
//		// if there are still more than three points in the polygon (it's not a triangle) then execute triangulate function once more
//		if (thePoly.size() > 3) {
//			triangulate();
//		}
//		else {
//			// otherwise draw the remaining triangle
//			drawTriangle(0,1,2);
//		}
//	}
//	
//	// function to find the leftmost point
//	private int leftmostPoint() {
//		// first, I look for the first undiscarded point
//		int i;
//		int minIndex = 0;
//		for (i=0; i<thePoly.size(); i++) {
//			if (discardedPoints.indexOf(i) == -1) {
//				minIndex = i;
//				break;
//			}
//		}
//		// then I check for all undiscarded points to find the one with the lowest x value (the leftmost)
//		for (i=0; i<thePoly.size(); i++) {
//			if (discardedPoints.indexOf(i) == -1 && thePoly.get(i).x < thePoly.get(minIndex).x) {
//				minIndex = i;
//			}
//		}
//		return minIndex;
//	}
//	// these two functions have already been explained in the post "Algorithm to determine if a point is inside a triangle with mathematics (no hit test involved)"
//	private boolean isInsideTriangle(Vector2 A,Vector2 B,Vector2 C,Vector2 P) {
//		float planeAB = (A.x-P.x)*(B.y-P.y)-(B.x-P.x)*(A.y-P.y);
//		float planeBC = (B.x-P.x)*(C.y-P.y)-(C.x - P.x)*(B.y-P.y);
//		float planeCA = (C.x-P.x)*(A.y-P.y)-(A.x - P.x)*(C.y-P.y);
//		return sign(planeAB)==sign(planeBC) && sign(planeBC)==sign(planeCA);
//	}
//	private int sign( float n) {
//		return ((int) (Math.abs(n)/n));
//	}
}
