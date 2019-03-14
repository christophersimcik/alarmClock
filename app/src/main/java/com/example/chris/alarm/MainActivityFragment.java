package com.example.chris.alarm;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static com.example.chris.alarm.MainActivity.hrTime;
import static com.example.chris.alarm.MainActivity.minTime;
import static com.example.chris.alarm.MainActivity.posTime;
import static com.example.chris.alarm.MainActivity.secTime;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {
    final Handler handler = new Handler();
    final Handler backgroundHandler = new Handler();
    Runnable runnable;
    Runnable backgroundRun;
    static String[] todaysDate = new String[2];
    public Boolean setAlarms = false;
    public Locale locale = Locale.getDefault();
    //test moon
    static float synodicTimer = 0f;
    static float timer, ampm;
    FrameLayout fragWindow;
    public static Boolean am = true;
    public static Boolean day = true;
    public int viewHeight;
    BackGroundView backGroundView;
    public float initElevation;
    public FragmentManager fragMan;
    public FloatingActionButton fab;
    public static Boolean visible = true;
    public TimeFragment timeFragment;
    SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm:ss a");
    SimpleDateFormat mins = new SimpleDateFormat("mm");
    SimpleDateFormat secs = new SimpleDateFormat("ss");
    SimpleDateFormat hrs = new SimpleDateFormat("hh");
    SimpleDateFormat hrsTwentyFour = new SimpleDateFormat("HH");

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_main, container, false);
        fab = v.findViewById(R.id.fab);
        fab.setBackgroundTintList(ColorStateList.valueOf(Color.TRANSPARENT));
        fragWindow = v.findViewById(R.id.fragment_alarm_set);
        fragMan = getChildFragmentManager();
        timeFragment = new TimeFragment();
        backGroundView = v.findViewById(R.id.customBackGround);
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
                minTime = Integer.parseInt(mins.format(date));
                secTime = Integer.parseInt(secs.format(date));
                hrTime = Integer.parseInt(hrs.format(date));
                posTime =(Integer.parseInt((hrsTwentyFour.format(date)))*60)+minTime;
                Log.i("mins", String.valueOf(minTime));
                backGroundView.invalidate();
                handler.postDelayed(runnable, 10);


            }
        };

        runnable.run();
        final ValueAnimator alphaAnimator = ValueAnimator.ofInt(255,0);
        alphaAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int alphaVal = (int) valueAnimator.getAnimatedValue();
                fab.setImageAlpha(alphaVal);

            }
        });
        final ValueAnimator valueAnimator = ValueAnimator.ofFloat(1f,0f);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float scaleFactor = (float) valueAnimator.getAnimatedValue();
                fab.setScaleType(ImageView.ScaleType.FIT_CENTER);
                fab.setScaleX(scaleFactor);
                fab.setScaleY(scaleFactor);

            }
        });
        final AnimatorSet animatorSet = new AnimatorSet();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setAlarms = true;
                animatorSet.playTogether(valueAnimator,alphaAnimator);
                animatorSet.setDuration(300);
                animatorSet.setInterpolator(new DecelerateInterpolator());
                animatorSet.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animator) {
                        initElevation = fab.getElevation();
                        fab.setElevation(0f);
                    }

                    @Override
                    public void onAnimationEnd(Animator animator) {
                        SetAlarmFragment setAlarmFragment = new SetAlarmFragment();
                        fragMan.beginTransaction().remove(fragMan.findFragmentById(R.id.fragment_alarm_set)).commit();
                        fragMan.beginTransaction().add(R.id.fragment_alarm_set, setAlarmFragment, "alarm").commit();
                    }

                    @Override
                    public void onAnimationCancel(Animator animator) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animator) {
                    }
                });
                animatorSet.start();
            }

        });
        backgroundRun = new Runnable() {
            @Override
            public void run() {
                if(!setAlarms) {
                    Drawable drawable = getActivity().getDrawable(R.drawable.plus_icon_full);
                    drawable.mutate().setColorFilter(BackGroundView.fabColor, PorterDuff.Mode.SRC_ATOP);
                    fab.setImageDrawable(drawable);
                }else{
                    Drawable drawable = getActivity().getDrawable(R.drawable.plus_icon_full);
                    drawable.mutate().setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_ATOP);
                    fab.setImageDrawable(drawable);
                }
                handler.postDelayed(backgroundRun,100);

            }
        };
        todaysDate = getDate();
        backgroundRun.run();
        if(fragMan.findFragmentById(R.id.fragment_alarm_set ) == null) {
            TimeFragment timeFragment = new TimeFragment();
            fragMan.beginTransaction().add(R.id.fragment_alarm_set,timeFragment,"time").commit();
        }

        return v;
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
        handler.removeCallbacks(backgroundRun);
        }
    public void enableFab(){
        fab.setEnabled(true);
        Log.i("image alpha = ", String.valueOf(fab.getImageAlpha()));
        fab.setImageAlpha(255);
        fab.setElevation(initElevation);
        //fab.show();
    }
    public void restoreFAB(FloatingActionButton fabVal){
        //fabVal.setScaleX(1);
        //fabVal.setScaleY(1);
        //fabVal.setEnabled(false);
    }
    public void animateFabAlarm(){
        fab.setElevation(0f);
        final ValueAnimator alphaSetAnimator = ValueAnimator.ofInt(0,100);
        alphaSetAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int alphaVal = (int) valueAnimator.getAnimatedValue();
                fab.setImageAlpha(alphaVal);

            }
        });
        final ValueAnimator valueSetAnimator = ValueAnimator.ofFloat(0f,1f);
        valueSetAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float scaleFactor = (float) valueAnimator.getAnimatedValue();
                fab.setScaleType(ImageView.ScaleType.FIT_CENTER);
                fab.setScaleX(scaleFactor);
                fab.setScaleY(scaleFactor);

            }
        });
        AnimatorSet setAlarmSet = new AnimatorSet();
        setAlarmSet.playTogether(alphaSetAnimator,valueSetAnimator);
        setAlarmSet.setDuration(300);
        setAlarmSet.setInterpolator(new DecelerateInterpolator());
        setAlarmSet.start();
    }
    public String[] getDate() {
        String[] dayDate = new String[2];
        Calendar calendar = Calendar.getInstance(locale);
        String dayOfWeek = getDay(calendar.get(Calendar.DAY_OF_WEEK));
        String monthOfYear = getMonth(calendar.get(Calendar.MONTH));
        dayDate[0] = monthOfYear + ", " + getDate(calendar) + " " + getYear(calendar);
        dayDate[1] = dayOfWeek;
        return dayDate;
    }
    public String getDate(Calendar cal){
        return String.valueOf(cal.get(Calendar.DAY_OF_MONTH));
    }
    public String getYear(Calendar cal){
        return String.valueOf(cal.get(Calendar.YEAR));
    }
    public String getMonth(int month) {
        String aMonth;
        switch (month) {
            case 0:
                aMonth = "Jan";
                break;
            case 1:
                aMonth = "Feb";
                break;
            case 2:
                aMonth = "Mar";
                break;
            case 3:
                aMonth = "Apr";
                break;
            case 4:
                aMonth = "May";
                break;
            case 5:
                aMonth = "Jun";
                break;
            case 6:
                aMonth = "Jul";
                break;
            case 7:
                aMonth = "Aug";
                break;
            case 8:
                aMonth = "Sep";
                break;
            case 9:
                aMonth = "Oct";
                break;
            case 10:
                aMonth = "Nov";
                break;
            case 11:
                aMonth = "Dec";
                break;
            default:
                aMonth = "error";
        }
        return aMonth;
    }
    public String getDay(int day){
        String aDay;
        switch(day){
            case 1: aDay = "Sunday";
                break;
            case 2: aDay = "Monday";
                break;
            case 3: aDay = "Tuesday";
                break;
            case 4: aDay = "Wednesday";
                break;
            case 5: aDay = "Thursday";
                break;
            case 6: aDay = "Friday";
                break;
            case 7: aDay = "Saturday";
                break;
            default: aDay = "error";
        }
        return aDay;
    }
        }


