package com.dandelion.app.Framework;

/**
 * Created by ubanerji on 7/27/13.
 */
import com.dandelion.app.Framework.Graphics.ImageFormat;

public interface Image {
    public int getWidth();
    public int getHeight();
    public ImageFormat getFormat();
    public void dispose();
}
