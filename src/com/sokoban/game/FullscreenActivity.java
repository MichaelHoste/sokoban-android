package com.sokoban.game;

import android.app.ActionBar;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class FullscreenActivity extends Activity {
  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    final View sokobanView = new SokobanView(this);
    setContentView(sokobanView);
    sokobanView.setBackgroundColor(Color.WHITE);

    /*sokobanView.setOnTouchListener(new OnSwipeTouchListener(this) {
      @Override
      public void onSwipeLeft() {
        Level level = ((SokobanView) sokobanView).getLevel();
        level.move('l');
        Log.e("Level", "Level: left");
      }

      @Override
      public void onSwipeRight() {
        Level level = ((SokobanView) sokobanView).getLevel();
        level.move('r');
        Log.e("Level", "Level: right");
      }

      @Override
      public void onSwipeUp() {
        Level level = ((SokobanView) sokobanView).getLevel();
        level.move('u');
        Log.e("Level", "Level: up");
      }

      @Override
      public void onSwipeDown() {
        Level level = ((SokobanView) sokobanView).getLevel();
        level.move('d');
        Log.e("Level", "Level: down");
      }
    });*/
  }
}