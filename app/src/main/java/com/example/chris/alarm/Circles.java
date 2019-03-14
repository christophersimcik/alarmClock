package com.example.chris.alarm;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Pair;
import android.view.View;

import java.io.CharArrayReader;
import java.nio.charset.MalformedInputException;
import java.util.ArrayList;

public class Circles {
    public float timeMin,timeSec,timeHour;
    public double times;
    public Canvas circleCanvas;
    public Paint secPaint,minPaint,hrPaint,controlPaint;
    public RectF rectA;
    SweepGradient secGradient,minGradient,hrGradient;
    public int prevSec,prevMin,prevHr;
    public int lesserDim;
    Bitmap secSrc,secDest,minSrc,minDest,hrSrc,hrDest;
    public PorterDuffXfermode porterDuffXfermode;
    public int[] colors,colorsB;
    public float[] posS,posM,posH;
    public float[] posSB,posMB,posHB;
    //diameter of sec,min,hour rings
    public static int hoursDiam;
    public int minsDiam,secsDiam;

    public Circles(int color){
        porterDuffXfermode = new PorterDuffXfermode(PorterDuff.Mode.DST_OUT);
        colors = new int[]{Color.TRANSPARENT,color};
        colorsB = new int[]{color,color,Color.parseColor("#32FFFFFF"),color,color};
        circleCanvas = new Canvas();
        secPaint = new Paint();
        minPaint = new Paint();
        hrPaint = new Paint();
        controlPaint = new Paint();
        controlPaint.setColor(Color.WHITE);
        posM = new float[2];
        posS = new float[2];
        posH = new float[2];
        posSB = new float[5];
        posMB = new float[5];
        posHB = new float[5];
        posS[0]=0f;
        posM[0]=0f;
        posH[0]=0f;
        posSB[0] = 0f;
        posSB[4] = 1f;
        posMB[0] = 0f;
        posMB[4] = 1f;
        posHB[0] = 0f;
        posHB[4] = 1f;
    }
        public void setTimes(float minute,float secs,float hours){
            timeMin = minute*6f;
            timeSec = secs*6f;
            timeHour =hours*30f;
        }

         public Boolean updateMin() {
             if (prevMin != MainActivity.minTime) {
                 prevMin = MainActivity.minTime;
                 return true;
             } else {
                 return false;
             }
         }
         public Boolean updateSec(){
        if(prevSec != MainActivity.secTime){
            prevSec = MainActivity.secTime;
            return true;
        }else{
            return false;
        }
    }
        public Boolean updateHr(){
        if(prevHr != MainActivity.hrTime){
            prevHr = MainActivity.hrTime;
            return true;
        }else{
            return false;
        }
    }
         public float mapNums(int size, int place){
        return ((place * 255)/size);
         }
 public void redrawMins(Canvas canvas){
        minSrc = Bitmap.createBitmap(minsDiam,minsDiam, Bitmap.Config.ARGB_8888);
        minDest = Bitmap.createBitmap(minsDiam,minsDiam,Bitmap.Config.ARGB_8888);
        rectA = new RectF(0,0, minDest.getWidth(), minDest.getHeight());
        //posM[1]=timeMin/360f;
        posMB[1]=(timeMin/360f)-.25f;
        posMB[2]=(timeMin/360f);
        posMB[3]= (posMB[2]);
        circleCanvas.setBitmap(minDest);
        minGradient = new SweepGradient(circleCanvas.getWidth()/2,circleCanvas.getHeight()/2,colorsB,posMB);
        minPaint.setShader(minGradient);
        //circleCanvas.drawArc(rectA,0,timeMin,true,minPaint);
        circleCanvas.drawCircle(rectA.centerX(),rectA.centerY(),rectA.width()/2,minPaint);
        minPaint.setShader(null);
        circleCanvas.setBitmap(minSrc);
        circleCanvas.drawCircle(circleCanvas.getWidth()/2,circleCanvas.getHeight()/2,(circleCanvas.getWidth()/2)-25,controlPaint);
        circleCanvas.setBitmap( minDest);
        controlPaint.setXfermode(porterDuffXfermode);
        circleCanvas.drawBitmap(minSrc,0,0,controlPaint);
        controlPaint.setXfermode(null);
        }
        public void drawMins (Canvas canvas){
        canvas.rotate(-90,canvas.getWidth()/2,canvas.getHeight()/2);
        canvas.drawBitmap( minDest,(canvas.getWidth()/2)-(minsDiam/2),(canvas.getHeight()/2)-(minsDiam/2),controlPaint);
        canvas.rotate(90,canvas.getWidth()/2,canvas.getHeight()/2);
        }
    public void redrawSecs(Canvas canvas){
        secSrc = Bitmap.createBitmap(secsDiam,secsDiam, Bitmap.Config.ARGB_8888);
        secDest = Bitmap.createBitmap(secsDiam,secsDiam,Bitmap.Config.ARGB_8888);
        rectA = new RectF(0,0,secDest.getWidth(),secDest.getHeight());
        //posS[1]=timeSec/360f;
        posSB[1]=(timeSec/360f)-.25f;
        posSB[2]=(timeSec/360f);
        posSB[3] = posSB[2];
        circleCanvas.setBitmap(secDest);
        secGradient = new SweepGradient(circleCanvas.getWidth()/2,circleCanvas.getHeight()/2,colorsB,posSB);
        secPaint.setShader(secGradient);
        //circleCanvas.drawArc(rectA,0,timeSec,true,secPaint);
        circleCanvas.drawCircle(rectA.centerX(),rectA.centerY(),rectA.width()/2,secPaint);
        secPaint.setShader(null);
        circleCanvas.setBitmap(secSrc);
        circleCanvas.drawCircle(circleCanvas.getWidth()/2,circleCanvas.getHeight()/2,(circleCanvas.getWidth()/2)-10,controlPaint);
        circleCanvas.setBitmap(secDest);
        controlPaint.setXfermode(porterDuffXfermode);
        circleCanvas.drawBitmap(secSrc,0,0,controlPaint);
        controlPaint.setXfermode(null);
        }
    public void drawSecs (Canvas canvas){
        canvas.rotate(-90,canvas.getWidth()/2,canvas.getHeight()/2);
        canvas.drawBitmap(secDest,(canvas.getWidth()/2)-(secsDiam/2),(canvas.getHeight()/2)-(secsDiam/2),controlPaint);
        canvas.rotate(90,canvas.getWidth()/2,canvas.getHeight()/2);
    }

