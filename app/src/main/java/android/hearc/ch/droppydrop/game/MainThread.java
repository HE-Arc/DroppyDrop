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
        fps=60;

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
                //if (actualPoint.x != lastPoint.x && actualPoint.y != lastPoint.y) {
                    synchronized (surfaceHolder) {

                        gameView.addPoint(actualPoint);
                        //gameView.invalidate();
                        //Log.i(TAG, "onTouchEvent: succesfully add a point");
                        //this.gameView.update();
                        this.gameView.postInvalidate();
                        //this.gameView.draw(canvas);
                        //this.gameView.draw(canvas); //not sure if it's good to have this here
                    }

                //}
                //lastPoint.x = actualPoint.x;
                //lastPoint.y = actualPoint.y;
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
            try {
                sleep(30); //try to tweak this ?
            }catch(Exception e){

            }
        }
    }
}