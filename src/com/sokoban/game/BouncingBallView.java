package com.sokoban.game;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
   
public class BouncingBallView extends View {
  private float ballRadius = 80; // Ball's radius
  private float ballX = ballRadius + 20;  // Ball's center (x,y)
  private float ballY = ballRadius + 40;
  private float ballSpeedX = 5;  // Ball's speed (x,y)
  private float ballSpeedY = 3;
  private RectF ballBounds;      // Needed for Canvas.drawOval
  private Paint paint;           // The paint (e.g. style, color) used for drawing
  private float previousX;
  private float previousY;
  private Bitmap box64;
  private Bitmap boxgoal64;
  private Bitmap empty64;
  private Bitmap floor64;
  private Bitmap goal64;
  private Bitmap pusher64;
  private Bitmap pushergoal64;
  private Bitmap wall64;
  private Level level;
  private RectF rect;
  private int box_size;
  private int start_x;
  private int start_y;
   
  // Constructor
  public BouncingBallView(Context context) {
    super(context);
      
    level = new Level("    #####              #   #              #$  #            ###  $##           #  $ $ #         ### # ## #   #######   # ## #####  ..## $  $          ..###### ### #@##  ..#    #     #########    #######        ", 19, 11, "", "", "");
    level.print();
      
    ballBounds = new RectF();
    paint = new Paint();
      
    box64        = BitmapFactory.decodeResource(getResources(), R.drawable.box64);
    boxgoal64    = BitmapFactory.decodeResource(getResources(), R.drawable.boxgoal64);
    empty64      = BitmapFactory.decodeResource(getResources(), R.drawable.empty64);
    floor64      = BitmapFactory.decodeResource(getResources(), R.drawable.floor64);
    goal64       = BitmapFactory.decodeResource(getResources(), R.drawable.goal64);
    pusher64     = BitmapFactory.decodeResource(getResources(), R.drawable.pusher64);
    pushergoal64 = BitmapFactory.decodeResource(getResources(), R.drawable.pushergoal64);
    wall64       = BitmapFactory.decodeResource(getResources(), R.drawable.wall64);
      
    rect = new RectF();
  }
  
  // Called back to draw the view. Also called by invalidate().
  @Override
  protected void onDraw(Canvas canvas) {     
    // print level to screen
    for(int m=0; m<level.rows_number; m++) {
      for(int n=0; n<level.cols_number; n++) {
        char type = level.read_pos(m, n);
          
        Bitmap sprite = null;
        if(type == 's')
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

        if(sprite != null) {
          rect.set(start_x + n*box_size, start_y + m*box_size, start_x + (n+1)*box_size, start_y + (m+1)*box_size);
          canvas.drawBitmap(sprite, null, rect, null);
        }
      }
    }
      
    // Update the position of the ball, including collision detection and reaction.
    update();
  
    // Delay
    try {  
       Thread.sleep(30);  
    } catch (InterruptedException e) { }
      
    invalidate(); // Force a re-draw
  }
   
  // Detect collision and update the position of the ball.
  private void update() {

  }
   
  // Called back when the view is first created or its size changes.
  @Override
  public void onSizeChanged(int w, int h, int oldW, int oldH) {
    // redefine box size
    float size_x = 0.9f * (float) w  / (float) level.cols_number;
    float size_y = 0.9f * (float) h / (float) level.rows_number;
    box_size = (int) Math.min(size_x, size_y);
    start_x = (w - (int) (box_size * (float) level.cols_number)) / 2;
    start_y = (h - (int) (box_size * (float) level.rows_number)) / 2;
  }
   
  // Touch-input handler
  @Override
  public boolean onTouchEvent(MotionEvent event) {
    float currentX = event.getX();
    float currentY = event.getY();
    float deltaX, deltaY;
    switch (event.getAction()) {
       case MotionEvent.ACTION_MOVE:
          // Modify rotational angles according to movement
          deltaX = currentX - previousX;
          deltaY = currentY - previousY;
          ballSpeedX = deltaX;
          ballSpeedY = deltaY;
    }
    // Save current x, y
    previousX = currentX;
    previousY = currentY;
    return true;  // Event handled
  }
}