    public void redrawHrs(Canvas canvas){
        hrSrc = Bitmap.createBitmap(hoursDiam,hoursDiam, Bitmap.Config.ARGB_8888);
        hrDest = Bitmap.createBitmap(hoursDiam,hoursDiam,Bitmap.Config.ARGB_8888);
        rectA = new RectF(0,0,hrDest.getWidth(),hrDest.getHeight());
        //posH[1]=timeHour/360f;
        posHB[1]=(timeHour/360f)-.25f;
        posHB[2]=(timeHour/360f);
        posHB[3]= (posHB[2]);
        circleCanvas.setBitmap(hrDest);
        hrGradient = new SweepGradient(circleCanvas.getWidth()/2,circleCanvas.getHeight()/2,colorsB,posHB);
        hrPaint.setShader(hrGradient);
        //circleCanvas.drawArc(rectA,0,timeHour,true,hrPaint);
        circleCanvas.drawCircle(rectA.centerX(),rectA.centerY(),rectA.width()/2,hrPaint);
        hrPaint.setShader(null);
        circleCanvas.setBitmap(hrSrc);
        circleCanvas.drawCircle(circleCanvas.getWidth()/2,circleCanvas.getHeight()/2,(circleCanvas.getWidth()/2)-50,hrPaint);
        circleCanvas.setBitmap(hrDest);
        controlPaint.setXfermode(porterDuffXfermode);
        circleCanvas.drawBitmap(hrSrc,0,0,controlPaint);
        controlPaint.setXfermode(null);
    }
    public void drawHrs (Canvas canvas){
        canvas.rotate(-90,canvas.getWidth()/2,canvas.getHeight()/2);
        canvas.drawBitmap(hrDest,(canvas.getWidth()/2)-(hoursDiam/2),(canvas.getHeight()/2)-(hoursDiam/2),controlPaint);
        canvas.rotate(90,canvas.getWidth()/2,canvas.getHeight()/2);
    }
    public void getDimensions(Canvas canvas) {
            lesserDim = Math.min(canvas.getWidth(),canvas.getHeight());
            hoursDiam = lesserDim/2+(lesserDim/4);
            minsDiam = hoursDiam - 100;
            secsDiam = minsDiam - 50;
            }
    }
