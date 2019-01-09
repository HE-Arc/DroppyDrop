package android.hearc.ch.droppydrop.game;

import android.graphics.Canvas;
import android.graphics.Point;
import android.hearc.ch.droppydrop.sensor.AccelerometerPointer;
import android.util.Log;
import android.view.SurfaceHolder;

public class MainThread extends Thread {
    private SurfaceHolder surfaceHolder;
    private GameView gameView;
    private boolean running;
    public static Canvas canvas;
    public AccelerometerPointer accPointer;
    private Point lastPoint;
    private long previousTime;
    private long fps;

    private static final String TAG = "MainThread";


    public MainThread(SurfaceHolder surfaceHolder, GameView gameView) {
        super();
        accPointer = new AccelerometerPointer(gameView.getContext(), gameView.getHeight(), gameView.getWidth());
        this.surfaceHolder = surfaceHolder;
        this.gameView = gameView;
        lastPoint = new Point(-1, -1);
        previousTime=System.currentTimeMillis();;
        fps=30; //try to adjust for maximum comfort, we could use this for the difficulty

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
            long sleepTimeMs = (long) (1000f/ fps - elapsedTimeMs);

            try {
                canvas = this.surfaceHolder.lockCanvas();
                if (canvas == null) {
                    Thread.sleep(1);

                    continue;

                }else if (sleepTimeMs > 0){

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

        }
    }
}