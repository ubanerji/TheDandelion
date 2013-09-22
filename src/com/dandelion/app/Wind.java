package com.dandelion.app;

public class Wind {
	private int speedX, speedY;

	public Wind(int speedX, int speedY) {
		this.speedX = speedX;
		this.speedY = speedY;
	}

	private void update(){}

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
}
