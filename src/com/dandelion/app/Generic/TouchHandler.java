package com.dandelion.app.Generic;

import java.util.List;

import android.view.View.OnTouchListener;

import com.dandelion.app.Framework.Input.TouchEvent;

/**
 * Created by ubanerji on 8/4/13.
 */
public interface TouchHandler extends OnTouchListener {
    public boolean isTouchDown(int pointer);

    public int getTouchX(int pointer);

    public int getTouchY(int pointer);

    public List<TouchEvent> getTouchEvents();
}
