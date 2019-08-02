package delcab.delcab;

import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.PrimitiveIterator;

public class TaxiPackageActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap map;
    private TextView titleView;
    private String packages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_taxi_package);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        packages = "";

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        Global.goTo(map, Global.midlands(), 6);

        setInfoWindow(R.layout.custom_info_window);

        Print.out("packageId ... "+Global.get(getApplicationContext(), "packageId"));

        map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                if(!marker.getSnippet().equals("My location")){
                    startActivity(new Intent(getApplicationContext(), SelectPackageActivity.class)
                            .putExtra("packageId", marker.getTitle()).putExtra("packages", packages));

                }
            }
        });

        //START of HTTP request
        StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.POST, "http://delcab.ie/webservice/get_packages.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);


                    String message = jsonObject.getString("message");
                    packages = message;
                    Print.out(message);
                    String[] row = message.split("g3k7b3");
                    for(int i=1; i<row.length; i++){

                        String[] col = row[i].split("j7v4x1");
                        map.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(col[3]),
                                Double.parseDouble(col[4]))).title(col[0]).snippet("Collect from: "
                                +col[7]+"\n\nDestination: "+col[8]+"\n\nFare: €"+col[9]));
                    }

                    map.addMarker(new MarkerOptions().position(LocationTracker.getTracker().getLatLng()).snippet("My location").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));

                    map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                        @Override
                        public boolean onMarkerClick(Marker marker) {
                            if(marker.getSnippet().equals("My location")){
                                Print.toast(getApplicationContext(), "This is your location");
                                marker.hideInfoWindow();
                            }
                            else{
                                marker.showInfoWindow();
                            }
                            return true;
                        }
                    });



                }
                catch (Exception e) {
                    e.printStackTrace();
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


                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
        //END of HTTP request

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
