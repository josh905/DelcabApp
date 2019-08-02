package delcab.delcab;

import android.content.Context;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;


import java.util.HashMap;
import java.util.Map;

public class PriceActivity extends AppCompatActivity {

    private String durationStr, distanceStr;
    private TextView tv, priceTV;
    private double distanceNum, durationNum, price, baseCharge, costPerMinute, costPerKm, distanceInKm, durationInMins;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_price);

        price = 0.00;
        baseCharge = 4.50;
        costPerMinute = 0.40;
        costPerKm = 0.46;

        tv = findViewById(R.id.tv);
        priceTV = findViewById(R.id.priceTV);

        try{
            distanceNum = Double.parseDouble(Global.get(getApplicationContext(), "distanceNum"));
            durationNum = Double.parseDouble(Global.get(getApplicationContext(), "durationNum"));
        }
        catch (Exception e){
            e.printStackTrace();
            Print.toast(getApplicationContext(), "Could not convert driving details");
        }

        distanceStr = Global.get(getApplicationContext(), "distanceStr");
        durationStr = Global.get(getApplicationContext(), "durationStr");


        Print.out(distanceNum + " --- " + durationNum);
        Print.out(distanceStr + " --- " + durationStr);

        tv.append("\nJourney distance: " + distanceStr + "\n\nEstimated journey length: " + durationStr + "\n\n");


        if(distanceNum<1000 || durationNum<120){
            tv.append("Delcab does not allow journeys this short");
            return;
        }

        distanceInKm = distanceNum / 1000;
        durationInMins = durationNum / 60;

        price = baseCharge + (costPerMinute * durationInMins)
                + (costPerKm * distanceInKm);

        price = Global.euro(price);

        if(price>60){
            tv.append("We recommend you use a courier because this will cost:");
        }
        else{
            tv.append("This delivery will cost:");
        }



       // tv.append(Html.fromHtml("<b>€"+Double.toString(price)+"</b>"));

        priceTV.append("€"+price);



        Button submitBtn = findViewById(R.id.submitBtn);
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(distanceNum<1000 || durationNum<120){
                    Print.toast(getApplicationContext(), "Journey too short");
                    return;
                }

                //START of HTTP request
                StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.POST, "http://delcab.ie/webservice/upload_package.php", new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            String message = jsonObject.getString("message");

                            if(message.equals("completed with 1")){
                                Print.toast(getApplicationContext(), "Package uploaded.");
                            }
                            else{
                                Print.toast(getApplicationContext(), "Could not upload package");
                            }

                            startActivity(new Intent(getApplicationContext(), BusinessHomeActivity.class));
                            finish();

                        }
                        catch (Exception e) {
                            e.printStackTrace();
                            Print.toast(getApplicationContext(),"Could not upload package");
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

                        Context con = getApplicationContext();

                        params.put("business_id", Global.get(con, "businessId"));
                        params.put("business_name", Global.get(con, "businessName"));
                        params.put("start_ad", Global.get(con, "collectionAddress"));
                        params.put("end_ad", Global.get(con, "deliveryAddress"));
                        params.put("start_lat", Global.get(con, "collectionLat"));
                        params.put("start_lon", Global.get(con, "collectionLon"));
                        params.put("end_lat", Global.get(con, "deliveryLat"));
                        params.put("end_lon", Global.get(con, "deliveryLon"));
                        params.put("price", Double.toString(price));
                        params.put("delcab_cut", Double.toString(Global.euro(price*0.05)));
                        params.put("driver_pay", Double.toString(Global.euro(price*0.95)));

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