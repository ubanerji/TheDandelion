package com.dandelion.app;

import android.graphics.Rect;
import android.util.Log;

import com.dandelion.app.Framework.Image;

import java.util.ArrayList;

public class Tile {
	private int tileX, tileY, type;
	private Image tileImage;
	private Rect r;
	private Dandelion dd;
	private ArrayList<Dandelion> ddarray;

	public Tile(int tileX, int tileY, int type) {
		this.tileX = tileX*40;
		this.tileY = tileY*40;
		this.type = type;
		r = new Rect();

		if(type == 3){
            Log.e("Dandelion", "Creating tile type 3");
			tileImage = GameScreen.tilegrassTop;
		} else if(type == 2){
            Log.e ("Dandelion", "Creating tile type 2");
			tileImage = GameScreen.tilegrassBot;
		} else {
            Log.e ("Dandelion", "Creating tile type none " + type);
			this.type = 0;
		}
	}

	public void update(){
		r.set(tileX, tileY, 40, 40);
		ddarray = GameScreen.getDdarray();
		for(int i=0 ; i< ddarray.size(); i++){
			dd = ddarray.get(i);
			if(r.setIntersect(r, dd.yellowRed) && type != 0){
				if(r.setIntersect(r, dd.rect)){
					checkCollision(dd);
				}
			}
		}
	}

public void checkCollision(Dandelion ddcol){

		if(ddcol.rect.left +34 > tileX && ddcol.rect.left +34 < tileX+40){
			ddcol.setSpeedX(0);
			ddcol.setCenterX(ddcol.getCenterX() - 2);
		} else if(ddcol.rect.top+48 > tileY && ddcol.rect.top +48 < tileY+40){
			ddcol.setSpeedY(0);
			ddcol.setCenterY(ddcol.getCenterY() - 2);
		}

		if(ddcol.rect.left > tileX && ddcol.rect.left  < tileX+40){
			ddcol.setSpeedX(0);
			ddcol.setCenterX(ddcol.getCenterX() + 2);
		} else if(ddcol.rect.top > tileY && ddcol.rect.top  < tileY+40){
			ddcol.setSpeedY(0);
			ddcol.setCenterY(ddcol.getCenterY()  + 2);
		}
	}

	public int getTileX() {
		return tileX;
	}

	public int getTileY() {
		return tileY;
	}

	public int getType() {
		return type;
	}

	public Image getTileImage() {
		return tileImage;
	}

	public void setTileX(int tileX) {
		this.tileX = tileX;
	}

	public void setTileY(int tileY) {
		this.tileY = tileY;
	}

	public void setType(int type) {
		this.type = type;
	}

	public void setTileImage(Image tileImage) {
		this.tileImage = tileImage;
	}
}
