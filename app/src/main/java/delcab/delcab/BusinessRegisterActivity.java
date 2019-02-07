package delcab.delcab;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
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
    private String name, regNum, phone, email, password;
    private EditText nameBox, regBox, phoneBox, emailBox, passwordBox, repeatBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_register);

        registerBtn = findViewById(R.id.registerBtn);

        nameBox = findViewById(R.id.nameBox);
        regBox = findViewById(R.id.regBox);
        phoneBox = findViewById(R.id.phoneBox);
        emailBox = findViewById(R.id.emailBox);
        passwordBox = findViewById(R.id.passwordBox);
        repeatBox = findViewById(R.id.repeatBox);

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = nameBox.getText().toString();
                regNum = regBox.getText().toString();
                phone = phoneBox.getText().toString();
                email = emailBox.getText().toString();
                password = passwordBox.getText().toString();


                if(name.length() <2 || name.length() > 40){
                    Print.toast(getApplicationContext(), "Name must be 2-40 characters");
                    return;
                }
                if(regNum.length() > 10 || regNum.length() < 4){
                    Print.toast(getApplicationContext(), "Reg. no. must be 4-10 characters");
                    return;
                }

                try{
                    //not storing the int value. just using it to check the number is valid format
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
                }

                if(password.length()<6 || password.length()>80){
                    Print.toast(getApplicationContext(), "Password must be 6-80 characters");
                    return;
                }

                if(password.charAt(0)==' ' || password.charAt(password.length()-1)==' '){
                    Print.toast(getApplicationContext(), "Password can't start or end with space");
                    return;
                }

                //bcrypt password here





                //START of HTTP request
                StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://delcab.ie/webservice/business_register.php", new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String status = jsonObject.getString("status");

                            Print.toast(getApplicationContext(), "status.."+status);

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

                        params.put("name", name);
                        params.put("regNum", regNum);
                        params.put("phone", phone);
                        params.put("email", email);
                        params.put("password", password);


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
