package android.hearc.ch.droppydrop;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * source :
 *  - https://medium.com/mindorks/android-custom-views-tutorial-part-2-custom-attributes-3adde12c846d
 */
public class Level extends View {

    private static final String TAG = Level.class.getSimpleName();

    private Paint paintDrop;
    private Point p;

    public Level(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        init(attrs);
    }

    private void init(@Nullable AttributeSet set) {
        // TODO draw base level design
        paintDrop = new Paint(Paint.ANTI_ALIAS_FLAG);
        p = new Point(0,0);

        paintDrop.setColor(getContext().getColor(R.color.colorPrimary));

        if(set == null) return;

        //TODO get attrs with level id
        /*TypedArray ta = getContext().obtainStyledAttributes(set, R.styleable.GameManager);
        // assign custom attribs
        ta.recycle();*/
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
    public boolean onTouchEvent(MotionEvent event) {
        Log.i(TAG, "touch the game");

        if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
            p = new Point((int) event.getX(), (int) event.getY());
        }
        invalidate();
        return false;
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
