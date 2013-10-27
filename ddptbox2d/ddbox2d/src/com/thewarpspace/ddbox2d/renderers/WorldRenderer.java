package com.thewarpspace.ddbox2d.renderers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Blending;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.thewarpspace.ddbox2d.Actors;
import com.thewarpspace.ddbox2d.Levels;
import com.thewarpspace.ddbox2d.Tiles;
import com.thewarpspace.ddbox2d.screens.GameScreen;
import com.thewarpspace.ddbox2d.renderers.edgeconstruction.CreateEdgeGeo;

public class WorldRenderer {
	World world;
	Box2DDebugRenderer debugRenderer;  
    OrthographicCamera camera;
	private Texture ddTexture;
	private Texture bgTexture;  
	private Texture hhTexture;
    static final float BOX_STEP=1/60f;  
    static final int BOX_VELOCITY_ITERATIONS=6;  
    static final int BOX_POSITION_ITERATIONS=2;  
    static final float WORLD_TO_BOX=0.01f;  
    static final float BOX_WORLD_TO=100f;      
    
    SpriteBatch spriteBatch = new SpriteBatch();
    Array<Sprite> grassSprite = new Array<Sprite> ();
	private Actors actor;
	private Levels level;
	boolean debug = false;
	private BitmapFont font;
	private String textToDisplay;
	private Texture grassTexture;
	private Pixmap pixmap;
	private Texture tileNewTexture;
	private Pixmap pixmapSav;
	private boolean tileReset;
	Sprite theCanvas = new Sprite();
	private ShapeRenderer debugShapeRenderer;
	public boolean isDebug() {
		return debug;
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	public Pixmap getPixmap() {
		return pixmap;
	}

	public OrthographicCamera getCamera() {
		return camera;
	}

	public boolean isTileReset() {
		return tileReset;
	}

	public void setTileReset(boolean tileChanged) {
		this.tileReset = tileChanged;
	}

	public void render(Integer points){
		GL10 gl = Gdx.graphics.getGL10();
		gl.glClearColor(0, 0, 0, 1);
    	gl.glClear(GL10.GL_COLOR_BUFFER_BIT);  
    	gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    	camera.update();
    	camera.apply(gl);
    	spriteBatch.setProjectionMatrix(camera.combined);    	
		spriteBatch.begin();		
		drawBackground(camera.viewportWidth, camera.viewportHeight);
		debugShapeRenderer.setProjectionMatrix(camera.combined);
		drawHh();
		drawDd();		
		drawTiles();
		drawTilesNew();	
		textToDisplay = "Player Score: " + points.toString() +" ; Gravity: " + world.getGravity().toString();
		font.setScale(GameScreen.SCALEVIEW*1.0f, GameScreen.SCALEVIEW*1.2f);
		font.draw(spriteBatch, textToDisplay, GameScreen.CAMERAVIEWHEIGHT*0.05f, GameScreen.CAMERAVIEWHEIGHT*0.1f); 		
		spriteBatch.end();
		if(debug) {
			debugRenderer.render(world, camera.combined);  
		}
        world.step(BOX_STEP, BOX_VELOCITY_ITERATIONS, BOX_POSITION_ITERATIONS);  
        world.clearForces(); // As told in manual, this is needed
    }
    
	private void drawTilesNew() {
		// Redrawing entire map pixel by pixel is super slow, should only do it at the game load
		if(tileReset) {			
			Vector2 testC;
			Vector2 testS;			
			pixmap.setColor(-256);
			pixmap.fill(); // flush pixmap with transparent color
			long bt1 = System.currentTimeMillis();
			byte[] wallsBuffer = level.getWallsBuffer(); // the walls[] maintains only boundary bodies, while wallsBuffer[] maintains entire level map, caution expanded dimensions
			float[] extent = level.getExtent(); // stores the random extension from the tile point for pixel region
			for(int i = 1;i< level.getWidth()+1;i++){
				for(int j = 1;j<level.getHeight()+1;j++){
					if(wallsBuffer[i + j*(level.getWidth()+2)] == 3 || wallsBuffer[i + j*(level.getWidth()+2)] ==2){
						// if the present point is a tile, render its surrounding
						testC = new Vector2((i-0.5f)*level.getTileWidth(), (j-0.5f)*level.getTileHeight());
						for(int ix = -8; ix< 8; ix++) { // scan the square region around the tile point to find points within a radius
							for(int iy = -8; iy<8; iy++){
								testS = new Vector2((i-0.5f)*level.getTileWidth()+ix, (j-0.5f)*level.getTileHeight()+iy);
								if(testC.dst2(testS) < 10f + extent[(i-1) + (j-1)*level.getWidth()] ){
									pixmap.drawPixel((int)(testS.x), (int)(testS.y), pixmapSav.getPixel((int)(testS.x), (int)(testS.y)));
									// if the pixels are within 20f + random radius of the tile point, then draw them using the previously saved pixmap
								}
							}
						}					
					}
				}
			}
			for(int i = 1;i< pixmap.getWidth()-1;i++){
				for(int j = 1;j<pixmap.getHeight()-1;j++){
					if(pixmap.getPixel(i, j) != -256) { // if this is a non-transparent point, which means it's a tile-pixmap region point
						if(pixmap.getPixel(i-1, j) != -256 && pixmap.getPixel(i+1, j) != -256 &&pixmap.getPixel(i, j-1) != -256 &&pixmap.getPixel(i, j+1) != -256 ) {
							// if the point has no neighbor as transparent point. It indicates this point sits at the interior of the pixel region
						} else {
							pixmap.drawPixel(i, j, Color.rgba8888(Color.YELLOW));	
							// otherwise this point is at the boundary, which is a contour point. Draw the contour
						}
					}					
				}
			}			
			tileNewTexture.draw(pixmap, 0, 0); // if I created new Texture each time, there will be memory leak
			int[][] byteMap = new int[pixmap.getWidth()+1][pixmap.getHeight()+1];
			for(int i = 0; i< pixmap.getWidth()+1; i++){
				for(int j = 0; j< pixmap.getHeight()+1; j++){
					if(pixmap.getPixel(i, j) == -256) {
						byteMap[i][j] = 1;
					} else{
						byteMap[i][j] = -1;
					}
				}
			}
			long bt2 = System.currentTimeMillis();
			System.out.printf("yellow time  %d\n", bt2-bt1);			
			new CreateEdgeGeo(pixmap, camera, world, level); // create the edge-based geometry using the pixmap
			tileReset = false;			
		} 
		spriteBatch.draw(tileNewTexture, 0, camera.viewportHeight -pixmap.getHeight());		
	}
	
	public void removeTileRender(Vector2 vector2){ // removes tile texture only, will implement remove underlying bodies as well
		ArrayList<Vector2> listOfArcPoints = new ArrayList<Vector2> ();
		ArrayList<Vector2> listOfArcPointsBuff = new ArrayList<Vector2> ();
		ArrayList<ArrayList<Vector2>> listOfLists = new ArrayList<ArrayList<Vector2>>();
		ArrayList<Float> listOfAngles = new ArrayList<Float> ();
		Vector2 center = new Vector2(vector2.x, (camera.viewportHeight - vector2.y)) ;
		float rad = GameScreen.CLEANRANGE;
		float threshold = 1.5f; // controls how many extra points near the remover cycle should be searched for adding to list
		float thrshold = 5f; // controls the list generation: how close should the points be to be considered belonging to same list
	    int seg = 15;  // control the the number of points in the list to create less edges
		pixmap.setColor(-256);
		pixmap.fillCircle((int)center.x, (int)center.y, (int) rad ); // flush pixmap with transparent color		
		// Add contour around the removed pixel region
		for(int i = (int) center.x- (int) (rad*1.5);i< (int) center.x +(int) (rad*1.5);i++){
			for(int j = (int)center.y -(int) (rad*1.5);j<(int)center.y+(int) (rad*1.5);j++){
				float dist = (float) Math.sqrt((i- (int)center.x)*(i-(int)center.x) + (j-(int)center.y)*(j-(int)center.y));
				if( dist < rad + threshold) {
					if(pixmap.getPixel(i, j) != -256 ) {
						if(pixmap.getPixel(i-1, j) != -256 && pixmap.getPixel(i+1, j) != -256 &&pixmap.getPixel(i, j-1) != -256 &&pixmap.getPixel(i, j+1) != -256 ) {
							
						} else {
							pixmap.drawPixel(i, j, Color.rgba8888(Color.YELLOW));
							listOfArcPoints.add(new Vector2(i, j)); // record points on the Arc
							listOfAngles.add((float) Math.atan2(j - center.y, i - center.x)); // record angles for sorting
						}
					} 
				}
			}
		}
		
		// Bubble sorting the points according to angle
		boolean flag = true;
	    while ( flag )
	     { 
	            flag= false;    //set flag to false awaiting a possible swap
	            for( int j=0;  j < listOfAngles.size() -1;  j++ )
	            {
	                   if ( listOfAngles.get(j) < listOfAngles.get(j+1) )   // change to > for ascending sort
	                   {
	                          Collections.swap(listOfAngles, j, j+1);
	                          Collections.swap(listOfArcPoints, j, j+1);
	                          flag = true;              //shows a swap occurred  
	                  } 
	            } 
	      } 
	    // create several lists for those contour points on the remover circle
	    for(int i = 0;i<listOfArcPoints.size(); i++){
	    	if(i != listOfArcPoints.size()-1 ) {
		    	if(listOfArcPoints.get(i+1).dst(listOfArcPoints.get(i)) < thrshold) { 
		    		// if two points are close to each other, they belong to the same list
		    		listOfArcPointsBuff.add(listOfArcPoints.get(i));
		    	} else {
		    		listOfArcPointsBuff.add(listOfArcPoints.get(i));
		    		listOfLists.add(listOfArcPointsBuff);
		    		listOfArcPointsBuff = new ArrayList<Vector2>();
		    	}
	    	} else {
	    		if(listOfArcPoints.get(0).dst(listOfArcPoints.get(i)) < thrshold) {
		    		listOfArcPointsBuff.add(listOfArcPoints.get(i));
		    		listOfArcPointsBuff.add(listOfArcPoints.get(0));
		    		listOfLists.add(listOfArcPointsBuff);
		    		listOfArcPointsBuff = new ArrayList<Vector2>();
		    	} else {
		    		listOfArcPointsBuff.add(listOfArcPoints.get(i));
		    		listOfLists.add(listOfArcPointsBuff);
		    		listOfArcPointsBuff = new ArrayList<Vector2>();
		    	}
	    	}
	    }
	    // reduce the number of points in the list to create less edges
	    int counter =0;
	    for(Iterator<ArrayList<Vector2>> iter = listOfLists.iterator();iter.hasNext();){
	    	ArrayList<Vector2> list = (ArrayList<Vector2>) iter.next();
	    	int skipper = (int) (list.size()/seg) ;	    	
	    	if(list.size() >  seg +1) {
	    		counter = 0;
	    		int savSize = list.size();
		    	for(Iterator<Vector2> iter1 = list.iterator(); iter1.hasNext();){
		    		iter1.next();
		    		if(counter % skipper != 0) { 
		    			if(counter != savSize -1) {
		    				iter1.remove();
		    			}
		    		}
		    		counter ++;
		    	}
	    	}
	    }	    
	    // make edges around the remover to seal the removed geometry (not too good seal at this point)
		for(Iterator<ArrayList<Vector2>> iter = listOfLists.iterator();iter.hasNext();){
		    ArrayList<Vector2> list = (ArrayList<Vector2>) iter.next();    
		    Vector2 edgeStart = new Vector2();
			Vector2 edgeEnd = new Vector2();
			for(int i = 0 ; i < list.size()-1; i ++){
				edgeStart.set(list.get(i)) ;			
				edgeEnd.set(list.get(i+1));
				edgeStart.y = camera.viewportHeight -edgeStart.y;
				edgeEnd.y = camera.viewportHeight -edgeEnd.y;
				level.addTileEdge(edgeStart, edgeEnd, world);			
			}	
		}		
		tileNewTexture.draw(pixmap, 0, 0);
	}

	private void drawTiles() {
		for(Iterator<Fixture> iter = level.getTileArrayModifiable().iterator(); iter.hasNext();) {
	    	Fixture ft = iter.next();
	    	Tiles tile = (Tiles) ft.getBody().getUserData();
	    	Sprite grassSprite = new Sprite(grassTexture);
	    	grassSprite.setPosition(ft.getBody().getPosition().x - grassSprite.getWidth()/2.0f, ft.getBody().getPosition().y- grassSprite.getHeight()/2.0f );
	    	grassSprite.setScale(0.4f*GameScreen.SCALEVIEW);
	    	grassSprite.setRotation(tile.getRotAngle());
	    	grassSprite.draw(spriteBatch);
	    }
	}

	private void drawDd() {
		// TODO Auto-generated method stub
		for(Iterator<Fixture> iter = actor.getDdArray().iterator(); iter.hasNext();) {
	    	Fixture ft = iter.next();
	    	spriteBatch.draw(ddTexture, ft.getBody().getPosition().x - 10.0f*GameScreen.SCALEVIEW, ft.getBody().getPosition().y - 10.0f*GameScreen.SCALEVIEW, 20.0f*GameScreen.SCALEVIEW, 20.0f*GameScreen.SCALEVIEW);
	    }
	}

	private void drawHh() {
		// TODO Auto-generated method stub
		for(Iterator<Fixture> iter = actor.getHhArray().iterator(); iter.hasNext();) {
			Fixture ft = iter.next();
	    	spriteBatch.draw(hhTexture, ft.getBody().getPosition().x - 30.0f*GameScreen.SCALEVIEW, ft.getBody().getPosition().y - 30.0f*GameScreen.SCALEVIEW, 60.f*GameScreen.SCALEVIEW, 60.f*GameScreen.SCALEVIEW);
	    }
	}

	private void drawBackground(float width, float height) {
		// TODO Auto-generated method stub
		spriteBatch.draw(bgTexture, 0, 0, width, height);
	}

	public WorldRenderer(World world, Actors actor, Levels level){
		this.world = world;
		this.actor = actor;
		this.level = level;
		camera = new OrthographicCamera();  
        camera.viewportHeight = GameScreen.CAMERAVIEWHEIGHT;  
        camera.viewportWidth = GameScreen.CAMERAVIEWWIDTH;  
        camera.position.set(camera.viewportWidth * .5f, camera.viewportHeight * .5f, 0f);  
        camera.update();
        loadTextures();
        font = new BitmapFont();
        tileReset = true; // initialize the time-consuming drawing of the textures using pixel map
        debugRenderer = new Box2DDebugRenderer();  // default debugRenderer, should implement Sprites
        debugShapeRenderer = new ShapeRenderer();
	}
	
	private void loadTextures() {
		// TODO Auto-generated method stub
		ddTexture = new Texture(Gdx.files.internal("data/dandelion.png"));
		hhTexture = new Texture(Gdx.files.internal("data/hedgehog.png"));
		new Texture(Gdx.files.internal("data/tiledirt.png"));
		bgTexture = new Texture(Gdx.files.internal("data/background.png"));
		grassTexture = new Texture(Gdx.files.internal("data/grass.png"));
		pixmap = new Pixmap(Gdx.files.internal("data/tilepattern.png"));
		pixmapSav = new Pixmap(Gdx.files.internal("data/tilepattern.png")); // copy this pixel for later use
		pixmap.setBlending(Blending.None); // Blending is none, so new pixel can replace the old ones		
		tileNewTexture  = new Texture(pixmap);
	}
}
