package android.hearc.ch.droppydrop.game;

import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Point;
import android.hearc.ch.droppydrop.R;
import android.hearc.ch.droppydrop.sensor.AccelerometerPointer;
import android.preference.PreferenceManager;
import android.view.SurfaceHolder;

//inspired by https://www.androidauthority.com/android-game-java-785331/
public class MainThread extends Thread {
    private SurfaceHolder surfaceHolder;
    private GameView gameView;
    private boolean running;
    public static Canvas canvas;
    public AccelerometerPointer accPointer;
    private Point lastPoint;
    private long previousTime;
    private int fps;

    //https://stackoverflow.com/questions/6776327/how-to-pause-resume-thread-in-android
    private Object mPauseLock;
    private boolean mPaused;

    private static final String TAG = "MainThread";

    private SharedPreferences sharedPreferences;

    public MainThread(SurfaceHolder surfaceHolder, GameView gameView) {
        super();
        accPointer = new AccelerometerPointer(gameView.getContext(), gameView.getHeight(), gameView.getWidth());
        this.surfaceHolder = surfaceHolder;
        this.gameView = gameView;
        lastPoint = new Point(-1, -1);
        previousTime = System.currentTimeMillis();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this.gameView.getContext());

        //fps = 40; //try to adjust for maximum comfort, we could use this for the difficulty
        fps = sharedPreferences.getInt(this.gameView.getContext().getString(R.string.sensibility), 0) + 30;



        mPauseLock = new Object();
        mPaused = false;

    }

    public void onPause() {
        synchronized (mPauseLock) {
            mPaused = true;
            accPointer.stopAccelerometerSensor();
        }
    }

    public void onResume() {
        synchronized (mPauseLock) {
            mPaused = false;
            mPauseLock.notifyAll();
            accPointer.resumeAccelerometerSensor();
        }
    }

    public void setRunning(boolean isRunning) {
        running = isRunning;
    }

    @Override
    public void run() {

        while (running) {
            canvas = null;
            long currentTimeMillis = System.currentTimeMillis();
            long elapsedTimeMs = currentTimeMillis - previousTime;
            long sleepTimeMs = (long) (1000f / fps - elapsedTimeMs);

            try {
                canvas = this.surfaceHolder.lockCanvas();
                if (canvas == null) {
                    Thread.sleep(1);

                    continue;

                } else if (sleepTimeMs > 0) {

                    Thread.sleep(sleepTimeMs);

                }
                Point actualPoint = accPointer.getPointer();

                synchronized (surfaceHolder) {

                    gameView.addPoint(actualPoint);

                    this.gameView.update();

                    this.gameView.draw(canvas);
                }


            } catch (Exception e) {
            } finally {
                if (canvas != null) {
                    try {

                        surfaceHolder.unlockCanvasAndPost(canvas);
                        previousTime = System.currentTimeMillis();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            synchronized (mPauseLock) {
                while (mPaused) {
                    try {
                        mPauseLock.wait();
                    } catch (InterruptedException e) {
                    }
                }
            }
        }

    }
}