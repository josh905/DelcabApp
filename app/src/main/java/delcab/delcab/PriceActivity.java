package delcab.delcab;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.HttpURLConnection;
import java.net.URL;

public class PriceActivity extends AppCompatActivity {

    private String durationStr, distanceStr;
    private TextView tv;
    private double distance, duration, price, baseCharge, costPerMinute, costPerKm, weight;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_price);


        startActivity(new Intent(this, SplashActivity.class));

        Intent in = getIntent();
        durationStr = in.getStringExtra("duration");
        distanceStr = in.getStringExtra("distance");

        weight = 0;
        price = 0;
        baseCharge = 4.50;
        costPerMinute = 0.40;
        costPerKm = 0.46;

        tv = findViewById(R.id.tv);

        tv.append("\n\nJourney distance: " + distanceStr + "\n\nEstimated journey length: " + durationStr + "\n\n");

        //60 euro cut off point
        if(durationStr.contains("hour")){

            tv.append("This journey would cost over €70\n\nWe recommend you use a courier");
        }
        else if(durationStr.equals("1 min") || distanceStr.contains(" m")){
            tv.append("Delcab does not deliver for journeys this short");
        }

        else{


            String subDis = distanceStr.split(" ")[0];
            String subDur = durationStr.split(" ")[0];
            try{
                distance = Double.parseDouble(subDis);
                duration = Double.parseDouble(subDur);


            }
            catch (NumberFormatException e){
                e.printStackTrace();
                Print.toast(this,"Could not parse double");
            }

            if(duration < 2 || distance < 0.8){
                tv.append("Delcab does not deliver for journeys this short");
            }

            else{
                price = baseCharge + (costPerMinute * duration) + (costPerKm * distance);

                price = round(price,2);

                tv.append("The estimated price for this journey is €"+price);

            }



        }




        /*
        Intent in = getIntent();
        durationStr = in.getStringExtra("duration");
        distanceStr = in.getStringExtra("distance");


        final String[] getArr = {"dummy","dummy"};

        HttpRequest req = new HttpRequest();
        String response = req.toURL(this,"getdirection", getArr);
        Print.out("delcab.ie response: "+response);



        Print.out("1");

        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL("http://delcab.ie/p7d3bny8s/getdirection.php?j43sj3na98S3nAd8A3kAs5dk3ak3sh8ss9Sr49Dn4sSLDsn493SNm4sm3iSAM3js8a3DJs38hsSfd74Sh48shGSh4SRha43b2Aj43sj3na98S3nAd8A3kAs5dk3ak3sfge5wr1msKSn1ao3wlf34nFrnSkf9483Gs9rn34jsDj4093jFw3noi4ill1211DFg4jfgj43sj3na98S3nAd8A3kAs5dk3ak3sdummyj43sj3na98S3nAd8A3kAs5dk3ak3sdummyj43sj3na98S3nAd8A3kAs5dk3ak3s");
            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();
            // Connecting to url
            urlConnection.connect();

            Print.out("2");

            //Print.out(urlConnection.getHeaderFields().toString());
            Print.out(urlConnection.getResponseMessage());
           // Print.out(urlConnection.getContent().toString());
            //Print.out(urlConnection.getURL().getContent().toString());
            Print.out(urlConnection.getURL().getQuery());



            // Reading data from url
            iStream = urlConnection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            Print.out("2.5");
            Print.out(br.readLine());

            StringBuffer sb = new StringBuffer();
            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            Print.out("3");
            data = sb.toString();
            Print.out("4" + data);
            Log.d("mylog", "Downloaded URL: " + data.toString());
            br.close();
        } catch (Exception e) {
            Log.d("mylog", "Exception downloading URL: " + e.toString());
        }

            iStream.close();
            urlConnection.disconnect();


        Print.out(data);

      */



        //Print.toast(this,distance + " ... " + duration);




        //insert estimate

    }



    private double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }





}
