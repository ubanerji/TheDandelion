package dandeliongame;

import java.applet.Applet;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;

import javax.imageio.ImageIO;

public class StartingClass extends Applet implements Runnable, KeyListener {
	
	private static Background bg1, bg2;	
	private static int dStock;
	private URL base;
	private Image image, background, dandelion;
	public static Image tiledirt, tilegrassTop, tilegrassBot;
	private BufferedImage dandelion1;
	private Graphics2D second;
	public static ArrayList<Dandelion> ddarray = new ArrayList<Dandelion>();
	private ArrayList<Tile> tilearray = new ArrayList<Tile>();
	private int elapsedTime;
	private Random r = new Random();
	private Wind bgWind;
	private double rotationRequired;
	private double locationX;
	private double locationY;
	AffineTransform tx;
	
	@Override
	public void init(){
		//
		setSize(800,400);
		setBackground(Color.GRAY);
		setFocusable(true);
		addKeyListener(this);
		base = getDocumentBase();
		background = getImage(base,"data/background.png");
		dandelion = getImage(base,"data/dandelion.png");
		tiledirt = getImage(base,"data/tiledirt.png");
		tilegrassTop = getImage(base, "data/tilegrasstop.png");
		tilegrassBot = getImage(base, "data/tilegrassbot.png");
		try {
			dandelion1 = ImageIO.read(new File("data/dandelion.png"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("no file");
		}		
	}
	
	public static ArrayList<Dandelion> getDdarray() {
		return ddarray;
	}

	public static void setDdarray(ArrayList<Dandelion> ddarray) {
		StartingClass.ddarray = ddarray;
	}

	// @Override
	public void paint(Graphics2D g){
		g.drawImage(background, bg1.getBgX(), bg1.getBgY(), this);
		g.drawImage(background, bg2.getBgX(), bg2.getBgY(), this);	
		paintTiles(g);
		for(int i =0; i< ddarray.size(); i++){
			Dandelion dd = ddarray.get(i);
			if(elapsedTime % 10 == 0){
				rotationRequired = Math.toRadians(r.nextInt(40)-20);
			}
			locationX = dandelion.getWidth(null)/2;
			locationY = dandelion.getHeight(null)/2;
			tx = AffineTransform.getRotateInstance(rotationRequired, locationX, locationY);
			AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_BILINEAR);
			// g.drawImage(dandelion, dd.getCenterX(), dd.getCenterY(), this);
			g.drawImage(op.filter(dandelion1, null), dd.getCenterX(), dd.getCenterY(), null);
			// g.drawRect((int)dd.rect.getX(), (int)dd.rect.getY(), (int)dd.rect.getWidth(), (int)dd.rect.getHeight());
			// g.drawRect((int)dd.yellowRed.getX(), (int)dd.yellowRed.getY(), (int)dd.yellowRed.getWidth(), (int)dd.yellowRed.getHeight());
		}		
	}
	
	@Override
	public void start(){
		//
		bg1 = new Background(0,0);
		bg2 = new Background(2160,0);
		bg1.setSpeedX(-1);
		bg2.setSpeedX(-1);
		dStock = 5; // initialize # of dandelions
		elapsedTime = 0;
		bgWind = new Wind(2,1);
		try{
			loadMap("data/map1.txt");
		} catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
		Thread thread = new Thread(this);
		thread.start();
	}
	
	@Override
	public void update(Graphics g){
		//
		if(image == null){
			image = createImage(this.getWidth(), this.getHeight());
			second = (Graphics2D) image.getGraphics();
		}
		paint(second);
		g.drawImage(image, 0, 0, this);
	}
		
	@Override
	public void run(){
		while(true){			
			bg1.update();
			bg2.update();
			if(dStock> 0){				
				if((elapsedTime % 100 == 0)){
					Dandelion dd = new Dandelion(200, 200);				
					ddarray.add(dd);
					dStock -= 1;
				}				
			}			
			for(int i=0;i< ddarray.size(); i++){
				Dandelion dd = ddarray.get(i);
				if(elapsedTime % 4 ==0){
					dd.setSpeedX(r.nextInt(6)-2+ bgWind.getSpeedX());
					dd.setSpeedY(r.nextInt(4)-2+ bgWind.getSpeedY());					
					dd.update();
				}
			}
			if(elapsedTime % 1 == 0){
				updateTiles();
			}
			if(bg1.getBgX() < -2160){
				bg1.setBgX(2160);
			}
			if(bg2.getBgX() < -2160){
				bg2.setBgX(2160);
			}
			// System.out.println(bg1.getBgX());
			// System.out.println(bg2.getBgX());
			
			repaint();
			try {
				Thread.sleep(17);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			elapsedTime += 1;
		}
	}

	@Override
	public void keyPressed(KeyEvent arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getKeyCode()){
		case KeyEvent.VK_UP:
			System.out.println("test");
			break;
		case KeyEvent.VK_SPACE:
			System.out.println("change wind");
			bgWind.setSpeedX(r.nextInt(3));
			bgWind.setSpeedY(r.nextInt(3));
			break;
		}
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	
	private void updateTiles() {
		for (int i = 0; i < tilearray.size(); i++) {
			Tile t = (Tile) tilearray.get(i);
			t.update();
		}
	}
	
	private void paintTiles(Graphics2D g) {
		for (int i = 0; i < tilearray.size(); i++) {
			Tile t = (Tile) tilearray.get(i);
			g.drawImage(t.getTileImage(), t.getTileX(), t.getTileY(), this);
		}
	}
	
	private void loadMap(String filename) throws IOException {
		// TODO Auto-generated method stub
		ArrayList lines= new ArrayList();
		int width = 0;
		int height = 0;
		
		BufferedReader reader = new BufferedReader(new FileReader(filename));
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
		for(int j = 0; j< 10;j++){
			String line = (String) lines.get(j);
			for ( int i=0; i< width ; i++) {
				if(i < line.length()) {
					char ch = line.charAt(i);
					Tile t = new Tile(i,j,Character.getNumericValue(ch));
					tilearray.add(t);
				}
			}
		}
		
		
	}
	
}
