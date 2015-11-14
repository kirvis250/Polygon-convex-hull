package lt.minvib.skaiciuojamojigrafika;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by minda on 2015-11-13.
 */
public class GrapicView extends View implements AsyncBaseHullUpdater.OnCompleteListener {


    public  interface OnPointEnteredListener{
        void onPointEntered();
    }


    List<Point> mainList = new ArrayList<>();
    List<Point> hull  = new ArrayList<>();

    AsyncBaseHullUpdater updater;

    private Paint mainLine;
    private Paint hullLine;
    private Paint pointDot;




    private OnPointEnteredListener pointEnteredListener;


    public static enum Mode { MODE_INSTANT, MODE_BY_STEP  }

    private boolean automaticallyRecalculate =true;
    private Mode mode = Mode.MODE_INSTANT;


    public GrapicView(Context context) {
        super(context);
        construct();
    }

    public GrapicView(Context context, AttributeSet attrs) {
        super(context, attrs);
        construct();
    }

    public GrapicView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        construct();
    }

    @TargetApi(21)
    public GrapicView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        construct();
    }


    public void construct()
    {
        mainLine = new Paint();
        mainLine.setColor(Color.GREEN);
        mainLine.setStrokeWidth(5f);

        hullLine = new Paint();
        hullLine.setColor(Color.RED);
        hullLine.setStrokeWidth(3f);

        pointDot  = new Paint();
        pointDot.setColor(Color.BLACK);
        pointDot.setStrokeWidth(10f);
    }


    public void undo(){
        if(mainList.size() > 0) {
            mainList.remove(mainList.size() - 1);
            invalidate();
            if (automaticallyRecalculate) recalculate();
        }
    }

    public void reset(){
        mainList.clear();
        hull.clear();
        if(updater != null) updater.setRunning(false);
        invalidate();
    }

    public void add(Point pt){
        mainList.add(pt);
        invalidate();
        if(automaticallyRecalculate)   recalculate();
    }

    public void recalculate(){
        if(updater != null) updater.setRunning(false);
        if(mainList.size() > 3) {

            if(mode == Mode.MODE_BY_STEP) {
                updater = new AsyncHullStepUpdater(mainList, new Handler());
            } else if(mode == Mode.MODE_INSTANT) {
                updater = new AsyncHullUpdater(mainList, new Handler());
            }
            updater.setOnCompleteListener(this);
            updater.start();
        } else {
            hull = new ArrayList<>();
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {


        //   if(event.getAction() == MotionEvent.ACTION_DOWN){
        add(new Point((int) event.getX(), (int) event.getY()));
        //   }

        if(pointEnteredListener != null) pointEnteredListener.onPointEntered();

        Log.i(":", "touched" + event.toString());
        return true;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if(mainList.size()>0){
            canvas.drawPoint((int)mainList.get(0).x, (int)mainList.get(0).y,pointDot);
        }
        if(mainList.size()> 1) {
            for (int i = 1; i < mainList.size(); i++) {
                drawLine(canvas, mainList.get(i - 1), mainList.get(i), mainLine);
                canvas.drawPoint((int)mainList.get(i).x, (int)mainList.get(i).y,pointDot);
            }
            drawLine(canvas, mainList.get(mainList.size() - 1), mainList.get(0), mainLine);
        }

        if(hull.size() > 3)
        {
            for(int i = 1 ; i<hull.size();i++) {
                drawLine(canvas,hull.get(i-1),hull.get(i),hullLine);
            }
            drawLine(canvas,hull.get(hull.size()-1),hull.get(0),hullLine);
        }

        if(pointEnteredListener != null) pointEnteredListener.onPointEntered();
    }


    public void drawLine(Canvas canvas, Point one, Point two, Paint pt)
    {
        float startX = one.x;
        float startY = one.y;
        float endX = two.x;
        float endY = two.y;

        canvas.drawLine(startX, startY, endX, endY, pt);
    }

    @Override
    public void onComplete(List<Point> convex) {
        hull =convex;
        invalidate();
    }


    public int[] getCount(){

        int [] count = new int[2];
        count[0] = hull!= null ? hull.size() : 0;
        count[1] = mainList!= null ? mainList.size() : 0;
        return count;
    }

    public boolean isAutomaticallyRecalculate() {
        return automaticallyRecalculate;
    }

    public void setAutomaticallyRecalculate(boolean automaticallyRecalculate) {
        this.automaticallyRecalculate = automaticallyRecalculate;
    }

    public Mode getMode() {
        return mode;
    }

    public void setMode(Mode mode) {
        this.mode = mode;
    }


    public OnPointEnteredListener getPointEnteredListener() {
        return pointEnteredListener;
    }

    public void setPointEnteredListener(OnPointEnteredListener pointEnteredListener) {
        this.pointEnteredListener = pointEnteredListener;
    }


    /**
     * @deprecated not used
     * @param P
     * @return
     */
    public static  Point[] simpleHull_2D( Point [] P )
    {
        // initialize a deque D[] from bottom to top so that the
        // 1st three vertices of P[] are a ccw triangle
        int n = P.length;

        Point [] D = new Point[2*n+1];

        int bot = n-2, top = bot+3;    // initial bottom and top deque indices
        D[bot] = D[top] = P[2];        // 3rd vertex is at both bot and top
        if (isLeft(P[0], P[1], P[2]) > 0) {
            D[bot+1] = P[0];
            D[bot+2] = P[1];           // ccw vertices are: 2,0,1,2
        }
        else {
            D[bot+1] = P[1];
            D[bot+2] = P[0];           // ccw vertices are: 2,1,0,2
        }

        // compute the hull on the deque D[]
        for (int i=3; i < n; i++) {   // process the rest of vertices
            // test if next vertex is inside the deque hull
            if ((isLeft(D[bot], D[bot+1], P[i]) > 0) &&
                    (isLeft(D[top-1], D[top], P[i]) > 0) )
                continue;         // skip an interior vertex

            // incrementally add an exterior vertex to the deque hull
            // get the rightmost tangent at the deque bot
            while (isLeft(D[bot], D[bot+1], P[i]) <= 0)
                ++bot;                 // remove bot of deque
            D[--bot] = P[i];           // insert P[i] at bot of deque

            // get the leftmost tangent at the deque top
            while (isLeft(D[top-1], D[top], P[i]) <= 0)
                --top;                 // pop top of deque
            D[++top] = P[i];           // push P[i] onto top of deque
        }

        // transcribe deque D[] to the output hull array H[]
        int h;        // hull vertex counter
        Point [] H = new Point[top-bot+1];
        for (h=0; h <= (top-bot); h++){
            H[h] = D[bot + h];
        }

        return H;// h-1;
    }


    private static float isLeft( Point P0, Point P1, Point P2 )
    {
        return (P1.x - P0.x)*(P2.y - P0.y) - (P2.x - P0.x)*(P1.y - P0.y);
    }



}
