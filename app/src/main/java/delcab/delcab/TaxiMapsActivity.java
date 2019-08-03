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
    private LatLng start, end, cur;

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

        start = new LatLng(Double.parseDouble(Global.get(getApplicationContext(), "startLat")),
                Double.parseDouble(Global.get(getApplicationContext(), "startLon")));

        end = new LatLng(Double.parseDouble(Global.get(getApplicationContext(), "endLat")),
                Double.parseDouble(Global.get(getApplicationContext(), "endLon")));

        cur = null;

        if(Global.get(getApplicationContext(), "testMode").equals("on")){
            try{
                cur = new LatLng(Double.parseDouble(Global.get(getApplicationContext(), "testLat")),
                        Double.parseDouble(Global.get(getApplicationContext(), "testLon")));
            }
            catch(Exception ex){
                Print.toast(getApplicationContext(), "Can't get test location");
            }

        }
        else {
            cur = LocationTracker.getTracker().getLatLng();
        }

        map.addMarker(new MarkerOptions().position(start).snippet("Collection Address:\n\n"+Global.get(getApplicationContext(), "startAd")).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));

        map.addMarker(new MarkerOptions().position(cur).snippet("My location\n\n"+cur.toString()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));

        map.addMarker(new MarkerOptions().position(end).snippet("Delivery Address:\n\n"+Global.get(getApplicationContext(), "endAd")).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));



        Global.goTo(map, start, 11);



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
