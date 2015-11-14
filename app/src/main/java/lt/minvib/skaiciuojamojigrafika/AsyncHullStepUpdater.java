package lt.minvib.skaiciuojamojigrafika;


import android.graphics.Point;
import android.os.Handler;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by minda on 2015-11-13.
 */
public class AsyncHullStepUpdater extends AsyncBaseHullUpdater {

    public AsyncHullStepUpdater(List<Point> list, Handler handler) {
        super(list, handler);
    }

    protected  enum Turn { COUNTER_CLOCKWISE, CLOCKWISE_OR_COLLINEAR }

    @Override
    public void run() {
        super.run();

        if(!running.get()) return;
        finalList = list;

        try {

            List<Point> P =  list;

            // Nustatomas dydis (nereikalingas jeigu vietoj jo naudosime P.size() )
            int n = P.size();

            // Sukuriamas naujas sąrašas su 2*n+1 talpa, nes reiks talpinti jame dvigubą eilę
            ArrayList<Point> D = new ArrayList<>(2*n+1);

            // implementacijai su masyvais pradinio priskyrimo nereiktų.
            for(int i = 0; i < 2*n+1 ;i++)
                D.add(null);

            int bot = n-2, top = bot+3;    // Priskiriami pradiniai indekesai (D masyvo viduryje)


            // Jiems nustatomas trečias pradinio sąrašo elementas
            D.set(bot,P.get(2));
            D.set(top,P.get(2));

            // Formuojamas pirmasis apvalkas iš trikampio priklausomai nuo to, kokia tvarka duoti taškai
            if (getTurn(P.get(0), P.get(1), P.get(2)) == Turn.COUNTER_CLOCKWISE) {
                D.set(bot+1,P.get(0));
                D.set(bot+2,P.get(1));// taškai yra: 2,0,1,2
            }
            else {
                D.set(bot+1,P.get(1));
                D.set(bot+2,P.get(0));//taškai yra: 2,1,0,2
            }

            updateStep(D, null, bot, top);

            // skaičiuojamas kiekvienas sekantis apvalkas
            for (int i=3; i < n; i++) {

                updateStep(D, P.get(i), bot, top);

                // Patikrinimas, ar nauja  viršūnė yra apvalko viduje
                if ((getTurn(D.get(bot), D.get(bot + 1), P.get(i)) == Turn.COUNTER_CLOCKWISE) &&(getTurn(D.get(top-1),D.get(top), P.get(i)) == Turn.COUNTER_CLOCKWISE) ) {
                    updateStep(D, null, bot, top);
                    continue;   // virūnė yra viduje, daugiau nieko nebereikia daryti
                }

                // Trinamos virūnės esančios apačioje tol kol  D.get(bot+1) nėra apvalko išorinė viršūnė
                while (getTurn(D.get(bot), D.get(bot + 1), P.get(i)) == Turn.CLOCKWISE_OR_COLLINEAR) {
                    updateStep(D, P.get(i), bot, top);
                    ++bot;              // trinama apačioje
                    updateStep(D, null, bot, top);
                }
                D.set(--bot, P.get(i));   // pridedama Pi eilės apačioje
                updateStep(D, null, bot, top);

                // Trinamos virūnės esančios viršuje tol kol  D.get(top) nėra apvalko išorinė viršūnė
                while (getTurn(D.get(top - 1), D.get(top), P.get(i)) == Turn.CLOCKWISE_OR_COLLINEAR) {
                    updateStep(D, P.get(i), bot, top);
                    --top;                 // trinama viršuje
                    updateStep(D, null, bot, top);
                }
                D.set(++top, P.get(i));        // pridedama Pi eilės viršuje
                updateStep(D, null, bot, top);
            }
            updateStep(D, null, bot, top);

        }catch (Exception e){
            e.printStackTrace();
            if(!running.get()) return;
            finalList = list;
            onCompleted();
        }

    }


    public void updateStep(List<Point> D, Point aa, int bot, int top){
        finalList = calcD(D,  bot,  top);
        if(aa != null)
            finalList.add(aa);
        onProgressUpdate();
    }


    public List<Point> calcD(List<Point> D, int bot, int top){
        List<Point> ll  = new ArrayList<>();
        for (int h=0; h <= (top-bot); h++){
            ll.add(D.get(bot + h));
        }
        return  ll;
    }


    public void onProgressUpdate(){
        onCompleted();
        tryToSleep(50);
    }



    public void tryToSleep(int millis){
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    protected static Turn getTurn(Point a, Point b, Point c) {

        // use longs to guard against int-over/underflow
        long crossProduct = (((long)b.x - a.x) * ((long)c.y - a.y)) -
                (((long)b.y - a.y) * ((long)c.x - a.x));

        if(crossProduct > 0) {
            return Turn.COUNTER_CLOCKWISE;
        }
        else  {
            return Turn.CLOCKWISE_OR_COLLINEAR;
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