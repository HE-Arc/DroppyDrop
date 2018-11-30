package android.hearc.ch.droppydrop.game.Level;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.hearc.ch.droppydrop.R;
import android.hearc.ch.droppydrop.sensor.VibratorManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.List;

/**
 * source :
 *  - https://medium.com/mindorks/android-custom-views-tutorial-part-2-custom-attributes-3adde12c846d
 */
public class Level extends View {

    private static final String TAG = "LEVEL"; // Level.class.getSimpleName();

    private Paint paintDrop;
    private Paint paintTrack;

    private static final int LINE_SIZE = 30;
    private static final int CIRCLE_SIZE = 15;
    private List<Point> points;
    private Point startLine, endLine;

    private int DEVICE_DENSITY_DPI;

    private Rect levelRect;
    private Paint paintlvlRect;

    private VibratorManager vibratorManager;

    public Level(Context context, LevelModel level) {
        super(context);
        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager)this.getContext().getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(metrics);

        DEVICE_DENSITY_DPI = metrics.densityDpi;
        vibratorManager=new VibratorManager(this.getContext());
        points = new ArrayList<>();

        init(level);
    }

    public Level(Context context)
    {
        this(context, null);
    }

    private void init(LevelModel level) {
        // Trace painting tool
        paintDrop = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintDrop.setColor(level.DropColorInt);
        paintDrop.setStrokeWidth(LINE_SIZE);

        // Pointer painting tools
        paintTrack = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintTrack.setColor(level.TrackColorInt);
        paintTrack.setStrokeWidth(CIRCLE_SIZE);




        paintlvlRect = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintlvlRect.setStyle(Paint.Style.STROKE);
        paintlvlRect.setColor(Color.BLACK);
        paintlvlRect.setStrokeWidth(10);

        int borderDistance=convertDpToPixel(100);
        levelRect= new Rect(borderDistance, borderDistance, 3*borderDistance, 6*borderDistance);





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

        //TODO draw dead zone
        canvas.drawRect(levelRect, paintlvlRect);

        Log.i(TAG, "onDraw");
        if(points.size()>1) {
            for (int i = 1; i < points.size() && points.size() > 1; i++) {
                startLine = points.get(i - 1);
                endLine = points.get(i);

                // Paint a line between each points
                canvas.drawLine(startLine.x, startLine.y, endLine.x, endLine.y, paintTrack);
                // Paint a dot to make it looks round
                canvas.drawCircle(startLine.x, startLine.y, CIRCLE_SIZE, paintTrack);
            }


            // Paint the last point for the pointer position
            canvas.drawCircle(endLine.x, endLine.y, CIRCLE_SIZE, paintDrop);

        }
    }

    public boolean addPoint(Point p){
        // TODO can add the point ? Does it touch a dead zone ?
        // TODO does a point have the same position ?
        if(points != null && p.x!=0 && p.y!=0){
            if(p.x>levelRect.right || p.y>levelRect.bottom || p.x <levelRect.left ||p.y<levelRect.top)
            {

                vibratorManager.startVibrator();

            }

            return points.add(new Point(p));
        }
        return false;
    }

    private int convertDpToPixel(float dp) {
        return (int) (dp * (DEVICE_DENSITY_DPI / 160f));
    }

}
