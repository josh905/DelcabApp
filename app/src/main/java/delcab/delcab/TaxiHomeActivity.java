package delcab.delcab;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AlertDialog;


import android.util.AndroidException;
import android.view.View;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class TaxiHomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_taxi_home);



        findViewById(R.id.deliveriesBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        findViewById(R.id.packagesBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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
                getApplicationContext().getSharedPreferences("DELCAB", 0).edit().clear().apply();
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
                finish();
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

        //START of HTTP request
        StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.POST, "http://delcab.ie/webservice/get_taxi_package.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);

                    String message = jsonObject.getString("message");
                    Print.out(message);

                    if(message.contains("package num is")){
                        Global.set(getApplicationContext(), "packageId", message.split("num is ")[1]);

                        if(message.split("num is ")[1].equals("0")){
                            try{
                                Print.out("Trying to stop tracking");
                                LocationTracker.getTracker().stop(getApplicationContext());
                            }
                            catch (Exception e){
                                Print.out("Could not stop tracking");
                            }
                        }
                        else{
                            try{
                                Print.out("Trying to start tracking");
                                LocationTracker.getTracker().start(getApplicationContext());
                            }
                            catch (Exception e){
                                Print.out("Could not start tracking");
                            }
                        }

                    }
                    else{
                        Print.out("couldn't check package num");
                    }

                }
                catch (Exception e) {
                    e.printStackTrace();
                    Print.out("Could not get package details for taxi");
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
