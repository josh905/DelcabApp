package delcab.delcab;

import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class TaxiMapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap map;
    private TextView titleView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_taxi_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        setInfoWindow(R.layout.custom_info_window);

        LatLng start = new LatLng(Double.parseDouble(Global.get(getApplicationContext(), "startLat")),
                Double.parseDouble(Global.get(getApplicationContext(), "startLon")));

        LatLng end = new LatLng(Double.parseDouble(Global.get(getApplicationContext(), "endLat")),
                Double.parseDouble(Global.get(getApplicationContext(), "endLon")));

        Global.goTo(map, start, 9);



    }


    public void setInfoWindow(final int theWindow){

        if (map != null) {
            map.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                // @Override
                public View getInfoWindow(Marker marker) {
                    return null;
                }

                @Override
                public View getInfoContents(final Marker marker) {
                    View v = getLayoutInflater().inflate(theWindow, null);
                    titleView = v.findViewById(R.id.titleView);

                    titleView.setText(marker.getSnippet());

                    return v;
                }


            });




        }



    }

}
