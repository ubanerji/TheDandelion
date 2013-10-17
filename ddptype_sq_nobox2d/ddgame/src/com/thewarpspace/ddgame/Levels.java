package com.thewarpspace.ddgame;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Levels {
	ArrayList<Tiles> tilearray = new ArrayList<Tiles>();
	int width = 0;
	int height = 0;
	
	public ArrayList<Tiles> getTilearray() {
		return tilearray;
	}
	
	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public Levels(){
		try {
			loadMap("C:/Users/Bo/Documents/GitHub/ddptype/ddgame-android/assets/data/map2.txt");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void loadMap(String string) throws IOException {
		// TODO Auto-generated method stub
		ArrayList lines= new ArrayList();		
		Rectangle rect = new Rectangle();
		BufferedReader reader = new BufferedReader(new FileReader(string));
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
		System.out.println(height);
		for(int j = 0; j< 50;j++){
			String line = (String) lines.get(j);
			for ( int i=0; i< width ; i++) {
				if(i < line.length()) {
					char ch = line.charAt(i);
					if(Character.getNumericValue(ch) != -1) {
						Tiles t = new Tiles(new Vector2(i,height -j-2), Character.getNumericValue(ch));
						tilearray.add(t);
					}
				}
			}
		}		
	}
}
