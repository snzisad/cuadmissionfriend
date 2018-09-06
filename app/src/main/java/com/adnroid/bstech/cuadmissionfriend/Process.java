package com.adnroid.bstech.cuadmissionfriend;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

public class Process extends AppCompatActivity {
    TextView textView_unit_process, textView_unit_process_example;

    ScrollView scrollView_admission_process;
    ProgressBar progressBar;
    LinearLayout linearLayout_load_error;

    private Context context = Process.this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_process);
        super.setTitle("আবেদন প্রক্রিয়া");

        //initialize all global variables
        initializeGlobalVariables();

        //get data from server and set it to view
        getAndSetData();
    }

    private void initializeGlobalVariables(){
        textView_unit_process = (TextView) findViewById(R.id.textView_unit_process);
        textView_unit_process_example = (TextView) findViewById(R.id.textView_unit_process_example);

        scrollView_admission_process = (ScrollView) findViewById(R.id.scrollView_admission_process);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        linearLayout_load_error = (LinearLayout) findViewById(R.id.linearLayout_load_error);

        linearLayout_load_error.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getAndSetData();
            }
        });
    }


    private void getAndSetData(){
        showProgressBar();

        final RequestQueue requestQueue = Volley.newRequestQueue(context);
        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.GET, APILink.application_process, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    textView_unit_process.setText(response.getString("process"));
                    textView_unit_process_example.setText(response.getString("example"));

                    showScrollView();
                    requestQueue.stop();

                } catch (Exception e) {
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

        requestQueue.add(objectRequest);

    }

    private void showProgressBar(){
        scrollView_admission_process.setVisibility(View.GONE);
        linearLayout_load_error.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
    }

    private void showError(){
        scrollView_admission_process.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
        linearLayout_load_error.setVisibility(View.VISIBLE);
    }

    private void showScrollView(){
        linearLayout_load_error.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
        scrollView_admission_process.setVisibility(View.VISIBLE);
    }
}
