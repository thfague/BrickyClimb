package iut.android.brickyclimb.models;

import android.graphics.Color;
import android.graphics.Paint;

import java.io.Serializable;

public class Brick implements Serializable {

    public static final int LOW = 2;
    public static final int MID = 4;

    private int xPosition;
    private int yPosition;

    private int width;
    private int height;

    private int pv;

    public Brick(int xPos, int yPos, int width, int height, int pvs){
        xPosition = xPos;
        yPosition = yPos;
        this.width = width;
        this.height = height;
        pv = pvs;
    }

    public int getxPosition() {
        return xPosition;
    }

    public int getyPosition() {
        return yPosition;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void onCollide(){
        pv--;
    }

    public int getPv() {
        return pv;
    }
}
