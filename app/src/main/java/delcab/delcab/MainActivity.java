package delcab.delcab;

import android.content.Intent;
import android.content.SharedPreferences;

import android.os.Bundle;


import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;

import android.widget.Button;
import android.widget.ImageView;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //if logged in go to home for bus or taxi and this.finish();

        startActivity(new Intent(MainActivity.this, SplashActivity.class));

        ImageView taxiImg = findViewById(R.id.taxiImg);
        ImageView busImg = findViewById(R.id.busImg);
        Button taxiBtn = findViewById(R.id.taxiBtn);
        Button busBtn = findViewById(R.id.busBtn);

        taxiImg.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, TaxiAccountActivity.class));
            }
        });

        busImg.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, BusinessAccountActivity.class));
            }
        });

        taxiBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, TaxiAccountActivity.class));
            }
        });

        busBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, BusinessAccountActivity.class));
            }
        });


        //check shared preferences and go to taxi or business page if logged in

        SharedPreferences getter = getSharedPreferences("DELCAB",MODE_PRIVATE);
        String accountType = getter.getString("accountType","");

        if(accountType.equals("taxi")){
            startActivity(new Intent(getApplicationContext(),TaxiHomeActivity.class));
        }
        else if(accountType.equals("business")){
            startActivity(new Intent(getApplicationContext(),BusinessHomeActivity.class));
        }



    }
}

