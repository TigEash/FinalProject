package com.gamecodeschool.snake;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.view.MotionEvent;

class PauseButton implements BitmapObject {

    private Bitmap mBitmapPauseButton;
    private Point location;

    PauseButton(Context context, Point mr, int s) {
        mBitmapPauseButton = BitmapFactory.decodeResource(context.getResources(), R.drawable.pause);

        // Smaller scaling factors to make the pause button smaller
        int heightScaling = 3;  // Decreased from 6
        int widthScaling = 8;   // Decreased from 16
        mBitmapPauseButton = Bitmap.createScaledBitmap(mBitmapPauseButton, s * widthScaling, s * heightScaling, false);
    }

    void PauseCreate(int w, int h) {
        location = new Point(w / 2, h / 2);
        int positionScaling = 5;
        location.x *= positionScaling;
        location.y *= positionScaling;
    }

    public void draw(Canvas canvas, Paint paint) {
        canvas.drawBitmap(mBitmapPauseButton, location.x, location.y, paint);
    }

    boolean onTouchEvent(MotionEvent event) {
        float xDifference = Math.abs((float) location.x - event.getX() + 200); // Consider adjusting the offset if needed
        float yDifference = Math.abs((float) location.y - event.getY());

        if (event.getAction() == MotionEvent.ACTION_UP) {
            if (xDifference < 220 && yDifference < 200) {  // Consider reviewing these touch boundary checks if necessary
                return true;
            }
        }
        return false;
    }
}
