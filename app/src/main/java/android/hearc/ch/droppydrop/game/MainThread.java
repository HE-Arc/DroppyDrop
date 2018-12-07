package android.hearc.ch.droppydrop.game;

import android.graphics.Canvas;
import android.hearc.ch.droppydrop.sensor.AccelerometerPointer;
import android.util.Log;
import android.view.SurfaceHolder;

public class MainThread extends Thread {
    private SurfaceHolder surfaceHolder;
    private GameView gameView;
    private boolean running;
    public static Canvas canvas;
    public AccelerometerPointer accPointer;

    private static final String TAG = "MainThread";


    public MainThread(SurfaceHolder surfaceHolder, GameView gameView) {
        super();
        accPointer = new AccelerometerPointer(gameView.getContext(), gameView.getHeight(), gameView.getWidth());
        this.surfaceHolder = surfaceHolder;
        this.gameView = gameView;

    }

    public void setRunning(boolean isRunning) {
        running = isRunning;
    }

    @Override
    public void run() {
        while (running) {
            canvas = null;

            try {
                canvas = this.surfaceHolder.lockCanvas();
                synchronized(surfaceHolder) {
                    if(gameView.addPoint(accPointer.getPointer())){
                        //gameView.invalidate();
                        Log.i(TAG, "onTouchEvent: succesfully add a point");
                        this.gameView.update();
                        this.gameView.draw(canvas);
                    }else{
                        Log.e(TAG, "onTouchEvent: cannot add point");
                    }
                }
            } catch (Exception e) {} finally {
                if (canvas != null) {
                    try {
                        surfaceHolder.unlockCanvasAndPost(canvas);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            try {
                sleep(16);
            }catch(Exception e){

            }
        }
    }
}