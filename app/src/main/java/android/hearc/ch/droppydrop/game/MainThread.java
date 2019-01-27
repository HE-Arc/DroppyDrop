package android.hearc.ch.droppydrop.game;

import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Point;
import android.hearc.ch.droppydrop.R;
import android.hearc.ch.droppydrop.sensor.AccelerometerPointer;
import android.preference.PreferenceManager;
import android.view.SurfaceHolder;

/**
 * MainThread
 */
public class MainThread extends Thread {
    private static final String TAG = MainThread.class.getName();

    private SurfaceHolder surfaceHolder;
    private GameView gameView;
    private boolean running;
    private Point lastPoint;
    private long previousTime;
    private int fps;
    private Object mPauseLock;
    private boolean mPaused;
    private SharedPreferences sharedPreferences;

    public static Canvas canvas;
    public AccelerometerPointer accPointer;

    /**
     * Constructor of MainThread
     * @param surfaceHolder surfaceHoldeer to draw on
     * @param gameView GameView
     */
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

    /**
     * OnPause, stop the accelerometer sensor
     */
    public void onPause() {
        synchronized (mPauseLock) {
            mPaused = true;
            accPointer.stopAccelerometerSensor();
        }
    }

    /**
     * OnResume, restrat the accelerometer sensor
     */
    public void onResume() {
        synchronized (mPauseLock) {
            mPaused = false;
            mPauseLock.notifyAll();
            accPointer.resumeAccelerometerSensor();
        }
    }

    /**
     * Set isRunning State
     * @param isRunning
     */
    public void setRunning(boolean isRunning) {
        running = isRunning;
    }

    /**
     * Get the new pointer position and ask GameView to redraw itself
     */
    @Override
    public void run() {
        long currentTimeMillis;
        long elapsedTimeMs;
        long sleepTimeMs;

        while (running) {
            canvas = null;
            currentTimeMillis = System.currentTimeMillis();
            elapsedTimeMs = currentTimeMillis - previousTime;
            sleepTimeMs = (long) (1000f / fps - elapsedTimeMs);

            try {
                canvas = this.surfaceHolder.lockCanvas();
                if (canvas == null) {
                    Thread.sleep(1);
                    continue;
                }
                else if (sleepTimeMs > 0) {
                    Thread.sleep(sleepTimeMs);
                }
                Point actualPoint = accPointer.getPointer();

                synchronized (surfaceHolder) {
                    this.gameView.update(actualPoint);

                    this.gameView.draw(canvas);
                }
            }
            catch (Exception e) {}
            finally {
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