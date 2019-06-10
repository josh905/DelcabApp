package delcab.delcab;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
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

public class BusinessRegisterActivity extends AppCompatActivity {

    private Button registerBtn;
    private String holderName, regNum, phone, username, password;
    private EditText holderNameBox, regBox, phoneBox, usernameBox, passwordBox, repeatBox;
    private int usernameCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_register);

        registerBtn = findViewById(R.id.registerBtn);

        holderNameBox = findViewById(R.id.holderNameBox);
        regBox = findViewById(R.id.regBox);
        phoneBox = findViewById(R.id.phoneBox);
        usernameBox = findViewById(R.id.usernameBox);
        passwordBox = findViewById(R.id.passwordBox);
        repeatBox = findViewById(R.id.repeatBox);

        usernameCount = 0;

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holderName = holderNameBox.getText().toString();
                regNum = regBox.getText().toString();
                phone = phoneBox.getText().toString();
                username = usernameBox.getText().toString();
                password = passwordBox.getText().toString();


                if(holderName.length() <2 || holderName.length() > 40){
                    Print.toast(getApplicationContext(), "Name must be 2-40 characters");
                    return;
                }
                if(regNum.length() > 10 || regNum.length() < 4){
                    Print.toast(getApplicationContext(), "Reg. no. must be 4-10 characters");
                    return;
                }

                if(!android.text.TextUtils.isDigitsOnly(regNum)){
                    Print.toast(getApplicationContext(), "Enter only numbers for registration number");
                    return;
                }

                if(phone.length()!=10){
                    Print.toast(getApplicationContext(), "Phone number must be 7-10 digits");
                    return;
                }

                if(!android.text.TextUtils.isDigitsOnly(phone)){
                    Print.toast(getApplicationContext(), "Enter only numbers for phone number");
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



                //checking if user already exists with that username:

                //START of HTTP request
                StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.POST, "http://delcab.ie/webservice/username_check.php", new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            String strUsernameCount = jsonObject.getString("message");

                            usernameCount = Integer.parseInt(strUsernameCount);




                            if(usernameCount>0){
                                Print.toast(getApplicationContext(), "Username is taken");
                                return;
                            }

                            //START of HTTP request
                            StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.POST, "http://delcab.ie/webservice/business_registration_check.php", new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    try {
                                        JSONObject jsonObject = new JSONObject(response);


                                        String busName = jsonObject.getString("busName");
                                        String compName = jsonObject.getString("compName");

                                        String busRegDate = jsonObject.getString("busRegDate");
                                        String compRegDate = jsonObject.getString("compRegDate");

                                        if(busName.equals("none") && compName.equals("none")){
                                            Print.toast(getApplicationContext(), "Not a registered Irish number");
                                        }


                                        else{
                                            Intent select = new Intent(getApplicationContext(), BusinessSelectActivity.class);

                                            select.putExtra("holderName", holderName);
                                            select.putExtra("regNum", regNum);
                                            select.putExtra("phone", phone);
                                            select.putExtra("username", username);
                                            select.putExtra("password", password);
                                            select.putExtra("busName", busName);
                                            select.putExtra("compName", compName);
                                            select.putExtra("busRegDate", busRegDate);
                                            select.putExtra("compRegDate", compRegDate);

                                            startActivity(select);



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

                                    params.put("regNum", regNum);

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
