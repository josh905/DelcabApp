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

public class TaxiPasswordActivity extends AppCompatActivity {

    private String truePass, curPass, newPass, repPass, hashPass;
    private EditText curPassBox, newPassBox, repPassBox;
    private int taxiNum;
    private Button confirmBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_taxi_password);

        SharedPreferences getter = getSharedPreferences("DELCAB",MODE_PRIVATE);


        curPassBox = findViewById(R.id.curPassBox);
        newPassBox = findViewById(R.id.newPassBox);
        repPassBox = findViewById(R.id.repPassBox);

        confirmBtn = findViewById(R.id.confBtn);

        truePass = getter.getString("password","");

        taxiNum = Integer.parseInt(getter.getString("taxiNum", ""));


        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                curPass = curPassBox.getText().toString();
                newPass = newPassBox.getText().toString();
                repPass = repPassBox.getText().toString();



                if(newPass.length()<6 || newPass.length()>80){
                    Print.toast(getApplicationContext(), "Password must be 6-80 chars");
                    return;
                }

                if(repPass.length()<6 || repPass.length()>80){
                    Print.toast(getApplicationContext(), "Password must be 6-80 chars");
                    return;
                }


                if(curPass.length()<6 || curPass.length()>80){
                    Print.toast(getApplicationContext(), "Password must be 6-80 chars");
                    return;
                }



                if(!BCrypt.checkpw(curPass,truePass)){
                    Print.toast(getApplicationContext(),"Incorrect current password");
                    return;
                }

                if(!newPass.equals(repPass)){
                    Print.toast(getApplicationContext(), "Passwords don't match");
                    return;
                }

                hashPass = BCrypt.hashpw(newPass, BCrypt.gensalt());


                //START of HTTP request
                StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.POST, "http://delcab.ie/webservice/change_taxi_password.php", new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            String message = jsonObject.getString("message");

                            if(message.equals("completed with 1")){

                                SharedPreferences.Editor editor = getSharedPreferences("DELCAB", MODE_PRIVATE).edit();
                                editor.putString("password", hashPass);
                                editor.apply();

                                Print.toast(getApplicationContext(), "Password updated successfully");
                                startActivity(new Intent(getApplicationContext(), TaxiHomeActivity.class));

                            }

                            else{
                                Print.toast(getApplicationContext(), "Password could not be updated");
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

                        params.put("password", hashPass);
                        params.put("taxiNum", Integer.toString(taxiNum));


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
