package com.thewarpspace.ddbox2d.renderers.edgeconstruction;

import java.util.ArrayList;
import java.util.Iterator;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.thewarpspace.ddbox2d.Levels;

public class CreateEdgeGeo {	
	OrthographicCamera camera;
	Pixmap pixmap;
	private int[][] contourMap;
	ArrayList<ArrayList<Vector2>> listOfLists = new ArrayList<ArrayList<Vector2>>();
	
	public CreateEdgeGeo(Pixmap pixmap, OrthographicCamera camera, World world, Levels level){	
		this.camera= camera;
		this.pixmap = pixmap;
		Vector2 startingPoint = new Vector2();
		contourMap = new int [pixmap.getWidth()][pixmap.getHeight()];
		for(int i = 0;i< pixmap.getWidth();i++){
			for(int j = 0;j<pixmap.getHeight();j++){
				contourMap[i][j] = 0;
			}
		}
		ArrayList<Vector2> possibleStartingPoints = new ArrayList<Vector2>();
		possibleStartingPoints = getPossibleStartingPoints(pixmap); // find all possible starting points		
		// find contour using marching square algorithm
		for(Iterator<Vector2> iter = possibleStartingPoints.iterator();iter.hasNext();){
			startingPoint = iter.next();
			if(contourMap[(int) startingPoint.x][(int) startingPoint.y] != 1 && contourMap[(int) startingPoint.x][(int) startingPoint.y] != -256){
				// contourMap = -256 to mark ill contour points on a hole, which is not suitable as starting point for marching squares
				ArrayList<Vector2> vertices= new ArrayList<Vector2>();
				marchingSquares(startingPoint, vertices); // found contour points for each boundary pixel, in counter-clock-wise order
				//System.out.println(vertices.size());
				if(vertices.size() >2) {
					listOfLists.add(vertices); // each list contains the contour points of one outline of tiles
					// the interior holes of a tile region will have their own contour list
					for(Iterator<Vector2> iter1 = vertices.iterator(); iter1.hasNext();){
						contourMap[(int) iter1.next().x][(int) iter1.next().y] = 1; // mark already listed contour points
					}
				}				
			}
		}		
		for(Iterator<ArrayList<Vector2>> iter1 = listOfLists.iterator(); iter1.hasNext();){
			ArrayList<Vector2> listTmp = iter1.next();		
            listTmp = RDP(listTmp, 1.0f); // found the contours, now reduce the number of vertices using RDP algorithm
            buildEdges(listTmp, world, level); // build Edges for all tiles in the world
		}		
	}
	
	public ArrayList<Vector2> getPossibleStartingPoints(Pixmap pixmap) {
		// TODO Auto-generated method stub
		ArrayList<Vector2> tmpPoints = new ArrayList<Vector2>();
		int i =0;
		int j =0;
		for(i = 1; i < pixmap.getWidth()-1; i++){
			for(j = 1; j< pixmap.getHeight()-1; j++){				
				if(pixmap.getPixel(i, j) != -256) {
					if(pixmap.getPixel(i-1, j) != -256 && pixmap.getPixel(i+1, j) != -256 &&pixmap.getPixel(i, j-1) != -256 &&pixmap.getPixel(i, j+1) != -256 ) {
						// if the point has no neighbor as transparent point. It indicates this point sits at the interior of the pixel region
						
					} else { // boundary point
						tmpPoints.add(new Vector2(i,j));					
					}						
				}	
			}
		}
		return tmpPoints; // no starting point
	}
	
