package com.thewarpspace.ddbox2d;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.thewarpspace.ddbox2d.renderers.WorldRenderer;

public class Levels {
	int width = 0;
	int height = 0;	  
    static final float WORLD_TO_BOX=0.01f;  
    static final float BOX_WORLD_TO=100f;  
    ArrayList<Fixture> tileArrayModifiable = new ArrayList<Fixture>();
    ArrayList<Fixture> tileArrayPreset = new ArrayList<Fixture>();
    // the two tile arrays, to distinguish tiles defined by user realtime, which can be modified
    // and the predefined tiles, which should be modified using different trigger
	
	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}
	
	public ArrayList<Fixture> getTileArrayModifiable() {
		return tileArrayModifiable;
	}

	public ArrayList<Fixture> getTileArrayPreset() {
		return tileArrayPreset;
	}

	public void addTile(Vector2 position, World world){
		BodyDef tileBodyDef = new BodyDef();
		tileBodyDef.position.set(position);
        Body tileBody = world.createBody(tileBodyDef);
		CircleShape tileCircle = new CircleShape();
		tileCircle.setRadius(2.0f);
        FixtureDef fD = new FixtureDef();
        fD.shape = tileCircle;
        fD.density = 1.0f;  
        fD.friction = 0.5f;  
        fD.restitution = 0.1f;  
        Fixture tileFixture = tileBody.createFixture(fD);
        tileArrayModifiable.add(tileFixture);
        tileCircle.dispose();
	}

	public Levels(World world, WorldRenderer renderer){
		try {
			loadMap("C:/Users/Bo/Documents/GitHub/ddptbox2d/ddbox2d-android/assets/data/map2.txt", world, renderer);
			// I don't know how to make the relative reference to the assets correct, as they are stored under -android
			// I don't know how to make this function working with Gdx.files.internal, which should be the correct practice
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void loadMap(String string, World world, WorldRenderer renderer) throws IOException {
		// TODO Auto-generated method stub
		ArrayList lines= new ArrayList();	
		BufferedReader reader = new BufferedReader(new FileReader(string));
		Fixture tileFixture;
		
		while(true){
			String line = reader.readLine();
			if(line == null ){
				reader.close();
				break;
			}
			if(!line.startsWith("!")){
				lines.add(line);
				width= Math.max(width, line.length());
			}
		}		
		height = lines.size();
		System.out.println(width);
		System.out.println(height);
		for(int j = 0; j< 50;j++){
			String line = (String) lines.get(j);
			for ( int i=0; i< width ; i++) {
				if(i < line.length()) {
					char ch = line.charAt(i);
					if(Character.getNumericValue(ch) != -1) {
						if( Character.getNumericValue(ch) == 2){ // side walls
						   // the numbers allow making tiles of different properties
						   BodyDef tileBodyDef = new BodyDef();
						   tileBodyDef.position.set(new Vector2(((i+0.5f)*1.0f/width)*renderer.getCamera().viewportWidth,(height -j-0.5f)*1.0f/height*renderer.getCamera().viewportHeight));
				           // Not very clear about the box, world, pixel units conversion here.
						   // Also the order of y coordinates have to be flipped
						   Body tileBody = world.createBody(tileBodyDef);
				           PolygonShape tileBox = new PolygonShape();
				           tileBox.setAsBox(renderer.getCamera().viewportWidth*0.5f/width, renderer.getCamera().viewportHeight*0.5f/height);
				           tileFixture = tileBody.createFixture(tileBox, 0.0f);	
				           tileArrayPreset.add(tileFixture);
				           tileBox.dispose();
						} else if ( Character.getNumericValue(ch) == 3) { // interior objects
							// interior wall using circles -- might be more realistic when ddseeds move about them
							BodyDef tileBodyDef = new BodyDef();
							tileBodyDef.position.set(new Vector2(((i+0.5f)*1.0f/width)*renderer.getCamera().viewportWidth,(height -j-0.5f)*1.0f/height*renderer.getCamera().viewportHeight));
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
