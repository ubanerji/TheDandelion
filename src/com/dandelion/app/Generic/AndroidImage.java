package com.dandelion.app.Generic;

import android.graphics.Bitmap;

import com.dandelion.app.Framework.Image;
import com.dandelion.app.Framework.Graphics.ImageFormat;

/**
 * Created by ubanerji on 8/4/13.
 */
public class AndroidImage implements Image {
    Bitmap bitmap;
    ImageFormat format;

    public AndroidImage(Bitmap bitmap, ImageFormat format) {
        this.bitmap = bitmap;
        this.format = format;
    }

    @Override
    public int getWidth() {
        return bitmap.getWidth();
    }

    @Override
    public int getHeight() {
        return bitmap.getHeight();
    }

    @Override
    public ImageFormat getFormat() {
        return format;
    }

    @Override
    public void dispose() {
        bitmap.recycle();
    }
}
