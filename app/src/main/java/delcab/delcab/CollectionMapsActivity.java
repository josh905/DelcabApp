package delcab.delcab;

import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Marker;
import com.google.android.libraries.places.compat.Place;
import com.google.android.libraries.places.compat.ui.PlaceSelectionListener;
import com.google.android.libraries.places.compat.ui.SupportPlaceAutocompleteFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class CollectionMapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap map;
    private LatLng collectionLoc;
    private String collectionAddress;
    private SupportPlaceAutocompleteFragment placeAutocompleteFragment;
    private TextView titleView;
    private Button addressBtn;
    private String windowText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.colmap);
        mapFragment.getMapAsync(this);

        placeAutocompleteFragment = (SupportPlaceAutocompleteFragment) getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        placeAutocompleteFragment.input.setBackgroundColor((getResources().getColor(R.color.white)));

        placeAutocompleteFragment.input.setHint("Enter Collection Address");

        placeAutocompleteFragment.searchIcon.setBackgroundColor(getResources().getColor(R.color.white));

        placeAutocompleteFragment.clearIcon.setBackgroundColor(getResources().getColor(R.color.white));

        titleView = findViewById(R.id.titleView);

        addressBtn = findViewById(R.id.addressBtn);


    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;


        setInfoWindow(R.layout.custom_info_window);

        Global.goTo(map, Global.midlands(), 6);

        addressBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(collectionAddress ==null || collectionAddress.isEmpty() || collectionLoc==null){
                    Print.toast(getApplicationContext(), "Please select an address from the dropdown");
                    return;
                }

               Intent intent = new Intent(getApplicationContext(), DeliveryMapsActivity.class);
                intent.putExtra("collectionAddress", collectionAddress);
                intent.putExtra("lat", collectionLoc.latitude);
                intent.putExtra("lon", collectionLoc.longitude);
                startActivity(intent);

            }
        });

        placeAutocompleteFragment.setCountry("IE");



        placeAutocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                try{
                    collectionAddress = place.getAddress().toString();
                    collectionLoc = place.getLatLng();
                }
                catch(Exception e){
                    Print.toast(getApplicationContext(), "Invalid address");
                    return;
                }


                Print.out(collectionAddress);
                windowText = collectionAddress.replace(", ", ",\n");

                /*
                StringBuilder sb = new StringBuilder();
                for(int i=collectionAddress.length()-1; i>collectionAddress.length()-7; i--){
                    sb.append(collectionAddress.charAt(i));
                }
                Print.out(sb.reverse().toString());
                if(!sb.reverse().toString().equals("Ireland")){
                    Print.toast(getApplicationContext(), "Address must be in Ireland");
                    return;
                }
                */

                map.clear();

                Marker marker = map.addMarker(new MarkerOptions().position(collectionLoc).title(windowText));
                marker.showInfoWindow();


                Global.goToTilt(map, collectionLoc, 18);

            }

            @Override
            public void onError(Status status) {
                Print.out(status.getStatusMessage());
            }
        });




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

                    titleView.setText(windowText);


                    return v;
                }


            });




        }



    }

}
