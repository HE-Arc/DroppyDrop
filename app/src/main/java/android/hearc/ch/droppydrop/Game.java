package android.hearc.ch.droppydrop;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.ViewGroup;
import android.widget.LinearLayout;

/**
 * source :
 *  - https://medium.com/mindorks/android-custom-views-tutorial-part-2-custom-attributes-3adde12c846d
 */
public class Game extends View {

    private static final String TAG = "GAME"; //Game.class.getSimpleName();

    private Rect rect;
    private Paint backgroundPaint;
    private Paint paintDrop;

    private Point p;

    private AccelerometerPointer accPointer;

    public Game(Context context) {
        super(context);

        int h = this.getMeasuredHeight();
        int w = this.getMeasuredWidth();
        Log.i(TAG, "Game: h: " + h + "");
        init(null);
    }

    public Game(Context context, AttributeSet attrs) {
        super(context, attrs);
        Log.i(TAG, "is in game constructor");
        accPointer = new AccelerometerPointer(context);
        init(null);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.i(TAG, "touch the game");

        if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
            p = new Point((int) event.getX(), (int) event.getY());
        }
        invalidate();
        return false;
    }

    private void init(@Nullable AttributeSet set) {
        // TODO draw base level design
        backgroundPaint = new Paint();
        paintDrop = new Paint(Paint.ANTI_ALIAS_FLAG);
        p = new Point(0,0);

        backgroundPaint.setColor(0xFFFFFF);
        paintDrop.setColor(getContext().getColor(R.color.colorPrimary));

        if(set == null) return;

        TypedArray ta = getContext().obtainStyledAttributes(set, R.styleable.Game);
        // assign custom attribs
        ta.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Log.i("GAME", "is in game onMeasure");
        // TODO implements onMeasure : size min & max
        final int newHeight= MeasureSpec.getSize(heightMeasureSpec);
        final int newWidth= MeasureSpec.getSize(widthMeasureSpec);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);

        //Log.i(TAG, "is in game onDraw");
        //Log.i(TAG, p.toString());
        /*if(canvas!=null)
            Log.i("GAME", "onDraw: canevas not null");*/

        //TODO draw drop at the center
        canvas.drawCircle(p.x, p.y, 100, paintDrop);
    }

}
