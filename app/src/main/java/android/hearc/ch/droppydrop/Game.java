package android.hearc.ch.droppydrop;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public class Game extends View {

    private Paint backgroundPaint;
    private Paint paintDrop;

    private Point p;

    public Game(Context context) {
        super(context);
        init();
    }

    public Game(Context context, AttributeSet attrs) {
        super(context, attrs);
        Log.i("GAME", "is in game constructor");
        init();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.i("GAME", "touch the game");

        if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
            p = new Point((int) event.getX(), (int) event.getY());
        }
        invalidate();
        return false;
    }

    private void init() {
        // TODO draw base level design
        backgroundPaint = new Paint();
        backgroundPaint.setColor(0xFFFFFF);

        paintDrop = new Paint();
        paintDrop.setColor(0xFF0000);

        p = new Point(0,0);

        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Log.i("GAME", "is in game onMeasure");
        // TODO implements onMeasure : size min & max

        setMeasuredDimension(300, 300);
    }

    @Override
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);

        Log.i("GAME", "is in game onDraw");
        Log.i("GAME", p.toString());
        /*if(canvas!=null)
            Log.i("GAME", "onDraw: canevas not null");*/

        //canvas.save();

        //TODO draw drop at the center
        canvas.drawCircle(p.x, p.y, 50, paintDrop);

        //canvas.restore();
    }

}
