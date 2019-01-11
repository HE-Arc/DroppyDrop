package android.hearc.ch.droppydrop.game;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.hearc.ch.droppydrop.R;
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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
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

    private int viewWidth;
    private int viewHeight;

    private LevelModel level;

    //Vibrator service
    Context mcontext;
    Intent intent;

    private Drawable image;

    private Bitmap bitmap;

    private int pOffset;

    private float canvasToBmpWidthRatio;
    private float canvasToBmpHeightRatio;

    public GameView(Context context,int levelId) {
        super(context);

        getHolder().setKeepScreenOn(true);

        getHolder().addCallback(this);

        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager)this.getContext().getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(metrics);

        DEVICE_DENSITY_DPI = metrics.densityDpi;

        CIRCLE_SIZE=convertDpToPixel(5);
        LINE_SIZE=convertDpToPixel(5);


        points = new Vector<Point>();

        level= new LevelModel(context,levelId);

        // Trace painting tool
        paintDrop = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintDrop.setColor(level.DropColorInt);
        paintDrop.setStrokeWidth(LINE_SIZE);

        // Pointer painting tools
        paintTrack = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintTrack.setColor(level.TrackColorInt);
        paintTrack.setStrokeWidth(CIRCLE_SIZE*2);

        paintlvlRect = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintlvlRect.setStyle(Paint.Style.STROKE);
        paintlvlRect.setColor(Color.BLACK);
        paintlvlRect.setStrokeWidth(LINE_SIZE);

        viewWidth=0;
        viewHeight=0;

        int borderDistance=convertDpToPixel(75);
        levelRect= new Rect(borderDistance, borderDistance, 3*borderDistance, 6*borderDistance);

        mainThread = new MainThread(getHolder(), this);
        setFocusable(true);


        mcontext = context;
        intent = new Intent(this.getContext(), VibratorService.class);


        image = getResources().getDrawable(level.ImageId, null);

        pOffset=(int)(Math.sqrt(2)/2*CIRCLE_SIZE);

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

    @Override
    protected void onSizeChanged(int xNew, int yNew, int xOld, int yOld){
        super.onSizeChanged(xNew, yNew, xOld, yOld);

        viewWidth = xNew;
        viewHeight = yNew;

        image.setBounds(0, 0, xNew, yNew);

        Bitmap b=((BitmapDrawable)image).getBitmap();
        bitmap=Bitmap.createScaledBitmap(b,xNew,yNew,false);



        //canvasToBmpWidthRatio=width/xNew;
        //canvasToBmpHeightRatio=height/yNew;

        //Log.i("BMPSIZE_xNew",String.valueOf(xNew));
        //Log.i("BMPSIZE_yNew",String.valueOf(yNew));
        //Log.i("BMPSIZE_width",String.valueOf(width));
        //Log.i("BMPSIZE_height",String.valueOf(height));
    }

    public void update() { //game logic

    }

    @Override
    public void draw(Canvas canvas) { //rendering
        super.draw(canvas);
            if(canvas!=null) {
                if(image!=null)
                {
                    image.draw(canvas);
                }
                else{
                    canvas.drawColor(Color.MAGENTA);
                }

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


    private void checkCollision(List<Integer> pixelList,int threshold){
        int pixelColor=pixelList.remove(0);
        int redValue = Color.red(pixelColor);
        int blueValue = Color.blue(pixelColor);
        int greenValue = Color.green(pixelColor);

        if (redValue<threshold && blueValue<threshold && greenValue<threshold)
        {
            mcontext.startService(intent);
        }
        else{
            mcontext.stopService(intent);
            if(pixelList.size()>0)
            {
                checkCollision(pixelList, threshold);
            }

        }
    }

    public void addPoint(Point p,Canvas canvas){

        synchronized (points) {
            if (points != null && p.x > 0 && p.y > 0) {
                points.add(new Point(p));
                if(bitmap!=null)
                {
                    int treshold=50;

                    List<Integer> pixelList=new ArrayList<>();
                    pixelList.add(bitmap.getPixel(p.x,p.y));
                    //pixelList.add(bitmap.getPixel(p.x-pOffset,p.y-pOffset));
                    //pixelList.add(bitmap.getPixel(p.x+pOffset,p.y-pOffset));
                    //pixelList.add(bitmap.getPixel(p.x-pOffset,p.y+pOffset));
                    //pixelList.add(bitmap.getPixel(p.x+pOffset,p.y+pOffset));

                    checkCollision(pixelList,treshold);


                }
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

