package com.example.chris.alarm;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.ColorSpace;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.PorterDuffXfermode;
import android.graphics.RadialGradient;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.Xfermode;
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.support.v4.graphics.ColorUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.util.Random;
import java.util.Vector;


public class BackGroundView extends View {
    public static int clockDiameter;
    private int[] top = new int[2];
    private int counter = 0;
    // int to text if vals need updating
    private int[] bottom = new int[2];
    public static int[] tempCols = new int[2];
    public float h, s, v = 1f;
    public Circles circles;
    private int sunrayCol = Color.argb(0,255,255,255);
    private int[] sunRays = new int[]{Color.TRANSPARENT,sunrayCol,Color.TRANSPARENT};
    private int[] nimbus = new int[]{
            Color.TRANSPARENT,
            getResources().getColor(R.color.nimbus_blue, null),
            getResources().getColor(R.color.nimbus_red, null),
            Color.TRANSPARENT
    };
    private float[] nimbusStages = new float[]{
            .0f,.4f,.65f,1f
    };
    private Bitmap moon = Bitmap.createBitmap(40, 40, Bitmap.Config.ARGB_8888),
            rotMoon,
            scene,
            bmpMoonBack;
    public int twinkleCntrl = 0;
    private Canvas myCanvas = new Canvas(), aCanvas = new Canvas();
    private RadialGradient radGrad,sunGrad;
    private int backCol = Color.rgb(41, 200, 255);
    private Point topStart, topEnd, bottomStart, bottomEnd;
    // array axes are top to bottom
    private int[] axes = new int[4];
    public static int fabColor = Color.WHITE;
    public static int darkFab = Color.BLACK;
    private Rect[] rects = new Rect[2];
    private Rect rect;
    static float[] time = new float[]{0, 0};
    float[]timeA= new float[]{0f,0f,0f};
    private float[] darkCols = new float[3];
    float[] sunColors = new float[3];
    private int twilight;
    private int mid, radius = 20;
    public static int modeSelect = 0;
    private Paint paint,painter;
    private int[] moonCols = new int[]{
            getResources().getColor(R.color.moon_transparency, null),
            getResources().getColor(R.color.moon, null)
    };
    private int translucencyCol = Color.rgb(0,255,255);
    public Shader[] colArray = new Shader[2];
    public static String[] modesList = new String[]{
            "Clear", "SRC", "DST", "SRC_OVER", "DST_OVER", "SRC_IN", "DST_IN", "SRC_OUT",
            "DST_OUT", "SRC_ATOP", "DST_ATOP", "XOR", "DARKEN", "LIGHTEN", "MULTIPLY", "SCREEN"
    };
    public static PorterDuffXfermode[] modes = new PorterDuffXfermode[]{
            new PorterDuffXfermode(PorterDuff.Mode.CLEAR),
            new PorterDuffXfermode(PorterDuff.Mode.SRC),
            new PorterDuffXfermode(PorterDuff.Mode.DST),
            new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER),
            new PorterDuffXfermode(PorterDuff.Mode.DST_OVER),
            new PorterDuffXfermode(PorterDuff.Mode.SRC_IN),
            new PorterDuffXfermode(PorterDuff.Mode.DST_IN),
            new PorterDuffXfermode(PorterDuff.Mode.SRC_OUT),
            new PorterDuffXfermode(PorterDuff.Mode.DST_OUT),
            new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP),
            new PorterDuffXfermode(PorterDuff.Mode.DST_ATOP),
            new PorterDuffXfermode(PorterDuff.Mode.XOR),
            new PorterDuffXfermode(PorterDuff.Mode.DARKEN),
            new PorterDuffXfermode(PorterDuff.Mode.LIGHTEN),
            new PorterDuffXfermode(PorterDuff.Mode.MULTIPLY),
            new PorterDuffXfermode(PorterDuff.Mode.SCREEN)
    };

    public BackGroundView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setDrawingCacheEnabled(true);
        TypedArray a = getContext().getTheme().obtainStyledAttributes(attrs,
                R.styleable.BackgroundView, 0, 0);
        for (int i = 0; i < a.length(); i++) {
        }
        try {
            top[1] = a.getInteger(R.styleable.BackgroundView_upTop, 0);
            top[0] = a.getInteger(R.styleable.BackgroundView_upBot, 0);//0 is default

            bottom[0] = a.getInteger(R.styleable.BackgroundView_downTop, 0);
            bottom[1] = a.getInteger(R.styleable.BackgroundView_downBot, 0);

        } finally {
            a.recycle();


        }
        paint = new Paint();
        circles = new Circles(this.getResources().getColor(R.color.alpha_white,null));
        Color.colorToHSV(-12285525,darkCols);

    }

    @Override
    protected void onDraw(Canvas canvas){
        if(circles.lesserDim == 0){
            circles.getDimensions(canvas);
        }
        if(MainActivityFragment.timer < .025 || MainActivityFragment.timer  > .075){
            Log.i("colors = ",String.valueOf(tempCols[1]));
        }
        getPositions();
        //setGradient(top,bottom);
        //canvas.drawRect(rects[0],paint);
        //paint.setShader(colArray[1]);
        //canvas.drawRect(rects[1],paint);
        drawBackground(canvas);
        if(MainActivityFragment.day) {
            drawSun(canvas);
        }else {
            //drawMoonBack(canvas);
            drawMoon(MainActivityFragment.timer, canvas, paint);
        }
        if(counter > 1){
            counter = 0;
        }
        counter += .01;
        circles.setTimes(MainActivity.minTime,MainActivity.secTime,MainActivity.hrTime);

        if(circles.updateSec()){
            circles.redrawSecs(canvas);
        }
        if(circles.updateMin()){
            circles.redrawMins(canvas);
        }
        if(circles.updateHr()){
            circles.redrawHrs(canvas);
        }
            circles.drawSecs(canvas);
            circles.drawMins(canvas);
            circles.drawHrs(canvas);
    }

    public void setGradient(int[] colsA) {
        if (colsA != null) {
            colArray[0] = new LinearGradient(topStart.x, topStart.y, topEnd.x, topEnd.y, colsA,null, Shader.TileMode.MIRROR);
        }

    }

    public void getPositions() {
        mid = getWidth() / 2;
        //axes[0] = 0;
        //axes[1] = (getHeight())/2;
        //axes[2] = axes[1];
        //axes[3] = getHeight();
        //topStart = new Point(mid,axes[1]);
        topStart = new Point(mid, getHeight());
        //topEnd = new Point(mid,0);
        topEnd = new Point(mid, 0);
        //rects[0] = new Rect(0,0,getWidth(),axes[1]);
        //rects[1] = new Rect (0,axes[2],getWidth(),axes[3]);
        rect = new Rect(0, 0, getWidth(), getHeight());
    }

    static void updateTime() {
        time[1] = MainActivityFragment.timer;
    }


    public int[] colorChanger() {
        int r, g = 125, b;
        float h = 200, s, l;
        int[] newCols = new int[2];
        float[] sunColors = new float[]{0,0,0};
        float cyclePos = MainActivityFragment.timer;
        // output = output_start + ((output_end - output_start) / (input_end - input_start)) * (input - input_start)
        r = (int) (255 + ((80 - 255) / 1f * cyclePos));
        b = (int) (41 + ((255 - 41) / 1f * cyclePos));
        s = .43f + ((.76f - .23f) / 1f * cyclePos);
        l = .43f + ((.96f - .23f) / 1f * cyclePos);

        newCols[0] = Color.rgb(r, g, b);
        tempCols[0] = newCols[0];
        newCols[1] = Color.HSVToColor(255, new float[]{h, s, l});
        tempCols[1] = newCols[1];
        return newCols;
    }

    public void drawSun(Canvas canvas) {
        Paint sunPaint = new Paint();
        Random rand = new Random();
        float cyclePos = MainActivityFragment.timer;
        int adjHeight = getHeight() + 150;
        int adjZero = -150;
        h =  (20f + ((60f - 20f) / 1f * cyclePos));
        s =  (1f + ((.3f - 1f) / 1f * cyclePos));
        sunColors[0]=h;
        sunColors[1]=s;
        sunColors[2]=v;
        sunPaint.setColor(Color.HSVToColor(sunColors));
        int radius = 20;
        int elevation = (int) (adjHeight + ((adjZero - adjHeight) / 1f * cyclePos));
        //sunPaint.setAlpha((elevation - getHeight())*-1);
        //int getAlph = (int)(((elevation - getHeight())/(0f-getHeight()))*255f);
        //sunPaint.setAlpha(getAlph);
        canvas.drawCircle(mid, elevation, radius, sunPaint);
        // output = output_start + ((output_end - output_start) / (input_end - input_start)) * (input - input_start)
        int rayAlpha = (int) (50/1f*MainActivityFragment.timer);
        sunrayCol = Color.argb(rayAlpha,255,255,255);
        sunRays[1] = sunrayCol;
        //sunGrad = new RadialGradient(mid, elevation,150,sunRays,sunStages ,Shader.TileMode.CLAMP);
        //sunPaint.setShader(sunGrad);
        //canvas.drawCircle(mid,elevation,150,sunPaint);
        //sunPaint.setShader(null);
    }

    public void drawMoon(float moonPhase, Canvas canvas, Paint paint) {
        moon.setHasAlpha(true);
        myCanvas.setBitmap(moon);
        LinearGradient moongradient;
        int offset;
        int adjHeight = getHeight() + 150;
        int adjZero = -150;
        int elevation = (int) (adjHeight + ((adjZero - adjHeight) / 1f * moonPhase));
        // output = output_start + ((output_end - output_start) / (input_end - input_start)) * (input - input_start)
        offset = (int) (40 + ((-40 - 40) / 29.53f * MainActivityFragment.synodicTimer));
        //Log.i("yum", " " + offset);
        Paint moonsPaint = new Paint();
        Path pathOval = new Path();
        Path pathCircle = new Path();
        pathCircle.addCircle(20, 20, 20, Path.Direction.CW);
        pathOval.addCircle(20 + offset, 20, 20, Path.Direction.CW);
        moongradient = new LinearGradient(20 + offset, 20, 20 - offset, 20,moonCols,null, Shader.TileMode.CLAMP);
        moonsPaint.setShader(moongradient);
        moonsPaint.setXfermode(modes[1]);
        myCanvas.drawPath(pathCircle, moonsPaint);
        moonsPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        myCanvas.drawPath(pathOval, moonsPaint);
        moonsPaint.setXfermode(null);
        Matrix matrix = new Matrix();
        matrix.setRotate(20);
        rotMoon = Bitmap.createBitmap(moon, 0, 0, moon.getWidth(), moon.getHeight(), matrix, true);
        Log.i("moon has alpah",String.valueOf(rotMoon.hasAlpha()));
        Paint xPaint = new Paint();
        canvas.drawBitmap(rotMoon, mid - (rotMoon.getWidth() / 2), elevation - (rotMoon.getHeight() / 2), paint);
        drawNimbus(canvas,elevation);
        rotMoon.recycle();
        }

    public static void switchModes(Context context, BackGroundView backGroundView) {
        if (modeSelect < modes.length) {
            modeSelect++;
        } else {
            modeSelect = 0;
        }
        Toast.makeText(context, (String) modesList[modeSelect], Toast.LENGTH_SHORT).show();
        backGroundView.invalidate();
    }


    public void drawNimbus(Canvas canvas, int elevation){
        radGrad = new RadialGradient(mid, elevation, 100, nimbus, nimbusStages, Shader.TileMode.MIRROR);
        paint.setShader(radGrad);
        canvas.drawCircle(mid, elevation, 100, paint);
    }
    public void drawMoonBack(Canvas canvas){
        float cyclePos = MainActivityFragment.timer;
        int adjHeight = getHeight() + 150;
        int adjZero = -150;
        int elevation = (int) (adjHeight + ((adjZero - adjHeight) / 1f * cyclePos));
        int elev = (int) (getHeight() + ((-getHeight()) / 1f * cyclePos));
        Paint moonDark = new Paint();
        if(elevation > 0 && elevation < getHeight()) {
            moonDark.setColor(getBitmap().getPixel(0, elevation));
        }
        canvas.drawCircle(mid,elevation,radius,moonDark);

    }
        public Bitmap getBitmap(){
        Bitmap bitmap = Bitmap.createBitmap(1,this.getMeasuredHeight()+300, Bitmap.Config.ARGB_8888);
        aCanvas.setBitmap(bitmap);
        aCanvas.drawLine(0,0,0,getMeasuredHeight(),paint);
        return bitmap;
        }

        public int[] colorChangerNight(float[] inputCols) {
            float h = inputCols[0], s = .76f/*inputCols[1]*/, l = inputCols[2];
            int[] newCols = new int[2];
            float cyclePos = MainActivityFragment.timer;
            newCols[1] = Color.HSVToColor(255,inputCols);
            // output = output_start + ((output_end - output_start) / (input_end - input_start)) * (input - input_start)
            s = s + ((.10f - s) / 1f * cyclePos);
            l = l + ((.10f - l) / 1f * cyclePos);
            tempCols[0] = newCols[0];
            newCols[0] = Color.HSVToColor(255, new float[]{h, s, l});
            tempCols[1] = newCols[1];
            return newCols;
        }

        public void drawBackground(Canvas canvas) {

                setGradient(timeGradient(MainActivityFragment.timer));
                paint.setShader(colArray[0]);
                canvas.drawRect(rect, paint);

        }

        public int[] timeGradient(float timeOfDay){
        float dayHueB,daySatB,dayValB,dayHueM,daySatM,dayValM,dayHueT,daySatT,dayValT;
        float nightHueB,nightSatB,nightValB,nightHueM,nightSatM,nightValM,nightHueT,nightSatT,nightValT;
        if(MainActivityFragment.day) {
            int[] cols = new int[3];
            dayHueB = 20f + ((200f - 20f) / 1f) * MainActivityFragment.timer;
            daySatB = .40f + ((.1f - .40f) / 1f) * MainActivityFragment.timer;
            daySatT = .30f + ((.80f - .30f) / 1f) * timeOfDay;
            dayValT = .30f + ((.50f - .30f) / 1f) * timeOfDay;
            daySatM = .4f + ((.90f - .4f) / 1f) * timeOfDay;
            // output = output_start + ((output_end - output_start) / (input_end - input_start)) * (input - input_start)
            cols[0] = Color.HSVToColor(new float[]{dayHueB,daySatB,1f});
            fabColor = Color.HSVToColor(new float[]{dayHueB,daySatB/2f,1f});
            cols[1] = Color.HSVToColor(new float[]{195f,.78f,daySatM});
            cols[2] = Color.HSVToColor(new float[]{205f,.78f,daySatT});
            return cols;
        }
        else{
            int[] cols = new int[3];
            nightHueB = 20f + ((200f - 20f) / 1f) * MainActivityFragment.timer;
            nightSatB = .40f + ((.1f - .40f) / 1f) * MainActivityFragment.timer;
            nightValB = .97f + ((.30f - .97f) / 1f) * (MainActivityFragment.timer);
            nightSatT = .30f + ((.10f - .30f) / 1f) * timeOfDay;
            float lumTop = .30f + ((.50f - .30f) / 1f) * timeOfDay;
            nightValM = .4f + ((.20f - .4f) / 1f) * timeOfDay;
            // output = output_start + ((output_end - output_start) / (input_end - input_start)) * (input - input_start)
            cols[0] = Color.HSVToColor(new float[]{nightHueB,nightSatB,nightValB});
            fabColor = Color.HSVToColor(new float[]{nightHueB,nightSatB/2f,nightValB+((1f-nightValB)/2)});
            cols[1] = Color.HSVToColor(new float[]{195f,.78f,nightValM});
            cols[2] = Color.HSVToColor(new float[]{205f, .78f,nightSatT});

            return cols;
        }


        }





}
