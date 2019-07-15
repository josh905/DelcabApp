package delcab.delcab;

import android.content.Intent;
import android.content.SharedPreferences;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;
import org.mindrot.jbcrypt.BCrypt;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TaxiRegisterActivity extends AppCompatActivity {

    private Button registerBtn;
    private String driverName, taxiNum, phone, username, password;
    private EditText driverNameBox, taxiNumBox, phoneBox, usernameBox, passwordBox, repeatBox;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_taxi_register);

        registerBtn = findViewById(R.id.registerBtn);

        driverNameBox = findViewById(R.id.driverNameBox);
        taxiNumBox = findViewById(R.id.taxiNumBox);
        phoneBox = findViewById(R.id.phoneBox);
        usernameBox = findViewById(R.id.usernameBox);
        passwordBox = findViewById(R.id.passwordBox);
        repeatBox = findViewById(R.id.repeatBox);



        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                driverName = driverNameBox.getText().toString();
                taxiNum = taxiNumBox.getText().toString();
                phone = phoneBox.getText().toString();
                username = usernameBox.getText().toString();
                password = passwordBox.getText().toString();


                if(driverName.length() <2 || driverName.length() > 40){
                    Print.toast(getApplicationContext(), "Name must be 2-40 characters");
                    return;
                }
                if(taxiNum.length() != 5){
                    Print.toast(getApplicationContext(), "Taxi number must be 5 digits");
                    return;
                }

                if(!android.text.TextUtils.isDigitsOnly(taxiNum)){
                    Print.toast(getApplicationContext(), "Enter only numbers for taxi number");
                    return;
                }

                if(phone.length()!=10){
                    Print.toast(getApplicationContext(), "Mobile number must be 10 digits");
                    return;
                }

                if(!(phone.charAt(0)=='0' && phone.charAt(1)=='8')){
                    Print.toast(getApplicationContext(), "Mobile number must start with 08");
                    return;
                }

                if(!android.text.TextUtils.isDigitsOnly(phone)){
                    Print.toast(getApplicationContext(), "Enter only numbers for mobile number");
                    return;
                }

                if(username.length()<6 || username.length() > 20){
                    Print.toast(getApplicationContext(), "Username must be 6-20 characters");
                    return;
                }

                Pattern pattern = Pattern.compile("\\p{Alnum}+");
                Matcher matcher = pattern.matcher(username);
                if (!matcher.matches()) {
                    Print.toast(getApplicationContext(), "Username can only be letters and numbers");
                    return;
                }


                if(!password.equals(repeatBox.getText().toString())){
                    Print.toast(getApplicationContext(), "Passwords do not match");
                    return;
                }


                if(password.length()<6 || password.length()>80){
                    Print.toast(getApplicationContext(), "Password must be 6-80 characters");
                    return;
                }

                if(password.charAt(0)==' ' || password.charAt(password.length()-1)==' '){
                    Print.toast(getApplicationContext(), "Password can't start or end with space");
                    return;
                }


                //changing password to hashed password
                password = BCrypt.hashpw(password, BCrypt.gensalt());


                //this request checks for username in both tables and checks validity of taxi number

                //START of HTTP request
                StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.POST, "http://delcab.ie/webservice/taxi_check.php", new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            String message = jsonObject.getString("message");



                            if(message.equals("username taken")){
                                Print.toast(getApplicationContext(), "Username is taken");
                                return;
                            }

                            else if(message.equals("taxi num taken")){
                                Print.toast(getApplicationContext(), "An account exists with that taxi number");
                                return;
                            }

                            else if(message.equals("not taxi num")){
                                Print.toast(getApplicationContext(), "That's not a registered taxi number");
                                return;
                            }




                            //START of HTTP request
                            StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.POST, "http://delcab.ie/webservice/taxi_register.php", new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    try {
                                        JSONObject jsonObject = new JSONObject(response);


                                        String message = jsonObject.getString("message");

                                        if(message.contains("completed with 1")){

                                            //START of HTTP request
                                            StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.POST, "http://delcab.ie/webservice/get_taxi_details.php", new Response.Listener<String>() {
                                                @Override
                                                public void onResponse(String response) {
                                                    try {
                                                        JSONObject jsonObject = new JSONObject(response);

                                                        if(!jsonObject.getString("message").contains("row was fetched")){
                                                            Print.toast(getApplicationContext(),"Could not get user details");

                                                        }
                                                        else{

                                                            SharedPreferences.Editor editor = getSharedPreferences("DELCAB", MODE_PRIVATE).edit();

                                                            editor.putString("accountType", "taxi");
                                                            editor.putString("taxiId", jsonObject.getInt("taxiId")+"");
                                                            editor.putString("driverName", jsonObject.getString("driverName"));
                                                            editor.putString("username", jsonObject.getString("username"));
                                                            editor.putString("taxiNum", jsonObject.getInt("taxiNum")+"");
                                                            editor.putString("phone", jsonObject.getString("phone"));
                                                            editor.putString("password", jsonObject.getString("password"));
                                                            editor.putString("dateJoined", jsonObject.getString("dateJoined"));

                                                            editor.apply();

                                                            startActivity(new Intent(getApplicationContext(),TaxiHomeActivity.class));

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

                                    params.put("driverName",driverName);
                                    params.put("taxiNum",taxiNum);
                                    params.put("phone",phone);
                                    params.put("username",username);
                                    params.put("password",password);

                                    return params;
                                }
                            };

                            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                            requestQueue.add(stringRequest);
                            //END of HTTP request





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

                        params.put("username", username);
                        params.put("taxiNum", taxiNum);

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
