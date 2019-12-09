package iut.android.brickyclimb.models;

import android.util.Log;

import java.io.Serializable;

public class Ball implements Serializable {

    private int xPosition;
    private int yPosition;

    private float xVel;
    private float yVel;

    private int radius;

    public Ball(int radius, int xPos, int yPos, float xVel, float yVel){
        this.radius = radius;
        xPosition = xPos;
        yPosition = yPos;
        this.xVel = xVel;
        this.yVel = yVel;
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

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }


    /**
     * @param brick
     * @return 0 or collision side (1 X, 2 Y, 3 ANGLE)
     */
    public int collide(Brick brick) {
        if (!((brick.getxPosition() >= xPosition + radius)
                || (brick.getxPosition() + brick.getWidth() <= xPosition-radius)
                || (brick.getyPosition() >= yPosition + radius)
                || (brick.getyPosition() + brick.getHeight() <= yPosition-radius))) {
            return (((xPosition < brick.getxPosition() && xVel>0) || (xPosition > brick.getxPosition()+brick.getWidth() && xVel<0)) ? 1 : 0)+((yPosition < brick.getyPosition() && yVel>0) || (yPosition > brick.getyPosition()+brick.getHeight() && yVel<0)?2:0);
        }
        return 0;
    }

    public boolean collide(Player player){
        if(yPosition+radius >= player.getyPosition())
            if(xPosition+radius >= player.getxPosition() && xPosition-radius <= player.getxPosition()+player.getWidth() && yPosition+radius <= player.getyPosition()+player.getHeight())
                return true;
        return false;
    }

    public boolean fastCollide(Player player) {
        if(yPosition+radius >= player.getyPosition())
            if(xPosition >= player.getxPosition() && xPosition <= player.getxPosition()+player.getWidth())
                return true;
        return false;
    }

    public float getxVel() {
        return xVel;
    }

    public void setxVel(float xVel) {
        this.xVel = xVel;
    }

    public float getyVel() {
        return yVel;
    }

    public void setyVel(float yVel) {
        this.yVel = yVel;
    }
}
