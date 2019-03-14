package com.example.chris.alarm;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.support.annotation.Nullable;
import android.support.v7.widget.ViewUtils;
import android.util.AttributeSet;
import android.util.Size;
import android.view.Gravity;
import android.view.View;
import android.view.ViewOutlineProvider;

public class CircleView extends View {
    public int radius = 500;
    private int color = Color.WHITE;
    public int centerX,centerY;
    Paint paint = new Paint();
    ShapeDrawable shadow = new ShapeDrawable();
    public CircleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        paint.setColor(color);

    }

    @Override
    public void draw(Canvas canvas) {
        centerX = canvas.getWidth()/2;
        centerY = canvas.getHeight()/2;
        paint.setColor(color);
        canvas.drawCircle(centerX,centerY,radius,paint);
        setOutlineProvider(viewOutlineProvider);
        super.draw(canvas);
    }
    public void setRadius(int rad){
        radius = rad;
    }
    public int getRadius(){
        return radius;
    }
    public void setColor(int col){ color = col;}
    public int getColor() {return color;}

    ViewOutlineProvider viewOutlineProvider = new ViewOutlineProvider() {
        @Override
        public void getOutline(View view, Outline outline) {
            outline.setOval(centerX-radius,centerY-radius,centerX+radius,centerY+radius);
        }
    };

}
