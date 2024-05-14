package com.gamecodeschool.snake;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.view.MotionEvent;

import java.util.ArrayList;

class Snake implements BitmapObject {

    private ArrayList<Point> segmentLocations;
    private int mSegmentSize;
    private Point mMoveRange;
    private int halfWayPoint;

    private enum Heading {
        UP, RIGHT, DOWN, LEFT
    }

    private Heading heading = Heading.RIGHT;

    private Bitmap mBitmapHeadRight;
    private Bitmap mBitmapHeadLeft;
    private Bitmap mBitmapHeadUp;
    private Bitmap mBitmapHeadDown;
    private Bitmap mBitmapBody;

    // Scale factor for the head size, 20% larger
    private float headScaleFactor = 1.2f;

    Snake(Context context, Point mr, int ss) {
        segmentLocations = new ArrayList<>();
        mSegmentSize = ss;
        mMoveRange = mr;

        // Load the initial head bitmap
        mBitmapHeadRight = BitmapFactory.decodeResource(context.getResources(), R.drawable.head);
        // Scale the head bitmap
        mBitmapHeadRight = Bitmap.createScaledBitmap(mBitmapHeadRight,
                (int) (ss * headScaleFactor), (int) (ss * headScaleFactor), false);

        // Setup matrix for transformations
        Matrix matrix = new Matrix();

        // Create the left-facing head
        matrix.preScale(-1, 1);
        mBitmapHeadLeft = Bitmap.createBitmap(mBitmapHeadRight, 0, 0,
                mBitmapHeadRight.getWidth(), mBitmapHeadRight.getHeight(), matrix, true);

        // Create the up-facing head
        matrix.setRotate(-90);
        mBitmapHeadUp = Bitmap.createBitmap(mBitmapHeadRight, 0, 0,
                mBitmapHeadRight.getWidth(), mBitmapHeadRight.getHeight(), matrix, true);

        // Create the down-facing head
        matrix.setRotate(180);
        mBitmapHeadDown = Bitmap.createBitmap(mBitmapHeadRight, 0, 0,
                mBitmapHeadRight.getWidth(), mBitmapHeadRight.getHeight(), matrix, true);

        // Load and scale the body bitmap
        mBitmapBody = BitmapFactory.decodeResource(context.getResources(), R.drawable.body);
        mBitmapBody = Bitmap.createScaledBitmap(mBitmapBody, ss, ss, false);

        // Calculate the halfway point across the screen
        halfWayPoint = mr.x * ss / 2;
    }

    void reset(int w, int h) {
        heading = Heading.RIGHT;
        segmentLocations.clear();
        segmentLocations.add(new Point(w / 2, h / 2));
    }

    void move() {
        for (int i = segmentLocations.size() - 1; i > 0; i--) {
            segmentLocations.get(i).x = segmentLocations.get(i - 1).x;
            segmentLocations.get(i).y = segmentLocations.get(i - 1).y;
        }

        Point p = segmentLocations.get(0);

        switch (heading) {
            case UP:
                p.y--;
                break;
            case RIGHT:
                p.x++;
                break;
            case DOWN:
                p.y++;
                break;
            case LEFT:
                p.x--;
                break;
        }
    }

    boolean detectDeath() {
        boolean dead = false;
        Point head = segmentLocations.get(0);
        if (head.x == -1 || head.x > mMoveRange.x || head.y == -1 || head.y > mMoveRange.y) {
            dead = true;
        }

        for (int i = segmentLocations.size() - 1; i > 0; i--) {
            if (head.x == segmentLocations.get(i).x && head.y == segmentLocations.get(i).y) {
                dead = true;
            }
        }

        return dead;
    }

    boolean checkDinner(Point l) {
        if (segmentLocations.get(0).x == l.x && segmentLocations.get(0).y == l.y) {
            segmentLocations.add(new Point(-10, -10));
            return true;
        }
        return false;
    }

    public void draw(Canvas canvas, Paint paint) {
        if (!segmentLocations.isEmpty()) {
            switch (heading) {
                case RIGHT:
                    canvas.drawBitmap(mBitmapHeadRight, segmentLocations.get(0).x * mSegmentSize,
                            segmentLocations.get(0).y * mSegmentSize, paint);
                    break;
                case LEFT:
                    canvas.drawBitmap(mBitmapHeadLeft, segmentLocations.get(0).x * mSegmentSize,
                            segmentLocations.get(0).y * mSegmentSize, paint);
                    break;
                case UP:
                    canvas.drawBitmap(mBitmapHeadUp, segmentLocations.get(0).x * mSegmentSize,
                            segmentLocations.get(0).y * mSegmentSize, paint);
                    break;
                case DOWN:
                    canvas.drawBitmap(mBitmapHeadDown, segmentLocations.get(0).x * mSegmentSize,
                            segmentLocations.get(0).y * mSegmentSize, paint);
                    break;
            }

            for (int i = 1; i < segmentLocations.size(); i++) {
                canvas.drawBitmap(mBitmapBody, segmentLocations.get(i).x * mSegmentSize,
                        segmentLocations.get(i).y * mSegmentSize, paint);
            }
        }
    }

    void switchHeading(MotionEvent motionEvent) {
        if (motionEvent.getX() >= halfWayPoint) {
            switch (heading) {
                case UP:
                    heading = Heading.RIGHT;
                    break;
                case RIGHT:
                    heading = Heading.DOWN;
                    break;
                case DOWN:
                    heading = Heading.LEFT;
                    break;
                case LEFT:
                    heading = Heading.UP;
                    break;
            }
        } else {
            switch (heading) {
                case UP:
                    heading = Heading.LEFT;
                    break;
                case LEFT:
                    heading = Heading.DOWN;
                    break;
                case DOWN:
                    heading = Heading.RIGHT;
                    break;
                case RIGHT:
                    heading = Heading.UP;
                    break;
            }
        }
    }
}
