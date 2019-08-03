package delcab.delcab;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AlertDialog;


import android.view.View;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class TaxiHomeActivity extends AppCompatActivity {

    private ScheduledExecutorService exec;
    private int runCount;
    private String end_lat, end_lon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_taxi_home);



//        getApplicationContext().getSharedPreferences("DELCAB", 0).edit().clear().apply();
//        startActivity(new Intent(getApplicationContext(),MainActivity.class));
//        finish();


        runCount = 0;

        exec = null;

        findViewById(R.id.deliveriesBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean active = true;

                try{
                    Integer.parseInt(Global.get(getApplicationContext(), "packageId"));
                }
                catch(Exception ex){
                    active = false;
                }

                if(active){
                    startActivity(new Intent(getApplicationContext(), TaxiCurrentDeliveryActivity.class));
                }
                else{
                    Print.toast(getApplicationContext(), "You have no active packages");
                }

            }
        });

        findViewById(R.id.packagesBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), TaxiPackageActivity.class));
            }
        });

        findViewById(R.id.profileBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),TaxiProfileActivity.class));
            }
        });

        findViewById(R.id.logoutBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LocationTracker.getTracker().stop(getApplicationContext());
                getApplicationContext().getSharedPreferences("DELCAB", 0).edit().clear().apply();
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
                finish();
            }
        });


        findViewById(R.id.testOnBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), TestModeActivity.class));
                //finish();
            }
        });

        findViewById(R.id.testOffBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Global.set(getApplicationContext(), "testMode", "off");
                try{
                    Print.out("trying to shut exec");
                    exec.shutdown();
                }
                catch (Exception ex){
                    Print.out("cant shut exec");
                }
                runCount = 0;
                startActivity(new Intent(getApplicationContext(), SplashActivity.class));
            }
        });





    }


    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Closing App")
                .setMessage("Are you sure you want to close Delcab?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //finishAffinity();
                        moveTaskToBack(true);
                    }

                })
                .setNegativeButton("No", null)
                .show();
    }


    @Override
    public void onResume(){

        super.onResume();












        if(Global.get(getApplicationContext(), "testMode").equals("on")){

            LocationTracker.getTracker().stop(getApplicationContext());

            //START of HTTP request
            StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.POST, "http://delcab.ie/webservice/get_package_details.php", new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {


                    try {

                        JSONObject jsonObject = new JSONObject(response);

                        String message = jsonObject.getString("message");
                        Print.out(message);


                        if(message.equals("package found")){
                            Print.out("package found .....");

                            Global.set(getApplicationContext(), "packageId", jsonObject.getString("package_id"));
                            Global.set(getApplicationContext(), "businessId", jsonObject.getString("business_id"));
                            Global.set(getApplicationContext(), "businessName", jsonObject.getString("business_name"));
                            Global.set(getApplicationContext(), "startAd", jsonObject.getString("start_ad"));
                            Global.set(getApplicationContext(), "endAd", jsonObject.getString("end_ad"));
                            Global.set(getApplicationContext(), "driverPay", jsonObject.getString("driver_pay"));
                            Global.set(getApplicationContext(), "startLat", jsonObject.getString("start_lat"));
                            Global.set(getApplicationContext(), "startLon", jsonObject.getString("start_lon"));
                            Global.set(getApplicationContext(), "endLat", jsonObject.getString("end_lat"));
                            Global.set(getApplicationContext(), "endLon", jsonObject.getString("end_lon"));
                            end_lat = jsonObject.getString("end_lat");
                            end_lon = jsonObject.getString("end_lon");



                            if(runCount<1){
                                //if(exec==null) {

                                Runnable runnable = new Runnable() {
                                    @Override
                                    public void run() {
                                        runCount++;
                                        if (runCount > 1) {

                                            if(Global.get(getApplicationContext(), "packageId").equals("none") ||
                                                    Global.get(getApplicationContext(), "packageId").equals("")){
                                                try{
                                                    Print.out("shutting down package is none");
                                                    exec.shutdown();
                                                }
                                                catch(Exception ex){
                                                    Print.out("cant shutdown package is none");
                                                }
                                                return;
                                            }

                                            LatLng end = new LatLng(Double.parseDouble(end_lat), Double.parseDouble(end_lon));

                                            LatLng start = new LatLng(Double.parseDouble(Global.get(getApplicationContext(), "testLat")),
                                                    Double.parseDouble(Global.get(getApplicationContext(), "testLon")));

                                            try {
                                                Print.out("calling directions");
                                                Global.drivingDetailsToDestination(getApplicationContext(), start, end);
                                                //startActivity(new Intent(getApplicationContext(), SplashActivity.class));
                                            } catch (IOException e) {
                                                Print.out("couldnt get directions");
                                            }



                                            Print.out("about to parse dest");

                                            double dest = 10000;

                                            String details = Global.get(getApplicationContext(), "durationToDest");

                                            Print.out("durdest is "+ details);
                                            try{
                                                dest = Double.parseDouble(details);
                                            }
                                            catch(Exception ex){
                                                dest = 10000;
                                            }

                                            if (dest < 90) {

                                                //if(packageId.equals("none")){
                                                //  runCount = 0;
                                                //   return;
                                                // }

                                                //START of HTTP request
                                                StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.POST, "http://delcab.ie/webservice/package_complete.php", new Response.Listener<String>() {
                                                    @Override
                                                    public void onResponse(String response) {
                                                        try {
                                                            JSONObject jsonObject = new JSONObject(response);


                                                            String message = jsonObject.getString("message");

                                                            Print.out("In proximity msg: " + message);

                                                            if (message.equals("completed with 1")) {
                                                                Print.toast(getApplicationContext(), "Package delivered.");

                                                                Global.set(getApplicationContext(), "packageId", "");
                                                                Global.set(getApplicationContext(), "businessId", "");
                                                                Global.set(getApplicationContext(), "businessName", "");
                                                                Global.set(getApplicationContext(), "startAd", "");
                                                                Global.set(getApplicationContext(), "endAd", "");
                                                                Global.set(getApplicationContext(), "driverPay", "");
                                                                Global.set(getApplicationContext(), "startLat", "");
                                                                Global.set(getApplicationContext(), "startLon", "");
                                                                Global.set(getApplicationContext(), "endLat", "");
                                                                Global.set(getApplicationContext(), "endLon", "");

                                                                startActivity(new Intent(getApplicationContext(), SplashActivity.class));

                                                            }


                                                        } catch (Exception e) {
                                                            e.printStackTrace();
                                                        }
                                                    }

                                                }, new Response.ErrorListener() {
                                                    @Override
                                                    public void onErrorResponse(VolleyError error) {
                                                        Print.out("Volley error ... " + error.toString());
                                                    }
                                                }) {
                                                    @Override
                                                    protected Map<String, String> getParams() throws AuthFailureError {
                                                        Map<String, String> params = new HashMap<>();
                                                        params.put("key1", RequestHeader.key1);
                                                        params.put("key2", RequestHeader.key2);

                                                        params.put("package_id", Global.get(getApplicationContext(), "packageId"));


                                                        return params;
                                                    }
                                                };

                                                RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                                                requestQueue.add(stringRequest);
                                                //END of HTTP request

                                            }


                                        }
                                        else {
                                            startActivity(new Intent(getApplicationContext(), SplashActivity.class));
                                        }
                                    }
                                };

                                exec = Executors.newScheduledThreadPool(1);

                                //every 15 seconds, execute runnable
                                exec.scheduleAtFixedRate(runnable, 0, 20, TimeUnit.SECONDS);
                            }
                            //}

                        }

                        else{
                            Print.out("package not found .....");
                            Global.set(getApplicationContext(), "packageId", "");
                            Global.set(getApplicationContext(), "businessId", "");
                            Global.set(getApplicationContext(), "businessName", "");
                            Global.set(getApplicationContext(), "startAd", "");
                            Global.set(getApplicationContext(), "endAd", "");
                            Global.set(getApplicationContext(), "driverPay", "");
                            Global.set(getApplicationContext(), "startLat", "");
                            Global.set(getApplicationContext(), "startLon", "");
                            Global.set(getApplicationContext(), "endLat", "");
                            Global.set(getApplicationContext(), "endLon", "");
                            try{
                                Print.out("shutting as package not found");
                                exec.shutdown();
                                //exec = null;
                            }
                            catch (Exception ex){
                                Print.out("Can't shutdown exec");
                            }

                            runCount = 0;
                        }



                    }
                    catch (Exception e) {
                        e.printStackTrace();
                        Print.out("Could not get package details");
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

                    params.put("taxi_id", Global.get(getApplicationContext(), "taxiId"));


                    return params;
                }
            };

            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            requestQueue.add(stringRequest);
            //END of HTTP request

        }


















        else{ //if in normal mode

            LocationTracker.getTracker().start(getApplicationContext());

            //START of HTTP request
            StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.POST, "http://delcab.ie/webservice/get_package_details.php", new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {


                    try {

                        JSONObject jsonObject = new JSONObject(response);

                        String message = jsonObject.getString("message");
                        Print.out(message);


                        if(message.equals("package found")){
                            Print.out("package found .....");

                            Global.set(getApplicationContext(), "packageId", jsonObject.getString("package_id"));
                            Global.set(getApplicationContext(), "businessId", jsonObject.getString("business_id"));
                            Global.set(getApplicationContext(), "businessName", jsonObject.getString("business_name"));
                            Global.set(getApplicationContext(), "startAd", jsonObject.getString("start_ad"));
                            Global.set(getApplicationContext(), "endAd", jsonObject.getString("end_ad"));
                            Global.set(getApplicationContext(), "driverPay", jsonObject.getString("driver_pay"));
                            Global.set(getApplicationContext(), "startLat", jsonObject.getString("start_lat"));
                            Global.set(getApplicationContext(), "startLon", jsonObject.getString("start_lon"));
                            Global.set(getApplicationContext(), "endLat", jsonObject.getString("end_lat"));
                            Global.set(getApplicationContext(), "endLon", jsonObject.getString("end_lon"));
                            end_lat = jsonObject.getString("end_lat");
                            end_lon = jsonObject.getString("end_lon");


                        }

                        else{
                            Print.out("package not found .....");
                            Global.set(getApplicationContext(), "packageId", "");
                            Global.set(getApplicationContext(), "businessId", "");
                            Global.set(getApplicationContext(), "businessName", "");
                            Global.set(getApplicationContext(), "startAd", "");
                            Global.set(getApplicationContext(), "endAd", "");
                            Global.set(getApplicationContext(), "driverPay", "");
                            Global.set(getApplicationContext(), "startLat", "");
                            Global.set(getApplicationContext(), "startLon", "");
                            Global.set(getApplicationContext(), "endLat", "");
                            Global.set(getApplicationContext(), "endLon", "");
//                            try{
//                                exec.shutdown();
//                                //exec = null;
//                            }
//                            catch (Exception ex){
//                                Print.out("Can't shutdown exec");
//                            }
//
//                            runCount = 0;
                        }



                    }
                    catch (Exception e) {
                        e.printStackTrace();
                        Print.out("Could not get package details");
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

                    params.put("taxi_id", Global.get(getApplicationContext(), "taxiId"));


                    return params;
                }
            };

            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            requestQueue.add(stringRequest);
            //END of HTTP request

        }















    }



}
