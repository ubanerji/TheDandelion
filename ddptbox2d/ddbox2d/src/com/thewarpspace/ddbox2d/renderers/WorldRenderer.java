package com.thewarpspace.ddbox2d.renderers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;

public class WorldRenderer {
	World world;
	Box2DDebugRenderer debugRenderer;  
    OrthographicCamera camera;  
    static final float BOX_STEP=1/60f;  
    static final int BOX_VELOCITY_ITERATIONS=6;  
    static final int BOX_POSITION_ITERATIONS=2;  
    static final float WORLD_TO_BOX=0.01f;  
    static final float BOX_WORLD_TO=100f;  
    
    public OrthographicCamera getCamera() {
		return camera;
	}

	public void render(){
    	Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);  
        debugRenderer.render(world, camera.combined);  
        world.step(BOX_STEP, BOX_VELOCITY_ITERATIONS, BOX_POSITION_ITERATIONS);  
        world.clearForces(); // As told in manual, this is needed
    }
    
	public WorldRenderer(World world){
		this.world = world;
		camera = new OrthographicCamera();  
        camera.viewportHeight = 320;  
        camera.viewportWidth = 480;  
        camera.position.set(camera.viewportWidth * .5f, camera.viewportHeight * .5f, 0f);  
        camera.update();  
        debugRenderer = new Box2DDebugRenderer();  // default debugRenderer, should implement Sprites
	}
}
