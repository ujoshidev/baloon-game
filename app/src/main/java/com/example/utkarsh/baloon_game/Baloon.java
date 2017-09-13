package com.example.utkarsh.baloon_game;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

import com.example.utkarsh.baloon_game.utils.PixelHelper;

/**
 * Created by Utkarsh on 23-01-2017.
 */

public class Baloon extends ImageView implements ValueAnimator.AnimatorUpdateListener, Animator.AnimatorListener {

    private ValueAnimator mAnimator;
    private  BaloonListner mListner;
    private boolean isPop;
    public Baloon(Context context) {
        super(context);
    }

    public Baloon(Context context, int color, int rawHeight) {
        super(context);
        mListner = (BaloonListner) context;

        this.setImageResource(R.drawable.balloon);
        this.setColorFilter(color);

        int rawWidth = rawHeight/2;

        int dpHeight = PixelHelper.pixelsToDp(rawHeight,context);
        int dpWidth = PixelHelper.pixelsToDp(rawWidth,context);

        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(dpWidth, dpHeight);
        setLayoutParams(params);
    }

    public void releaseBaloon(int screenHeight, int duration){
        mAnimator=new ValueAnimator();
        mAnimator.setDuration(duration);
        mAnimator.setFloatValues(screenHeight, 0f);
        mAnimator.setInterpolator(new LinearInterpolator());
        mAnimator.setTarget(this);
        mAnimator.addListener(this);
        mAnimator.addUpdateListener(this);
        mAnimator.start();
    }

    @Override
    public void onAnimationUpdate(ValueAnimator animation) {
        setY((float)mAnimator.getAnimatedValue());
    }

    @Override
    public void onAnimationStart(Animator animation) {
    }

    @Override
    public void onAnimationEnd(Animator animation) {
        if(!isPop){
            mListner.popBaloon(this,false);
        }
    }

    @Override
    public void onAnimationCancel(Animator animation) {
    }

    @Override
    public void onAnimationRepeat(Animator animation) {
    }

    public interface BaloonListner {
      public void popBaloon(Baloon baloon, boolean touched);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(!isPop && event.getAction()== MotionEvent.ACTION_DOWN){
            mListner.popBaloon(this,true);
            isPop=true;
            mAnimator.cancel();
        }
        return super.onTouchEvent(event);
    }
}
