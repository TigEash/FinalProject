package com.gamecodeschool.snake;

import android.content.Context;
import android.graphics.Point;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class ConcreteFruitFactory implements FruitFactory {
    private final Context mContext;
    private final Point mSpawnRange;
    private final int mSize;

    public ConcreteFruitFactory(Context context, Point spawnRange, int size){
        this.mContext = context;
        this.mSpawnRange = spawnRange;
        this.mSize = size;

        Log.d("FruitFactory", "Fruit Factory Initialized");
    }

    @Override
    public Fruit createFruit(String type) {

        switch(type.toLowerCase()){
            case "apple":
                return new Apple(mContext, mSpawnRange, mSize);
            case "banana":
                return new Banana(mContext, mSpawnRange, mSize);
            case "grapes":
                return new Grapes(mContext, mSpawnRange, mSize);
            case "mango":
                return new Mango(mContext, mSpawnRange, mSize);
            case "ice-cube":
                return new IceCube(mContext, mSpawnRange, mSize);
            default:
                return null;
        }
    }
}
