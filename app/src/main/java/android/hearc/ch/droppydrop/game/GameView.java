package android.hearc.ch.droppydrop.game;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.hearc.ch.droppydrop.R;
import android.hearc.ch.droppydrop.game.Level.LevelModel;
import android.hearc.ch.droppydrop.score.Score;
import android.hearc.ch.droppydrop.score.ScoreManager;
import android.hearc.ch.droppydrop.sensor.AccelerometerPointer;
import android.hearc.ch.droppydrop.sensor.VibratorService;
import android.preference.PreferenceManager;
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

    private Paint paintGold;

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

    private int diagonalCollisionmargin;

    private int[] pixels;

    private Point lastPoint;
    private Point drop;

    private boolean doNotDrawNextLine;

    private List<Integer> pixelList;

    private int score;

    private boolean[][] uniquePassageMatrix;

    private int difficulty;
    private SharedPreferences sharedPreferences;

    private boolean firstSizeChanged;

    //idea comes from Maxime Grava from inf3dlm-a
    private Canvas bmpCanvas;
    private Bitmap viewBitmap;

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

        paintGold = new Paint();

        paintGold.setColor(Color.rgb(255, 215, 0));
        paintGold.setStyle(Paint.Style.FILL);
        paintGold.setTextSize(100);
        paintGold.setTextAlign(Paint.Align.LEFT);


        viewWidth = 0;
        viewHeight = 0;

        score = 0;

        firstSizeChanged = true;

        mainThread = new MainThread(getHolder(), this);
        setFocusable(true);


        mcontext = context;
        intent = new Intent(this.getContext(), VibratorService.class);

        collisionMargin = circle_radius;

        diagonalCollisionmargin = (int) (collisionMargin * 0.7);

        pixelColorCheckTreshold = 50;


        doNotDrawNextLine = false;

        pixelList = new ArrayList<>();

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this.getContext());

        difficulty = sharedPreferences.getInt("Difficulty", 0) + 1;
    }


    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        mainThread.onPause();
        if (mainThread.accPointer == null)
            mainThread.accPointer = new AccelerometerPointer(getContext(), height, width);
        else
            mainThread.accPointer.resetPointer(height, width);
        mainThread.onResume();

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mainThread.setRunning(true);
        mainThread.start();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        saveScore();
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
        //To init many things we need the real viewWidth and viewHeight
        if (firstSizeChanged) {
            firstSizeChanged = false;

            viewWidth = xNew;
            viewHeight = yNew;

            viewBitmap = Bitmap.createBitmap(viewWidth, viewHeight, Bitmap.Config.ARGB_8888);
            bmpCanvas = new Canvas(viewBitmap);
            bmpCanvas.drawPaint(paintWhite);

            Bitmap b = BitmapFactory.decodeResource(getContext().getResources(), level.ImageId);

            Bitmap scaledBitmap = Bitmap.createScaledBitmap(b, xNew, yNew, false);
            pixels = new int[viewWidth * viewHeight];
            scaledBitmap.getPixels(pixels, 0, viewWidth, 0, 0, viewWidth, viewHeight);

            bitmap = createTransparentBitmapFromBitmap(scaledBitmap, Color.WHITE); //white pixels replaced with transparent ones
            bitmap.setHasAlpha(true);
            image = new BitmapDrawable(getContext().getResources(), bitmap);
            image.setBounds(0, 0, xNew, yNew);


            lastPoint = new Point(viewWidth / 2, viewHeight / 2);//TODO if starting point changes, adapt this too

            int uniquePassageMatrixWidth = viewWidth / (2 * circle_radius);
            int uniquePassageMatrixHeight = viewHeight / (2 * circle_radius);

            uniquePassageMatrix = new boolean[uniquePassageMatrixWidth][uniquePassageMatrixHeight];
            for (int i = 0; i < uniquePassageMatrixWidth; i++) {
                for (int j = 0; j < uniquePassageMatrixHeight; j++) {
                    uniquePassageMatrix[i][j] = false;
                }
            }
        }
    }

    public void update(Point p) { //game logic

        if (points != null && bitmap != null) {
            if (p.x - circle_radius > 0 && p.y - circle_radius > 0 && p.x + circle_radius < viewWidth && p.y + circle_radius < viewHeight) {
                drop = p;

                int discreteX = p.x / (2 * circle_radius);
                int discreteY = p.y / (2 * circle_radius);

                if (!uniquePassageMatrix[discreteX][discreteY]) { //gains score only for areas never painted before
                    uniquePassageMatrix[discreteX][discreteY] = true;
                    score += 10;
                }

                pixelList.clear();

                if (p.y < lastPoint.y) { //if drop goes up
                    if (p.x < lastPoint.x) {
                        pixelList.add(pixels[xyToIndex(p.x - diagonalCollisionmargin, p.y - diagonalCollisionmargin)]); // if drop goes left
                        pixelList.add(pixels[xyToIndex(p.x - collisionMargin, p.y)]);
                        pixelList.add(pixels[xyToIndex(p.x, p.y - collisionMargin)]);
                        pixelList.add(pixels[xyToIndex(p.x + diagonalCollisionmargin, p.y - diagonalCollisionmargin)]);
                        pixelList.add(pixels[xyToIndex(p.x - diagonalCollisionmargin, p.y + diagonalCollisionmargin)]);
                    } else {
                        pixelList.add(pixels[xyToIndex(p.x + diagonalCollisionmargin, p.y - diagonalCollisionmargin)]); // if drop goes right
                        pixelList.add(pixels[xyToIndex(p.x, p.y - collisionMargin)]);
                        pixelList.add(pixels[xyToIndex(p.x + collisionMargin, p.y)]);
                        pixelList.add(pixels[xyToIndex(p.x - diagonalCollisionmargin, p.y - diagonalCollisionmargin)]);
                        pixelList.add(pixels[xyToIndex(p.x + diagonalCollisionmargin, p.y + diagonalCollisionmargin)]);

                    }
                } else { //if drop goes down
                    if (p.x < lastPoint.x) {
                        pixelList.add(pixels[xyToIndex(p.x - diagonalCollisionmargin, p.y + diagonalCollisionmargin)]); // if drop goes left
                        pixelList.add(pixels[xyToIndex(p.x - collisionMargin, p.y)]);
                        pixelList.add(pixels[xyToIndex(p.x, p.y + collisionMargin)]);
                        pixelList.add(pixels[xyToIndex(p.x - diagonalCollisionmargin, p.y - diagonalCollisionmargin)]);
                        pixelList.add(pixels[xyToIndex(p.x + diagonalCollisionmargin, p.y + diagonalCollisionmargin)]);
                    } else {
                        pixelList.add(pixels[xyToIndex(p.x + diagonalCollisionmargin, p.y + diagonalCollisionmargin)]); // if drop goes right
                        pixelList.add(pixels[xyToIndex(p.x, p.y + collisionMargin)]);
                        pixelList.add(pixels[xyToIndex(p.x + collisionMargin, p.y)]);
                        pixelList.add(pixels[xyToIndex(p.x - diagonalCollisionmargin, p.y + diagonalCollisionmargin)]);
                        pixelList.add(pixels[xyToIndex(p.x + diagonalCollisionmargin, p.y - diagonalCollisionmargin)]);
                    }
                }

                pixelList.add(pixels[xyToIndex(p.x, p.y)]);

                checkCollision(pixelList);


            } else {

                doNotDrawNextLine = true; //to avoid draw a line across the screen

                if (p.y + circle_radius >= viewHeight) {
                    mainThread.accPointer.setPointerY(circle_radius);
                    drop.set(p.x, circle_radius);
                } else if (p.y - circle_radius <= 0) {
                    mainThread.accPointer.setPointerY(viewHeight - circle_radius);
                    drop.set(p.x, viewHeight - circle_radius);
                }
                if (p.x - circle_radius <= 0) {
                    mainThread.accPointer.setPointerX(viewWidth - circle_radius);
                    drop.set(viewWidth - circle_radius, p.y);
                } else if (p.x + circle_radius >= viewWidth) {
                    mainThread.accPointer.setPointerX(circle_radius);
                    drop.set(circle_radius, p.y);
                }

            }
        }
    }

    @Override
    public void draw(Canvas canvas) { //rendering
        super.draw(canvas);
        if (canvas != null) {

            if (!doNotDrawNextLine)
                bmpCanvas.drawLine(lastPoint.x, lastPoint.y, drop.x, drop.y, paintTrack);
            else
                doNotDrawNextLine = false;

            bmpCanvas.drawCircle(lastPoint.x, lastPoint.y, circle_radius, paintTrack);
            bmpCanvas.drawCircle(drop.x, drop.y, circle_radius, paintDrop);
            lastPoint.set(drop.x, drop.y);
            canvas.drawBitmap(viewBitmap, 0, 0, null);
            if (image != null) {
                image.draw(canvas);
            } else {
                canvas.drawColor(Color.MAGENTA);
            }
            canvas.drawText(String.valueOf(score), (int) (viewWidth * 0.65), 100, paintGold);
        }
    }


    private void checkCollision(List<Integer> pixelList) {
        int redValue;
        int greenValue;
        int blueValue;
        for (Integer pixelColor : pixelList) {
            redValue = Color.red(pixelColor);
            blueValue = Color.blue(pixelColor);
            greenValue = Color.green(pixelColor);

            if (redValue < pixelColorCheckTreshold && blueValue < pixelColorCheckTreshold && greenValue < pixelColorCheckTreshold) {
                mcontext.startService(intent);
                score -= 25 * difficulty;
                break;
            }
        }

    }


    private int xyToIndex(int x, int y) {
        return x + viewWidth * y;
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

    private void saveScore() {
        String default_username = getResources().getString(R.string.default_username);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        String username = sharedPreferences.getString(getResources().getString(R.string.username), default_username);
        ScoreManager.getInstance(getContext()).saveScore(new Score(level.levelId + 1, score, username));
    }

    public void destroy() {
        saveScore();
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



