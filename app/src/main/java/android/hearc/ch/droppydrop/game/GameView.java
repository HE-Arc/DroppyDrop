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
import android.hearc.ch.droppydrop.game.Level.LevelModel;
import android.hearc.ch.droppydrop.sensor.AccelerometerPointer;
import android.hearc.ch.droppydrop.sensor.VibratorService;
import android.util.DisplayMetrics;
import android.util.Log;
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


    private Paint paintWhite;

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

    private List<Integer> pixelList;

    public GameView(Context context, int levelId) {
        super(context);

        getHolder().setKeepScreenOn(true);

        getHolder().addCallback(this);

        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) this.getContext().getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(metrics);

        DEVICE_DENSITY_DPI = metrics.densityDpi;

        circle_radius = convertDpToPixel(10);
        line_width = convertDpToPixel(10);


        points = new Vector<Point>();
        drawLineBools = new Vector<Boolean>();

        level = new LevelModel(context, levelId);

        // Trace painting tool
        paintDrop = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintDrop.setColor(level.DropColorInt);
        paintDrop.setStrokeWidth(line_width);

        // Pointer painting tools
        paintTrack = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintTrack.setColor(level.TrackColorInt);
        paintTrack.setStrokeWidth(circle_radius * 2);

        paintWhite = new Paint();
        paintWhite.setColor(Color.WHITE);
        paintWhite.setStyle(Paint.Style.FILL);


        viewWidth = 0;
        viewHeight = 0;


        mainThread = new MainThread(getHolder(), this);
        setFocusable(true);


        mcontext = context;
        intent = new Intent(this.getContext(), VibratorService.class);


