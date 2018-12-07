package android.hearc.ch.droppydrop.game;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.hearc.ch.droppydrop.game.Level.LevelModel;
import android.hearc.ch.droppydrop.sensor.AccelerometerPointer;
import android.hearc.ch.droppydrop.sensor.VibratorManager;
import android.os.Handler;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.List;


public class GameView extends SurfaceView implements SurfaceHolder.Callback {
    private MainThread thread;

    private Context context;


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

    public GameView(Context context,int levelId) {
        super(context);
        this.context=context;
        getHolder().addCallback(this);

        vibratorManager=new VibratorManager(this.getContext());
        points = new ArrayList<>();

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

        int borderDistance=250;
        levelRect= new Rect(borderDistance, borderDistance, 3*borderDistance, 6*borderDistance);

        thread = new MainThread(getHolder(), this);
        setFocusable(true);


    }



    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if (thread.accPointer==null)
            thread.accPointer = new AccelerometerPointer(context, height, width);
        else
            thread.accPointer.resetPointer(height, width);


    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Log.i(TAG, "is in onMeasure");
        final int newHeight= MeasureSpec.getSize(heightMeasureSpec);
        final int newWidth= MeasureSpec.getSize(widthMeasureSpec);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);


    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        thread.setRunning(true);
        thread.start();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        boolean retry = true;
        while (retry) {
            try {
                thread.setRunning(false);
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            retry = false;
        }
    }

    public void update() {

    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        if (canvas != null) {
            canvas.drawColor(Color.WHITE);
            //TODO draw dead zone
            canvas.drawRect(levelRect, paintlvlRect);
            //TODO:
            //check si il y a bien les lignes qui se dessinnent entre chaque point ?
            //+ check pour pas dessiner le "1er trait qui part du coin en haut à gauche au milieu"
            //+ check pour pas redessiner sur endroit où c'est déjà dessiné
            Log.i(TAG, "onDraw");
                if(points.size()>1) {
                    for (int i = 1; i < points.size()-1; i++) {
                        startLine = points.get(i - 1);


                        // Paint a line between each points
                        //canvas.drawLine(startLine.x, startLine.y, endLine.x, endLine.y, paintTrack);
                        // Paint a dot to make it looks round
                        canvas.drawCircle(startLine.x, startLine.y, CIRCLE_SIZE, paintTrack);
                    }

                    endLine = points.get(points.size() - 1);
                    // Paint the last point for the pointer position
                    canvas.drawCircle(endLine.x, endLine.y, CIRCLE_SIZE, paintDrop);

                }
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



}
