package android.hearc.ch.droppydrop;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * source :
 *  - https://medium.com/mindorks/android-custom-views-tutorial-part-2-custom-attributes-3adde12c846d
 */
public class Level extends View {

    private static final String TAG = "LEVEL"; // Level.class.getSimpleName();

    private Paint paintDrop;
    private Paint paintPointer;
    private static final int LINE_SIZE = 30;
    private static final int CIRCLE_SIZE = 15;
    private List<Point> points;
    private Point startLine, endLine;

    public Level(Context context, AttributeSet attrs) {
        super(context, attrs);

        points = new ArrayList<>();

        init(attrs);
    }

    public Level(Context context)
    {
        this(context, null);
    }

    private void init(@Nullable AttributeSet set) {
        // Trace painting tool
        paintDrop = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintDrop.setColor(getContext().getColor(R.color.colorPrimary));
        paintDrop.setStrokeWidth(LINE_SIZE);

        // Pointer painting tools
        paintPointer = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintPointer.setColor(getContext().getColor(R.color.colorPrimaryDark));
        paintPointer.setStrokeWidth(CIRCLE_SIZE);

        if(set == null) return;

        // TODO get attrs with level id
        // TODO dead zone reader from attribs
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
        for(int i=1; i < points.size() && points.size() > 1; i++)
        {
            startLine = points.get(i-1);
            endLine = points.get(i);

            // Paint a line between each points
            canvas.drawLine(startLine.x, startLine.y, endLine.x, endLine.y, paintDrop);
            // Paint a dot to make it looks round
            canvas.drawCircle(startLine.x, startLine.y, CIRCLE_SIZE, paintDrop);
        }

        //TODO draw dead zone

        // Paint the last point for the pointer position
        canvas.drawCircle(endLine.x, endLine.y, CIRCLE_SIZE, paintPointer);
    }

    public boolean addPoint(Point p){
        // TODO can add the point ? Does it touch a dead zone ?
        // TODO does a point have the same position ?
        if(points != null){
            return points.add(new Point(p));
        }
        return false;
    }
}
