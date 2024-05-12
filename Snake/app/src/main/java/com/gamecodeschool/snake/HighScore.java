package com.gamecodeschool.snake;

import android.graphics.Canvas;
import android.graphics.Paint;

public class HighScore extends HUDObject{
    HighScore(Paint painter, String text, Canvas canvas, int xLoc, int yLoc){
        super(painter, text, canvas, xLoc, yLoc);
    }

    public String getText(){
        return this.text;
    }
    public void setText(String text){
        this.text = text;
    }

    public void setText(float highscoreAsFloat){ this.text= Float.toString(highscoreAsFloat);}


    public void setText(int highscoreAsInt){ this.text= Integer.toString(highscoreAsInt);}
    public void draw(){
        this.canvas.drawText("" + this.text, this.xLoc, this.yLoc, this.painter);
    }
}
