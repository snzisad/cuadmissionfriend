package com.adnroid.bstech.cuadmissionfriend;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.adnroid.bstech.cuadmissionfriend.CustomAdapter.PlaceAutocompleteAdapter;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class LocationMap extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.OnConnectionFailedListener {

    private GoogleMap mMap;
    private static final int LOCATION_REQUEST = 500;
    ArrayList<LatLng> listpoints;

    AutoCompleteTextView autoCompleteTextView_location;
    Button button_view_route;
    TextView textView_time, textView_distance;

    SharedPreferences sharedPreferences;

    GoogleApiClient client;
    PlaceAutocompleteAdapter placeAutocompleteAdapter;

    private Location mylocation;
    private FusedLocationProviderClient mFusedLocationProviderClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        sharedPreferences = getSharedPreferences("Home",0);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST);
            return;
        }

        mMap.setMyLocationEnabled(true);

        getDeviceLocation();

        init();
    }

    private void init() {
        autoCompleteTextView_location = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView_location);
        button_view_route = (Button) findViewById(R.id.button_view_route);

        textView_time = (TextView) findViewById(R.id.textView_time);
        textView_distance = (TextView) findViewById(R.id.textView_distance);

        client = new GoogleApiClient.Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();

        placeAutocompleteAdapter = new PlaceAutocompleteAdapter(this, client, null, null);

        autoCompleteTextView_location.setAdapter(placeAutocompleteAdapter);

        button_view_route.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDeviceLocation();
                getRoute();
            }
        });

        autoCompleteTextView_location.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if ((i == EditorInfo.IME_ACTION_DONE) || (i == EditorInfo.IME_ACTION_SEARCH)
                        || (keyEvent.getAction() == KeyEvent.ACTION_DOWN)
                        || keyEvent.getAction() == KeyEvent.KEYCODE_ENTER) {
                    getRoute();
                }

                return false;

            }
        });
    }

    private void getDeviceLocation() {
        try {
            mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

            Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();

            locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    if(task.isSuccessful()){
                        mylocation = task.getResult();

                        LatLng home = new LatLng(mylocation.getLatitude(), mylocation.getLongitude());

                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putFloat("Latitude", (float) mylocation.getLatitude());
                        editor.putFloat("Longitude", (float) mylocation.getLongitude());
                        editor.commit();

                        mMap.addMarker(new MarkerOptions()
                                .position(home)
                                .title("I am here")
                                .icon(BitmapDescriptorFactory.defaultMarker())
                        );

                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(home, 17));
                    }
                }
            });
        } catch(SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case LOCATION_REQUEST:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mMap.setMyLocationEnabled(true);
                }
                break;
        }
    }


    private String generateRequestURL(){
        String origin = sharedPreferences.getFloat("Latitude",0)+","+sharedPreferences.getFloat("Longitude",0);
        String destination = makeSearchAble(autoCompleteTextView_location.getText().toString());
        String url = "https://maps.googleapis.com/maps/api/directions/json?origin="+origin+"&destination="+destination;
//        String url = "https://maps.googleapis.com/maps/api/directions/json?origin=22.3567202,91.8402153&destination=22.471266,91.788412";
        return url;
    }

    public String makeSearchAble(String search){
        search = search.replace(" ","+");
        return search;
    }

    private void getRoute(){

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait");
        progressDialog.show();

        final RequestQueue requestQueue = Volley.newRequestQueue(this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, generateRequestURL(), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    requestQueue.stop();

                    JSONArray routes = response.getJSONArray("routes");

                    if(routes.length()<=0){
                        progressDialog.cancel();
                        Toast.makeText(LocationMap.this, "Sorry!! No location found", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    JSONArray legs = routes.getJSONObject(0).getJSONArray("legs");

                    JSONObject parent = legs.getJSONObject(0);

                    textView_distance.setText(parent.getJSONObject("distance").getString("text"));
                    textView_time.setText(parent.getJSONObject("duration").getString("text"));

                    listpoints = new ArrayList<>();

                    JSONArray steps = parent.getJSONArray("steps");

                    for(int i=0; i<steps.length(); i++){
                        JSONObject chield = steps.getJSONObject(i);
                        JSONObject end_location = chield.getJSONObject("end_location");

                        LatLng latLng = new LatLng(end_location.getDouble("lat"), end_location.getDouble("lng"));
                        listpoints.add(latLng);
                    }

                    showRouteInMap();

                    progressDialog.cancel();

                } catch (Exception e) {
                    e.printStackTrace();
                    progressDialog.cancel();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                requestQueue.stop();
                progressDialog.cancel();
                Toast.makeText(LocationMap.this, "Sorry! Something went wrong. Please try again", Toast.LENGTH_SHORT).show();
            }
        });

        requestQueue.add(jsonObjectRequest);
    }

    private void showRouteInMap(){
        PolylineOptions polylineOptions = new PolylineOptions();

        //set home latitude and longitude
        LatLng latLng = new LatLng(sharedPreferences.getFloat("Latitude",0), sharedPreferences.getFloat("Longitude",0));
        polylineOptions.add(latLng);

        //set all polyline points
        for(int i=0;i<listpoints.size(); i++){
            polylineOptions.add(listpoints.get(i));
        }

        polylineOptions.width(10);
        polylineOptions.color(Color.BLUE);

        //show route
        mMap.addPolyline(polylineOptions);

        //add marker in destination point
        mMap.addMarker(new MarkerOptions()
                .position(listpoints.get(listpoints.size()-1))
                .title("My Destination")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
        );

    }
}
