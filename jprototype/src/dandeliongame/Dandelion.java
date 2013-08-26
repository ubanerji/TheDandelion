package dandeliongame;

import java.awt.Rectangle;



public class Dandelion {

	private int centerX, centerY, speedX, speedY;
	public Rectangle yellowRed = new Rectangle(0, 0, 0, 0); // if static, seems all instances share same value, wrong
	public Rectangle rect = new Rectangle(0, 0, 0, 0);
	
	public Dandelion(int x, int y) {		
		// TODO Auto-generated constructor stub
		centerX = x;
		centerY = y;
		speedX = 1;
		speedY = 0;
	}
	
	public void update(){
		centerX += speedX;
		centerY += speedY;
		yellowRed.setBounds(centerX-33, centerY-26 , 100, 100);
		rect.setBounds(centerX, centerY, 34, 48);
	}

	public int getCenterX() {
		return centerX;
	}

	public int getCenterY() {
		return centerY;
	}

	public int getSpeedX() {
		return speedX;
	}

	public int getSpeedY() {
		return speedY;
	}

	public void setCenterX(int centerX) {
		this.centerX = centerX;
	}

	public void setCenterY(int centerY) {
		this.centerY = centerY;
	}

	public void setSpeedX(int speedX) {
		this.speedX = speedX;
	}

	public void setSpeedY(int speedY) {
		this.speedY = speedY;
	}
	
}
