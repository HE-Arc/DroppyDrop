package android.hearc.ch.droppydrop;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.graphics.Canvas;

public class GameManager extends View {

    private static final String TAG = "GAME"; //GameManager.class.getSimpleName();

    private AccelerometerPointer accPointer;

    public GameManager(Context context, AttributeSet attrs) {
        super(context, attrs);
        Log.i(TAG, "is in game constructor");

        int h = this.getMeasuredHeight();
        int w = this.getMeasuredWidth();

        accPointer = new AccelerometerPointer(context, h, w);

        init(null);
    }

    public GameManager(Context context) {
        this(context, null);
    }

    private void init(@Nullable AttributeSet set) {
        if(set == null) return;

        TypedArray ta = getContext().obtainStyledAttributes(set, R.styleable.GameManager);
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
        return false;
    }

    @Override
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);
    }

}
