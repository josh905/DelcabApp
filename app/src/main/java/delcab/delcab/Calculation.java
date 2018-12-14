package delcab.delcab;

import com.google.android.gms.maps.model.LatLng;

public class Calculation {
    private static Calculation calc;

    private Calculation() {}

    public LatLng start, end;
    public double distance, price;
    public String name;

    public static Calculation getCalculation() {

        if (calc == null) {
            calc = new Calculation();
        }
        return calc;
    }


    public void addCalculation(LatLng start, LatLng end, double distance){
        this.start = start;
        this.end = end;
        this.distance = distance;


    }







}