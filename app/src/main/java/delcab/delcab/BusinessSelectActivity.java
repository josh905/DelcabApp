package delcab.delcab;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;

import android.view.View;
import android.view.View.OnClickListener;

import android.widget.ArrayAdapter;
import android.widget.Button;

import android.widget.Spinner;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class BusinessSelectActivity extends AppCompatActivity {

    private Spinner spinner;
    private ArrayAdapter<String> adapter;
    private String holderName, regNum, phone, username, password, chosen, busName, compName, busRegDate, compRegDate;

    private Context regContext;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_select);
        spinner = findViewById(R.id.spinner);

        final Intent got = getIntent();


        holderName = "none";
        regNum = "none";
        phone = "none";
        username = "none";
        password = "none";
        busName = "none";
        busRegDate = "none";
        compName = "none";
        compRegDate = "none";

        if(got.getStringExtra("holderName")!=null){
            holderName = got.getStringExtra("holderName");
        }

        if(got.getStringExtra("regNum")!=null){
            regNum = got.getStringExtra("regNum");
        }

        if(got.getStringExtra("phone")!=null){
            phone = got.getStringExtra("phone");
        }

        if(got.getStringExtra("username")!=null){
            username = got.getStringExtra("username");
        }

        if(got.getStringExtra("password")!=null){
            password = got.getStringExtra("password");
        }

        if(got.getStringExtra("busName")!=null){
            busName = got.getStringExtra("busName");
        }

        if(got.getStringExtra("compName")!=null){
            compName = got.getStringExtra("compName");
        }

        if(got.getStringExtra("busRegDate")!=null){
            busRegDate = got.getStringExtra("busRegDate");
        }

        if(got.getStringExtra("compRegDate")!=null){
            compRegDate = got.getStringExtra("compRegDate");
        }

        List<String> list = new ArrayList<>();
        list.add("Select your business/company");
        if(!busName.equals("none")){
            busName = busName.replace("&amp;","&");
            list.add(busName);
        }
        if(!compName.equals("none")){
            compName = compName.replace("&amp;","&");
            list.add(compName);
        }
        list.add("None of these");

        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);

        Button proceedBtn = findViewById(R.id.proceedBtn);
        proceedBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {


                chosen = spinner.getSelectedItem().toString();

                if(chosen.equals("Select your business/company")){
                    Print.toast(getApplicationContext(), "Select a valid option");
                }

                else if(chosen.equals("None of these")){
                    Print.toast(getApplicationContext(), "Business/Company must be registered in Ireland");
                    finish();
                }

                else{

                    //START of HTTP request
                    StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.POST, "http://delcab.ie/webservice/business_register.php", new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);

                                String message = jsonObject.getString("message");

                                if(message.equals("completed with 1")){

                                    //START of HTTP request
                                    StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.POST, "http://delcab.ie/webservice/get_business_details.php", new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response) {
                                            try {
                                                JSONObject jsonObject = new JSONObject(response);

                                                if(!jsonObject.getString("message").contains("row was fetched")){
                                                    Print.toast(getApplicationContext(),"Could not get user details");
                                                    //return;
                                                }
                                                else{

                                                    SharedPreferences.Editor editor = getSharedPreferences("DELCAB", MODE_PRIVATE).edit();

                                                    editor.putString("accountType", "business");
                                                    editor.putInt("businessId", jsonObject.getInt("businessId"));
                                                    editor.putInt("regNum", jsonObject.getInt("regNum"));
                                                    editor.putString("holderName", jsonObject.getString("holderName"));
                                                    editor.putString("businessName", jsonObject.getString("businessName"));
                                                    editor.putString("dateRegistered", jsonObject.getString("dateRegistered"));
                                                    editor.putString("phone", jsonObject.getString("phone"));
                                                    editor.putString("password", jsonObject.getString("password"));
                                                    editor.putString("dateJoined", jsonObject.getString("dateJoined"));
                                                    editor.putString("username", jsonObject.getString("username"));

                                                    editor.apply();


                                                    startActivity(new Intent(getApplicationContext(), BusinessHomeActivity.class));


                                                    finish();

                                                }

                                            }
                                            catch (JSONException e) {
                                                e.printStackTrace();
                                                Print.out("not json");
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

                                            params.put("username", username);

                                            return params;
                                        }
                                    };

                                    RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                                    requestQueue.add(stringRequest);
                                    //END of HTTP request

                                }

                                else{
                                    Print.toast(getApplicationContext(),"Could not register user.");
                                }


                            } catch (JSONException e) {
                                e.printStackTrace();
                                Print.out("not json");
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
                            params.put("holderName",holderName);
                            params.put("regNum",regNum);
                            params.put("phone",phone);
                            params.put("username",username);
                            params.put("password",password);

                            if(chosen.equals(busName)){

                                params.put("busName",busName);
                                params.put("busRegDate",busRegDate);
                            }
                            else{
                                //from now on its called bus regardless
                                params.put("busName",compName);
                                params.put("busRegDate",compRegDate);
                            }


                            return params;
                        }
                    };

                    RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                    requestQueue.add(stringRequest);
                    //END of HTTP request

                }

            }
        });


    }


}