//        image = getResources().getDrawable(level.ImageId, null);


        collisionMargin = circle_radius - 4;

        pixelColorCheckTreshold = 50;


        doNotDrawNextLine = false;

        pixelList = new ArrayList<>();

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


        Bitmap b = BitmapFactory.decodeResource(getContext().getResources(), level.ImageId);

        //Bitmap b = ((BitmapDrawable) image).getBitmap();
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(b, xNew, yNew, false);
        pixels = new int[viewWidth * viewHeight];
        scaledBitmap.getPixels(pixels, 0, viewWidth, 0, 0, viewWidth, viewHeight);

        bitmap = createTransparentBitmapFromBitmap(scaledBitmap, Color.WHITE); //white pixels replaced with transparent ones
        bitmap.setHasAlpha(true);
        image = new BitmapDrawable(getContext().getResources(), bitmap);
        image.setBounds(0, 0, xNew, yNew);


        lastPoint = new Point(viewWidth / 2, viewHeight / 2);//TODO if starting point changes, adapt this too
    }

    public void update() { //game logic

    }

    @Override
    public void draw(Canvas canvas) { //rendering
        super.draw(canvas);
        if (canvas != null) {
            canvas.drawPaint(paintWhite);
            if (points.size() > 1) {

                for (int i = 1; i < points.size() ; i++) {
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

            if (image != null) {

                image.draw(canvas);
            } else {
                canvas.drawColor(Color.MAGENTA);
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

    private int xyToIndex(int x, int y) {
        return x + viewWidth * y;
    }

    public void addPoint(Point p, Canvas canvas) {

        synchronized (points) {
            if (points != null) {
                if (p.x - circle_radius > 0 && p.y - circle_radius > 0 && p.x + circle_radius < viewWidth && p.y + circle_radius < viewHeight) {
                    points.add(new Point(p));
                    if (!doNotDrawNextLine)
                        drawLineBools.add(true);
                    else
                        doNotDrawNextLine = false;
                    if (bitmap != null) {

                        pixelList.clear();

                        if (p.y < lastPoint.y) { //if drop goes up
                            if (p.x < lastPoint.x) {
                                pixelList.add(pixels[xyToIndex(p.x - collisionMargin, p.y - collisionMargin)]); // if drop goes left
                                pixelList.add(pixels[xyToIndex(p.x - collisionMargin, p.y)]);
                                pixelList.add(pixels[xyToIndex(p.x, p.y - collisionMargin)]);
                                pixelList.add(pixels[xyToIndex(p.x + collisionMargin, p.y - collisionMargin)]);
                                pixelList.add(pixels[xyToIndex(p.x - collisionMargin, p.y + collisionMargin)]);
                            } else {
                                pixelList.add(pixels[xyToIndex(p.x + collisionMargin, p.y - collisionMargin)]); // if drop goes right
                                pixelList.add(pixels[xyToIndex(p.x, p.y - collisionMargin)]);
                                pixelList.add(pixels[xyToIndex(p.x + collisionMargin, p.y )]);
                                pixelList.add(pixels[xyToIndex(p.x - collisionMargin, p.y - collisionMargin)]);
                                pixelList.add(pixels[xyToIndex(p.x + collisionMargin, p.y + collisionMargin)]);

                            }
                        } else { //if drop goes down
                            if (p.x < lastPoint.x) {
                                pixelList.add(pixels[xyToIndex(p.x - collisionMargin, p.y + collisionMargin)]); // if drop goes left
                                pixelList.add(pixels[xyToIndex(p.x - collisionMargin, p.y)]);
                                pixelList.add(pixels[xyToIndex(p.x , p.y + collisionMargin)]);
                                pixelList.add(pixels[xyToIndex(p.x - collisionMargin, p.y - collisionMargin)]);
                                pixelList.add(pixels[xyToIndex(p.x + collisionMargin, p.y + collisionMargin)]);
                            } else {
                                pixelList.add(pixels[xyToIndex(p.x + collisionMargin, p.y + collisionMargin)]); // if drop goes right
                                pixelList.add(pixels[xyToIndex(p.x , p.y + collisionMargin)]);
                                pixelList.add(pixels[xyToIndex(p.x + collisionMargin, p.y )]);
                                pixelList.add(pixels[xyToIndex(p.x - collisionMargin, p.y + collisionMargin)]);
                                pixelList.add(pixels[xyToIndex(p.x + collisionMargin, p.y - collisionMargin)]);
                            }
                        }

                        pixelList.add(pixels[xyToIndex(p.x, p.y)]);
                        checkCollision(pixelList);


                    }
                } else {
                    if (p.y + circle_radius >= viewHeight) {
                        mainThread.accPointer.setPointerY(circle_radius);
                        drawLineBools.add(false); //to avoid draw a line across the screen
                        doNotDrawNextLine = true;
                    }
                    if (p.y - circle_radius < 0) {
                        mainThread.accPointer.setPointerY(viewHeight - circle_radius);
                        drawLineBools.add(false); //to avoid draw a line across the screen
                        doNotDrawNextLine = true;
                    }
                    if (p.x - circle_radius < 0) {
                        mainThread.accPointer.setPointerX(viewWidth - circle_radius);
                        drawLineBools.add(false); //to avoid draw a line across the screen
                        doNotDrawNextLine = true;
                    }
                    if (p.x + circle_radius >= viewWidth) {
                        mainThread.accPointer.setPointerX(circle_radius);
                        drawLineBools.add(false); //to avoid draw a line across the screen
                        doNotDrawNextLine = true;
                    }
                }
                lastPoint.set(p.x, p.y);
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

    /**
     * create a transparent bitmap from an existing bitmap by replacing certain
     * color with transparent
     *
     * @param bitmap the original bitmap with a color you want to replace
     * @return a replaced color immutable bitmap
     */
    //http://www.java2s.com/Code/Android/2D-Graphics/Createatransparentbitmapfromanexistingbitmapbyreplacingcertaincolorwithtransparent.htm
    public Bitmap createTransparentBitmapFromBitmap(Bitmap bitmap,
                                                    int replaceThisColor) {
        if (bitmap != null) {
            int picw = bitmap.getWidth();
            int pich = bitmap.getHeight();
            int[] pix = new int[picw * pich];
            bitmap.getPixels(pix, 0, picw, 0, 0, picw, pich);

            for (int y = 0; y < pich; y++) {
                // from left to right
                for (int x = 0; x < picw; x++) {
                    int index = y * picw + x;
                    int r = (pix[index] >> 16) & 0xff;
                    int g = (pix[index] >> 8) & 0xff;
                    int b = pix[index] & 0xff;

                    if (r > 0xff - pixelColorCheckTreshold && g > 0xff - pixelColorCheckTreshold && b > 0xff - pixelColorCheckTreshold) {
                        pix[index] = Color.TRANSPARENT;
                    }
                }

                // from right to left
                for (int x = picw - 1; x >= 0; x--) {
                    int index = y * picw + x;
                    int r = (pix[index] >> 16) & 0xff;
                    int g = (pix[index] >> 8) & 0xff;
                    int b = pix[index] & 0xff;

                    if (r > 0xff - pixelColorCheckTreshold && g > 0xff - pixelColorCheckTreshold && b > 0xff - pixelColorCheckTreshold) {
                        pix[index] = Color.TRANSPARENT;
                    }
                }
            }

            Bitmap bm = Bitmap.createBitmap(pix, picw, pich,
                    Bitmap.Config.ARGB_4444);

            return bm;
        }
        return null;
    }


}



