package com.dandelion.app;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.Log;

import com.dandelion.app.Framework.Game;
import com.dandelion.app.Framework.Graphics;
import com.dandelion.app.Framework.Image;
import com.dandelion.app.Framework.Input.TouchEvent;
import com.dandelion.app.Framework.Screen;
import com.dandelion.app.Generic.AndroidGame;
import com.dandelion.app.Generic.AndroidImage;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by ubanerji on 8/11/13.
 */
public class GameScreen extends Screen {

    private static Background bg1, bg2;
    private static int dStock;
    private Image image, background, dandelion;
    public static Image tiledirt, tilegrassTop, tilegrassBot;
   // private Graphics2D second;
    public static ArrayList<Dandelion> ddarray = new ArrayList<Dandelion>();
    private ArrayList<Tile> tilearray = new ArrayList<Tile>();
    private int elapsedTime;
    private Random r = new Random();
    private Wind bgWind;
    private float rotationRequired;
    private double locationX;
    private double locationY;


    enum GameState {
        Ready, Running, Paused, GameOver
    }

    GameState state = GameState.Ready;

    // Variable Setup
    // You would create game objects here.

    int livesLeft = 1;
    Paint paint;

    int xPos = 0;

    public GameScreen(Game game) {
        super(game);

        // Initialize game objects here

        // Defining a paint object
        paint = new Paint();
        paint.setTextSize(30);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setAntiAlias(true);
        paint.setColor(Color.WHITE);

        //Load up the backgroung Image
        background = game.getGraphics().newImage("background.png", Graphics.ImageFormat.RGB565);

        dandelion = game.getGraphics().newImage("dandelion.png", Graphics.ImageFormat.RGB565);
        tiledirt = game.getGraphics().newImage("tiledirt.png", Graphics.ImageFormat.RGB565);
        tilegrassTop = game.getGraphics().newImage("tilegrasstop.png", Graphics.ImageFormat.RGB565);
        tilegrassBot = game.getGraphics().newImage("tilegrassbot.png", Graphics.ImageFormat.RGB565);


        bg1 = new Background(0,0);
        bg2 = new Background(2160,0);
        bg1.setSpeedX(-1);
        bg2.setSpeedX(-1);
        dStock = 5; // initialize # of dandelions
        elapsedTime = 0;
        bgWind = new Wind(2,1);
        try{
            loadMap("map1.txt");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static ArrayList<Dandelion> getDdarray() {
        return ddarray;
    }

    public void setDdarray(ArrayList<Dandelion> ddarray) {
        this.ddarray = ddarray;
    }

    private void paintTiles(Graphics g) {
        for (int i = 0; i < tilearray.size(); i++) {
            Tile t = (Tile) tilearray.get(i);
            if (t.getType() != 0) {
                g.drawImage(t.getTileImage(), t.getTileX(), t.getTileY());
            }
        }
    }

    @Override
    public void update(float deltaTime) {
        List<TouchEvent> touchEvents = game.getInput().getTouchEvents();

        // We have four separate update methods in this example.
        // Depending on the state of the game, we call different update methods.
        // Refer to Unit 3's code. We did a similar thing without separating the
        // update methods.

        if (state == GameState.Ready)
            updateReady(touchEvents);
        if (state == GameState.Running)
            updateRunning(touchEvents, deltaTime);
        if (state == GameState.Paused)
            updatePaused(touchEvents);
        if (state == GameState.GameOver)
            updateGameOver(touchEvents);
    }

    private void updateReady(List<TouchEvent> touchEvents) {

        // This example starts with a "Ready" screen.
        // When the user touches the screen, the game begins.
        // state now becomes GameState.Running.
        // Now the updateRunning() method will be called!

        if (touchEvents.size() > 0)
            state = GameState.Running;
    }

    private void updateRunning(List<TouchEvent> touchEvents, float deltaTime) {

        //This is identical to the update() method from our Unit 2/3 game.


        // 1. All touch input is handled here:
        int len = touchEvents.size();
        for (int i = 0; i < len; i++) {
            TouchEvent event = touchEvents.get(i);

            if (event.type == TouchEvent.TOUCH_DOWN) {

                if (event.x < 640) {
                    // Move left.
                }

                else if (event.x > 640) {
                    // Move right.
                }

            }

            if (event.type == TouchEvent.TOUCH_UP) {

                if (event.x < 640) {
                    // Stop moving left.
                }

                else if (event.x > 640) {
                    // Stop moving right. }
                }
            }


        }

        //
        //while (state == GameState.Running)
        //{

        //}

        // 2. Check miscellaneous events like death:

        if (livesLeft == 0) {
            state = GameState.GameOver;
        }


        // 3. Call individual update() methods here.
        // This is where all the game updates happen.
        // For example, robot.update();
    }

    private void updatePaused(List<TouchEvent> touchEvents) {
        int len = touchEvents.size();
        for (int i = 0; i < len; i++) {
            TouchEvent event = touchEvents.get(i);
            if (event.type == TouchEvent.TOUCH_UP) {

            }
        }
    }

    private void updateGameOver(List<TouchEvent> touchEvents) {
        int len = touchEvents.size();
        for (int i = 0; i < len; i++) {
            TouchEvent event = touchEvents.get(i);
            if (event.type == TouchEvent.TOUCH_UP) {
                if (event.x > 300 && event.x < 980 && event.y > 100
                        && event.y < 500) {
                    nullify();
                    game.setScreen(new MainMenuScreen(game));
                    return;
                }
            }
        }

    }

    @Override
    public void paint(float deltaTime) {
        Graphics g = game.getGraphics();

        // First draw the game elements.

        // Example:
        g.drawImage(background, 0, 0);
        //g.drawImage(Assets.character, characterX, characterY);

        // Secondly, draw the UI above the game elements.
        if (state == GameState.Ready)
            drawReadyUI();
        if (state == GameState.Running)
            drawRunningUI();
        if (state == GameState.Paused)
            drawPausedUI();
        if (state == GameState.GameOver)
            drawGameOverUI();

    }

    private void nullify() {

        // Set all variables to null. You will be recreating them in the
        // constructor.
        paint = null;

        // Call garbage collector to clean up memory.
        System.gc();
    }

    private void drawReadyUI() {
        Graphics g = game.getGraphics();

        g.drawARGB(155, 0, 0, 0);
        g.drawString("Tap each side of the screen to move in that direction.",
                640, 300, paint);

    }

    private void updateTiles() {
        for (int i = 0; i < tilearray.size(); i++) {
            Tile t = (Tile) tilearray.get(i);
            t.update();
        }
    }

    private void drawRunningUI() {
        Graphics g = game.getGraphics();

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

        g.drawImage(background, bg1.getBgX(), bg1.getBgY());
        g.drawImage(background, bg2.getBgX(), bg2.getBgY());

        paintTiles(g);
        for(int i =0; i< ddarray.size(); i++){
            Dandelion dd = ddarray.get(i);
            g.drawImage(dandelion, dd.getCenterX(), dd.getCenterY());
        }
    }

    private void drawPausedUI() {
        Graphics g = game.getGraphics();
        // Darken the entire screen so you can display the Paused screen.
        g.drawARGB(155, 0, 0, 0);

    }

    private void drawGameOverUI() {
        Graphics g = game.getGraphics();
        g.drawRect(0, 0, 1281, 801, Color.BLACK);
        g.drawString("GAME OVER.", 640, 300, paint);

    }

    @Override
    public void pause() {
        if (state == GameState.Running)
            state = GameState.Paused;

    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {

    }

    @Override
    public void backButton() {
        pause();
    }

    private void loadMap(String filename) throws IOException {
        // TODO Auto-generated method stub
        ArrayList lines= new ArrayList();
        int width = 0;
        int height = 0;

        BufferedReader reader = new BufferedReader(new InputStreamReader(game.getFileIO().readAsset(filename)));
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