	public void marchingSquares(Vector2 startPoint, ArrayList<Vector2> contourVector) {        
        // if we found a starting pixel we can begin
		int squareValue;
        if (startPoint!=null) {
            // moving the graphic pen to the starting pixel
            // pX and pY are the coordinates of the starting point;
            int pX=(int) startPoint.x;
            int pY=(int) startPoint.y;
            //System.out.printf("%d ===== %d\n", pX, pY);
            // stepX and stepY can be -1, 0 or 1 and represent the step in pixels to reach
            // next contour point
            int stepX = -256;
            int stepY = -256;
            // we also need to save the previous step, that's why we use prevX and prevY
            int prevX = -256;
            int prevY = -256;
            // closedLoop will be true once we traced the full contour
            boolean closedLoop=false;
            while (!closedLoop) {
                // the core of the script is getting the 2x2 square value of each pixel
            	squareValue=getSquareValue(pX,pY);            	
                switch (squareValue) {
                        /* going UP with these cases:
                        
                        +---+---+   +---+---+   +---+---+
                        | 1 |   |   | 1 |   |   | 1 |   |
                        +---+---+   +---+---+   +---+---+
                        |   |   |   | 4 |   |   | 4 | 8 |
                        +---+---+   +---+---+  +---+---+
                        
                        */
                    case 1 :
                    case 5 :
                    case 13 :
                        stepX=0;
                        stepY=-1;
                        break;
                        /* going DOWN with these cases:
                        
                        +---+---+   +---+---+   +---+---+
                        |   |   |   |   | 2 |   | 1 | 2 |
                        +---+---+   +---+---+   +---+---+
                        |   | 8 |   |   | 8 |   |   | 8 |
                        +---+---+   +---+---+  +---+---+
                        
                        */
                    case 8 :
                    case 10 :
                    case 11 :
                        stepX=0;
                        stepY=1;
                        break;
                        /* going LEFT with these cases:
                        
                        +---+---+   +---+---+   +---+---+
                        |   |   |   |   |   |   |   | 2 |
                        +---+---+   +---+---+   +---+---+
                        | 4 |   |   | 4 | 8 |   | 4 | 8 |
                        +---+---+   +---+---+  +---+---+
                        
                        */
                    case 4 :
                    case 12 :
                    case 14 :
                        stepX=-1;
                        stepY=0;
                        break;
                        /* going RIGHT with these cases:
                        
                        +---+---+   +---+---+   +---+---+
                        |   | 2 |   | 1 | 2 |   | 1 | 2 |
                        +---+---+   +---+---+   +---+---+
                        |   |   |   |   |   |   | 4 |   |
                        +---+---+   +---+---+  +---+---+
                        
                        */
                    case 2 :
                    case 3 :
                    case 7 :
                        stepX=1;
                        stepY=0;
                        break;
                    case 6 :
                        /* special saddle point case 1:
                        
                        +---+---+ 
                        |   | 2 | 
                        +---+---+
                        | 4 |   |
                        +---+---+
                        
                        going LEFT if coming from UP
                        else going RIGHT 
                        
                        */
                    	
                        if (prevX==0&&prevY==-1) {
                            stepX=-1;
                            stepY=0;                            
                        }
                        else {
                            stepX=1;
                            stepY=0;
                        }
                    	
                        break;
                    case 9 :
                        /* special saddle point case 2:
                        
                        +---+---+ 
                        | 1 |   | 
                        +---+---+
                        |   | 8 |
                        +---+---+
                        
                        going UP if coming from RIGHT
                        else going DOWN 
                        
                        */
                    	
                        if (prevX==1&&prevY==0) {
                            stepX=0;
                            stepY=-1;
                        }
                        else {
                            stepX=0;
                            stepY=1;
                        }
                    	
                        break;
                }
                // moving onto next point
                pX+=stepX;
                pY+=stepY;
                // saving contour point
                if(pX < 0 || pX > pixmap.getWidth()-1 || pY < 0 || pY > pixmap.getHeight()-1) {                	
                	//System.out.println("pX, pY out of range~~~~~~~~~~");
                	contourMap[(int) startPoint.x][(int) startPoint.y] = -256; // possibly an interior point, skip for now
                	return;
                } else {
	                contourVector.add(new Vector2(pX, pY));
	                for(int i = -1 ; i<= 1 ; i++){
	                	for(int j = -1; j<= 1 ; j++){
	                		if(pX+i >= 0 && pX+i <= pixmap.getWidth()-1 && pY+j >= 0 && pY+j <= pixmap.getHeight()-1) {        
	                			contourMap[pX+i][pY+j] = 1;
	                		}
	                	}
	                }
	                
                }
                prevX=stepX;
                prevY=stepY;
                //  drawing the line
                // if we returned to the first point visited, the loop has finished;
                if (pX==startPoint.x&&pY==startPoint.y) {
                    closedLoop=true;
                }
            }
        }
    }

