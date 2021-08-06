package com.example.catchit;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;

import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    //Frame
    private FrameLayout gameFrame;
    private int frameHeight, frameWidth, initialFrameWidth;
    private LinearLayout startLayout;

    //Images
    private ImageView basket, acorn, nuts, spider;

    //Size
    private int basketSize;

    //Position
    private float basketX, basketY;
    private float spiderX, spiderY;
    private float nutsX, nutsY;
    private float acornX, acornY;

    //Score
    private TextView scoreLabel, highScoreLabel;
    private int score, highScore, timeCount;
    private SharedPreferences settings;

    //Class
    private Timer timer;
    private Handler handler = new Handler();

    //Status
    private boolean start_flg=false;
    private boolean action_flg=false;

    private boolean nuts_flg=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gameFrame=findViewById(R.id.gameFrame);
        startLayout=findViewById(R.id.startLayout);
        basket=findViewById(R.id.basket);
        spider=findViewById(R.id.spider);
        nuts=findViewById(R.id.almond);
        acorn=findViewById(R.id.acorn);
        scoreLabel=findViewById(R.id.scoreLabel);
        highScoreLabel=findViewById(R.id.highScoreLabel);


        //High Score
        settings=getSharedPreferences("GAME_DATA", Context.MODE_PRIVATE);
        highScore=settings.getInt("HIGH_SCORE", 0);
        highScoreLabel.setText("High Score : "+ highScore);
    }

    public void changePos(){
        //Add timeCount
        timeCount+=20;

        //Acorn
        acornY+=12;
        float acornCenterX=acornX+acorn.getWidth()/2;
        float acornCenterY=acornY+acorn.getHeight()/2;

        if (hitCheck(acornCenterX,acornCenterY)) {
            acornY=frameHeight +100;
            score+= 10;
        }

        if (acornY>frameHeight){
            acornY=-100;
            acornX= (float) Math.floor(Math.random()*(frameWidth-acorn.getWidth()));
        }
        acorn.setX(acornX);
        acorn.setY(acornY);

        //nuts
        if (!nuts_flg && timeCount % 10000 ==0){
            nuts_flg=true;
            nutsY=-20;
            nutsX=(float) Math.floor(Math.random()* (frameWidth-nuts.getWidth()));
        }
        if (nuts_flg){
            nutsY+=20;
            float nutsCenterX=nutsX + nuts.getWidth()/2;
            float nutsCenterY=nutsY+ nuts.getHeight()/2;

            if(hitCheck(nutsCenterX,nutsCenterY)){
                nutsY=frameHeight+30;
                score+=30;
                //Change the FrameWidth
                if (initialFrameWidth>frameWidth*11/10) {
                    frameWidth=frameWidth*11/10;
                    changeFrameWidth(frameWidth);
                }
            }
            if (nutsY>frameHeight) nuts_flg=false;
            nuts.setY(nutsY);
            nuts.setX(nutsX);
        }

        //spider
        spiderY+=18;
        float spiderCenterX=spiderX+spider.getWidth()/2;
        float spiderCenterY=spiderY+spider.getHeight()/2;

        if (hitCheck(spiderCenterX,spiderCenterY)){
            spiderY=frameHeight+100;
            //change FrameWidth
            frameWidth=frameWidth*8/10;
            changeFrameWidth(frameWidth);
        }
        if (spiderY>frameHeight){
            spiderY=-100;
            spiderX=(float) Math.floor(Math.random()*(frameWidth-spider.getWidth()));
        }
        spider.setX(spiderX);
        spider.setY(spiderY);

        //Move basket
        if (action_flg){
            // moving right
            basketX+=10;
        } else{
            //going left
            basketX-=10;
        }

        //Checking basket position
        if (basketX <0){
            basketX=0;
        }
        if (frameWidth-basketSize<=basketX){
            basketX=frameWidth-basketSize;
        }
        basket.setX(basketX);

        scoreLabel.setText("Score : "+ score);
    }

    public void changeFrameWidth(int frameWidth) {
        ViewGroup.LayoutParams params=gameFrame.getLayoutParams();
        params.width=frameWidth;
        gameFrame.setLayoutParams(params);

        if (frameWidth<=basketSize+10) {
            gameOver();
        }
    }

    public void gameOver() {
        //stop timer
        timer.cancel();
        timer=null;
        start_flg=false;

        //sleep 1 second before showing startLayout
        try{
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e){
            e.printStackTrace();
        }

        changeFrameWidth(initialFrameWidth);
        startLayout.setVisibility(View.VISIBLE);
        basket.setVisibility(View.INVISIBLE);
        spider.setVisibility(View.INVISIBLE);
        acorn.setVisibility(View.INVISIBLE);
        nuts.setVisibility(View.INVISIBLE);

        //Update high score
        if (score>highScore){
            highScore=score;
            highScoreLabel.setText("High Score : "+ highScore);

            SharedPreferences.Editor editor=settings.edit();
            editor.putInt("HIGH_SCORE", highScore);
            editor.commit();
        }
    }

    public boolean hitCheck(float x, float y){
        if (basketX<=x && x<= basketX+basketSize &&
                basketY<=y && y<+frameHeight) {
            return true;
        }
        return false;
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (start_flg) {
            if (event.getAction()==MotionEvent.ACTION_DOWN){
                action_flg=true;
            }  else if (event.getAction()==MotionEvent.ACTION_UP) {
               action_flg= false;
            }
        }
        return true;
    }

    public void startGame(View view){
        start_flg=true;
        startLayout.setVisibility(View.INVISIBLE);

        if (frameHeight==0) {
            frameHeight=gameFrame.getHeight();
            frameWidth=gameFrame.getWidth();
            initialFrameWidth=frameWidth;

            basketSize=basket.getWidth();
            basketX=basket.getX();
            basketY=basket.getY();
        }
        frameWidth=initialFrameWidth;

        basket.setX(0.0f);
        spider.setY(3000.0f);
        acorn.setY(3000.0f);
        nuts.setY(3000.0f);

        spiderY=spider.getY();
        acornY=acorn.getY();
        nutsY=nuts.getY();

        basket.setVisibility(View.VISIBLE);
        spider.setVisibility(View.VISIBLE);
        acorn.setVisibility(View.VISIBLE);
        nuts.setVisibility(View.VISIBLE);

        timeCount=0;
        score=0;
        scoreLabel.setText("Score : 0");

        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (start_flg) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            changePos();
                        }
                    });
                }
            }
        }, 0, 20);
    }


}
