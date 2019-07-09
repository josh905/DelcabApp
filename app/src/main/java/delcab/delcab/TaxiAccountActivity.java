package delcab.delcab;

import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class TaxiAccountActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_taxi_account);


        /*

        //Shared prefs testing
        String username = "none";
        int num = 1;

        SharedPreferences prefs = getSharedPreferences("Delcab", MODE_PRIVATE);

        username = prefs.getString("username", "No username defined");//"No username defined" is the default value.
        num = prefs.getInt("num", 0); //0 is the default value.

        Print.toast(getApplicationContext(),username + " ... " + num);

        */

        findViewById(R.id.registerBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), TaxiRegisterActivity.class));
            }
        });

        findViewById(R.id.loginBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), TaxiLoginActivity.class));
            }
        });

    }
}
