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

public class BusinessLoginActivity extends AppCompatActivity {

    private String message, username, password;
    private EditText usernameBox, passwordBox;
    private Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_login);

        usernameBox = findViewById(R.id.usernameBox);
        passwordBox =  findViewById(R.id.passwordBox);

        loginButton = findViewById(R.id.loginBtn);


        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                username = usernameBox.getText().toString();
                password = passwordBox.getText().toString();


                //START of HTTP request
                StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.POST, "http://delcab.ie/webservice/business_login.php", new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);


                            String message = jsonObject.getString("message");

                            if(message.contains("no such username")){
                                Print.toast(getApplicationContext(), "Not a registered username");
                            }
                            else if(message.contains("password is")){
                                if(!BCrypt.checkpw(password, message.split("is ")[1])){
                                    Print.toast(getApplicationContext(),"Incorrect password");
                                }
                                else{

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
                                                    editor.putString("businessId", jsonObject.getInt("businessId")+"");
                                                    editor.putString("regNum", jsonObject.getInt("regNum")+"");
                                                    editor.putString("holderName", jsonObject.getString("holderName"));
                                                    editor.putString("businessName", jsonObject.getString("businessName"));
                                                    editor.putString("dateRegistered", jsonObject.getString("dateRegistered"));
                                                    editor.putString("phone", jsonObject.getString("phone"));
                                                    editor.putString("password", jsonObject.getString("password"));
                                                    editor.putString("dateJoined", jsonObject.getString("dateJoined"));
                                                    editor.putString("username", jsonObject.getString("username"));

                                                    editor.apply();

                                                    startActivity(new Intent(getApplicationContext(),BusinessHomeActivity.class));

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
