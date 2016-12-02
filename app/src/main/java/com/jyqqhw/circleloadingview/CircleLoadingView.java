package com.jyqqhw.circleloadingview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * Created by floyd1992 on 2016/12/2 0002.
 */
public class CircleLoadingView extends View{

    private static final String TAG = "CircleLoadingView";
    private static final boolean DEBUG_MODE = false;

    private static final int DEFAULT_MIN_WIDTH = 60;
    private static final int DEFAULT_MIN_HEIGHT = 60;
    private static final int BG_COLOR = Color.parseColor("#00aaaaaa");
    private static final int PG_COLOR = Color.parseColor("#23ca76");
    private static final int DIRECTION_UP = 0;
    private static final int DIRECTION_DOWN = 1;
    private static final int PADDING = 0;
    private static final int STROKE_WIDTH = 3;
    private static final float MAX_ANGLE = 80;
    private static final float MIN_ANGLE = 0;
    private static final float DELTA_ANGLE = 1.6f;
    private static final int INIT_ANGLE_ONE = 10;
    private static final int INIT_ANGLE_TWO = 190;

    private Paint arc0, bg;
    private int width, height;
    private int extra;
    private int radius;
    private Point center;
    private RectF rect;
    private int direction = DIRECTION_UP;
    private float sweepAgl = MIN_ANGLE;
    private int startAgl = INIT_ANGLE_ONE, startAgl2 = INIT_ANGLE_TWO;
    private float ra;
    private float deltaRa1, deltaRa2;

    private long maxTime, minTime;

    public CircleLoadingView(Context context) {
        super(context);
        init();
    }

    public CircleLoadingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CircleLoadingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        arc0 = new Paint(Paint.ANTI_ALIAS_FLAG);
        arc0.setStyle(Paint.Style.STROKE);
        arc0.setColor(PG_COLOR);
        arc0.setStrokeWidth(STROKE_WIDTH);
        arc0.setStrokeCap(Paint.Cap.ROUND);

        bg = new Paint(Paint.ANTI_ALIAS_FLAG);
        bg.setStyle(Paint.Style.STROKE);
        bg.setColor(BG_COLOR);
        bg.setStrokeWidth(STROKE_WIDTH);

        center = new Point();

        rect = new RectF();
        deltaRa2 = 180.0f * DELTA_ANGLE / MAX_ANGLE;
        deltaRa1 = deltaRa2 * 3;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        if(widthMode == MeasureSpec.AT_MOST){
            widthSize = DEFAULT_MIN_WIDTH;
        }
        if(heightMode == MeasureSpec.AT_MOST){
            heightSize = DEFAULT_MIN_HEIGHT;
        }
        extra = (int) (2 * (arc0.getStrokeWidth() + PADDING));
        int w = widthSize + extra;
        int h = heightSize + extra;
        setMeasuredDimension(w, h);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        width = right - left;
        height = bottom - top;
        center.x = width/2;
        center.y = height/2;
        int r = width>height?height:width;
        radius = r - extra;
        rect = new RectF(extra, extra, width - extra, height - extra);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawArc0(canvas);
        postInvalidate();
    }

    private void drawArc0(Canvas canvas){
        if(sweepAgl >= MAX_ANGLE){
            sweepAgl = MAX_ANGLE;
            direction = DIRECTION_DOWN;
            if(DEBUG_MODE){
                if(System.currentTimeMillis() - maxTime >100){
                    Log.e(TAG,"sweep comes to max value, time is: "+(System.currentTimeMillis()- minTime));
                    Log.w(TAG,"a max circle time --> "+(System.currentTimeMillis()-maxTime));
                }
                maxTime = System.currentTimeMillis();
            }
        } else if(sweepAgl <= MIN_ANGLE) {
            sweepAgl = MIN_ANGLE;
            direction = DIRECTION_UP;
            if(DEBUG_MODE){
                if(System.currentTimeMillis() - minTime >100){
                    Log.e(TAG,"sweep comes to min value, time is: "+(System.currentTimeMillis()- maxTime));
                    Log.w(TAG,"a min circle time --> "+(System.currentTimeMillis()-minTime));
                }
                minTime = System.currentTimeMillis();
            }
        }
        if(DIRECTION_UP == direction && sweepAgl < MAX_ANGLE){
            sweepAgl += DELTA_ANGLE;
        }
        if(DIRECTION_DOWN == direction && sweepAgl > MIN_ANGLE){
            sweepAgl -= DELTA_ANGLE;
        }
        if(ra < 360){
            if(DIRECTION_DOWN == direction){
                ra += deltaRa2;
            }else{
                ra += deltaRa1;
            }
        } else{
            ra -= 360;
        }
        if(DEBUG_MODE){
            Log.i(TAG,"the deltaRa1="+deltaRa1+",deltaRa2="+deltaRa2+",ra="+ra+",sweepAgl="+sweepAgl);
        }
        canvas.drawArc(rect, 0, 360,false, bg);
        canvas.rotate(ra, center.x, center.y);
        canvas.drawArc(rect, startAgl- sweepAgl, 2*sweepAgl, false, arc0);
        canvas.drawArc(rect, startAgl2 -sweepAgl, 2*sweepAgl, false, arc0);
    }
}
