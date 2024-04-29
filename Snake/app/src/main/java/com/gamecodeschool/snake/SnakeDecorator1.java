package com.gamecodeschool.snake;

import android.content.Context;
import android.graphics.Point;

public class SnakeDecorator1 extends Snake{
//TODO name it supersnake or something along the lines?
    private final int health;

    SnakeDecorator1(Context context, Point mr, int ss) {
        super(context, mr, ss);
        health=2;
    }
}
