package iut.android.brickyclimb.views;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.text.TextPaint;
import android.view.View;

import iut.android.brickyclimb.activities.GameActivity;
import iut.android.brickyclimb.controllers.GameActivityListener;
import iut.android.brickyclimb.R;
import iut.android.brickyclimb.models.Ball;
import iut.android.brickyclimb.models.Brick;
import iut.android.brickyclimb.models.Player;

public class GameView extends View implements GameActivityListener {

    private Bitmap playerBitmap;
    private Bitmap playerSrc;

    private Paint ballPaint;
    private Paint ballBorderPaint;
    private int ballBorderWidth;
    private TextPaint scorePaint;
    private Paint playPausePaint;
    private Paint brickPaintHigh;
    private Paint brickPaintMid;
    private Paint brickPaintLow;
    private Paint brickBorderPaint;
    private int brickBorderWidth;

    private GameActivity context;

    public GameView(GameActivity context) {
        super(context);
        this.context = context;
        context.addListener(this);

        setBackgroundColor(Color.BLACK);

        playerSrc = BitmapFactory.decodeResource(getResources(), R.drawable.player);
        playerBitmap = Bitmap.createScaledBitmap(playerSrc, context.getPlayer().getWidth(), context.getPlayer().getHeight(), true);

        ballPaint = new Paint();
        ballPaint.setColor(Color.RED);

        ballBorderPaint = new Paint();
        ballBorderPaint.setColor(Color.RED);
        ballBorderWidth=context.getScreenSize().x/216;

        scorePaint = new TextPaint();
        scorePaint.setColor(Color.WHITE);
        scorePaint.setTextSize(context.getScreenSize().x/25f);

        playPausePaint = new Paint();
        playPausePaint.setColor(Color.WHITE);

        brickPaintHigh = new Paint();
        brickPaintHigh.setColor(Color.BLUE);

        brickPaintMid = new Paint();
        brickPaintMid.setColor(0xFFA000FF);

        brickPaintLow = new Paint();
        brickPaintLow.setColor(Color.RED);

        brickBorderPaint = new Paint();
        brickBorderPaint.setColor(Color.YELLOW);
        brickBorderWidth = context.getScreenSize().x/100;

    }

    @Override
    protected void onDraw(Canvas canvas){
        drawBall(canvas, context.getBall());
        drawPlayer(canvas, context.getPlayer());
        for (Brick b : context.getBricks()) drawBrick(canvas, b);
        canvas.drawText(getContext().getString(R.string.score) + context.getScore(), context.getScreenSize().x/22f, context.getScreenSize().y/26f, scorePaint);
        if(context.isPaused()) drawPlayButton(canvas, context.getPauseButtonx(), context.getPauseButtony(), context.getPauseButtonWidth());
        else drawPauseButton(canvas, context.getPauseButtonx(), context.getPauseButtony(), context.getPauseButtonWidth());
    }

    private void drawBall(Canvas canvas, Ball ball){
        canvas.drawCircle(ball.getxPosition(), ball.getyPosition(), ball.getRadius(), ballBorderPaint);
        canvas.drawCircle(ball.getxPosition(), ball.getyPosition(), ball.getRadius()-ballBorderWidth, ballPaint);
    }

    private void drawPlayer(Canvas canvas, Player player){
        canvas.drawBitmap(playerBitmap, player.getxPosition(), player.getyPosition(), null);

    }

    private void drawPauseButton(Canvas canvas, int xPos, int yPos, int width){
        canvas.drawRect(xPos, yPos, xPos+width/3, width, playPausePaint);
        canvas.drawRect(xPos+2*width/3, yPos, xPos+width, width, playPausePaint);
    }

    private void drawPlayButton(Canvas canvas, int xPos, int yPos, int width){
        Path path = new Path();
        path.setFillType(Path.FillType.EVEN_ODD);
        path.moveTo(xPos, yPos);
        path.lineTo(xPos+width, yPos+width/2);
        path.lineTo(xPos, yPos+width);
        path.lineTo(xPos, yPos);
        path.close();
        canvas.drawPath(path, playPausePaint);
    }

    private void drawBrick(Canvas canvas, Brick brick){
        canvas.drawRect(brick.getxPosition(), brick.getyPosition(), brick.getxPosition()+brick.getWidth(), brick.getyPosition()+brick.getHeight(), brickBorderPaint);
        Paint brickPaint = brickPaintHigh;
        if(brick.getPv()<=Brick.LOW) brickPaint = brickPaintLow;
        else if (brick.getPv()<=Brick.MID) brickPaint = brickPaintMid;
        canvas.drawRect(brick.getxPosition()+brickBorderWidth, brick.getyPosition()+brickBorderWidth, brick.getxPosition()+brick.getWidth()-brickBorderWidth, brick.getyPosition()+brick.getHeight()-brickBorderWidth, brickPaint);
        canvas.drawText(String.valueOf(brick.getPv()), brick.getxPosition()+brick.getWidth()/2f, brick.getyPosition()+brick.getHeight()/2f, scorePaint);
    }

    @Override
    public void onPlayerWidthChanged() {
        playerBitmap = Bitmap.createScaledBitmap(playerSrc, context.getPlayer().getWidth(), context.getPlayer().getHeight(), true);
    }
}
