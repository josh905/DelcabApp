package delcab.delcab;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import delcab.delcab.directioncalculations.FetchURL;
import delcab.delcab.directioncalculations.TaskLoadedCallback;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.maps.GeoApiContext;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, TaskLoadedCallback {

    private GoogleMap mMap;
    private EditText collectionText, destinationText;
    private Button calcBtn, viewPriceBtn;
    private LatLng startLoc, endLoc;
    private Address startAdr, endAdr;
    private GeoApiContext geoCon;

    private MarkerOptions startMark, endMark;
    private Polyline currentPolyline;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        startActivity(new Intent(MapsActivity.this,SplashActivity.class));

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        collectionText = findViewById(R.id.input_search);
        destinationText = findViewById(R.id.input_search2);

        calcBtn = findViewById(R.id.calcBtn);

        //viewPriceBtn = findViewById(R.id.viewPriceBtn);

        Print.out("Map has been created");


    }

    private void geoLocate(){


        boolean found = true;
        try {

            startLoc = null;
            endLoc = null;

            Print.out("geo loc");

            String collectionSearch = collectionText.getText().toString();
            String destinationSearch = destinationText.getText().toString();

            Geocoder coder = new Geocoder(MapsActivity.this);
            List<Address> startList = new ArrayList<>();

            try {
                startList = coder.getFromLocationName(collectionSearch, 1); //only give 1 result (the most likely address theyre looking for)
                Print.out(startList.toString());
            } catch (IOException e) {
                Print.out(startList.toString());
                Print.out(e.getMessage());
                Print.toast(this, "Could not find location");
                found = false;
                return;
            }

            if (startList.size() > 0) {
                startAdr = startList.get(0);
                Print.out(startAdr.toString());
            }
            else{
                found = false;
                return;
            }




            List<Address> endList = new ArrayList<>();

            try {
                endList = coder.getFromLocationName(destinationSearch, 1); //only give 1 result (the most likely address theyre looking for)
                Print.out(endList.toString());
            } catch (IOException e) {
                Print.out(endList.toString());
                Print.out(e.getMessage());
                Print.toast(this, "Could not find location");
                found = false;
                return;
            }

            if (endList.size() > 0) {
                endAdr = endList.get(0);
                Print.out(endAdr.toString());
            }
            else{
                found = false;
                return;
            }

            try {

                startLoc = new LatLng(startAdr.getLatitude(), startAdr.getLongitude());

                endLoc = new LatLng(endAdr.getLatitude(), endAdr.getLongitude());
            } catch (Exception e) {
                Print.toast(this, "Could not find location");
                found = false;
                return;
            }

            if (!startAdr.getCountryCode().equals("IE")) {
                Print.toast(getApplicationContext(), "Collection address is not Ireland");
                found = false;
                return;
            }

            if (!endAdr.getCountryCode().equals("IE")) {
                Print.toast(getApplicationContext(), "Delivery address is not Ireland");
                found = false;
                return;
            }


            startMark = new MarkerOptions().position(startLoc).title(startAdr.getAddressLine(0));
            endMark = new MarkerOptions().position(endLoc).title(endAdr.getAddressLine(0));


            if(found) {
                new FetchURL(mMap, endLoc, MapsActivity.this).execute(getUrl(startMark.getPosition(), endMark.getPosition(), "driving"), "driving");
            }
            else {
                Print.toast(this,"Location not found");
            }

        }
        catch (Exception e){
            found = false;
            e.printStackTrace();
            Print.toast(getApplicationContext(), "Collection address is not Ireland");
            return;
        }





        //move camera here

        //calculate price


        //show price and why

        //insert into db



        /*
        Calculation calc = Calculation.getCalculation();
        calc.addCalculation(startLoc, endLoc, 45);


        calculateDirections();

        */

    }


    /*
    private void calculateDirections(){
        Print.out("calculate directions method");


        com.google.maps.model.LatLng destination = new com.google.maps.model.LatLng(
                endLoc.latitude,
                endLoc.longitude
        );
        DirectionsApiRequest directions = new DirectionsApiRequest(geoCon);

        //maybe set this to false coz then just the best route will be used
        directions.alternatives(true);
        directions.origin(
                new com.google.maps.model.LatLng(startLoc.latitude, startLoc.longitude)
        );

        Print.out(" ... lat ... " + startLoc.latitude + " ... long ... " + startLoc.longitude + " ... lat ... " + endLoc.latitude + " ... long ... " + endLoc.longitude);


        directions.destination(destination).setCallback(new PendingResult.Callback<DirectionsResult>() {
            @Override
            public void onResult(DirectionsResult result) {
                Print.out("on result here");
               Print.out( "calculateDirections: routes: " + result.routes[0].toString());
                Print.out("calculateDirections: duration: " + result.routes[0].legs[0].duration.toString());
                Print.out( "calculateDirections: distance: " + result.routes[0].legs[0].distance.toString());
                Print.out( "calculateDirections: geocodedWayPoints: " + result.geocodedWaypoints[0].toString());
            }

            @Override
            public void onFailure(Throwable e) {
                Print.out( "calculateDirections: Failed to get directions: " + e.getMessage() );

            }
        });
    }
    */


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng nciLocation = new LatLng(53.348726, -6.243148);
        //  mMap.addMarker(new MarkerOptions().position(nciLocation).title(getString("NCI Location")));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(nciLocation));
        CameraPosition pos = new CameraPosition.Builder().target(nciLocation).zoom(18).tilt(89).bearing(20).build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(pos));



        calcBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                geoLocate();
            }
        });

        /*
        viewPriceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MapsActivity.this, PriceActivity.class));
            }
        });
        */

        //mMap.setMyLocationEnabled(true);


        /*
        geoCon = null;

        geoCon = new GeoApiContext.Builder().apiKey(getString(R.string.google_maps_key)).build();


        collectionText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {


                if (keyCode == EditorInfo.IME_ACTION_SEARCH
                        || keyCode == EditorInfo.IME_ACTION_DONE
                        || event.getAction() == KeyEvent.ACTION_DOWN
                        || event.getAction() == KeyEvent.KEYCODE_ENTER
                        )
                {

                }
                return false;
            }
        });


        collectionText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if(actionId == EditorInfo.IME_ACTION_SEARCH
                        || actionId == EditorInfo.IME_ACTION_DONE
                        || event.getAction() == KeyEvent.ACTION_DOWN
                        || event.getAction() == KeyEvent.KEYCODE_ENTER
                        )
                {
                    geoLocate();
                }

                return false;
            }
        });
        */

    }

    private String getUrl(LatLng origin, LatLng dest, String directionMode) {
        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        // Mode
        String mode = "mode=" + directionMode;
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + mode;
        // Output format
        String output = "json";
        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=" + getString(R.string.google_maps_key);
        return url;
    }

    /*
    private String getDirectionsURL(LatLng origin, LatLng dest, String directionMode) {
        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        // Mode
        String mode = "mode=" + directionMode;
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + mode;
        // Output format
        String output = "json";
        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=" + getString(R.string.google_maps_key);
        return url;
    }
    */



    @Override
    public void onTaskDone(Object... values) {
        if (currentPolyline != null)
            currentPolyline.remove();
        currentPolyline = mMap.addPolyline((PolylineOptions) values[0]);
    }




}
