package dandeliongame;

import java.awt.Image;

public class Background {
	private int bgX, bgY, speedX, speedY;	

	public Background(int bgX, int bgY){		
		this.bgX = bgX;
		this.bgY = bgY;
		this.speedX = -3;
	}
	
	public int getBgX() {
		return bgX;
	}

	public int getBgY() {
		return bgY;
	}

	public void setBgX(int bgX) {
		this.bgX = bgX;
	}

	public void setBgY(int bgY) {
		this.bgY = bgY;
	}

	public int getSpeedX() {
		return speedX;
	}

	public int getSpeedY() {
		return speedY;
	}

	public void setSpeedX(int speedX) {
		this.speedX = speedX;
	}

	public void setSpeedY(int speedY) {
		this.speedY = speedY;
	}
	
	public void update(){
		//
		this.bgX += speedX;
	}
}
