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

public class BusinessRegisterActivity extends AppCompatActivity {

    private Button registerBtn;
    private String holderName, regNum, phone, email, password;
    private EditText holderNameBox, regBox, phoneBox, emailBox, passwordBox, repeatBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_register);

        registerBtn = findViewById(R.id.registerBtn);

        holderNameBox = findViewById(R.id.holderNameBox);
        regBox = findViewById(R.id.regBox);
        phoneBox = findViewById(R.id.phoneBox);
        emailBox = findViewById(R.id.emailBox);
        passwordBox = findViewById(R.id.passwordBox);
        repeatBox = findViewById(R.id.repeatBox);

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holderName = holderNameBox.getText().toString();
                regNum = regBox.getText().toString();
                phone = phoneBox.getText().toString();
                email = emailBox.getText().toString();
                password = passwordBox.getText().toString();


                if(holderName.length() <2 || holderName.length() > 40){
                    Print.toast(getApplicationContext(), "Name must be 2-40 characters");
                    return;
                }
                if(regNum.length() > 10 || regNum.length() < 4){
                    Print.toast(getApplicationContext(), "Reg. no. must be 4-10 characters");
                    return;
                }

                try{
                    //not storing the int value. just parsing to check the input is a number
                    Integer.parseInt(regNum);
                }
                catch (NumberFormatException ex){
                    Print.toast(getApplicationContext(), "Reg. no. must be numerical");
                    return;
                }

                if(phone.length() < 6 || phone.length() > 13){
                    Print.toast(getApplicationContext(), "Phone must be 6-13 digits");
                    return;
                }

                try{
                    //not storing the long value. just using it to check the number is valid format
                    Long.parseLong(phone);
                }
                catch (NumberFormatException ex){
                    Print.toast(getApplicationContext(), "Phone no. must be numerical");
                    return;
                }

                //check if the android email xml works
                if(email.length()<5 || email.length() > 60){
                    Print.toast(getApplicationContext(), "Email must be 5-60 characters");
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



                //START of HTTP request
                StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.POST, "http://delcab.ie/webservice/business_registration_check.php", new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String status = jsonObject.getString("status");
                            String org1 = "not set";
                            String org2 = "not set";

                            if(status.equals("complete")){
                                org1 = jsonObject.getString("org1");
                                org2 = jsonObject.getString("org2");

                                if(org1.equals("none") && org2.equals("none")){
                                    Print.toast(getApplicationContext(), "Not a registered Irish number");
                                }

                                else{
                                    Intent select = new Intent(getApplicationContext(), BusinessSelectActivity.class);

                                    select.putExtra("holderName", holderName);
                                    select.putExtra("regNum", regNum);
                                    select.putExtra("phone", phone);
                                    select.putExtra("email", email);
                                    select.putExtra("password", password);
                                    select.putExtra("org1", org1);
                                    select.putExtra("org2", org2);

                                    startActivity(select);
                                }

                            }

                            else{
                                Print.toast(getApplicationContext(), "Could not check reg number");
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



            }
        });

    }
}
