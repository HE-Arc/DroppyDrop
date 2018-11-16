package android.hearc.ch.droppydrop.game;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.hearc.ch.droppydrop.sensor.AccelerometerPointer;
import android.hearc.ch.droppydrop.game.Level.Level;
import android.hearc.ch.droppydrop.R;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

public class GameManagerLayout extends RelativeLayout {

    private static final String TAG = "GAME"; //GameManagerLayout.class.getSimpleName();

    private AccelerometerPointer accPointer;
    private Handler accHandler;
    private Runnable getPointerRunnable;
    private Level level;

    public GameManagerLayout(Context context) {
        this(context, null);
    }

    public GameManagerLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        int h = this.getMeasuredHeight();
        int w = this.getMeasuredWidth();

        accPointer = new AccelerometerPointer(context, h, w);
        level = new Level(context);

        // Timer
        // TODO as a class or function -> that provide start and stop
        accHandler = new Handler();
        getPointerRunnable = new Runnable() {
            // TODO can we make it as a service ?
            @Override
            public void run() {
                if(level.addPoint(accPointer.getPointer())){
                    Log.i(TAG, "onTouchEvent: succesfully add a point");
                    level.invalidate();
                } else {
                    Log.e(TAG, "onTouchEvent: cannot add point");
                }
                accHandler.postDelayed(this, 10);
            }
        };
        getPointerRunnable.run();

        init(attrs);
    }

    private void init(AttributeSet set)
    {
        addView(level);
        if(set == null) return;

        TypedArray ta = getContext().obtainStyledAttributes(set, R.styleable.GameManagerLayout);
        // assign custom attribs
        ta.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Log.i(TAG, "is in onMeasure");
        final int newHeight= MeasureSpec.getSize(heightMeasureSpec);
        final int newWidth= MeasureSpec.getSize(widthMeasureSpec);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        accPointer.resetPointer(newHeight, newWidth);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.i(TAG, "touch the game");

        // TODO show on pause menu
        return false;
    }

    @Override
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);
    }

}
