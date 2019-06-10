package delcab.delcab.directioncalculations;


import android.content.Context;
import android.content.Intent;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import delcab.delcab.PriceActivity;
import delcab.delcab.Print;


public class DataParser {

    private Context con;
    private GoogleMap map;
    private LatLng endLoc;

    public DataParser(GoogleMap map, LatLng endLoc, Context con){
        this.con = con;
        this.map = map;
        this.endLoc = endLoc;
    }

    public String distanceStr;
    public String durationStr;

    public List<List<HashMap<String, String>>> parse(JSONObject jObject) {


        //call method to retrieve distance and duration
        distanceDuration(jObject);



        List<List<HashMap<String, String>>> routes = new ArrayList<>();
        JSONArray jRoutes;
        JSONArray jLegs;
        JSONArray jSteps;
        try {
            jRoutes = jObject.getJSONArray("routes");
            /** Traversing all routes */
            for (int i = 0; i < jRoutes.length(); i++) {
                jLegs = ((JSONObject) jRoutes.get(i)).getJSONArray("legs");
                List path = new ArrayList<>();
                /** Traversing all legs */
                for (int j = 0; j < jLegs.length(); j++) {
                    jSteps = ((JSONObject) jLegs.get(j)).getJSONArray("steps");

                    /** Traversing all steps */
                    for (int k = 0; k < jSteps.length(); k++) {
                        String polyline = "";
                        polyline = (String) ((JSONObject) ((JSONObject) jSteps.get(k)).get("polyline")).get("points");
                        List<LatLng> list = decodePoly(polyline);

                        /** Traversing all points */
                        for (int l = 0; l < list.size(); l++) {
                            HashMap<String, String> hm = new HashMap<>();
                            hm.put("lat", Double.toString((list.get(l)).latitude));
                            hm.put("lng", Double.toString((list.get(l)).longitude));
                            path.add(hm);
                        }
                    }
                    routes.add(path);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
        }
        return routes;
    }


    /**
     * Method to decode polyline points
     * Courtesy : https://jeffreysambells.com/2010/05/27/decoding-polylines-from-google-maps-direction-api-with-java
     */
    private List<LatLng> decodePoly(String encoded) {

        List<LatLng> poly = new ArrayList<>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }

        return poly;
    }

    public HashMap<String, String> parseDirections(String jsonData){
        JSONArray jsonArray = null;
        JSONObject jsonObject = null;

        try{
            jsonObject = new JSONObject(jsonData);
            jsonArray = jsonObject.getJSONArray("routes").getJSONObject(0).getJSONArray("legs");
        }
        catch(JSONException e){
            e.printStackTrace();
        }

        return getDuration(jsonArray);

    }

    private HashMap<String, String> distanceDuration(JSONObject jsonObject){
        JSONArray jsonArray = null;


        try{
        //    jsonObject = new JSONObject(jsonData);
            jsonArray = jsonObject.getJSONArray("routes").getJSONObject(0).getJSONArray("legs");
        }
        catch(JSONException e){
            e.printStackTrace();
        }

        return getDuration(jsonArray);

    }


    private HashMap<String, String> getDuration(JSONArray dirJSON) {

        Print.out(dirJSON.toString());
        HashMap<String, String> dirMap = new HashMap<>();
        String duration = "";
        String distance = "";


        try {
            duration = dirJSON.getJSONObject(0).getJSONObject("duration").getString("text");
            distance = dirJSON.getJSONObject(0).getJSONObject("distance").getString("text");

            dirMap.put("duration", duration);
            dirMap.put("distance", distance);



/*

            double dist = 0;
            double dur = 0;

            String[] distArr = distance.split(" ");
            distance = distArr[0];

            String[] durArr = duration.split(" ");
            duration = durArr[0];

            durationStr = duration;
            distanceStr = distance;

            try{
                dist = Double.parseDouble(distance);
                dur = Double.parseDouble(duration);
            }
            catch(NumberFormatException e){
                e.printStackTrace();
            }


            Single.use().setJourneyDistance(dist);
            Single.use().setJourneyDuration(dur);
            Single.use().setTest(12345.67);
            */



            //insert to db

            final String finalDur = duration;
            final String finalDis = distance;

            final String[] arr = {distance,duration};

            //HttpRequest req = new HttpRequest();
           // String response = req.toURL(con,"post_estimate", arr);
           // Print.out("delcab.ie response: "+response);



            Intent in = new Intent(con, PriceActivity.class);
            in.putExtra("duration", duration);
            in.putExtra("distance", distance);

            con.startActivity(in);

            /*
            Print.toast(con, distance + " ... " + duration);


            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                   Print.toast(con, "Calculating price ...");
                }
            },2500);



            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                   con.startActivity(new Intent(con,SplashActivity.class));
                }
            },4500);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Print.toast(con, "Price 34533");
                }
            },6500);


            map.moveCamera(CameraUpdateFactory.newLatLng(endLoc));
            CameraPosition pos = new CameraPosition.Builder().target(endLoc).zoom(18).tilt(89).bearing(20).build();
            map.animateCamera(CameraUpdateFactory.newCameraPosition(pos));



            CountDownTimer theTimer = new CountDownTimer(5000,5000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    // Print.toast(con, "Calculating price ...");

                }

                @Override
                public void onFinish() {

                    con.startActivity(new Intent(con, SplashActivity.class));

                    Intent in = new Intent(con, PriceActivity.class);
                    in.putExtra("duration", finalDur);
                    in.putExtra("distance", finalDis);
                    con.startActivity(in);
                }
            };
            theTimer.start();

*/


        } catch (JSONException e) {
            e.printStackTrace();
        }
        return dirMap;

    }



}

