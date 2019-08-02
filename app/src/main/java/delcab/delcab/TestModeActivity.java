package delcab.delcab;

import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
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

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class TestModeActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap map;
    private LatLng loc;
    private String address;
    private SupportPlaceAutocompleteFragment placeAutocompleteFragment;
    private TextView titleView;
    private Button addressBtn;
    private String windowText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_mode);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.testmap);
        mapFragment.getMapAsync(this);

        placeAutocompleteFragment = (SupportPlaceAutocompleteFragment) getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        placeAutocompleteFragment.input.setBackgroundColor((getResources().getColor(R.color.white)));

        placeAutocompleteFragment.input.setHint("Enter new location");

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
                if(address ==null || address.isEmpty() || loc ==null){
                    Print.toast(getApplicationContext(), "Please select an address from the dropdown");
                    return;
                }

                //Print.toast(getApplicationContext(), loc.toString());

                Global.set(getApplicationContext(), "testLat", Double.toString(loc.latitude));
                Global.set(getApplicationContext(), "testLon", Double.toString(loc.longitude));

                LocationTracker.getTracker().stop(getApplicationContext());


                //START of HTTP request
                StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.POST, "http://delcab.ie/webservice/update_location.php", new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            String message = jsonObject.getString("message");
                            Print.out(message);


                            Global.set(getApplicationContext(), "testMode", "on");

                            startActivity(new Intent(getApplicationContext(), SplashActivity.class));

                            finish();

                        }
                        catch (Exception e) {
                            e.printStackTrace();
                            Print.out("Could not update location");
                        }
                    }

                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Print.out("Volley error ... "+error.toString());
                    }
                }){
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> params = new HashMap<>();
                        params.put("key1", RequestHeader.key1);
                        params.put("key2", RequestHeader.key2);

                        params.put("taxi_id", Global.get(getApplicationContext(), "taxiId"));
                        params.put("lat", Double.toString(loc.latitude));
                        params.put("lon", Double.toString(loc.longitude));

                        return params;
                    }
                };

                RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                requestQueue.add(stringRequest);
                //END of HTTP request


            }
        });

        placeAutocompleteFragment.setCountry("IE");



        placeAutocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                try{
                    address = place.getAddress().toString();
                    loc = place.getLatLng();
                }
                catch(Exception e){
                    Print.toast(getApplicationContext(), "Invalid address");
                    return;
                }


                Print.out(address);
                windowText = address.replace(", ", ",\n");



                map.clear();

                Marker marker = map.addMarker(new MarkerOptions().position(loc).title(windowText));
                marker.showInfoWindow();


                Global.goToTilt(map, loc, 18);

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
