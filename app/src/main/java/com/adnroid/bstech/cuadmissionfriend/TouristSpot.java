package com.adnroid.bstech.cuadmissionfriend;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.adnroid.bstech.cuadmissionfriend.HelperClass.APILink;
import com.adnroid.bstech.cuadmissionfriend.HelperClass.IDCollection;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class TouristSpot extends AppCompatActivity {
    LinearLayout linearLayout_touristSpot, linearLayout_load_error;
    ProgressBar progressBar_touristSpot;
    ScrollView scrollView_spot_list;
    private Context context = TouristSpot.this;
    List<String> tourist_spot_name, tourist_spot_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tourist_spot);
        super.setTitle("দর্শনীয় স্থান");

        //initialize all global variables
        initializeGlobalVariables();

        //get data from server and set it to layout
        getDataFromServer();
    }

    private void initializeGlobalVariables(){
        linearLayout_touristSpot = (LinearLayout) findViewById(R.id.linearLayout_touristSpot);
        linearLayout_load_error = (LinearLayout) findViewById(R.id.linearLayout_load_error);
        scrollView_spot_list = (ScrollView) findViewById(R.id.scrollView_spot_list);
        progressBar_touristSpot = (ProgressBar) findViewById(R.id.progressBar_touristSpot);

        linearLayout_load_error.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDataFromServer();
            }
        });
    }

    private void getDataFromServer(){
        showProgressBar();

        tourist_spot_name = new ArrayList<>();
        tourist_spot_id = new ArrayList<>();

        final RequestQueue requestQueue = Volley.newRequestQueue(context);

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, APILink.tourist_spot_list, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject chield = response.getJSONObject(i);
                        tourist_spot_name.add(chield.getString("name"));
                        tourist_spot_id.add(chield.getString("id"));
                    }
                    requestQueue.stop();
                    addDataInLayout();

                }catch (Exception e) {
                    Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
                    showError();
                    requestQueue.stop();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                showError();
                requestQueue.stop();
            }
        });

        requestQueue.add(request);
    }

    private void addDataInLayout(){
        linearLayout_touristSpot.removeAllViews();

        for(int i=0; i<tourist_spot_name.size(); i++){
            View v = getLayoutInflater().inflate(R.layout.layout_spot_list, null);

            CardView cardView_spot_list = v.findViewById(R.id.cardView_spot_list);
            TextView textView_spot_name = v.findViewById(R.id.textView_spot_name);

            textView_spot_name.setText(tourist_spot_name.get(i));

            final int j = i;
            cardView_spot_list.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    IDCollection.touristSpotID = tourist_spot_id.get(j);
                    IDCollection.title = tourist_spot_name.get(j);
                    startActivity(new Intent(context, SpotDetails.class));
                }
            });

            linearLayout_touristSpot.addView(v);
        }
        showScrollView();
    }

    private void showProgressBar(){
        scrollView_spot_list.setVisibility(View.GONE);
        linearLayout_load_error.setVisibility(View.GONE);
        progressBar_touristSpot.setVisibility(View.VISIBLE);
    }

    private void showError(){
        scrollView_spot_list.setVisibility(View.GONE);
        progressBar_touristSpot.setVisibility(View.GONE);
        linearLayout_load_error.setVisibility(View.VISIBLE);
    }

    private void showScrollView(){
        linearLayout_load_error.setVisibility(View.GONE);
        progressBar_touristSpot.setVisibility(View.GONE);
        scrollView_spot_list.setVisibility(View.VISIBLE);
    }
}
