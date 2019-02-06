package delcab.delcab;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.Image;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.os.Bundle;


import android.view.View;
import android.view.View.OnClickListener;

import android.widget.Button;
import android.widget.EditText;
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


    }
}

