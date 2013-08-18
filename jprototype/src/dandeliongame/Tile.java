package dandeliongame;

import java.awt.Image;
import java.awt.Rectangle;
import java.util.ArrayList;


public class Tile {
	private int tileX, tileY, type;
	private Image tileImage;
	private Rectangle r;
	private Dandelion dd;
	private ArrayList<Dandelion> ddarray;

	public Tile(int tileX, int tileY, int type) {
		this.tileX = tileX*40;
		this.tileY = tileY*40;
		this.type = type;
		r = new Rectangle();
		
		if(type == 3){
			tileImage = StartingClass.tilegrassTop;
		} else if(type == 2){
			tileImage = StartingClass.tilegrassBot;
		} else {
			this.type = 0;
		}
	}
	
	public void update(){
		//
		r.setBounds(tileX, tileY, 40, 40);
		ddarray=StartingClass.getDdarray();
		for(int i=0 ; i< ddarray.size(); i++){
			dd = ddarray.get(i);
			if(r.intersects(dd.yellowRed) && type != 0){
				if(r.intersects(dd.rect)){
					checkCollision(dd);
				}
			}
		}
	}
	
public void checkCollision(Dandelion ddcol){
		
		if(ddcol.rect.getX()+34 > tileX && ddcol.rect.getX() +34 < tileX+40){
			ddcol.setSpeedX(0);
			ddcol.setCenterX(ddcol.getCenterX() - 2);
		} else if(ddcol.rect.getY()+48 > tileY && ddcol.rect.getY() +48 < tileY+40){
			ddcol.setSpeedY(0);
			ddcol.setCenterY(ddcol.getCenterY() - 2);
		} 
		
		if(ddcol.rect.getX() > tileX && ddcol.rect.getX()  < tileX+40){
			ddcol.setSpeedX(0);
			ddcol.setCenterX(ddcol.getCenterX() + 2);
		} else if(ddcol.rect.getY() > tileY && ddcol.rect.getY()  < tileY+40){
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
