package delcab.delcab;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static android.content.Context.LOCATION_SERVICE;

public class LocationTracker{
    private static LocationTracker tracker;

    private LocationListener locationListener;
    private LocationManager locationManager;
    private ScheduledExecutorService exec;
    private double lat, lon;
    private int runCount;
    private int status;

    private LatLng latLng;


    private LocationTracker() {}

    public int getStatus(){
        return status;
    }

    public LatLng getLatLng(){
        return latLng;
    }

    public void start(final Context con){

        if(status==1){
            Print.out("Already on");
            return;
        }

        Print.out("Location tracking started.");

        status = 1;

        runCount = 0;

        lat = 0.0;
        lon = 0.0;


        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                try {
                    lat = Global.round(location.getLatitude(),8);
                    lon = Global.round(location.getLongitude(),8);

                    latLng = new LatLng(lat, lon);

                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };

        locationManager = (LocationManager) con.getSystemService(LOCATION_SERVICE);

        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 5, locationListener);
        }
        catch (SecurityException e){
            e.printStackTrace();
        }



        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                runCount++;
                if(runCount>1 && lat!=0 && lat!=0.0 && lon!=0 && lon!=0.0){
                    Print.out(lat + " & " + lon);

                    //START of HTTP request
                    StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.POST, "http://delcab.ie/webservice/update_location.php", new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);

                                String message = jsonObject.getString("message");
                                Print.out(message);

                            }
                            catch (Exception e) {
                                e.printStackTrace();
                                Print.out("Could not update location");
                            }
                        }

                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Print.out("Volley error ... "+error.toString());
                        }
                    }){
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String, String> params = new HashMap<>();
                            params.put("key1", RequestHeader.key1);
                            params.put("key2", RequestHeader.key2);

                            params.put("taxi_id", Global.get(con, "taxiId"));
                            params.put("lat", Double.toString(lat));
                            params.put("lon", Double.toString(lon));

                            return params;
                        }
                    };

                    RequestQueue requestQueue = Volley.newRequestQueue(con);
                    requestQueue.add(stringRequest);
                    //END of HTTP request

                }

            }
        };

        exec = Executors.newScheduledThreadPool(1);

        //every 15 seconds, execute runnable
        exec.scheduleAtFixedRate(runnable, 0, 15, TimeUnit.SECONDS);

    }

    public void stop(final Context con){
        locationManager.removeUpdates(locationListener);
        exec.shutdown();
        //locationListener = null;
        status = 0;
        Print.out("Location tracking stopped.");
    }

    public static LocationTracker getTracker() {

        if (tracker == null) {
            tracker = new LocationTracker();
        }
        return tracker;
    }
}