package lt.minvib.skaiciuojamojigrafika;


import android.graphics.Point;
import android.os.Handler;

import java.util.List;

/**
 * Created by minda on 2015-11-13.
 */
public class AsyncHullUpdater extends AsyncBaseHullUpdater {


    public AsyncHullUpdater(List<Point> list, Handler handler) {
        super(list, handler);
    }



    @Override
    public void run() {
        super.run();

        if(!running.get()) return;
        finalList = list;

        try {
            finalList = SimpleHull.simpleHull_2D(list);
        }catch (Exception e){
            e.printStackTrace();
        }

        if(!running.get()) return;

        onCompleted();
    }

}