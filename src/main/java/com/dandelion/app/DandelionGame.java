package com.dandelion.app;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

import com.dandelion.app.Framework.Audio;
import com.dandelion.app.Framework.FileIO;
import com.dandelion.app.Framework.Game;
import com.dandelion.app.Framework.Graphics;
import com.dandelion.app.Framework.Input;
import com.dandelion.app.Framework.Screen;
import com.dandelion.app.Generic.AndroidGame;

public class DandelionGame extends AndroidGame {

    @Override
    public Screen getInitScreen() {
        return new LoadingScreen(this);
    }

    @Override
    public void onBackPressed() {
        getCurrentScreen().backButton();
    }
}
