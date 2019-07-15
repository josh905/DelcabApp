package delcab.delcab;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class ConfirmPackageActivity extends AppCompatActivity {

    private String collectionAddress, deliveryAddress;
    private double collectionLat, collectionLon, deliveryLat, deliveryLon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_package);

        collectionAddress = getIntent().getStringExtra("collectionAddress");
        deliveryAddress = getIntent().getStringExtra("deliveryAddress");
        collectionLat = getIntent().getDoubleExtra("collectionLat", 0);
        collectionLon = getIntent().getDoubleExtra("collectionLon", 0);
        deliveryLat = getIntent().getDoubleExtra("deliveryLat", 0);
        deliveryLon = getIntent().getDoubleExtra("deliveryLon", 0);



        Button conBtn = findViewById(R.id.conBtn);

        TextView colBox = findViewById(R.id.colBox);
        TextView delBox = findViewById(R.id.delBox);

        String colStr = "<b>Collection Address:</b><br><br>"+collectionAddress;
        String delStr = "<b>Delivery Address:</b><br><br>"+deliveryAddress;
        colBox.setText(Html.fromHtml(colStr));
        delBox.setText(Html.fromHtml(delStr));


        conBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                collectionLat = Global.round(collectionLat, 10);
                collectionLon = Global.round(collectionLon, 10);
                deliveryLat = Global.round(deliveryLat, 10);
                deliveryLon = Global.round(deliveryLon, 10);


                try {
                    startActivity(new Intent(getApplicationContext(), SplashActivity.class));
                    Global.drivingDetails(getApplicationContext(), new LatLng(collectionLat,collectionLon), new LatLng(deliveryLat,deliveryLon));
                    new CountDownTimer(1000, 1000) {
                        @Override
                        public void onTick(long millisUntilFinished) {

                        }

                        @Override
                        public void onFinish() {
                            Global.set(getApplicationContext(), "collectionAddress", collectionAddress);
                            Global.set(getApplicationContext(), "deliveryAddress", deliveryAddress);
                            Global.set(getApplicationContext(), "collectionLat", Double.toString(collectionLat));
                            Global.set(getApplicationContext(), "collectionLon", Double.toString(collectionLon));
                            Global.set(getApplicationContext(), "deliveryLat", Double.toString(deliveryLat));
                            Global.set(getApplicationContext(), "deliveryLon", Double.toString(deliveryLon));

                            startActivity(new Intent(getApplicationContext(), PriceActivity.class));
                        }
                    }.start();


                } catch (Exception e) {
                    Print.toast(getApplicationContext(), "Could not get driving details");
                }




            }
        });

    }




}
