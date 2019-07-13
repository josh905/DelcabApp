package delcab.delcab.directioncalculations;


import android.app.PendingIntent;
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

import delcab.delcab.Global;
import delcab.delcab.PriceActivity;
import delcab.delcab.Print;
import delcab.delcab.UploadPackage;


public class DataParser {

  // private Context con;
    private GoogleMap map;
    private LatLng endLoc;

    public DataParser(GoogleMap map, LatLng endLoc){
        //this.con = con;
        this.map = map;
        this.endLoc = endLoc;
    }


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

            Print.out("distance..."+distance);


            /*
            Intent in = new Intent(con, UploadPackage.class);
            in.putExtra("duration", duration);
            in.putExtra("distance", distance);

            con.startActivity(in);
            */


          //  Global.set(con, "duration", duration);
            //Global.set(con, "distance", distance);



        } catch (JSONException e) {
            e.printStackTrace();
        }
        return dirMap;

    }



}

