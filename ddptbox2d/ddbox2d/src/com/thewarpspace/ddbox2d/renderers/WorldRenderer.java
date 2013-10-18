package com.thewarpspace.ddbox2d.renderers;

import java.util.Iterator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.World;
import com.thewarpspace.ddbox2d.Actors;
import com.thewarpspace.ddbox2d.Levels;

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
	private Actors actor;
	private Levels level;
	boolean debug = false;
	private BitmapFont font;
	private String textToDisplay;
    
    public boolean isDebug() {
		return debug;
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	public OrthographicCamera getCamera() {
		return camera;
	}

	public void render(Integer points){
    	Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);  
		spriteBatch.begin();
		drawBackground(camera.viewportWidth, camera.viewportHeight);
		drawHh();
		drawDd();		
		drawTiles();
		textToDisplay = "Player Score: " + points.toString() +" ; Gravity: " + world.getGravity().toString();
		font.draw(spriteBatch, textToDisplay, 20.f, 20.0f); 
		spriteBatch.end();
		if(debug) {
			debugRenderer.render(world, camera.combined);  
		}
        world.step(BOX_STEP, BOX_VELOCITY_ITERATIONS, BOX_POSITION_ITERATIONS);  
        world.clearForces(); // As told in manual, this is needed
    }
    
	private void drawHh() {
		// TODO Auto-generated method stub
		for(Iterator<Fixture> iter = actor.getHhArray().iterator(); iter.hasNext();) {
			Fixture ft = iter.next();
	    	spriteBatch.draw(hhTexture, ft.getBody().getPosition().x - 30.0f, ft.getBody().getPosition().y - 30.0f, 60.0f, 60.0f);
	    }
	}

	private void drawTiles() {
		// TODO Auto-generated method stub
		for(Iterator<Fixture> iter = level.getTileArrayPreset().iterator(); iter.hasNext();) {
	    	Fixture ft = iter.next();
	    	spriteBatch.draw(tileTexture, ft.getBody().getPosition().x - level.getTileWidth()/2.0f, ft.getBody().getPosition().y - level.getTileHeight()/2.0f, level.tileWidth, level.tileHeight);
	    }
		for(Iterator<Fixture> iter = level.getTileArrayModifiable().iterator(); iter.hasNext();) {
	    	Fixture ft = iter.next();
	    	spriteBatch.draw(tileTexture, ft.getBody().getPosition().x- level.getTileWidth()/2.0f, ft.getBody().getPosition().y - level.getTileHeight()/2.0f, level.tileWidth, level.tileHeight);
	    }
	}

	private void drawDd() {
		// TODO Auto-generated method stub
		for(Iterator<Fixture> iter = actor.getDdArray().iterator(); iter.hasNext();) {
	    	Fixture ft = iter.next();
	    	spriteBatch.draw(ddTexture, ft.getBody().getPosition().x - 10.0f, ft.getBody().getPosition().y - 10.0f, 20.0f, 20.0f);
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
        camera.viewportHeight = 320;  
        camera.viewportWidth = 480;  
        camera.position.set(camera.viewportWidth * .5f, camera.viewportHeight * .5f, 0f);  
        camera.update();  
        loadTextures();
        font = new BitmapFont();
        debugRenderer = new Box2DDebugRenderer();  // default debugRenderer, should implement Sprites
	}
	
	private void loadTextures() {
		// TODO Auto-generated method stub
		ddTexture = new Texture(Gdx.files.internal("data/dandelion.png"));
		hhTexture = new Texture(Gdx.files.internal("data/hedgehog.png"));
		tileTexture = new Texture(Gdx.files.internal("data/tiledirt.png"));
		bgTexture = new Texture(Gdx.files.internal("data/background.png"));
	}
}
