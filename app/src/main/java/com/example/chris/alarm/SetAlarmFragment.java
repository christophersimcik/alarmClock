package com.example.chris.alarm;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.shapes.ArcShape;
import android.graphics.drawable.shapes.Shape;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.graphics.ColorUtils;
import android.util.Log;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;
import java.util.zip.Inflater;

import it.beppi.knoblibrary.Knob;

public class SetAlarmFragment extends Fragment {
    public Knob hoursKnob, minsKnob;
    public int aMpM = 0, oldHour, oldMin;
    public int hoursCol,minsCol;
    public CircleView circleView;
    private ViewTreeObserver viewTreeObserver;
    public TextView hourText,minText,colonText,ampmText,cancelButton;
    public Size hoursSize, minsSize;
    public SetAlarmFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.set_alarm_fragment, container, false);
        hoursCol = BackGroundView.fabColor;
        hoursKnob = v.findViewById(R.id.hours_knob);
        hoursKnob.setCircularIndicatorColor(BackGroundView.fabColor);
        minsKnob = v.findViewById(R.id.mins_knob);
        hourText = v.findViewById(R.id.hours_text);
        minText = v.findViewById(R.id.mins_text);
        colonText = v.findViewById(R.id.colon_text);
        ampmText = v.findViewById(R.id.am_pm_text);
        cancelButton = v.findViewById(R.id.cancel_button);
        circleView = v.findViewById(R.id.alarm_set_background);
        circleRevealInit(v);
        //Init AM/PM
        if (MainActivityFragment.am) {
            ampmText.setText("AM");
            aMpM = 0;
        } else {
            ampmText.setText("PM");
            aMpM = 1;
        }
        //retrieve scaled dims
        setDimensions(hoursKnob, minsKnob);
        circleView.setRadius(hoursSize.getWidth() / 2);
        circleView.setAlpha(.25f);
        circleView.setColor(Color.BLACK);
        hoursKnob.setState(0);
        minsKnob.setState(0);
        final int[] colors = new int[2];
        hoursKnob.setOnStateChanged(new Knob.OnStateChanged() {
            @Override
            public void onState(int i) {
                if (i != oldHour) {
                    hoursKnob.setCircularIndicatorColor(BackGroundView.fabColor);
                    hoursCol = BackGroundView.fabColor;
                    drawHours();
                    if (i >= 1) {
                        hourText.setText(String.valueOf(i));
                    } else {
                        hourText.setText("12");
                        if (aMpM == 1) {
                            aMpM = 0;
                        } else {
                            aMpM = 1;
                        }
                    }
                    if (aMpM == 1) {
                        ampmText.setText("PM");
                    } else {
                        ampmText.setText("AM");
                    }
                }
                oldHour = i;
            }
        });
        minsKnob.setOnStateChanged(new Knob.OnStateChanged() {
            @Override
            public void onState(int i) {
                drawMins();
                if (String.valueOf(i).length() > 1) {
                    minText.setText(String.valueOf(i));
                } else {
                    minText.setText("0" + String.valueOf(i));
                }
            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelAnimator();
                MainActivityFragment main = (MainActivityFragment) getParentFragment();
                main.setAlarms = false;
                main.enableFab();

                }
        });
        //draw the knobs
        drawHours();
        drawMins();
        return v;
    }
    public void setDimensions(Knob hours, Knob mins){
        hoursSize = new Size(Circles.hoursDiam,Circles.hoursDiam);
        int minW = hoursSize.getWidth()-200,minH = hoursSize.getHeight()-200;
        minsSize = new Size(minW,minH);
        hours.getLayoutParams().height = hoursSize.getHeight();
        hours.getLayoutParams().width = hoursSize.getWidth();
        mins.getLayoutParams().height = minsSize.getHeight();
        mins.getLayoutParams().width = minsSize.getWidth();
    }
    public void drawHours(){
        hoursKnob.setCircularIndicatorRelativeRadius(50f/((float)hoursSize.getWidth()/2));
        hoursKnob.setCircularIndicatorRelativePosition(1f-(50f/((float)hoursSize.getWidth()/2f)));
        Bitmap bitmap = Bitmap.createBitmap(hoursSize.getWidth(),hoursSize.getWidth(), Bitmap.Config.ARGB_8888);
        Bitmap innerHours = Bitmap.createBitmap(hoursSize.getWidth(),hoursSize.getWidth(), Bitmap.Config.ARGB_8888);
        Canvas mCanvas = new Canvas(bitmap);
        Log.i("state = ", String.valueOf(hoursKnob.getState()));
        float sweep = (360/12)*hoursKnob.getState();
        Paint paint = new Paint();
        Paint xferPaint = new Paint();
        Paint controlPaint = new Paint();
        controlPaint.setColor(Color.WHITE);
        xferPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
        int[] colors = new int[]{
                ColorUtils.setAlphaComponent(hoursCol,10),
                hoursCol,
                Color.TRANSPARENT
        };
        float[] hoursGradPositions = new float[]{0f,sweep/360f,sweep/360f};
        Log.i("values sweep = ", String.valueOf(hoursGradPositions[0]) + " " + String.valueOf(hoursGradPositions[1]));
        SweepGradient hoursGradient = new SweepGradient(mCanvas.getWidth()/2,mCanvas.getHeight()/2,colors,hoursGradPositions);
        paint.setShader(hoursGradient);
        paint.setAntiAlias(true);
        mCanvas.rotate(-90,mCanvas.getWidth()/2,mCanvas.getHeight()/2);
        mCanvas.drawCircle(mCanvas.getWidth()/2,mCanvas.getHeight()/2,mCanvas.getWidth()/2,paint);
        mCanvas.setBitmap(innerHours);
        mCanvas.drawCircle(mCanvas.getWidth()/2,mCanvas.getHeight()/2,(mCanvas.getWidth()/2)-100,controlPaint);
        mCanvas.setBitmap(bitmap);
        mCanvas.drawBitmap(innerHours,0,0,xferPaint);
        mCanvas.rotate(90,mCanvas.getWidth()/2,mCanvas.getHeight()/2);
        Drawable drawable = new BitmapDrawable(getResources(),bitmap);
        hoursKnob.setKnobDrawable(drawable);
        hoursKnob.invalidate();

    }
    public void drawMins(){
        minsKnob.setCircularIndicatorRelativeRadius(35f/((float)minsSize.getWidth()/2));
        minsKnob.setCircularIndicatorRelativePosition(1f-(35f/((float)minsSize.getWidth()/2f)));
        Bitmap bitmap = Bitmap.createBitmap(minsSize.getWidth(),minsSize.getWidth(), Bitmap.Config.ARGB_8888);
        Bitmap innerHours = Bitmap.createBitmap(minsSize.getWidth(),minsSize.getWidth(), Bitmap.Config.ARGB_8888);
        Canvas mCanvas = new Canvas(bitmap);
        float sweep = (360/60)*minsKnob.getState();
        Paint paint = new Paint();
        Paint xferPaint = new Paint();
        Paint controlPaint = new Paint();
        controlPaint.setColor(Color.WHITE);
        xferPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
        int[] colors = new int[]{
                getResources().getColor(R.color.lo_trans_white, null),
                getResources().getColor(R.color.white,null),
                Color.TRANSPARENT
        };
        float[] hoursGradPositions = new float[]{0f,sweep/360f,sweep/360f};
        Log.i("values sweep = ", String.valueOf(hoursGradPositions[0]) + " " + String.valueOf(hoursGradPositions[1]));
        SweepGradient hoursGradient = new SweepGradient(mCanvas.getWidth()/2,mCanvas.getHeight()/2,colors,hoursGradPositions);
        paint.setShader(hoursGradient);
        paint.setAntiAlias(true);
        mCanvas.rotate(-90,mCanvas.getWidth()/2,mCanvas.getHeight()/2);
        mCanvas.drawCircle(mCanvas.getWidth()/2,mCanvas.getHeight()/2,mCanvas.getWidth()/2,paint);
        mCanvas.setBitmap(innerHours);
        mCanvas.drawCircle(mCanvas.getWidth()/2,mCanvas.getHeight()/2,(mCanvas.getWidth()/2)-70,controlPaint);
        mCanvas.setBitmap(bitmap);
        mCanvas.drawBitmap(innerHours,0,0,xferPaint);
        mCanvas.rotate(90,mCanvas.getWidth()/2,mCanvas.getHeight()/2);
        Drawable drawable = new BitmapDrawable(getResources(),bitmap);
        minsKnob.setKnobDrawable(drawable);
        minsKnob.invalidate();

    }
    public void cancelAnimator(){
        final ValueAnimator cancelAnim = ValueAnimator.ofFloat(cancelButton.getAlpha(),1f);
        final ValueAnimator scaleAmim = ValueAnimator.ofFloat(1f,1.15f);
        final AnimatorSet cancelSet = new AnimatorSet();
        cancelSet.playTogether(cancelAnim,scaleAmim);
        scaleAmim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator scaler) {
                float scaleVal = (float) scaler.getAnimatedValue();
                cancelButton.setScaleX(scaleVal);
                cancelButton.setScaleY(scaleVal);
            }
        });
        cancelAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator alphavalue) {
                float alphaVal = (float) alphavalue.getAnimatedValue();
                cancelButton.setAlpha(alphaVal);
            }
        });
        cancelSet.setDuration(100);
        cancelSet.setInterpolator(new DecelerateInterpolator());
        cancelSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
            FragmentManager fragMan = getChildFragmentManager();
            TimeFragment timeFragment = new TimeFragment();
            fragMan.beginTransaction().remove(fragMan.findFragmentById(R.id.fragment_alarm_set));
            fragMan.beginTransaction().add(R.id.fragment_alarm_set,timeFragment,"time").commit();
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        cancelSet.start();
    }
    private void circleRevealInit(final View a){
        viewTreeObserver = a.getViewTreeObserver();
        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                a.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                int viewCenterX = a.getWidth()/2, viewCenterY = a.getHeight()/2;
                Animator circularRevalAnim  = ViewAnimationUtils.createCircularReveal(
                        a,
                        viewCenterX,
                        viewCenterY,
                        0,
                        circleView.radius);
                circularRevalAnim.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animator) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animator) {
                        MainActivityFragment main = (MainActivityFragment)getParentFragment();
                        main.animateFabAlarm();
                    }

                    @Override
                    public void onAnimationCancel(Animator animator) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animator) {

                    }
                });
                circularRevalAnim.start();
            }
        });
    }


}
