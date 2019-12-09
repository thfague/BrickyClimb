package iut.android.brickyclimb.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Point;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import iut.android.brickyclimb.controllers.GameActivityListener;
import iut.android.brickyclimb.R;
import iut.android.brickyclimb.controllers.GameLoop;
import iut.android.brickyclimb.models.Ball;
import iut.android.brickyclimb.models.Brick;
import iut.android.brickyclimb.models.Player;
import iut.android.brickyclimb.services.MusicService;
import iut.android.brickyclimb.views.GameView;

public class GameActivity extends AppCompatActivity implements SensorEventListener, Serializable {

    public static final String PREFS_SETTINGS = "shared_prefs";
    public static final String PREFS_SCORE = "shared_prefs_score";
    private SharedPreferences sharedPrefSettings;
    private SharedPreferences sharedPrefScore;
    private SharedPreferences.Editor mEditorScore;

    private SensorManager sensorManager;
    private GameView gameView;
    private Point screenSize;
    private Player player;
    private Ball ball;
    private GameLoop gameLoop;
    private int fps;
    private Vibrator vibrator;
    private List<GameActivityListener> listeners;
    private int orientation;

    private List<Brick> bricks;

    private int sensibility;

    private float xRotation;
    private int xMaxPlayer;
    private int xMaxBall;
    private int yMaxBall;

    private int pauseButtonx;
    private int pauseButtony;
    private int pauseButtonWidth;

    private int score;

    private Boolean touchControls;
    private Boolean pause;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        sharedPrefSettings = getSharedPreferences(PREFS_SETTINGS,Context.MODE_PRIVATE);

        pause = false;
        listeners = new ArrayList<>();

        screenSize = new Point();
        getWindowManager().getDefaultDisplay().getSize(screenSize);
        orientation = getWindowManager().getDefaultDisplay().getRotation();

        bricks = new ArrayList<>();
        player = new Player((int) (screenSize.x / 3.5), screenSize.y / 40);
        player.setyPosition(screenSize.y - screenSize.y/50 - player.getHeight());
        ball = new Ball(screenSize.x / 30, screenSize.x / 2, screenSize.y / 2, (float) ((new Random().nextFloat() - 0.5) * 10), screenSize.y / 200f);

        refreshPlayerMax();
        refreshBallMax();


        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensibility = screenSize.x/30;

        pauseButtonx = screenSize.x - screenSize.x/13;
        pauseButtony = 10;
        pauseButtonWidth = screenSize.x/14;

        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        fps=90;
        gameView = new GameView(this);
        setContentView(gameView);

