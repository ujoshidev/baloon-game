package com.example.utkarsh.baloon_game;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements Baloon.BaloonListner {

    private ViewGroup mContentView;
    private int[] mColor = new int[3];
    private int xx,mScrHeight,mScrWidth;
    public static final int min_delay=500;
    public static final int max_delay=1000;
    public static final int min_duration=1500;
    public static final int max_duration=8000;
    public static final int num_of_pins=5;
    private int level,pinUsed;
    private int score;
    TextView scoreDisplay,levelDisplay;

    private List<ImageView> mPinImages = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContentView = (ViewGroup) findViewById(R.id.activity_main);
        setToFullScreen();

        ViewTreeObserver vto = mContentView.getViewTreeObserver();
        if(vto.isAlive()){
            vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    mContentView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    mScrWidth=mContentView.getWidth();
                    mScrHeight=mContentView.getHeight();
                }
            });
        }

        getWindow().setBackgroundDrawableResource(R.drawable.modern_background);

        mContentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setToFullScreen();
            }
        });


        mColor[0]= Color.argb(255,255,0,0);
        mColor[1]= Color.argb(255,0,255,0);
        mColor[2]= Color.argb(255,0,0,255);

/*         WHEN CLICK THE SCREEN THEN BALLONS WILL START LOADING...

        mContentView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent motionEvent) {
                if(motionEvent.getAction()==MotionEvent.ACTION_UP){
                    Baloon b = new Baloon(MainActivity.this,mColor[xx],100);
                    b.setX(motionEvent.getX());
                    b.setY(mScrHeight);
                    mContentView.addView(b);
                    b.releaseBaloon(mScrHeight,3000);

                if(xx+1==mColor.length){
                    xx=0;
                }else {
                    xx++;
                }
                }

                return false;

            }
        });
*/

        levelDisplay = (TextView)findViewById(R.id.level_display);
        scoreDisplay = (TextView)findViewById(R.id.score_display);
        updateDisplay();

        mPinImages.add((ImageView) findViewById(R.id.pin1));
        mPinImages.add((ImageView) findViewById(R.id.pin2));
        mPinImages.add((ImageView) findViewById(R.id.pin3));
        mPinImages.add((ImageView) findViewById(R.id.pin4));
        mPinImages.add((ImageView) findViewById(R.id.pin5));

    }

    private void setToFullScreen(){
        ViewGroup rootLayout = (ViewGroup) findViewById(R.id.activity_main);
        rootLayout.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);


    }

    @Override
    protected void onResume() {
        super.onResume();
        setToFullScreen();
    }

    private void startLevel(){
        level++;
        updateDisplay();
        BaloonLauncher bl=new BaloonLauncher();
        bl.execute(level);
    }

    public void startGame(View view) {
        startLevel();
    }


    private class BaloonLauncher extends AsyncTask<Integer,Integer,Void>{

        @Override
        protected Void doInBackground(Integer... params) {

            if(params.length!=1){
                throw new AssertionError("Expected 1 parameter for Current Level");
            }

            int level = params[0];
            int maxDelay = Math.max(min_delay,
                    (max_delay- ((level - 1)*500)));

            int minDelay=maxDelay/2;
            int baloonsLaunched=0;
            while(baloonsLaunched<3){
                Random rand = new Random(new Date().getTime());
                int xPos=rand.nextInt(mScrWidth-200);
                publishProgress(xPos);
                baloonsLaunched++;

                int delay = rand.nextInt(minDelay)+minDelay;
                try{
                    Thread.sleep(delay);
                }catch(Exception e){
                    e.printStackTrace();
                }
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            int xPos = values[0];
            launchBaloon(xPos);
        }


    }

    private void launchBaloon(int xPos) {
        Baloon b = new Baloon(this, mColor[xx],150);
        if(xx+1==mColor.length){
            xx=0;
        }else {
            xx++;
        }

        b.setX(xPos);
        b.setY(mScrHeight+b.getHeight());
        mContentView.addView(b);

        int duration = Math.max(min_duration,max_duration - (level*1000));
        b.releaseBaloon(mScrHeight,duration);
    }


    @Override
    public void popBaloon(Baloon baloon, boolean userTouch) {
        mContentView.removeView(baloon);
        if(userTouch){
            score++;
        }
        else{
            //TODO: popping Baloon
        }
        updateDisplay();

    }

    private void updateDisplay() {
        scoreDisplay.setText(String.valueOf(score));
        levelDisplay.setText(String.valueOf(level));
    }

}
