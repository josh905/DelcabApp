package delcab.delcab;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import com.google.android.gms.maps.model.LatLng;


import org.json.JSONObject;


import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


public class SelectPackageActivity extends AppCompatActivity {

    private String packages, packageId;
    private String distanceStr, durationStr, distanceFromLoc, durationFromLoc;
    private TextView tv, priceTV;
    private String[] col;
    private Button deliverBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_package);

        deliverBtn = findViewById(R.id.deliverBtn);

        packages = getIntent().getStringExtra("packages");
        packageId = getIntent().getStringExtra("packageId");

        tv = findViewById(R.id.tv);
        priceTV = findViewById(R.id.priceTV);

        Print.out(packageId+"**"+packages);

        String[] row = packages.split("g3k7b3");
        for(int i=1; i<row.length; i++){
            if(row[i].split("j7v4x1")[0].equals(packageId)){
                col = row[i].split("j7v4x1");
                break;
            }
        }
        Print.out("col is "+ Arrays.toString(col));

        try {
            Global.drivingDetails(getApplicationContext(),
                    new LatLng(Double.parseDouble(col[3]), Double.parseDouble(col[4])),
                    new LatLng(Double.parseDouble(col[5]), Double.parseDouble(col[6])));
        } catch (Exception e) {
            Print.out("Could not get driving details");
        }

        startActivity(new Intent(getApplicationContext(), SplashActivity.class).putExtra("duration", 2000));


        new CountDownTimer(800,800) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {

                try {
                    Global.drivingDetailsFromLocation(getApplicationContext(), LocationTracker.getTracker().getLatLng(),
                            new LatLng(Double.parseDouble(col[3]), Double.parseDouble(col[4]))
                            );
                } catch (IOException e) {
                    e.printStackTrace();
                    Print.out("Could not get location directions");
                }

                distanceStr = Global.get(getApplicationContext(), "distanceStr");
                durationStr = Global.get(getApplicationContext(), "durationStr");

                tv.append("Collect from: " + col[7] + "\n\nDeliver to: "+col[8] + "\n\n");


                new CountDownTimer(800,800) {
                    @Override
                    public void onTick(long millisUntilFinished) {

                    }

                    @Override
                    public void onFinish() {


                        distanceFromLoc = Global.get(getApplicationContext(), "distanceFromLoc");
                        durationFromLoc = Global.get(getApplicationContext(), "durationFromLoc");
                        tv.append("From your location to collection point: "+distanceFromLoc+", about "
                                +durationFromLoc+".\n\n");
                        tv.append("From collection point to destination: "+distanceStr+", about "
                                +durationStr+".\n\nPayment for this package: ");

                        priceTV.append("€"+col[9]);


                    }
                }.start();

            }
        }.start();


        deliverBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean assigned = true;

                try{
                    Integer.parseInt(Global.get(getApplicationContext(), "packageId"));
                }
                catch(Exception ex){
                    assigned = false;
                }

                if(assigned){
                    Print.toast(getApplicationContext(), "You are already assigned to a package");
                    return;
                }

                //START of HTTP request
                StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.POST, "http://delcab.ie/webservice/assign_driver.php", new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String message = jsonObject.getString("message");
                            Print.out(message);

                            if(message.equals("driver assigned")){

                                Print.toast(getApplicationContext(), "You are now assigned to a package");
                                startActivity(new Intent(getApplicationContext(), TaxiHomeActivity.class));
                                finish();
                            }
                            else{
                                Print.toast(getApplicationContext(), "Could not select this package");
                            }

                        }
                        catch (Exception e) {
                            e.printStackTrace();
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

                        params.put("package_id", packageId);
                        params.put("taxi_id", Global.get(getApplicationContext(), "taxiId"));
                        params.put("driver_name", Global.get(getApplicationContext(), "driverName"));
                        params.put("taxi_num", Global.get(getApplicationContext(), "taxiNum"));


                        return params;
                    }
                };

                RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                requestQueue.add(stringRequest);
                //END of HTTP request


            }

        });


    }
}
