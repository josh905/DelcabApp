package delcab.delcab;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class DeliveriesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deliveries);

        Button activeBtn = findViewById(R.id.activeBtn);
        Button deliveriesBtn = findViewById(R.id.pastBtn);

        activeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Global.get(getApplicationContext(), "packageId").equals("") ||
                        Global.get(getApplicationContext(), "packageId").equals("none")){
                    Print.toast(getApplicationContext(), "No active package");
                }
                else{
                    startActivity(new Intent(getApplicationContext(), TaxiMapsActivity.class));
                }

            }
        });

        deliveriesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }
}
