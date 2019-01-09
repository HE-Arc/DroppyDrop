package android.hearc.ch.droppydrop.game;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.VectorDrawable;
import android.hearc.ch.droppydrop.game.Level.LevelModel;
import android.hearc.ch.droppydrop.sensor.AccelerometerPointer;
import android.hearc.ch.droppydrop.sensor.VibratorManager;
import android.os.Handler;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Vector;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class GameView extends SurfaceView implements SurfaceHolder.Callback {

    private MainThread mainThread;

    private Context context;

    private static final String TAG = "LEVEL"; // Level.class.getSimpleName();

    private Paint paintDrop;
    private Paint paintTrack;

    private static final int LINE_SIZE = 30;
    private static final int CIRCLE_SIZE = 15;
    private Vector<Point> points;
    private Point startLine, endLine;

    private int DEVICE_DENSITY_DPI;

    private Rect levelRect;
    private Paint paintlvlRect;
    //private Vector<Point> pointsQueue;

    private VibratorManager vibratorManager;

    public GameView(Context context,int levelId) {
        super(context);
        this.context=context;

        getHolder().setKeepScreenOn(true);
        //getHolder().setFixedSize(720,1480);
        getHolder().addCallback(this);

        vibratorManager=new VibratorManager(this.getContext());
        points = new Vector<Point>();
        //pointsQueue=new LinkedBlockingQueue<Point>();

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

        int borderDistance=150;
        levelRect= new Rect(borderDistance, borderDistance, 3*borderDistance, 6*borderDistance);

        mainThread = new MainThread(getHolder(), this);
        setFocusable(true);


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
        Log.i(TAG, "is in game onMeasure");
        final int newHeight= MeasureSpec.getSize(heightMeasureSpec);
        final int newWidth= MeasureSpec.getSize(widthMeasureSpec);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(widthMeasureSpec,heightMeasureSpec);
    }

    public void update() { //game logic

    }

    @Override
    public void draw(Canvas canvas) { //rendering
        super.draw(canvas);
            if(canvas!=null) {

                //Point newPoint=pointsQueue.poll();

                //if(newPoint!=null)
                //    points.add(newPoint);

                canvas.drawColor(Color.WHITE);
                //TODO draw dead zone
                canvas.drawRect(levelRect, paintlvlRect);
                //TODO:
                //check si il y a bien les lignes qui se dessinnent entre chaque point ?
                //+ check pour pas dessiner le "1er trait qui part du coin en haut à gauche au milieu"
                //+ check pour pas redessiner sur endroit où c'est déjà dessiné
                //Log.i(TAG, "onDraw");
                if (points.size() > 1) {
                    //Iterator it = points.iterator();

                    //while(it.hasNext() ){ //try to optimise by iterating on a linkedlist
                    for (int i = 1; i < points.size() - 1; i++) {
                        Point p = points.elementAt(i);
                        Point lastP=points.elementAt(i-1);
                        //Log.i(TAG,"DRAW PIX ON "+p.x+";"+p.y);
                        canvas.drawCircle(p.x, p.y, CIRCLE_SIZE, paintTrack);
                        canvas.drawLine(lastP.x, lastP.y, p.x, p.y, paintTrack);
                        //this.getDrawingCache().getPixel(p.x,p.y);
                    }
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
                    vibratorManager.startVibrator();
                }
                points.add(new Point(p));
            }
        }
    }



}
