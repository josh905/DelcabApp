package delcab.delcab;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class PriceActivity extends AppCompatActivity {

    private String durationStr, distanceStr;
    private TextView tv;
    private double distance, duration, price, baseCharge, costPerMinute, costPerKm, weight;
    private int test1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_price);


        startActivity(new Intent(this, SplashActivity.class));

        Intent in = getIntent();
        durationStr = in.getStringExtra("duration");
        distanceStr = in.getStringExtra("distance");

        weight = 0;
        price = 0;
        baseCharge = 4.50;
        costPerMinute = 0.40;
        costPerKm = 0.46;

        tv = findViewById(R.id.tv);

        tv.append("\n\nJourney distance: " + distanceStr + "\n\nEstimated journey length: " + durationStr + "\n\n");

        //60 euro cut off point
        if(durationStr.contains("hour")){

            tv.append("This journey would cost over €70\n\nWe recommend you use a courier");
        }
        else if(durationStr.equals("1 min") || distanceStr.contains(" m")){
            tv.append("Delcab does not deliver for journeys this short");
        }


        else{


            String subDis = distanceStr.split(" ")[0];
            String subDur = durationStr.split(" ")[0];
            try{
                distance = Double.parseDouble(subDis);
                duration = Double.parseDouble(subDur);


            }
            catch (NumberFormatException e){
                e.printStackTrace();
                Print.toast(this,"Could not parse double");
            }

            if(duration < 2 || distance < 0.8){
                tv.append("Delcab does not deliver for journeys this short");
            }

            else{
                price = baseCharge + (costPerMinute * duration)
                        + (costPerKm * distance);

                price = round(price,2);

                tv.append("The estimated price for this journey is €"+price);

            }



        }




        Button insertBtn = findViewById(R.id.insertBtn);
        insertBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                //START of string request
                StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://delcab.ie/webservice/test3.php", new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String dogman = jsonObject.getString("dogman");
                            String catwoman = jsonObject.getString("catwoman");
                            String foodstuff = jsonObject.getString("foodstuff");
                            Print.out(dogman + " ... " + catwoman + " ... " + foodstuff);

                            //store it in shared preferences here

                        } catch (JSONException e) {
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
                        params.put("key1", Secure.key1);
                        params.put("key2", Secure.key2);

                        params.put("dog", "kobi");
                        params.put("cat", "tigger");
                        params.put("food", "pizza");

                        return params;
                    }
                };

                RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                requestQueue.add(stringRequest);
                //END of string request

            }
        });

        Button goToTestBtn = findViewById(R.id.goToTestBtn);
        goToTestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(PriceActivity.this, TestActivity.class);
                startActivity(in);
                finish();
            }
        });

    }



    private double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }





}
