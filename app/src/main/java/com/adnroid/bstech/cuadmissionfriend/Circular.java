package com.adnroid.bstech.cuadmissionfriend;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.adnroid.bstech.cuadmissionfriend.CustomAdapter.UnitListAdapter;
import com.adnroid.bstech.cuadmissionfriend.HelperClass.APILink;
import com.adnroid.bstech.cuadmissionfriend.HelperClass.IDCollection;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Circular extends AppCompatActivity {
    LinearLayout linearLayout_unit, linearLayout_load_error;

    ProgressBar progressBar_unit;
    Context context = Circular.this;
    ScrollView scrollView_unit_list;
    List<String> unit_name, unit_id;
    ListView listView_unit_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_circular);
        super.setTitle("সার্কুলার");

        //initialize all global variables
        initializeGlobalVariables();

        getDataFromServer();
    }

    private void initializeGlobalVariables(){
        linearLayout_unit = (LinearLayout) findViewById(R.id.linearLayout_unit);
        linearLayout_load_error = (LinearLayout) findViewById(R.id.linearLayout_load_error);

        progressBar_unit = (ProgressBar) findViewById(R.id.progressBar_unit);
        scrollView_unit_list = (ScrollView) findViewById(R.id.scrollView_unit_list);

        listView_unit_list = (ListView)findViewById(R.id.listView_unit_list);

        linearLayout_load_error.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDataFromServer();
            }
        });
    }

    private void getDataFromServer(){
        progressBar_unit.setVisibility(View.VISIBLE);
        linearLayout_load_error.setVisibility(View.GONE);
        scrollView_unit_list.setVisibility(View.GONE);

        unit_name = new ArrayList<>();
        unit_id = new ArrayList<>();
        final RequestQueue requestQueue = Volley.newRequestQueue(context);

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, APILink.unit_list, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject chield = response.getJSONObject(i);
                        unit_name.add(chield.getString("unit_name"));
                        unit_id.add(chield.getString("id"));
                    }
                    requestQueue.stop();
                    addDataInLayout();

                }catch (Exception e) {
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

    private void showError(){
        progressBar_unit.setVisibility(View.GONE);
        scrollView_unit_list.setVisibility(View.GONE);
        linearLayout_load_error.setVisibility(View.VISIBLE);
    }

    private void addDataInLayout(){
        int i=0;
        linearLayout_unit.removeAllViews();

        for(i=0; i<unit_name.size(); i++){
            View v = getLayoutInflater().inflate(R.layout.layout_unit_list, null);

            CardView cardView_unit_list = v.findViewById(R.id.cardView_unit_list);
            TextView textView_unit_name = v.findViewById(R.id.textView_unit_name);

            textView_unit_name.setText(unit_name.get(i));
            final int j =i;
            cardView_unit_list.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    IDCollection.unitID = unit_id.get(j);
                    IDCollection.title = unit_name.get(j);
                    startActivity(new Intent(context, UnitDetails.class));
                }
            });

            linearLayout_unit.addView(v);
        }


        progressBar_unit.setVisibility(View.GONE);
        linearLayout_load_error.setVisibility(View.GONE);
        scrollView_unit_list.setVisibility(View.VISIBLE);
    }

    private void addDataInListView(){
        UnitListAdapter adapter = new UnitListAdapter(context, unit_name, unit_id);
        listView_unit_list.setAdapter(adapter);

        Toast.makeText(context, "Total:"+unit_id.size(), Toast.LENGTH_SHORT).show();

        progressBar_unit.setVisibility(View.GONE);
        linearLayout_load_error.setVisibility(View.GONE);
        scrollView_unit_list.setVisibility(View.VISIBLE);
    }
}
