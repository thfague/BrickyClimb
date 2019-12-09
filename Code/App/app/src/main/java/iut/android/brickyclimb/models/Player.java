package iut.android.brickyclimb.models;

import java.io.Serializable;

public class Player implements Serializable {
    private int xPosition;
    private int yPosition;

    private int width;
    private int height;

    public Player(int width, int height){
        this.width = width;
        this.height = height;
    }


    public int getxPosition() {
        return xPosition;
    }

    public void setxPosition(int xPosition) {
        this.xPosition = xPosition;
    }

    public int getyPosition() {
        return yPosition;
    }

    public void setyPosition(int yPosition) {
        this.yPosition = yPosition;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }
}
