package com.example.arid3011Q4;

import androidx.fragment.app.FragmentActivity;

import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import android.location.Address;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    private TextView mTextViewResult;
    private RequestQueue mQueue;
    private Geocoder geocoder;
    String url = "https://api.covidtracking.com/v1/states/current.json";

    List<Address> addresses;
  ;
 ;

    public MapsActivity() throws JSONException {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mQueue = Volley.newRequestQueue(this);

        geocoder = new Geocoder(this);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);



        try {

            RequestQueue queue = Volley.newRequestQueue(MapsActivity.this);

            JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET,url , null, new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {

                    for (int i = 0; i < response.length(); i++) {

                        try {
                            // we are getting each json object.
                            JSONObject responseObj = response.getJSONObject(i);


                            addresses =geocoder.getFromLocationName(responseObj.getString("state")+" state", 1);


                            if (addresses.size() > 0) {
                                Address address = addresses.get(0);
                                LatLng london = new LatLng(address.getLatitude(), address.getLongitude());
                                Log.d("this",addresses+"");
                                MarkerOptions markerOptions = new MarkerOptions()
                                        .position(london)
                                        .title("State  ="+responseObj.getString("state")+"  Number of Positive Result  =" + responseObj.getString("positive"));
                                mMap.addMarker(markerOptions);

                            }

                        } catch (JSONException | IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(MapsActivity.this, "Fail to get the data..", Toast.LENGTH_SHORT).show();
                }
            });
            queue.add(jsonArrayRequest);
        } catch ( Exception e) {
            e.printStackTrace();
        }
    }
}