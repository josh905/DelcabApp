package delcab.delcab;

import com.google.android.gms.maps.model.LatLng;

public class Single {
    private static Single single, strTest;

    private Single() {}

    //mins and km.
    public double journeyDistance, journeyDuration, test;

    public static Single use() {

        if (single == null) {
            single = new Single();
        }
        return single;
    }


    public void setJourneyDistance(double journeyDistance) {
        this.journeyDistance = journeyDistance;
    }

    public void setJourneyDuration(double journeyDuration) {
        this.journeyDuration = journeyDuration;
    }


    @Override
    public String toString() {
        return "Single{" +
                "journeyDistance=" + journeyDistance +
                ", journeyDuration=" + journeyDuration +
                ", test=" + test +
                '}';
    }

    public void setTest(double test) {
        this.test = test;
    }
}