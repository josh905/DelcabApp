package delcab.delcab;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;

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
    private String holderName, regNum, phone, email, password, org1, org2, chosen;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_select);
        spinner = findViewById(R.id.spinner);

        final Intent got = getIntent();

        holderName = "none";
        regNum = "none";
        phone = "none";
        email = "none";
        password = "none";
        org1 = "none";
        org2 = "none";

        if(got.getStringExtra("holderName")!=null){
            holderName = got.getStringExtra("holderName");
        }

        if(got.getStringExtra("regNum")!=null){
            regNum = got.getStringExtra("regNum");
        }

        if(got.getStringExtra("phone")!=null){
            phone = got.getStringExtra("phone");
        }

        if(got.getStringExtra("email")!=null){
            email = got.getStringExtra("email");
        }

        if(got.getStringExtra("password")!=null){
            password = got.getStringExtra("password");
        }

        if(got.getStringExtra("org1")!=null){
            org1 = got.getStringExtra("org1");
        }

        if(got.getStringExtra("org2")!=null){
            org2 = got.getStringExtra("org2");
        }

        List<String> list = new ArrayList<>();
        list.add("Select your business/company");
        if(!org1.equals("none")){
            list.add(org1);
        }
        if(!org2.equals("none")){
            list.add(org2);
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

                                Print.toast(getApplicationContext(),message);


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
                            params.put("busName",chosen);
                            params.put("regNum",regNum);
                            params.put("phone",phone);
                            params.put("email",email);
                            params.put("password",password);


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

