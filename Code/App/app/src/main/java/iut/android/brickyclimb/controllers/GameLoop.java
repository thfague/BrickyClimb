package iut.android.brickyclimb.controllers;

import java.io.Serializable;

import iut.android.brickyclimb.activities.GameActivity;

public class GameLoop extends Thread implements Serializable {

    private GameActivity gameActivity;
    private int fps;
    private boolean alive;

    public GameLoop(GameActivity activity, int fps){
        gameActivity=activity;
        this.fps = fps;
    }

    @Override
    public void run(){
        alive = true;
        while(alive){
            gameActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    gameActivity.updatePositions();
                }
            });
            try {
                sleep(1000/fps);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void kill(){
        alive = false;
    }
}
