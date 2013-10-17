package com.thewarpspace.ddgame.renderers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Rectangle;
import com.thewarpspace.ddgame.Ddseeds;
import com.thewarpspace.ddgame.Tiles;
import com.thewarpspace.ddgame.Worlds;

public class WorldRenderer {
	
	Worlds world = new Worlds();
	OrthographicCamera cam = new OrthographicCamera();
	SpriteBatch spriteBatch = new SpriteBatch();
	boolean debug = false;
	private Texture ddTexture;
	private Texture tileTexture;
	private static final float CAMERA_WIDTH = 100f;
	private static final float CAMERA_HEIGHT = 50f;
	private int width;
	private int height;
	private float ppuX;
	private float ppuY;
	
	ShapeRenderer debugRenderer = new ShapeRenderer();
	private Texture bgTexture;
	
		public boolean isDebug() {
		return debug;
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	public void setSize(int w, int h){
		this.width = w;
		this.height = h;
		ppuX = (float)width / CAMERA_WIDTH;
		ppuY = (float)height / CAMERA_HEIGHT;
	}
	
	public WorldRenderer(Worlds world, boolean debug){
		this.world = world;
		this.cam = new OrthographicCamera(100, 50);
		this.cam.position.set(50.0f, 25.0f, 0);
		this.cam.update();
		this.debug = debug;
		spriteBatch = new SpriteBatch();
		loadTextures();
	}

	private void loadTextures() {
		// TODO Auto-generated method stub
		ddTexture = new Texture(Gdx.files.internal("data/dandelion.png"));
		tileTexture = new Texture(Gdx.files.internal("data/tiledirt.png"));
		bgTexture = new Texture(Gdx.files.internal("data/background.png"));
	}
	
	public void render(){
		spriteBatch.begin();
		drawBackground();
		drawDd();		
		drawTiles();
		spriteBatch.end();
		if(debug){
			drawCollisionTiles();
			drawDebug();
		}
	}

	private void drawBackground() {
		// TODO Auto-generated method stub
		spriteBatch.draw(bgTexture, 0, 0, width, height);
	}

	public void drawDebug(){
		debugRenderer.setProjectionMatrix(cam.combined);
		debugRenderer.begin(ShapeType.Rectangle);
		for(Tiles tile : world.getLevel().getTilearray()){
			Rectangle rect = tile.getBounds();
			debugRenderer.setColor(new Color(1,0,0,1));
			debugRenderer.rect(rect.x, rect.y, rect.width, rect.height);			
		}
		for(Ddseeds dd : world.getDdarray()){
			Rectangle rect = dd.getBounds();
			debugRenderer.setColor(new Color(0,1,0,1));
			debugRenderer.rect(rect.x, rect.y, rect.width, rect.height);
		}	
		debugRenderer.end();
	}

	private void drawCollisionTiles() {
		// TODO Auto-generated method stub
		debugRenderer.setProjectionMatrix(cam.combined);
		debugRenderer.begin(ShapeType.FilledRectangle);
		debugRenderer.setColor(new Color(1,1,1,1));
		for(Ddseeds dd : world.getDdarray()){
			for(Rectangle rect : dd.getCollisionRects()){
				debugRenderer.filledRect(rect.x, rect.y, rect.width, rect.height);
			}
		}
		debugRenderer.end();
	}

	private void drawTiles() {
		// TODO Auto-generated method stub
		for(Tiles tile : world.getLevel().getTilearray()){
			spriteBatch.draw(tileTexture, tile.getPosition().x *ppuX, tile.getPosition().y *ppuY, tile.SIZE* ppuX, tile.SIZE*ppuY);
		}
	}

	private void drawDd() {
		// TODO Auto-generated method stub
		for(Ddseeds dd : world.getDdarray()){
			spriteBatch.draw(ddTexture, dd.getPosition().x *ppuX, dd.getPosition().y *ppuY, dd.SIZE* ppuX, dd.SIZE*ppuY);
		}
	}
	
	
}
