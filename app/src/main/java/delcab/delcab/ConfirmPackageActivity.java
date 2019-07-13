package delcab.delcab;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


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

        colBox.setText("Collection Address:\n\n"+collectionAddress.replace(", ", ",\n"));

        delBox.setText("Delivery Address:\n\n"+deliveryAddress.replace(", ", ",\n"));

        conBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                collectionLat = Global.round(collectionLat, 10);
                collectionLon = Global.round(collectionLon, 10);
                deliveryLat = Global.round(deliveryLat, 10);
                deliveryLon = Global.round(deliveryLon, 10);

                Intent intent = new Intent(getApplicationContext(), CalculatePriceActivity.class);
                intent.putExtra("collectionAddress", collectionAddress);
                intent.putExtra("collectionLat", collectionLat);
                intent.putExtra("collectionLon", collectionLon);
                intent.putExtra("deliveryAddress", deliveryAddress);
                intent.putExtra("deliveryLat", deliveryLat);
                intent.putExtra("deliveryLon", deliveryLon);
                startActivity(intent);

            }
        });

    }
}
