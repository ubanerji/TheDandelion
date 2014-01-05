package com.thewarpspace.ddbox2d;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.thewarpspace.ddbox2d.renderers.WorldRenderer;
import com.thewarpspace.ddbox2d.screens.GameScreen;

public class Levels {
	int width = 0;
	int height = 0;
	public static float tileWidth;
	public static float tileHeight;
	static final float WORLD_TO_BOX = 0.01f;
	static final float BOX_WORLD_TO = 100f;
	ArrayList<Fixture> tileArrayModifiable = new ArrayList<Fixture>();
	ArrayList<Fixture> tileArrayPreset = new ArrayList<Fixture>();
	ArrayList<Fixture> tileArrayPresetModifiable = new ArrayList<Fixture>();
	ArrayList<Fixture> tileEdgesPresetModifiable = new ArrayList<Fixture>();
	ArrayList<Fixture> tileEdgesBuffered = new ArrayList<Fixture>();
	Array<Float> grassRotArray = new Array<Float>();

	// the two tile arrays, to distinguish tiles defined by user realtime, which
	// can be modified
	// and the predefined tiles, which should be modified using different
	// trigger
	private Pixmap tiles;
	private byte[] walls;
	private float[] extent;
	private byte[] wallsBuffer;
	Random rand = new Random();

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public static float getTileWidth() {
		return tileWidth;
	}

	public static void setTileWidth(float tileWidth) {
		Levels.tileWidth = tileWidth;
	}

	public static float getTileHeight() {
		return tileHeight;
	}

	public static void setTileHeight(float tileHeight) {
		Levels.tileHeight = tileHeight;
	}

	public ArrayList<Fixture> getTileArrayModifiable() {
		return tileArrayModifiable;
	}

	public ArrayList<Fixture> getTileArrayPreset() {
		return tileArrayPreset;
	}

	public ArrayList<Fixture> getTileArrayPresetModifiable() {
		return tileArrayPresetModifiable;
	}

	public ArrayList<Fixture> getTileEdgesPresetModifiable() {
		return tileEdgesPresetModifiable;
	}

	public ArrayList<Fixture> getTileEdgesBuffered() {
		return tileEdgesBuffered;
	}

	public Array<Float> getGrassRotArray() {
		return grassRotArray;
	}

	public byte[] getWalls() {
		return walls;
	}

	public byte[] getWallsBuffer() {
		return wallsBuffer;
	}

	public float[] getExtent() {
		return extent;
	}

	public void addTile(Vector2 position, World world, Float rot, float f) {
		Tiles tile = new Tiles(position, world, rot, f);
		tileArrayPresetModifiable.add(tile.getTileFixture());
	}

	public void addTileEdge(Vector2 edgeStart, Vector2 edgeEnd, World world) {
		TileEdges tileEdge = new TileEdges(edgeStart, edgeEnd, world);
		tileEdgesPresetModifiable.add(tileEdge.getEdgeFixture());
	}

	public void addTileEdgeBuffered(Vector2 edgeStart, Vector2 edgeEnd,
			World world) {
		TileEdges tileEdge = new TileEdges(edgeStart, edgeEnd, world);
		tileEdgesBuffered.add(tileEdge.getEdgeFixture());
	}

	public void addGrass(Vector2 position, World world, Float rot, int rad) {
		Tiles tile = new Tiles(position, world, rot, rad);
		tileArrayModifiable.add(tile.getTileFixture());
	}

