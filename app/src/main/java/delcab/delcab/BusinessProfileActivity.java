package delcab.delcab;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class BusinessProfileActivity extends AppCompatActivity {

    private EditText holderNameBox, usernameBox, busNameBox, regNumBox, phoneBox;
    private String holderName, username, busName, phone;
    private int regNum;
    private Button updateBtn;
    private TextView changeTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_profile);

        //set max chars in business name to appear in the box to like 20 and then "..."
        SharedPreferences getter = getSharedPreferences("DELCAB",MODE_PRIVATE);


        holderNameBox = findViewById(R.id.holderNameBox);
        usernameBox = findViewById(R.id.usernameBox);
        busNameBox = findViewById(R.id.busNameBox);
        regNumBox = findViewById(R.id.regNumBox);
        phoneBox = findViewById(R.id.phoneBox);

        updateBtn = findViewById(R.id.updateBtn);
        changeTV = findViewById(R.id.changeTV);


        holderName = getter.getString("holderName","");
        username = getter.getString("username","");
        busName = getter.getString("businessName","");
        regNum = getter.getInt("regNum",0);
        phone = getter.getString("phone","");


        holderNameBox.setText(holderName);
        usernameBox.setText(username);
        busNameBox.setText(busName);
        regNumBox.setText(regNum+"");
        phoneBox.setText(phone);


        //because you're not allowed change these
        usernameBox.setInputType(0);
        busNameBox.setInputType(0);
        regNumBox.setInputType(0);



        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String inpHolderName = holderNameBox.getText().toString();
                String inpPhone = phoneBox.getText().toString();

                if (inpHolderName.length() < 2 || inpHolderName.length() > 40) {
                    Print.toast(getApplicationContext(), "Name must be 2-40 characters");
                    return;
                }

                if (inpPhone.length() != 10) {
                    Print.toast(getApplicationContext(), "Phone number must be 10 digits");
                    return;
                }

                if (!(inpPhone.charAt(0) == '0' && inpPhone.charAt(1) == '8')) {
                    Print.toast(getApplicationContext(), "Phone number must start with 08");
                    return;
                }

                if (!android.text.TextUtils.isDigitsOnly(inpPhone)) {
                    Print.toast(getApplicationContext(), "Enter only numbers for phone number");
                    return;
                }

                phone = inpPhone;
                holderName  = inpHolderName;

                //START of HTTP request
                StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.POST, "http://delcab.ie/webservice/update_business.php", new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            String message = jsonObject.getString("message");

                            if(message.equals("completed with 1")){

                                SharedPreferences.Editor editor = getSharedPreferences("DELCAB", MODE_PRIVATE).edit();
                                editor.putString("phone", phone);
                                editor.putString("holderName", holderName);
                                editor.apply();

                                Print.toast(getApplicationContext(), "Profile updated successfully");
                                startActivity(new Intent(getApplicationContext(), BusinessHomeActivity.class));

                            }

                            else{
                                Print.toast(getApplicationContext(), "Details could not be updated");
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

                        params.put("regNum", Integer.toString(regNum));
                        params.put("holderName", holderName);
                        params.put("phone", phone);

                        return params;
                    }
                };

                RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                requestQueue.add(stringRequest);
                //END of HTTP request


            }
        });


        changeTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),BusinessPasswordActivity.class));
            }
        });



    }
}
