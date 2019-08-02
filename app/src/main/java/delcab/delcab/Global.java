package delcab.delcab;


import android.content.Context;

import java.io.IOException;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.GoogleMap;

import org.json.JSONException;
import org.json.JSONObject;


import java.math.BigDecimal;
import java.math.RoundingMode;

import java.sql.Time;

import java.util.HashMap;
import java.util.Map;


public class Global {

    private static String drivingResponse;

    public static void set(Context con, String key, String value){
        con.getSharedPreferences("DELCAB", Context.MODE_PRIVATE).edit()
                .putString(key, value).apply();
    }

    public static String get(Context con, String key){
        return con.getSharedPreferences("DELCAB", Context.MODE_PRIVATE)
                .getString(key, "none");
    }




    public static String internetStatus() throws InterruptedException, IOException {
        String status = "down";
        //this is considered the only accurate way to see is user's internet available
        //this is because they could be connected to WiFi or other network...
        //but that network has no internet access
        //so this provides the true test
        final String command = "ping -c 1 google.com";
        //only wait 0.5 seconds ... how?
        if(Runtime.getRuntime().exec(command).waitFor() == 0) status = "up";

        else status = "down";

        return status;

    }

    public static LatLng midlands(){
        return new LatLng(53.3888024,-7.8011687);
    }

    public static void goTo(GoogleMap theMap, LatLng thePlace, int zoomLevel){
        CameraPosition pos = new CameraPosition.Builder().target(thePlace).zoom(zoomLevel).build();
        theMap.animateCamera(CameraUpdateFactory.newCameraPosition(pos));
    }

    public static void goToTilt(GoogleMap theMap, LatLng thePlace, int zoomLevel) {
        CameraPosition pos = new CameraPosition.Builder()
                .target(thePlace).zoom(zoomLevel).tilt(89).bearing(20).build();
        theMap.animateCamera(CameraUpdateFactory.newCameraPosition(pos));
    }

    public static double round(double value, int places){
        return new BigDecimal(value).setScale(places, RoundingMode.DOWN).doubleValue();
    }

    public static double euro(double value) {
        return new BigDecimal(value).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    public static double crowDistance(){
        return 0;
    }


    public static double timeInMins(Time start, Time now){
        return 0;
    }


    public static void drivingDetails(final Context con, LatLng start, LatLng end) throws IOException {

        String str_origin = "origin=" + start.latitude + "," + start.longitude;

        String str_dest = "destination=" + end.latitude + "," + end.longitude;

        String mode = "mode=driving";
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + mode;

        // Building the url to the web service
        final String url = "https://maps.googleapis.com/maps/api/directions/json?" + parameters + "&key=" + con.getString(R.string.google_maps_key);


        //START of HTTP request
        StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.POST, "http://delcab.ie/webservice/driving_details.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);

                    String strDriving = jsonObject.toString();

                    //if its missing either of these or both of these
                    if(!(strDriving.contains("distance") && strDriving.contains("duration"))){
                        Print.toast(con, "Could not get driving details");
                        return ;
                    }

                    String chopped =  strDriving.substring(strDriving.indexOf("\"distance\":"), strDriving.indexOf("\"end_address\":"));
                    Print.out(chopped);
                    String[] split = chopped.split("\"duration\":");

                    String distance = split[0].substring(split[0].indexOf("\"value\":"));
                    String duration = split[1].substring(split[1].indexOf("\"value\":"));
                    distance = distance.substring(8, distance.length() - 2);
                    duration = duration.substring(8, duration.length() - 2);


                    set(con, "distanceNum",distance);

                    set(con,"durationNum",duration);


                    set(con, "distanceStr", split[0].substring(20, split[0].indexOf("\",\"value\":")));

                    set(con, "durationStr", split[1].substring(9, split[1].indexOf("\",\"value\":")));


                }
                catch (Exception e) {
                    e.printStackTrace();
                    Print.out("Could not parse driving details");
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

                params.put("drivingurl", url);

                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(con);
        requestQueue.add(stringRequest);
        //END of HTTP request


    }




    public static void drivingDetailsFromLocation(final Context con, LatLng start, LatLng end) throws IOException {

        String str_origin = "origin=" + start.latitude + "," + start.longitude;

        String str_dest = "destination=" + end.latitude + "," + end.longitude;

        String mode = "mode=driving";
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + mode;

        // Building the url to the web service
        final String url = "https://maps.googleapis.com/maps/api/directions/json?" + parameters + "&key=" + con.getString(R.string.google_maps_key);


        //START of HTTP request
        StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.POST, "http://delcab.ie/webservice/driving_details.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);

                    String strDriving = jsonObject.toString();

                    //if its missing either of these or both of these
                    if(!(strDriving.contains("distance") && strDriving.contains("duration"))){
                        Print.toast(con, "Could not get driving details");
                        return ;
                    }

                    String chopped =  strDriving.substring(strDriving.indexOf("\"distance\":"), strDriving.indexOf("\"end_address\":"));
                    Print.out(chopped);
                    String[] split = chopped.split("\"duration\":");


                    set(con, "distanceFromLoc", split[0].substring(20, split[0].indexOf("\",\"value\":")));

                    set(con, "durationFromLoc", split[1].substring(9, split[1].indexOf("\",\"value\":")));


                }
                catch (Exception e) {
                    e.printStackTrace();
                    Print.out("Could not parse driving details");
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

                params.put("drivingurl", url);

                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(con);
        requestQueue.add(stringRequest);
        //END of HTTP request


    }




    public static void drivingDetailsToDestination(final Context con, LatLng start, LatLng end) throws IOException {

        String str_origin = "origin=" + start.latitude + "," + start.longitude;

        String str_dest = "destination=" + end.latitude + "," + end.longitude;

        String mode = "mode=driving";
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + mode;

        // Building the url to the web service
        final String url = "https://maps.googleapis.com/maps/api/directions/json?" + parameters + "&key=" + con.getString(R.string.google_maps_key);


        //START of HTTP request
        StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.POST, "http://delcab.ie/webservice/driving_details.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);

                    String strDriving = jsonObject.toString();

                    //if its missing either of these or both of these
                    if(!(strDriving.contains("distance") && strDriving.contains("duration"))){
                        Print.toast(con, "Could not get driving details");
                        return ;
                    }

                    String chopped =  strDriving.substring(strDriving.indexOf("\"distance\":"), strDriving.indexOf("\"end_address\":"));
                    Print.out(chopped);
                    String[] split = chopped.split("\"duration\":");

                    String duration = split[1].substring(split[1].indexOf("\"value\":"));

                    duration = duration.substring(8, duration.length() - 2);

                    Print.out("DURDO is "+duration);

                    set(con,"durationToDest",duration);


                }
                catch (Exception e) {
                    e.printStackTrace();
                    Print.out("Could not parse driving details");
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

                params.put("drivingurl", url);

                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(con);
        requestQueue.add(stringRequest);
        //END of HTTP request


    }





}
