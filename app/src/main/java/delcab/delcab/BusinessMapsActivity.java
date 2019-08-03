package delcab.delcab;

import androidx.fragment.app.FragmentActivity;

import android.content.Context;
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class BusinessMapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap map;
    private TextView titleView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        setInfoWindow(R.layout.custom_info_window);

        //START of HTTP request
        StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.POST, "http://delcab.ie/webservice/business_packages.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);

                    String message = jsonObject.getString("message");

                    Print.out("message is..."+message);

                    String[] idleRows = message.split("n9h3k8")[0].split("g3k7b3");

                    String[] assignedRows = message.split("n9h3k8")[1].split("g3k7b3");

                    ArrayList<String> idleList = new ArrayList<>();

                    ArrayList<String> assignedList = new ArrayList<>();


                    for(int i=0; i<idleRows.length; i++) {
                        if(!idleRows[i].contains("details of")){
                            idleList.add(idleRows[i]);
                        }
                    }

                    for(int i=0; i<assignedRows.length; i++) {
                        if(!assignedRows[i].contains("details of")){
                            assignedList.add(assignedRows[i]);
                        }
                    }

                    Print.out(idleList.toString());
                    Print.out(assignedList.toString());




                    for(int i=0; i<idleList.size(); i++){
                        Print.out(idleList.get(i));

                        String[] cols = idleList.get(i).split("j7v4x1");
                        String snippet = "Not Collected.\n\nDestination: "+cols[3];
                        LatLng loc = new LatLng(Double.parseDouble(cols[0]), Double.parseDouble(cols[1]));

                        map.addMarker(new MarkerOptions().position(loc).snippet(snippet).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));

                    }

                    for(int i=0; i<assignedList.size(); i++){
                        Print.out(assignedList.get(i));

                        String[] cols = assignedList.get(i).split("j7v4x1");
                        String snippet = "Current taxi location with package.\n\nDestination: "+cols[3];
                        LatLng loc = new LatLng(Double.parseDouble(cols[4]), Double.parseDouble(cols[5]));

                        map.addMarker(new MarkerOptions().position(loc).snippet(snippet).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));

                    }

                    Global.goTo(map, Global.dublin(), 11);



                }
                catch (Exception e) {
                    e.printStackTrace();
                    Print.toast(getApplicationContext(),"Could not get packages");
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

                Context con = getApplicationContext();

                params.put("businessId", Global.get(con, "businessId"));

                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
        //END of HTTP request

    }



    public void setInfoWindow(final int theWindow) {

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