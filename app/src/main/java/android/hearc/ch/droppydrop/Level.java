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

import java.util.LinkedList;
import java.util.List;

/**
 * source :
 *  - https://medium.com/mindorks/android-custom-views-tutorial-part-2-custom-attributes-3adde12c846d
 */
public class Level extends View {

    private static final String TAG = "LEVEL"; // Level.class.getSimpleName();

    private Paint paintDrop;
    private List<Point> points;

    public Level(Context context, AttributeSet attrs) {
        super(context, attrs);

        points = new LinkedList<>();

        init(attrs);
    }

    public Level(Context context)
    {
        this(context, null);
    }

    private void init(@Nullable AttributeSet set) {

        // TODO draw base level design
        paintDrop = new Paint(Paint.ANTI_ALIAS_FLAG);

        paintDrop.setColor(getContext().getColor(R.color.colorPrimary));

        if(set == null) return;

        //TODO get attrs with level id
        /*TypedArray ta = getContext().obtainStyledAttributes(set, R.styleable.GameManager);
        // assign custom attribs
        ta.recycle();*/
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Log.i(TAG, "is in game onMeasure");
        final int newHeight= MeasureSpec.getSize(heightMeasureSpec);
        final int newWidth= MeasureSpec.getSize(widthMeasureSpec);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);
        Log.i(TAG, "onDraw");
        for (Point p : points) {
            canvas.drawCircle(p.x, p.y, 100, paintDrop);
        }
    }

    public boolean addPoint(Point p){
        // TODO can add the point ? Does it touch a dead zone ?
        if(points != null){
            return points.add(p);
            //return true;
        }
        return false;
    }
}
