package com.sokoban.game;

import android.app.ActionBar;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
  
public class FullscreenActivity extends Activity {
   /** Called when the activity is first created. */
   @Override
   public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      View bouncingBallView = new BouncingBallView(this);
      setContentView(bouncingBallView);    
      bouncingBallView.setBackgroundColor(Color.WHITE);
   }
}