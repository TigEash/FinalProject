package com.gamecodeschool.snake;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;

import java.util.Random;

public class Grapes implements Fruit {
    private Point location = new Point();
    private Point mSpawnRange;
    private int mSize;
    private Bitmap mBitmapGrapes;

    public Grapes(Context context, Point sr, int s) {
        mSpawnRange = sr;
        mSize = s;
        location.x = -10; // Initially off-screen
        mBitmapGrapes = BitmapFactory.decodeResource(context.getResources(), R.drawable.grapes);
        mBitmapGrapes = Bitmap.createScaledBitmap(mBitmapGrapes, s, s, false);
    }

    @Override
    public void spawn() {
        Random random = new Random();
        location.x = random.nextInt(mSpawnRange.x) + 1;
        location.y = random.nextInt(mSpawnRange.y - 1) + 1;
    }

    @Override
    public Point getLocation() {
        return location;
    }

    @Override
    public void draw(Canvas canvas, Paint paint) {
        canvas.drawBitmap(mBitmapGrapes, location.x * mSize, location.y * mSize, paint);
    }
}

