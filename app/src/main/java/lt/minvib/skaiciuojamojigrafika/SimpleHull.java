package lt.minvib.skaiciuojamojigrafika;


import android.graphics.Point;

import java.util.ArrayList;
import java.util.List;

/**
 * Orginalus pateiktas http://geomalgorithms.com/a12-_hull-3.html
 */
public class SimpleHull {


    protected static enum Turn { COUNTER_CLOCKWISE, CLOCKWISE_OR_COLLINEAR }

    /** Simple hull algoritmas Pagal http://geomalgorithms.com/a12-_hull-3.html
     *  perrašytas Mindaugo Viburio naudojant sąrašus java kalboje
     *
     * @param P - Su savimi nesikertantis daugiakampis;
     * @return gaubiantis apvalkas
     */
    public static   List<Point> simpleHull_2D( List<Point> P )
    {
        // Nustatomas dydis (nereikalingas jeigu vietoj jo naudosime P.size() )
        int n = P.size();

        // Sukuriamas naujas sąrašas su 2*n+1 talpa, nes reiks talpinti jame dvigubą eilę
        ArrayList<Point>  D = new ArrayList<>(2*n+1);

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

        // skaičiuojamas kiekvienas sekantis apvalkas
        for (int i=3; i < n; i++) {

            // Patikrinimas, ar nauja  viršūnė yra apvalko viduje
            if ((getTurn(D.get(bot), D.get(bot + 1), P.get(i)) == Turn.COUNTER_CLOCKWISE) &&(getTurn(D.get(top-1),D.get(top), P.get(i)) == Turn.COUNTER_CLOCKWISE) )
                continue;   // virūnė yra viduje, daugiau nieko nebereikia daryti

            // Trinamos virūnės esančios apačioje tol kol  D.get(bot+1) nėra apvalko išorinė viršūnė
            while (getTurn(D.get(bot), D.get(bot + 1), P.get(i)) == Turn.CLOCKWISE_OR_COLLINEAR)
                ++bot;              // trinama apačioje
            D.set(--bot, P.get(i));   // pridedama Pi eilės apačioje

            // Trinamos virūnės esančios viršuje tol kol  D.get(top) nėra apvalko išorinė viršūnė
            while (getTurn(D.get(top - 1), D.get(top), P.get(i)) == Turn.CLOCKWISE_OR_COLLINEAR)
                --top;                 // trinama viršuje
            D.set(++top,P.get(i));        // pridedama Pi eilės viršuje
        }

        // Suformuojamas apvalko viršūnių sąrašas kurio dydis yra top-bot+1
        List<Point> H = new ArrayList<>();
        for (int h=0; h <= (top-bot); h++){
            H.add(D.get(bot + h));
        }

        return H;// h-1;
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



}
