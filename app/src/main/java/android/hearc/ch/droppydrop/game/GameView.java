package android.hearc.ch.droppydrop.game;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.VectorDrawable;
import android.hearc.ch.droppydrop.game.Level.LevelModel;
import android.hearc.ch.droppydrop.sensor.AccelerometerPointer;
import android.hearc.ch.droppydrop.sensor.VibratorManager;
import android.hearc.ch.droppydrop.sensor.VibratorService;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Vector;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class GameView extends SurfaceView implements SurfaceHolder.Callback {

    private MainThread mainThread;


    private static final String TAG = "LEVEL"; // Level.class.getSimpleName();

    private Paint paintDrop;
    private Paint paintTrack;

    private int LINE_SIZE ;
    private int CIRCLE_SIZE ;
    private Vector<Point> points;

    private int DEVICE_DENSITY_DPI;

    private Rect levelRect;
    private Paint paintlvlRect;

    //Vibrator service
    Context mcontext;
    Intent intent;

    public GameView(Context context,int levelId) {
        super(context);

        getHolder().setKeepScreenOn(true);

        getHolder().addCallback(this);

        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager)this.getContext().getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(metrics);

        DEVICE_DENSITY_DPI = metrics.densityDpi;

        CIRCLE_SIZE=convertDpToPixel(5);
        LINE_SIZE=convertDpToPixel(10);


        points = new Vector<Point>();

        LevelModel level= new LevelModel(context,levelId);

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

        int borderDistance=convertDpToPixel(75);
        levelRect= new Rect(borderDistance, borderDistance, 3*borderDistance, 6*borderDistance);

        mainThread = new MainThread(getHolder(), this);
        setFocusable(true);


        mcontext = context;
        intent = new Intent(this.getContext(), VibratorService.class);


    }


    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        mainThread.setRunning(false);
        if (mainThread.accPointer==null)
            mainThread.accPointer = new AccelerometerPointer(getContext(), height, width);
        else
            mainThread.accPointer.resetPointer(height, width);
        mainThread.setRunning(true);

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mainThread.setRunning(true);
        mainThread.start();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        boolean retry = true;
        while (retry) {
            try {
                mainThread.setRunning(false);
                mainThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            retry = false;
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(widthMeasureSpec,heightMeasureSpec);
    }

    public void update() { //game logic

    }

    @Override
    public void draw(Canvas canvas) { //rendering
        super.draw(canvas);
            if(canvas!=null) {

                canvas.drawColor(Color.WHITE);
                //TODO draw dead zone
                canvas.drawRect(levelRect, paintlvlRect);
                //TODO:

                if (points.size() > 1) {

                    for (int i = 1; i < points.size() - 1; i++) {
                        Point p = points.elementAt(i);
                        Point lastP=points.elementAt(i-1);

                        // Paint a line between each points
                        if(i>1) //otherwise it is ugly
                            canvas.drawLine(lastP.x, lastP.y, p.x, p.y, paintTrack);
                        //this.getDrawingCache().getPixel(p.x,p.y);

                        // Paint a dot to make it look round
                        canvas.drawCircle(p.x, p.y, CIRCLE_SIZE, paintTrack);
                    }
                    // Paint the last point for the pointer position
                    canvas.drawCircle(points.lastElement().x, points.lastElement().y, CIRCLE_SIZE, paintDrop);
                }
            }
    }

    public void addPoint(Point p){
        // TODO can add the point ? Does it touch a dead zone ?
        // TODO does a point have the same position ?
        synchronized (points) {
            if (points != null && p.x > 0 && p.y > 0) {
                if (p.x > levelRect.right || p.y > levelRect.bottom || p.x < levelRect.left || p.y < levelRect.top) //dummy collision test valable only for our temporary rectangle
                {
                    mcontext.startService(intent);
                }else{
                    mcontext.stopService(intent);
                }
                points.add(new Point(p));
            }
        }
    }

    private int convertDpToPixel(float dp) {
        return (int) (dp * (DEVICE_DENSITY_DPI / 160f));
    }

    public void setOnPause() {
        mainThread.setRunning(false);

    }

    public void setOnResume() {
        mainThread.setRunning(true);
        mainThread.start();
    }

    public void destroy() {
        System.exit( 0 );
    }


}