    private int getSquareValue(int pX,int pY) {
        /*
        
        checking the 2x2 pixel grid, assigning these values to each pixel, if not transparent
        
        +---+---+
        | 1 | 2 |
        +---+---+
        | 4 | 8 | <- current pixel (pX,pY)
        +---+---+
        
        */
    	int squareValue=0;
    	// these are needed because for the edge of pixmap, beyond that values are undefined, will lead to wrong marching
    	int upperleft = pixmap.getPixel(pX-1,pY-1);
    	int upper = pixmap.getPixel(pX,pY-1);
    	int left = pixmap.getPixel(pX-1,pY);
    	int self = pixmap.getPixel(pX,pY);
        if(pX == 0) {
	        left = -256;
	        upperleft = -256;
        }
        if(pY == 0){ // remember the box2d coordinate is the up-side-down of pixel coordinate
        	upper = -256;
        	upperleft = -256;
        }
        
        if (upperleft != -256) {
            squareValue+=1;
        }
        // checking upper pixel
        if (upper!= -256) {
            squareValue+=2;
        }
        // checking left pixel
        if (left != -256) {
            squareValue+=4;
        }
        // checking the pixel itself
        if (self != -256) {
            squareValue+=8;
        }
        return squareValue;
    }
    
    public ArrayList<Vector2> RDP(ArrayList<Vector2> v, float epsilon) {
    	// this is used to reduce the amount of points from a very dense contour outline
    	// epsilon controls the density of points in the final produce, larger values give less points
        Vector2 firstPoint=v.get(0);
        Vector2 lastPoint=v.get(v.size()-1);
        if (v.size()<3) {
            return v;
        }
        int index=-1;
        float dist=0;
        for (int i=1; i<v.size()-1; i++) {
            float cDist=findPerpendicularDistance(v.get(i),firstPoint,lastPoint);
            if (cDist>dist) {
                dist=cDist;
                index=i;
            }
        }
        if (dist>epsilon) {
        	ArrayList<Vector2> l1 = new ArrayList<Vector2>();
        	ArrayList<Vector2> l2 = new ArrayList<Vector2>();
        	ArrayList<Vector2> rs = new ArrayList<Vector2>();
        	for(int i=0;i<index+1;i++) {
        		l1.add(v.get(i));
        	}
        	for(int i=index;i<v.size();i++) {
        		l2.add(v.get(i));
        	}
            ArrayList<Vector2> r1=RDP(l1,epsilon);
            ArrayList<Vector2> r2=RDP(l2,epsilon);
            for(int i=0; i< r1.size()-1; i++){
            	rs.add(r1.get(i));
            }
            for(int i=0; i< r2.size(); i++){
            	rs.add(r2.get(i));
            }
            return rs;
        }
        else {
        	ArrayList<Vector2> tmpArray = new ArrayList<Vector2>(); 
        	tmpArray.add(firstPoint);
        	tmpArray.add(lastPoint);
            return tmpArray;
        }
    }	
	
	public static float findPerpendicularDistance(Vector2 point, Vector2 lineStart, Vector2 lineEnd)
	{
		float result;
		float slope;
		float intercept;
	    if (lineStart.x == lineEnd.x){
	        result=Math.abs(point.x-lineStart.x);
	    }else{
	        slope = (lineEnd.y - lineStart.y) / (lineEnd.x - lineStart.x);
	        intercept = lineStart.y - (slope * lineStart.x);
	        result = (float) (Math.abs(slope * point.x - point.y + intercept) / Math.sqrt(Math.pow(slope, 2) + 1));
	    }
	   
	    return result;
	}

	private void buildEdges(ArrayList<Vector2> listTmp, World world, Levels level) {
		// generate edges for the contours
		Vector2 edgeStart = new Vector2();
		Vector2 edgeEnd = new Vector2();
		for(int i = 0 ; i < listTmp.size(); i ++){
			edgeStart.set(listTmp.get(i)) ;			
			if(i == listTmp.size() -1){
				edgeEnd.set(listTmp.get(0));
			} else {
				edgeEnd.set(listTmp.get(i+1));
			}			 					
			edgeStart.y = camera.viewportHeight - edgeStart.y;
			edgeEnd.y = camera.viewportHeight - edgeEnd.y;	
			level.addTileEdge(edgeStart, edgeEnd, world);
		}		
	}	
}
