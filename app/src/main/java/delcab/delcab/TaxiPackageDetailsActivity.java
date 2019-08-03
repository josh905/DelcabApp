package delcab.delcab;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;


public class TaxiPackageDetailsActivity extends AppCompatActivity {

    private double durdest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_taxi_package_details);

        TextView tv = findViewById(R.id.tv);

        durdest = 0;

        try{
            durdest = Double.parseDouble(Global.get(getApplicationContext(), "durationToDest"));
        }
        catch (Exception ex){
            durdest = 0;
        }

        Print.out("durd"+durdest);

        Print.out("dp"+Global.get(getApplicationContext(), "driverPay"));

        double pay = Double.parseDouble(Global.get(getApplicationContext(), "driverPay"));

        pay = Global.euro(pay);
        String strPay = Double.toString(pay);

        /*
        String[] arr = strPay.split(".");
        String cent = arr[1];
        if(cent.length()==1){
            strPay = strPay + "0";
        }
        */

        if(durdest==0){
            tv.setText("Collection Address: "+Global.get(getApplicationContext(), "startAd")+
                    "\n\nDelivery Address: "+Global.get(getApplicationContext(), "endAd")+
            "\n\n\nYou will receive €"+strPay+" for this journey");
        }
        else{
            tv.setText("Collection Address: "+Global.get(getApplicationContext(), "startAd")+
                    "\n\nDelivery Address: "+Global.get(getApplicationContext(), "endAd")+
                    "\n\nEstimated time remaining to destination: "+Math.round(durdest/60)+" mins" +
                    "\n\nYou will receive €"+strPay+" for this journey");
        }


    }
}
