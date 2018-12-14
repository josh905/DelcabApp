package delcab.delcab;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private EditText collectionText, destinationText;
    private Button calcBtn;
    private LatLng startLoc, endLoc;
    private Address startAdr, endAdr;

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

        calcBtn = findViewById(R.id.calcBtn);

        Print.out("Map has been created");


    }

    private void geoLocate(){

        Print.out("geo loc");

        String collectionSearch = collectionText.getText().toString();
        String destinationSearch = destinationText.getText().toString();

        Geocoder coder = new Geocoder(MapsActivity.this);
        List<Address> startList = new ArrayList<>();

        try{
            startList = coder.getFromLocationName(collectionSearch, 1); //only give 1 result (the most likely address theyre looking for)
            Print.out(startList.toString());
        }
        catch(IOException e){
            Print.out(startList.toString());
            Print.out(e.getMessage());

        }

        if(startList.size()>0){
            startAdr = startList.get(0);
            Print.out(startAdr.toString());
        }


        List<Address> endList = new ArrayList<>();

        try{
            endList = coder.getFromLocationName(destinationSearch, 1); //only give 1 result (the most likely address theyre looking for)
            Print.out(endList.toString());
        }
        catch(IOException e){
            Print.out(endList.toString());
            Print.out(e.getMessage());

        }

        if(endList.size()>0){
            endAdr = endList.get(0);
            Print.out(endAdr.toString());
        }

        startLoc = new LatLng(startAdr.getLatitude(), startAdr.getLongitude());

        endLoc = new LatLng(endAdr.getLatitude(), endAdr.getLongitude());

        if(!startAdr.getCountryCode().equals("IE")){
            Print.toast(getApplicationContext(), "Collection address is not Ireland");
            return;
        }

        if(!endAdr.getCountryCode().equals("IE")){
            Print.toast(getApplicationContext(), "Delivery address is not Ireland");
            return;
        }

        Calculation calc = Calculation.getCalculation();
        calc.addCalculation(startLoc, endLoc, 45);



    }


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
}