        onBricksAllDestroyed();
        score = 0;
        String touchControlsPref = sharedPrefSettings.getString(getString(R.string.controls_touch), "false");
        assert touchControlsPref != null;
        touchControls = touchControlsPref.equals("true");
    }

    @Override
    protected void onPause() {
        if (!touchControls) sensorManager.unregisterListener(this);
        gameLoop.kill();
        stopService(new Intent(this, MusicService.class));
        super.onPause();
    }

    @Override
    protected void onResume() {
        startService(new Intent(this,MusicService.class));
        if(!touchControls) {
            Sensor rotationVector = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
            if (rotationVector != null) {
                sensorManager.registerListener(this, rotationVector, SensorManager.SENSOR_DELAY_GAME);
            }
            else {
                touchControls = true;
            }
        }
        if(!isPaused()) {
            gameLoop = new GameLoop(this, fps);
            gameLoop.start();
        }
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        sharedPrefScore = getSharedPreferences(PREFS_SCORE,Context.MODE_PRIVATE);
        mEditorScore = sharedPrefScore.edit();
        mEditorScore.putInt("score_game",this.getScore());
        mEditorScore.apply();
        startActivity(new Intent(this, SaveScoreActivity.class));
        super.onDestroy();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    public void onSensorChanged(SensorEvent event) {
        if(!touchControls) {
            if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
                float[] rotationMatrix = new float[16];
                float[] orientations = new float[3];
                SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values);
                SensorManager.getOrientation(rotationMatrix, orientations);
                switch (orientation) {
                    case 0:
                        xRotation = (float) (Math.toDegrees(orientations[2]));
                        break;
                    case 1:
                        xRotation = (float) (Math.toDegrees(orientations[1])) * -1;
                        break;
                    case 2:
                        xRotation = (float) (Math.toDegrees(orientations[2])) * -1;
                    case 3:
                        xRotation = (float) (Math.toDegrees(orientations[1]));
                        break;
                }
            }
        }
    }

    public void updateBallPosition() {
        ball.setxPosition((int) (ball.getxPosition()+((ball.getxVel()>0)?Math.ceil(ball.getxVel()):Math.floor(ball.getxVel()))));
        ball.setyPosition((int) (ball.getyPosition()+ball.getyVel()));

        if (ball.getxPosition() > xMaxBall) {
            ball.setxPosition(xMaxBall);
            ball.setxVel(ball.getxVel()*-1);
        } else if (ball.getxPosition() < ball.getRadius()) {
            ball.setxPosition(ball.getRadius());
            ball.setxVel(ball.getxVel()*-1);
        }

        if (ball.getyPosition() > yMaxBall) {
            onBallCollideBottom();
        } else if (ball.getyPosition() < ball.getRadius()) {
            ball.setyPosition(ball.getRadius());
            ball.setyVel(ball.getyVel()*-1);
        }

        if ((ball.getyVel()>screenSize.y/40) ? ball.fastCollide(player) : ball.collide(player)) {
            onBallCollidePlayer();
        }

        List<Brick> collided = new ArrayList<>();
        for(Brick b : bricks){
            switch (ball.collide(b)){
                case 1: onBallCollideBrickX();
                        collided.add(b);
                        break;
                case 2: onBallCollideBrickY();
                        collided.add(b);
                        break;
                case 3: onBallCollideBrickY();
                        onBallCollideBrickX();
                        collided.add(b);
                        break;
            }
        }
        for(Brick b : collided){
            onBrickCollide(b);
        }

        gameView.invalidate();
    }

    private void onBallCollideBrickX() {
        ball.setxVel(ball.getxVel()*-1);
    }

    private void onBallCollideBrickY() {
        ball.setyVel(ball.getyVel()*-1);
    }

    private void onBrickCollide(Brick b){
        vibrator.vibrate(10);
        b.onCollide();
        if(b.getPv()<=0) {
            bricks.remove(b);
            ball.setyVel(ball.getyVel()*1.03f);
            score++;
        }
        if(bricks.isEmpty())
            onBricksAllDestroyed();
    }

    private void onBricksAllDestroyed() {
        ball.setyPosition(player.getyPosition()-ball.getRadius());
        ball.setxPosition(screenSize.x/2);
        ball.setyVel(-(Math.abs(ball.getyVel())));
        int nb = score/3+4;
        for (int i=0; i<nb; i++){
            int width = (int) (new Random().nextInt((int) (screenSize.x/3.6))+screenSize.x/10.8);
            int height = (int) (new Random().nextInt((int) (screenSize.y/10.4))+screenSize.y/41.6);
            int x = new Random().nextInt(screenSize.x-width);
            int y = new Random().nextInt(screenSize.y/3);
            int lives = new Random().nextInt(4)+2;
            bricks.add(new Brick(x, y, width, height, lives));
        }
    }

    public void onBallCollidePlayer() {
        ball.setyVel(-(Math.abs(ball.getyVel())));
        ball.setyPosition(player.getyPosition()-ball.getRadius());
        ball.setxVel((float) (ball.getxVel()+((ball.getxPosition() - player.getxPosition())/(float)(player.getWidth()) - 0.5) * 5));
        vibrator.vibrate(10);
    }

    public void onBallCollideBottom() {
        gameLoop.interrupt();
        finish();
    }

    public void updatePlayerPosition() {
        if(!touchControls) {
            player.setxPosition((int) ((xMaxPlayer + player.getWidth()) / 2f + xRotation * sensibility - player.getWidth() / 2f));
        }
        else{
            player.setxPosition((int) xRotation-player.getWidth()/2);
        }
        if (player.getxPosition() > xMaxPlayer) {
            player.setxPosition(xMaxPlayer);
        } else if (player.getxPosition() < 0) {
            player.setxPosition(0);
        }
    }

    public void updatePositions() {
        updatePlayerPosition();
        updateBallPosition();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        int action = event.getAction();
        switch (action) {
            case (MotionEvent.ACTION_DOWN):
                if(event.getX() >= pauseButtonx && event.getX() <= pauseButtonx+pauseButtonWidth && event.getY() >= pauseButtony && event.getY() <= pauseButtony+pauseButtonWidth){
                    if(isPaused()) Unpause();
                    else Pause();
                }
                return true;
            case (MotionEvent.ACTION_MOVE):
                if(touchControls) xRotation = event.getX();
                return true;
        }
        return super.onTouchEvent(event);
    }

    public void refreshPlayerMax(){
        xMaxPlayer = screenSize.x - player.getWidth();
    }

    private void refreshBallMax() {
        xMaxBall = screenSize.x - ball.getRadius();
        yMaxBall = screenSize.y - ball.getRadius();
    }

    private void setPlayerWidth(int width){
        player.setWidth(width);
        notifyWidthChanged();
        refreshPlayerMax();
    }

   public Player getPlayer(){
        return player;
   }

   public Ball getBall(){
        return ball;
   }

   public int getScore(){
        return score;
    }

    public void addListener(GameActivityListener listener){
        listeners.add(listener);
    }

    public void notifyWidthChanged(){
        for(GameActivityListener listener : listeners){
            listener.onPlayerWidthChanged();
        }
    }

    public List<Brick> getBricks() {
        return bricks;
    }
    public Point getScreenSize(){
        return screenSize;
    }

    public Boolean isPaused() {
        return pause;
    }

    public void Pause(){
        gameLoop.kill();
        pause=true;
    }

    public void Unpause(){
        gameLoop = new GameLoop(this, fps);
        gameLoop.start();
        pause=false;
    }

    public int getPauseButtonx() {
        return pauseButtonx;
    }

    public int getPauseButtony() {
        return pauseButtony;
    }

    public int getPauseButtonWidth() {
        return pauseButtonWidth;
    }
}
