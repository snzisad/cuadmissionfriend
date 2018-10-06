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

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;

public class UnitDetails extends AppCompatActivity {
    TextView textView_unit_subject, textView_unit_qualification, textView_unit_number_system, textView_unit_exam_subject, textView_unit_pass_mark;

    ScrollView scrollView_unit_details;
    ProgressBar progressBar;
    LinearLayout linearLayout_load_error;

    private Context context = UnitDetails.this;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unit_details);
        super.setTitle(IDCollection.title);


        //initialize all global variables
        initializeGlobalVariables();

        //get data from server and set it to view
        getAndSetData();
    }

    private void initializeGlobalVariables(){
        textView_unit_exam_subject = (TextView) findViewById(R.id.textView_unit_exam_subject);
        textView_unit_number_system = (TextView) findViewById(R.id.textView_unit_number_system);
        textView_unit_qualification = (TextView) findViewById(R.id.textView_unit_qualification);
        textView_unit_subject = (TextView) findViewById(R.id.textView_unit_subject);
        textView_unit_pass_mark = (TextView) findViewById(R.id.textView_unit_pass_mark);

        scrollView_unit_details = (ScrollView) findViewById(R.id.scrollView_unit_details);
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

        String url = APILink.unit_details+IDCollection.unitID+"/details";
        final RequestQueue requestQueue = Volley.newRequestQueue(context);
        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    textView_unit_subject.setText(response.getString("subject"));
                    textView_unit_number_system.setText(response.getString("number_calculation_process"));
                    textView_unit_qualification.setText(response.getString("application_qualification"));
                    textView_unit_exam_subject.setText(response.getString("exam_subject"));
                    textView_unit_pass_mark.setText(response.getString("pass_mark"));

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
        scrollView_unit_details.setVisibility(View.GONE);
        linearLayout_load_error.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
    }

    private void showError(){
        scrollView_unit_details.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
        linearLayout_load_error.setVisibility(View.VISIBLE);
    }

    private void showScrollView(){
        linearLayout_load_error.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
        scrollView_unit_details.setVisibility(View.VISIBLE);
    }
}
