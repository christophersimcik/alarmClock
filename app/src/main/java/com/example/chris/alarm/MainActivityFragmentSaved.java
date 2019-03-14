package com.example.chris.alarm;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

import static com.example.chris.alarm.MainActivity.hrTime;
import static com.example.chris.alarm.MainActivity.minTime;
import static com.example.chris.alarm.MainActivity.posTime;
import static com.example.chris.alarm.MainActivity.secTime;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragmentSaved extends Fragment {
    final Handler handler = new Handler();
    Runnable runnable;
    //test moon
    static float synodicTimer = 0f;
    static float timer, ampm;
    FrameLayout fragWindow;
    public static Boolean am = true;
    public static Boolean day = true;
    public int viewHeight;
    BackGroundView backGroundView;
    public FloatingActionButton fab;
    public FragmentManager fragMan;
    public static Boolean visible = true;
    public FragmentTransaction fragmentTransaction;
    public SetAlarmFragment setAlarmFragment;
    public static TextView dateView, dayView, timeView, posView;
    SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm:ss a");
    SimpleDateFormat mins = new SimpleDateFormat("mm");
    SimpleDateFormat secs = new SimpleDateFormat("ss");
    SimpleDateFormat hrs = new SimpleDateFormat("hh");
    SimpleDateFormat hrsTwentyFour = new SimpleDateFormat("HH");

    int textColors = Color.argb(175, 225, 225, 225);
    public MainActivityFragmentSaved() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_main, container, false);
        fab = v.findViewById(R.id.fab);
        fragWindow = v.findViewById(R.id.fragment_alarm_set);
        fragMan = getFragmentManager();
        backGroundView = v.findViewById(R.id.customBackGround);
     ;
        backGroundView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
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
        dateView = v.findViewById(R.id.date);
        dayView = v.findViewById(R.id.day);
        timeView = v.findViewById(R.id.times);
        posView =v.findViewById(R.id.day_position);
        timeView.setTextSize(32);
        dayView.setTextSize(24);
        dateView.setTextSize(18);
        timeView.setTextColor(textColors);
        dayView.setTextColor(textColors);
        dateView.setTextColor(textColors);
        backGroundView.post(new Runnable() {
            @Override
            public void run() {
                viewHeight = backGroundView.getHeight();
                Log.i("viewheight",String.valueOf(viewHeight));
            }
        });

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
                if(synodicTimer < MainActivity.synodicMonth) {
                    synodicTimer += .1f;
                }else{
                    synodicTimer = 0;
                }
                backGroundView.updateTime();
                //Log.i("timer value = ", String.valueOf(timer));
                timeView.setText(timeFormat.format(date));
                minTime = Integer.parseInt(mins.format(date));
                secTime = Integer.parseInt(secs.format(date));
                hrTime = Integer.parseInt(hrs.format(date));
                posTime =(Integer.parseInt((hrsTwentyFour.format(date)))*60)+minTime;
                posView.setText(String.valueOf(posTime));
                Log.i("mins", String.valueOf(minTime));
                backGroundView.invalidate();
                handler.postDelayed(runnable, 10);

            }
        };

            runnable.run();
        refreshDateView();
        return v;
    }
    public static void refreshDateView(){
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
    public void fragSwap(){
        if(visible) {
            setAlarmFragment = new SetAlarmFragment();
            fragMan.beginTransaction().replace(R.id.fragment_alarm_set,setAlarmFragment, "setalarm").commit();
            Log.i("OONA = ", String.valueOf(fragMan.getFragments().size()));
            visTime();
        }


    }
    public static void visTime() {
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

}
