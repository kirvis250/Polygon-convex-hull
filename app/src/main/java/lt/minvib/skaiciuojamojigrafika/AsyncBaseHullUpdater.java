package lt.minvib.skaiciuojamojigrafika;


import android.graphics.Point;
import android.os.Handler;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by minda on 2015-11-13.
 */
public abstract class AsyncBaseHullUpdater extends Thread {


    public interface OnCompleteListener{
        void onComplete(List<Point> convex);
    }

    protected List<Point> list;
    protected AtomicBoolean running = new AtomicBoolean(false);
    protected Handler handler;
    protected List<Point> finalList;
    protected OnCompleteListener onCompleteListener;


    public AsyncBaseHullUpdater(List<Point> list, Handler handler) {
        this.list = list;
        this.handler = handler;
    }

    @Override
    public synchronized void start() {
        super.start();
        running.set(true);
    }


    protected void onCompleted()
    {
        if(handler != null) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    if(onCompleteListener != null && running.get())
                        onCompleteListener.onComplete(finalList);
                }
            });
        } else {
            if(onCompleteListener != null&& running.get())
                onCompleteListener.onComplete(finalList);
        }
    }

    public List<Point> getList() {
        return list;
    }

    public void setList(List<Point> list) {
        this.list = list;
    }

    public boolean getRunning() {
        return running.get();
    }

    public void setRunning(boolean running) {
        this.running.set(running);
    }

    public OnCompleteListener getOnCompleteListener() {
        return onCompleteListener;
    }

    public void setOnCompleteListener(OnCompleteListener onCompleteListener) {
        this.onCompleteListener = onCompleteListener;
    }
}