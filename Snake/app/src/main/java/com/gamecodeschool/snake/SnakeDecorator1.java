package com.gamecodeschool.snake;

import android.content.Context;
import android.graphics.Point;

public class SnakeDecorator1 extends Snake{

    private final int health;

    SnakeDecorator1(Context context, Point mr, int ss) {
        super(context, mr, ss);
        health=2;
    }
}
