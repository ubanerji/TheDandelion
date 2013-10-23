package com.thewarpspace.ddbox2d.renderers;

import java.awt.Point;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Blending;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.thewarpspace.ddbox2d.Actors;
import com.thewarpspace.ddbox2d.Levels;
import com.thewarpspace.ddbox2d.Tiles;
import com.thewarpspace.ddbox2d.screens.GameScreen;

public class WorldRenderer {
	World world;
	Box2DDebugRenderer debugRenderer;  
    OrthographicCamera camera;
	private Texture ddTexture;
	private Texture tileTexture;
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
	private Texture tilePatternTexture;
	private Pixmap pixmap;
	private Texture tileNewTexture;
	private Pixmap pixmapSav;
	private boolean tileReset;
    
    public boolean isDebug() {
		return debug;
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
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
		gl.glClearColor(1, 0, 0, 1);
    	gl.glClear(GL10.GL_COLOR_BUFFER_BIT);  
    	gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    	camera.update();
    	camera.apply(gl);
    	spriteBatch.setProjectionMatrix(camera.combined);    	
		spriteBatch.begin();		
		drawBackground(camera.viewportWidth, camera.viewportHeight);
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
			byte[] walls = level.getWalls();
			float[] extent = level.getExtent(); // stores the random extention from the tile point for pixel region
			for(int i = 0;i< level.getWidth();i++){
				for(int j = 0;j<level.getHeight();j++){
					if(walls[i + j*level.getWidth()] ==3 || walls[i + j*level.getWidth()] ==2){
						// if the present point is a tile, render its surrounding
						testC = new Vector2((i+0.5f)*level.getTileWidth(), (j+0.5f)*level.getTileHeight());
						for(int ix = -8; ix< 8; ix++) { // scan the square region around the tile point to find points within a radius
							for(int iy = -8; iy<8; iy++){
								testS = new Vector2((i+0.5f)*level.getTileWidth()+ix, (j+0.5f)*level.getTileHeight()+iy);
								if(testC.dst2(testS) < 20f + extent[i + j*level.getWidth()] ){
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
			tileReset = false;
		} 
		spriteBatch.draw(tileNewTexture, 0, camera.viewportHeight -pixmap.getHeight());		
	}
	
	public void removeTileRender(Vector2 vector2){ // removes tile texture only, will implement remove underlying bodies as well
		pixmap.setColor(-256);
		pixmap.fillCircle((int) vector2.x, (int)(camera.viewportHeight - vector2.y), 5); // flush pixmap with transparent color		
		// Add contour around the removed pixel region
		for(int i = (int) vector2.x- 10;i< (int) vector2.x +10;i++){
			for(int j = (int)(camera.viewportHeight - vector2.y)-10;j<(int)(camera.viewportHeight - vector2.y)+10;j++){
				if(pixmap.getPixel(i, j) != -256 && pixmap.getPixel(i, j) != Color.rgba8888(Color.YELLOW)) {
					if(pixmap.getPixel(i-1, j) != -256 && pixmap.getPixel(i+1, j) != -256 &&pixmap.getPixel(i, j-1) != -256 &&pixmap.getPixel(i, j+1) != -256 ) {
						
					} else {
						pixmap.drawPixel(i, j, Color.rgba8888(Color.YELLOW));
					}
				}					
			}
		}
		tileNewTexture.draw(pixmap, 0, 0);
	}

	private void drawTiles() {
		// TODO Auto-generated method stub
//		for(Iterator<Fixture> iter = level.getTileArrayPreset().iterator(); iter.hasNext();) {
//	    	Fixture ft = iter.next();
//	    	spriteBatch.draw(tileTexture, ft.getBody().getPosition().x - level.getTileWidth()/2.0f, ft.getBody().getPosition().y - level.getTileHeight()/2.0f, level.tileWidth, level.tileHeight);
//	    }
		for(Iterator<Fixture> iter = level.getTileArrayModifiable().iterator(); iter.hasNext();) {
	    	Fixture ft = iter.next();
	    	Tiles tile = (Tiles) ft.getBody().getUserData();
	    	Sprite grassSprite = new Sprite(grassTexture);
	    	grassSprite.setPosition(ft.getBody().getPosition().x - grassSprite.getWidth()/2.0f, ft.getBody().getPosition().y- grassSprite.getHeight()/2.0f );
	    	grassSprite.setScale(0.4f*GameScreen.SCALEVIEW);
	    	grassSprite.setRotation(tile.getRotAngle());
	    	grassSprite.draw(spriteBatch);
//	    	spriteBatch.draw(grassTexture, ft.getBody().getPosition().x- level.getTileWidth()/2.0f, ft.getBody().getPosition().y - level.getTileHeight()/2.0f, level.tileWidth*4.0f, level.tileHeight*4.0f);
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
	}
	
	private void loadTextures() {
		// TODO Auto-generated method stub
		ddTexture = new Texture(Gdx.files.internal("data/dandelion.png"));
		hhTexture = new Texture(Gdx.files.internal("data/hedgehog.png"));
		tileTexture = new Texture(Gdx.files.internal("data/tiledirt.png"));
		bgTexture = new Texture(Gdx.files.internal("data/background.png"));
		grassTexture = new Texture(Gdx.files.internal("data/grass.png"));
		pixmap = new Pixmap(Gdx.files.internal("data/tilepattern.png"));
		pixmapSav = new Pixmap(Gdx.files.internal("data/tilepattern.png")); // copy this pixel for later use
		pixmap.setBlending(Blending.None); // Blending is none, so new pixel can replace the old ones
		tileNewTexture  = new Texture(pixmap);
	}
}
