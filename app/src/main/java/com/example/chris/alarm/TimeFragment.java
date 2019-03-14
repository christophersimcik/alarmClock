package com.example.chris.alarm;

import android.animation.Animator;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;


public class TimeFragment extends Fragment {
    TextView dateView,dayView,timeView;
    int textColor;
    public TimeFragment(){
    }
    final Handler handler = new Handler();
    Runnable runnable;
    //test moon
    static float synodicTimer = 0f;
    static float timer, ampm;
    FrameLayout fragWindow;
    public static Boolean am = true;
    public static Boolean day = true;
    public int viewHeight;
    MainActivity mainActivity;
    BackGroundView backGroundView;
    public FragmentManager fragMan;
    public static Boolean visible = true;
    public SetAlarmFragment setAlarmFragment;
    SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm:ss a");
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.time_fragment, container, false);
        if(savedInstanceState != null && savedInstanceState.containsKey("timer")) {
            timer = savedInstanceState.getFloat("timer");
        }else{
            timer = 0f;
        }
        if(savedInstanceState != null && savedInstanceState.containsKey("am/pm")) {
            am = savedInstanceState.getBoolean("am/pm");
        }else{
            am =true;
        }
        mainActivity = (MainActivity) getActivity();
        mainActivity.getColor(R.color.trans_white);
        dateView = v.findViewById(R.id.date);
        dayView = v.findViewById(R.id.day);
        timeView = v.findViewById(R.id.times);;
        timeView.setTextSize(32);
        dayView.setTextSize(24);
        dateView.setTextSize(18);
        textColor = getResources().getColor(R.color.trans_white,null);
        timeView.setTextColor(textColor);
        dayView.setTextColor(textColor);
        dateView.setTextColor(textColor);
        revealAnimationInit(v);
        runnable = new Runnable() {
            @Override
            public void run() {
                Date date = new Date();
                if(ampm > 2){
                    if(day){
                        day = false;
                    }else{
                        day = true;
                    }
                    ampm = 0;
                }
                ampm += .0025;
                Log.i("day = ", String.valueOf(day));
                if (am) {
                    timer += .0025;
                } else {
                    timer -= .0025;
                }
                if (timer > 1) {
                    am = false;
                }
                if (timer < 0) {
                    am = true;
                }
                if(synodicTimer < mainActivity.synodicMonth) {
                    synodicTimer += .1f;
                }else{
                    synodicTimer = 0;
                }
                //Log.i("timer value = ", String.valueOf(timer));
                timeView.setText(timeFormat.format(date));
                handler.postDelayed(runnable, 10);

            }
        };

        runnable.run();
        refreshDateView();
        return v;
    }
    public void refreshDateView(){
        dateView.setText(MainActivityFragment.todaysDate[0]);
        dayView.setText(MainActivityFragment.todaysDate[1]);




    }
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState){
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putFloat("timer",timer);
        savedInstanceState.putBoolean("am/pm", am);
    }
    @Override
    public void onPause(){
        super.onPause();
        handler.removeCallbacks(runnable);
    }
    /*public void fragSwap(){
        if(visible) {
            setAlarmFragment = new SetAlarmFragment();
            fragMan.beginTransaction().remove(fragMan.findFragmentByTag("time")).commit();
            fragMan.beginTransaction().add(R.id.fragment_alarm_set, setAlarmFragment, "alarm").commit();
            visTime();
        }


    }*/
    public  void visTime() {
        if (visible) {
            dateView.setVisibility(View.INVISIBLE);
            dayView.setVisibility(View.INVISIBLE);
            timeView.setVisibility(View.INVISIBLE);
            visible = false;
        } else {
            dateView.setVisibility(View.VISIBLE);
            dayView.setVisibility(View.VISIBLE);
            timeView.setVisibility(View.VISIBLE);
            visible = true;
        }
    }
    public void revealAnimationInit(final View a){
        ViewTreeObserver viewTreeObserver = a.getViewTreeObserver();
        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int vMidX = a.getWidth()/2,vMidY = a.getHeight()/2;
                a.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    Animator circularRevalAnim  = ViewAnimationUtils.createCircularReveal(
                            a,
                            vMidX,
                            vMidY,
                            0,
                            Math.min(vMidX,vMidY)/2);
                circularRevalAnim.start();
            }

        });
    }

}
