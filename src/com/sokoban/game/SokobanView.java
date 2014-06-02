package com.sokoban.game;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class SokobanView extends View {
  private float  oldEventX = 0;
  private float  oldEventY = 0;
  private float  oldEventTime = 0;
  private float  distanceX = 0;
  private float  distanceY = 0;
  private float  velocityX = 0;
  private float  velocityY = 0;
  private Bitmap box64;
  private Bitmap boxgoal64;
  private Bitmap floor64;
  private Bitmap goal64;
  private Bitmap pusher64;
  private Bitmap pushergoal64;
  private Bitmap wall64;
  private Bitmap cancel128;
  private Bitmap cancel128t;
  private Bitmap restart128;
  private Bitmap restart128t;
  private Level  level;
  private Path   path;
  private RectF  rect;
  private int    box_size;
  private int    startX;
  private int    startY;
  private int    buttonSize;
  private int    cancelStartX;
  private int    cancelStartY;
  private int    restartStartX;
  private int    restartStartY;

  // Constructor
  public SokobanView(Context context) {
    super(context);

    level = new Level("    #####              #   #              #$  #            ###  $##           #  $ $ #         ### # ## #   #######   # ## #####  ..## $  $          ..###### ### #@##  ..#    #     #########    #######        ",
                      19, 11, "", "", "");
    level.print();
    
    path = new Path();

    box64        = BitmapFactory.decodeResource(getResources(), R.drawable.box64);
    boxgoal64    = BitmapFactory.decodeResource(getResources(), R.drawable.boxgoal64);
    floor64      = BitmapFactory.decodeResource(getResources(), R.drawable.floor64);
    goal64       = BitmapFactory.decodeResource(getResources(), R.drawable.goal64);
    pusher64     = BitmapFactory.decodeResource(getResources(), R.drawable.pusher64);
    pushergoal64 = BitmapFactory.decodeResource(getResources(), R.drawable.pushergoal64);
    wall64       = BitmapFactory.decodeResource(getResources(), R.drawable.wall64);
    restart128   = BitmapFactory.decodeResource(getResources(), R.drawable.restart128);
    restart128t  = BitmapFactory.decodeResource(getResources(), R.drawable.restart128t);
    cancel128    = BitmapFactory.decodeResource(getResources(), R.drawable.cancel128);
    cancel128t   = BitmapFactory.decodeResource(getResources(), R.drawable.cancel128t);
    
    rect = new RectF();
  }

  // Called back to draw the view. Also called by invalidate().
  @Override
  protected void onDraw(Canvas canvas) {
    // print level to screen
    for (int m = 0; m < level.rows_number; m++) {
      for (int n = 0; n < level.cols_number; n++) {
        char type = level.read_pos(m, n);

        Bitmap sprite = null;
        if (type == 's')
          sprite = floor64;
        else if (type == '#')
          sprite = wall64;
        else if (type == '$')
          sprite = box64;
        else if (type == '*')
          sprite = boxgoal64;
        else if (type == '.')
          sprite = goal64;
        else if (type == '@')
          sprite = pusher64;
        else if (type == '+')
          sprite = pushergoal64;

        if (sprite != null) {
          rect.set(startX + n * box_size, 
                   startY + m * box_size, 
                   startX + (n + 1) * box_size, 
                   startY + (m + 1) * box_size);
          canvas.drawBitmap(sprite, null, rect, null);
        }
      }
    }
    
    rect.set(cancelStartX, cancelStartY, cancelStartX+buttonSize, cancelStartY+buttonSize);
    canvas.drawBitmap(cancel128t, null, rect, null);
    
    rect.set(restartStartX, restartStartY, restartStartX+buttonSize, restartStartY+buttonSize);
    canvas.drawBitmap(restart128t, null, rect, null);

    update();

    // Delay
    try {
      Thread.sleep(30);
    } catch (InterruptedException e) {
    }

    invalidate(); // Force a re-draw
  }

  private void update() {

  }

  // Called back when the view is first created or its size changes.
  @Override
  public void onSizeChanged(int w, int h, int oldW, int oldH) {
    // redefine box size
    float size_x = 0.9f * (float) w / (float) level.cols_number;
    float size_y = 0.9f * (float) h / (float) level.rows_number;
    box_size = (int) Math.min(size_x, size_y);
    startX = (w - (int) (box_size * (float) level.cols_number)) / 2;
    startY = (h - (int) (box_size * (float) level.rows_number)) / 2;
    buttonSize = h/8;
    cancelStartX = 30;
    cancelStartY = 30;
    restartStartX = w - buttonSize - 30;
    restartStartY = 30;
  }

  // Touch-input handler
  @Override
  public boolean onTouchEvent(MotionEvent event) {
    switch (event.getAction()) {
      case MotionEvent.ACTION_DOWN:
      case MotionEvent.ACTION_UP:
        distanceX = 0;
        distanceY = 0;
        velocityX = 0;
        velocityY = 0;
        break;
      case MotionEvent.ACTION_MOVE:
        int SWIPE_DISTANCE_THRESHOLD = 55;
        int SWIPE_VELOCITY_THRESHOLD = 150;
        
        distanceX = distanceX + event.getX() - oldEventX;
        distanceY = distanceY + event.getY() - oldEventY;
        velocityX = (event.getX() - oldEventX) / (event.getEventTime() - oldEventTime) * 1000;
        velocityY = (event.getY() - oldEventY) / (event.getEventTime() - oldEventTime) * 1000;
        
        int  moved     = 0;
        char direction = ' ';
        
        if (Math.abs(distanceX) > Math.abs(distanceY) && Math.abs(distanceX) > SWIPE_DISTANCE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
          if (distanceX > 0)
            direction = 'r';
          else
            direction = 'l';
          distanceX = 0;
          distanceY = 0;
        } else if (Math.abs(distanceY) > Math.abs(distanceX) && Math.abs(distanceY) > SWIPE_DISTANCE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
          if (distanceY > 0)
            direction = 'd';
          else 
            direction = 'u';
          distanceX = 0;
          distanceY = 0;
        }
        
        if (direction != ' ') {
          moved = level.move(direction);
          if(moved == 1)
            path.add_move(direction);
          else if(moved == 2)
            path.add_push(direction);
        }
        
        break;
    }
    
    switch (event.getAction()) {
    case MotionEvent.ACTION_DOWN:
      if(    event.getX() > cancelStartX 
          && event.getX() < cancelStartX + buttonSize
          && event.getY() > cancelStartY
          && event.getY() < cancelStartY + buttonSize) {
        level.delete_last_move(path);
      }
      else if(   event.getX() > restartStartX 
              && event.getX() < restartStartX + buttonSize
              && event.getY() > restartStartY
              && event.getY() < restartStartY + buttonSize) {
        level.restart();
        path = new Path();
      }
    }
         
    oldEventX = event.getX();
    oldEventY = event.getY();
    oldEventTime = event.getEventTime();

    return true;
  }
}