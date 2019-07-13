package delcab.delcab;

import androidx.fragment.app.FragmentActivity;
import delcab.delcab.directioncalculations.FetchURL;
import delcab.delcab.directioncalculations.TaskLoadedCallback;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

public class CalculatePriceActivity extends FragmentActivity implements OnMapReadyCallback, TaskLoadedCallback {

    private GoogleMap map;
    private String collectionAddress, deliveryAddress;
    private LatLng collectionLoc, deliveryLoc;
    private Marker start, end;
    private Polyline currentPolyline;
    //button

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculate_price);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Intent inbound = getIntent();
        collectionAddress = inbound.getStringExtra("collectionAddress");
        deliveryAddress = inbound.getStringExtra("deliveryAddress");
        collectionLoc = new LatLng(inbound.getDoubleExtra("collectionLat", 0),
                inbound.getDoubleExtra("collectionLon", 0));
        deliveryLoc = new LatLng(inbound.getDoubleExtra("deliveryLat", 0),
                inbound.getDoubleExtra("deliveryLon", 0));


        Global.set(getApplicationContext(),"collectionAddress", collectionAddress);
        Global.set(getApplicationContext(),"collectionLoc", collectionLoc.toString());
        Global.set(getApplicationContext(),"deliveryAddress", deliveryAddress);
        Global.set(getApplicationContext(),"deliveryLoc", deliveryLoc.toString());


    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        start = map.addMarker(new MarkerOptions().position(collectionLoc).title("Collection point"));
        end = map.addMarker(new MarkerOptions().position(deliveryLoc).title("Delivery point"));
        //start.showInfoWindow();
        //end.showInfoWindow();


        new FetchURL(map, deliveryLoc, getApplicationContext()).execute(getUrl(collectionLoc, deliveryLoc));



    }


    private String getUrl(LatLng origin, LatLng dest) {
        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        // Mode
        String mode = "mode=driving";
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + mode;
        // Output format
        String output = "json";
        // Building the url to the web service
        return "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=" + getString(R.string.google_maps_key);
    }

    @Override
    public void onTaskDone(Object... values) {
        Print.out("tasked");
        if (currentPolyline != null)
            currentPolyline.remove();
        currentPolyline = map.addPolyline((PolylineOptions) values[0]);
        //countdowntimer?
        //make the button visible
        Print.out(Global.get(getApplicationContext(),"distance"));
    }

}
