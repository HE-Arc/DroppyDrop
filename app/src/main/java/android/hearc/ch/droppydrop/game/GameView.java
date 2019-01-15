package android.hearc.ch.droppydrop.game;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.hearc.ch.droppydrop.game.Level.LevelModel;
import android.hearc.ch.droppydrop.sensor.AccelerometerPointer;
import android.hearc.ch.droppydrop.sensor.VibratorService;
import android.util.DisplayMetrics;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class GameView extends SurfaceView implements SurfaceHolder.Callback {

    private MainThread mainThread;


    private static final String TAG = "LEVEL"; // Level.class.getSimpleName();

    private Paint paintDrop;
    private Paint paintTrack;

    private int line_width;
    private int circle_radius;
    private Vector<Point> points;
    private Vector<Boolean> drawLineBools;

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

    private int pixelColorCheckTreshold;

    private int collisionMargin;

    private int[] pixels;

    private Point lastPoint;

    private boolean doNotDrawNextLine;

    public GameView(Context context, int levelId) {
        super(context);

        getHolder().setKeepScreenOn(true);

        getHolder().addCallback(this);

        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) this.getContext().getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(metrics);

        DEVICE_DENSITY_DPI = metrics.densityDpi;

        circle_radius = convertDpToPixel(5);
        line_width = convertDpToPixel(5);


        points = new Vector<Point>();
        drawLineBools=new Vector<Boolean>();

        level = new LevelModel(context, levelId);

        // Trace painting tool
        paintDrop = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintDrop.setColor(level.DropColorInt);
        paintDrop.setStrokeWidth(line_width);

        // Pointer painting tools
        paintTrack = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintTrack.setColor(level.TrackColorInt);
        paintTrack.setStrokeWidth(circle_radius * 2);

        paintlvlRect = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintlvlRect.setStyle(Paint.Style.STROKE);
        paintlvlRect.setColor(Color.BLACK);
        paintlvlRect.setStrokeWidth(line_width);

        viewWidth = 0;
        viewHeight = 0;

        int borderDistance = convertDpToPixel(75);
        levelRect = new Rect(borderDistance, borderDistance, 3 * borderDistance, 6 * borderDistance);

        mainThread = new MainThread(getHolder(), this);
        setFocusable(true);


        mcontext = context;
        intent = new Intent(this.getContext(), VibratorService.class);


        image = getResources().getDrawable(level.ImageId, null);

        //pOffset = (int) (Math.sqrt(2) / 2 * circle_radius);

        collisionMargin=circle_radius-1;

        pixelColorCheckTreshold =50;


        doNotDrawNextLine=false;


    }


    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        mainThread.setRunning(false);
        if (mainThread.accPointer == null)
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
        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);

    }

    @Override
    protected void onSizeChanged(int xNew, int yNew, int xOld, int yOld) {
        super.onSizeChanged(xNew, yNew, xOld, yOld);

        viewWidth = xNew;
        viewHeight = yNew;

        image.setBounds(0, 0, xNew, yNew);

        Bitmap b = ((BitmapDrawable) image).getBitmap();
        bitmap = Bitmap.createScaledBitmap(b, xNew, yNew, false);
        pixels=new int[viewWidth*viewHeight];
        bitmap.getPixels(pixels, 0, viewWidth, 0, 0, viewWidth, viewHeight);

        lastPoint=new Point(viewWidth/2,viewHeight/2);//TODO if starting point changes, adapt this too
    }

    public void update() { //game logic

    }

    @Override
    public void draw(Canvas canvas) { //rendering
        super.draw(canvas);
        if (canvas != null) {
            if (image != null) {
                image.draw(canvas);
            } else {
                canvas.drawColor(Color.MAGENTA);
            }

            if (points.size() > 1) {

                for (int i = 1; i < points.size() - 1; i++) {
                    Point p = points.elementAt(i);
                    Point lastP = points.elementAt(i - 1);

                    // Paint a line between each points
                    if (i > 1 && drawLineBools.elementAt(i)) //otherwise it is ugly
                        canvas.drawLine(lastP.x, lastP.y, p.x, p.y, paintTrack);
                    //this.getDrawingCache().getPixel(p.x,p.y);

                    // Paint a dot to make it look round
                    canvas.drawCircle(p.x, p.y, circle_radius, paintTrack);
                }
                // Paint the last point for the pointer position
                canvas.drawCircle(points.lastElement().x, points.lastElement().y, circle_radius, paintDrop);
            }
        }
    }


    private void checkCollision(List<Integer> pixelList) {
        int pixelColor = pixelList.remove(0);
        int redValue = Color.red(pixelColor);
        int blueValue = Color.blue(pixelColor);
        int greenValue = Color.green(pixelColor);

        if (redValue < pixelColorCheckTreshold && blueValue < pixelColorCheckTreshold && greenValue < pixelColorCheckTreshold) {
            mcontext.startService(intent);
        } else {
            mcontext.stopService(intent);
            if (pixelList.size() > 0) {
                checkCollision(pixelList);
            }

        }
    }

    private int xyToIndex(int x,int y)
    {
        return x+viewWidth*y;
    }

    public void addPoint(Point p, Canvas canvas) {

        synchronized (points) {
            if (points != null) {
                if (p.x - circle_radius > 0 && p.y -circle_radius > 0 && p.x+circle_radius < viewWidth && p.y+circle_radius < viewHeight) {
                    points.add(new Point(p));
                    if(!doNotDrawNextLine)
                        drawLineBools.add(true);
                    else
                        doNotDrawNextLine=false;
                    if (bitmap != null) {

                        List<Integer> pixelList = new ArrayList<>();

                        if(p.y<lastPoint.y) {
                            if (p.x < lastPoint.x)
                                pixelList.add(pixels[xyToIndex(p.x - collisionMargin, p.y - collisionMargin)]);
                            else
                                pixelList.add(pixels[xyToIndex(p.x + collisionMargin, p.y - collisionMargin)]);
                        }
                        else {
                            if (p.x < lastPoint.x)
                                pixelList.add(pixels[xyToIndex(p.x - collisionMargin, p.y + collisionMargin)]);
                            else
                                pixelList.add(pixels[xyToIndex(p.x + collisionMargin, p.y + collisionMargin)]);
                        }

                        pixelList.add(pixels[xyToIndex(p.x,p.y)]);
                        checkCollision(pixelList);


                    }
                }else{
                    if (p.y+circle_radius>=viewHeight)
                    {
                        mainThread.accPointer.setPointerY(circle_radius);
                        drawLineBools.add(false); //to avoid draw a line across the screen
                        doNotDrawNextLine=true;
                    }
                    if(p.y+circle_radius<0)
                    {
                        mainThread.accPointer.setPointerY(viewHeight-circle_radius);
                        drawLineBools.add(false); //to avoid draw a line across the screen
                        doNotDrawNextLine=true;
                    }
                    if(p.x+circle_radius<0)
                    {
                        mainThread.accPointer.setPointerX(viewWidth-circle_radius);
                        drawLineBools.add(false); //to avoid draw a line across the screen
                        doNotDrawNextLine=true;
                    }
                    if(p.x-circle_radius>=viewWidth)
                    {
                        mainThread.accPointer.setPointerX(circle_radius);
                        drawLineBools.add(false); //to avoid draw a line across the screen
                        doNotDrawNextLine=true;
                    }
                }
                lastPoint.set(p.x,p.y);
            }
        }
    }

    private int convertDpToPixel(float dp) {
        return (int) (dp * (DEVICE_DENSITY_DPI / 160f));
    }

    public void setOnPause() {
        mainThread.onPause();

    }

    public void setOnResume() {
        mainThread.onResume();
    }

    public void destroy() {
        System.exit(0);
    }


}

