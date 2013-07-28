package com.dandelion.app;

/**
 * Created by ubanerji on 7/27/13.
 */
import com.dandelion.app.Graphics.ImageFormat;

public interface Image {
    public int getWidth();
    public int getHeight();
    public ImageFormat getFormat();
    public void dispose();
}
