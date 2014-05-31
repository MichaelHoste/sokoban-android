package com.sokoban.game;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.View;
   
public class BouncingBallView extends View {
   private int xMin = 0;          // This view's bounds
   private int xMax;
   private int yMin = 0;
   private int yMax;
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
   
   // Constructor
   public BouncingBallView(Context context) {
      super(context);
      
      Level level = new Level("    #####              #   #              #$  #            ###  $##           #  $ $ #         ### # ## #   #######   # ## #####  ..## $  $          ..###### ### #@##  ..#    #     #########    #######        ", 19, 11, "", "", "");
      level.print();
      
      ballBounds = new RectF();
      paint = new Paint();
      
      int resourceId = R.drawable.box64;
      box64 = BitmapFactory.decodeResource(getResources(), resourceId);
   }
  
   // Called back to draw the view. Also called by invalidate().
   @Override
   protected void onDraw(Canvas canvas) {
      // Draw the ball
      ballBounds.set(ballX-ballRadius, ballY-ballRadius, ballX+ballRadius, ballY+ballRadius);
      paint.setColor(Color.GREEN);
      canvas.drawOval(ballBounds, paint);
      canvas.drawBitmap(box64, null, new RectF(ballX-ballRadius, ballY-ballRadius, ballX+ballRadius, ballY+ballRadius), null);
        
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
      // Get new (x,y) position
      ballX += ballSpeedX;
      ballY += ballSpeedY;
      // Detect collision and react
      if (ballX + ballRadius > xMax) {
         ballSpeedX = -ballSpeedX;
         ballX = xMax-ballRadius;
      } else if (ballX - ballRadius < xMin) {
         ballSpeedX = -ballSpeedX;
         ballX = xMin+ballRadius;
      }
      if (ballY + ballRadius > yMax) {
         ballSpeedY = -ballSpeedY;
         ballY = yMax - ballRadius;
      } else if (ballY - ballRadius < yMin) {
         ballSpeedY = -ballSpeedY;
         ballY = yMin + ballRadius;
      }
   }
   
   // Called back when the view is first created or its size changes.
   @Override
   public void onSizeChanged(int w, int h, int oldW, int oldH) {
      // Set the movement bounds for the ball
      xMax = w-1;
      yMax = h-1;
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