	public Levels(World world, Vector2 ddSpawnPos, Vector2 hhSpawnPos) {
		try {
			// loadMap("data/map2.txt", world);
			loadPixelMap("data/levelmap4.png", world, ddSpawnPos, hhSpawnPos);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// In this function, positions of dd and hh are set.
	private void loadPixelMap(String filename, World world, Vector2 ddSpawnPos,
			Vector2 hhSpawnPos) throws IOException {
		tiles = new Pixmap(Gdx.files.internal(filename));
		width = tiles.getWidth();
		height = tiles.getHeight();
		int countd = 0;
		int counth = 0;
		tileWidth = (float) (GameScreen.CAMERAVIEWWIDTH / (1.0 * width));
		tileHeight = (float) (GameScreen.CAMERAVIEWHEIGHT / (1.0 * height));
		walls = new byte[width * height];
		
		// buffer for boundary of entire array of walls
		wallsBuffer = new byte[(width + 2) * (height + 2)]; 
		for (int i = 0; i < (width + 2) * (height + 2); i++) {
			wallsBuffer[i] = -1; // flush buffer map
		}
		
		extent = new float[width * height];
		Fixture tileFixture;
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				int col = (tiles.getPixel(x, y) & 0xffffff00) >>> 8;
				byte wall = -1;
				if (col == 0xffffff) {
					wall = -1; // white represents empty
				} else if (col == 0x000000) {
					wall = 2; // black represents unmodifiable walls
				} else if (col == 0xffff00) {
					wall = 3; // yellow represents interior walls
				} else if (col == 0xff0000) {
					wall = 0; // red, spawn position for dandelion
					countd++;
					if (countd > 1) {
						System.out.println("Warning: More than one dd spawn point! Will only take first one");
					} else {
						ddSpawnPos.set(new Vector2(x * tileWidth, (height - y) * tileHeight));
					}
				} else if (col == 0x0000ff) {
					wall = 1; // blue, spawn position for hedgehog
					counth++;
					if (counth > 1) {
						System.out.println("Warning: More than one hh spawn point! Will only take first one");
					} else
						hhSpawnPos.set(new Vector2(x * tileWidth, (height - y) * tileHeight));
				}
				
				wallsBuffer[(x + 1) + (y + 1) * (width + 2)] = wall;
				extent[x + y * width] = 20.0f + rand.nextFloat() * 4.0f; 
			}
		}
		// find and mark only the boundary points to be used for adding bodies
		for (int j = 1; j < height + 1; j++) {
			for (int i = 1; i < width + 1; i++) {
				walls[i - 1 + (j - 1) * width] = -1;
				for (byte itype = 0; itype < 10; itype++) { 
					// loop over all possible types of terrain
					if (wallsBuffer[i + j * (width + 2)] == itype) {
						if (wallsBuffer[i + 1 + j * (width + 2)] != itype
								|| wallsBuffer[i - 1 + j * (width + 2)] != itype
								|| wallsBuffer[i + (j + 1) * (width + 2)] != itype
								|| wallsBuffer[i + (j - 1) * (width + 2)] != itype) {
							walls[i - 1 + (j - 1) * width] = itype;
						}
					}
				}
			}
		}
		
		for (int j = 0; j < height; j++) {
			for (int i = 0; i < width; i++) {
				if (walls[i + j * width] != -1) {
					if (walls[i + j * width] == 2) { // walls non-modifiable
						// the numbers allow making tiles of different
						// properties
						// BodyDef tileBodyDef = new BodyDef();
						// tileBodyDef.position.set(new
						// Vector2((i+0.5f)*tileWidth,(height
						// -j-0.5f)*tileHeight));
						// // Not very clear about the box, world, pixel units
						// conversion here.
						// // Also the order of y coordinates have to be flipped
						// Body tileBody = world.createBody(tileBodyDef);
						// PolygonShape tileBox = new PolygonShape();
						// tileBox.setAsBox(tileWidth/2.0f, tileHeight/2.0f);
						// tileFixture = tileBody.createFixture(tileBox, 0.0f);
						// tileArrayPreset.add(tileFixture);
						// tileBox.dispose();
					} else if (walls[i + j * width] == 3) { // walls modifiable
						// interior wall using circles -- might be more
						// realistic when ddseeds move about them
						// BodyDef tileBodyDef = new BodyDef();
						// tileBodyDef.position.set(new
						// Vector2((i+0.5f)*tileWidth,(height
						// -j-0.5f)*tileHeight));
						// Body tileBody = world.createBody(tileBodyDef);
						// CircleShape tileCircle = new CircleShape();
						// tileCircle.setRadius(2.0f);
						// FixtureDef fD = new FixtureDef();
						// fD.shape = tileCircle;
						// fD.density = 1.0f;
						// fD.friction = 0.5f;
						// fD.restitution = 0.1f;
						// tileFixture = tileBody.createFixture(fD);
						// tileArrayPresetModifiable.add(tileFixture);
						// tileCircle.dispose();
					}
				}
			}
		}
	}

	private void loadMap(String filename, World world) throws IOException {
		// TODO Auto-generated method stub
		ArrayList lines = new ArrayList();
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				Gdx.files.internal(filename).read()));
		Fixture tileFixture;

		while (true) {
			String line = reader.readLine();
			if (line == null) {
				reader.close();
				break;
			}
			if (!line.startsWith("!")) {
				lines.add(line);
				width = Math.max(width, line.length());
			}
		}
		
		height = lines.size();
		tileWidth = (float) (GameScreen.CAMERAVIEWWIDTH / (1.0 * width));
		tileHeight = (float) (GameScreen.CAMERAVIEWHEIGHT / (1.0 * height));
		System.out.println(width);
		System.out.println(height);
		for (int j = 0; j < height; j++) {
			String line = (String) lines.get(j);
			for (int i = 0; i < width; i++) {
				if (i < line.length()) {
					char ch = line.charAt(i);
					if (Character.getNumericValue(ch) != -1) {
						if (Character.getNumericValue(ch) == 2) { 
							// side walls the numbers allow making tiles of different properties
							BodyDef tileBodyDef = new BodyDef();
							tileBodyDef.position.set(new Vector2((i + 0.5f) * tileWidth, 
													 (height - j - 0.5f) * tileHeight));
							// Not very clear about the box, world, pixel units conversion here.
							// Also the order of y coordinates have to be flipped
							Body tileBody = world.createBody(tileBodyDef);
							PolygonShape tileBox = new PolygonShape();
							tileBox.setAsBox(tileWidth / 2.0f,
											 tileHeight / 2.0f);
							tileFixture = tileBody.createFixture(tileBox, 0.0f);
							tileArrayPreset.add(tileFixture);
							tileBox.dispose();
						} else if (Character.getNumericValue(ch) == 3) { 
							// interior objects interior wall using circles 
							// -- might be more realistic when ddseeds move about them
							BodyDef tileBodyDef = new BodyDef();
							tileBodyDef.position.set(new Vector2((i + 0.5f) * tileWidth, 
													 (height - j - 0.5f) * tileHeight));
							Body tileBody = world.createBody(tileBodyDef);
							CircleShape tileCircle = new CircleShape();
							tileCircle.setRadius(2.0f);
							FixtureDef fD = new FixtureDef();
							fD.shape = tileCircle;
							fD.density = 1.0f;
							fD.friction = 0.5f;
							fD.restitution = 0.1f;
							tileFixture = tileBody.createFixture(fD);
							tileArrayPreset.add(tileFixture);
							tileCircle.dispose();
						}
					}
				}
			}
		}
	}

}
