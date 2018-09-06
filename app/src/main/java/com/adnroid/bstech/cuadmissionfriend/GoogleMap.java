package com.adnroid.bstech.cuadmissionfriend;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import com.adnroid.bstech.cuadmissionfriend.CustomAdapter.PlaceAutocompleteAdapter;
import com.adnroid.bstech.cuadmissionfriend.HelperClass.LocationDatabase;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.util.List;

public class GoogleMap extends AppCompatActivity {
    AutoCompleteTextView autoCompleteTextView_location;
    Button button_view_route;

    GoogleApiClient client;
    PlaceAutocompleteAdapter placeAutocompleteAdapter;

    private static final LatLngBounds LAT_LNG_BOUNDS = new LatLngBounds(
            new LatLng(22.351803, 91.833237), //chawk bazar
            new LatLng(22.471246, 91.788455) //university of Chittagong
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_map);
        super.setTitle("যাতায়ত ম্যাপ");

        button_view_route = findViewById(R.id.button_view_route);
        autoCompleteTextView_location = findViewById(R.id.autoCompleteTextView_location);

        client = new GoogleApiClient.Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, null)
                .build();


        placeAutocompleteAdapter = new PlaceAutocompleteAdapter(this, client, LAT_LNG_BOUNDS, null);

        autoCompleteTextView_location.setAdapter(placeAutocompleteAdapter);

        if(!isGoogleMapsInstalled()){ //check google map installed or not in user's phone
            //if not, show alert dialog
            showErrorDialog();
            return ;
        }


        button_view_route.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(autoCompleteTextView_location.getText())){
                    autoCompleteTextView_location.setError("Required");
                }
                else{
                    Uri gmmIntentUri = Uri.parse("google.navigation:q="+makeSearchAble(autoCompleteTextView_location.getText().toString()));
                    Intent intent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                    intent.setPackage("com.google.android.apps.maps");
                    startActivity(intent);
                }
            }
        });
    }

    //google map installation checker
    public boolean isGoogleMapsInstalled()
    {
        try
        {
            ApplicationInfo info = getPackageManager().getApplicationInfo("com.google.android.apps.maps", 0 );
            return true;
        }
        catch(PackageManager.NameNotFoundException e)
        {
            return false;
        }
    }

    public boolean isGoogleMapsGOInstalled()
    {
        try
        {
            ApplicationInfo info = getPackageManager().getApplicationInfo("com.google.android.apps.mapslite", 0 );
            return true;
        }
        catch(PackageManager.NameNotFoundException e)
        {
            return false;
        }
    }


    public String makeSearchAble(String search){
        search = search.replace(" ","+");
        return search;
    }

    public void showErrorDialog(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("দুঃখিত!");
        builder.setMessage("আপনার ফোন Google Map ইন্সটল করা নেই। যাতায়ত ম্যাপ দেখার জন্য  অবশ্যই Google Map ইন্সটল করতে হবে।");
        builder.setPositiveButton("ইন্সটল করুন", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//                    final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                try{
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.google.android.apps.maps")));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.google.android.apps.maps")));
                }
                builder.show();
            }
        });

        builder.setNegativeButton("বাদ দিন", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                GoogleMap.super.onBackPressed();
            }
        });

        builder.setCancelable(false);
        builder.create();
        builder.show();
    }


}
