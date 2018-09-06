package com.adnroid.bstech.cuadmissionfriend;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.adnroid.bstech.cuadmissionfriend.HelperClass.APILink;
import com.adnroid.bstech.cuadmissionfriend.HelperClass.IDCollection;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;

public class FacultyDetails extends AppCompatActivity {

    ProgressBar progressBar;
    ImageView imageView_faculty_image;
    LinearLayout linearLayout_load_error;
    CoordinatorLayout coordinatorLayout_faculty_details;
    private Context context = FacultyDetails.this;
    Toolbar toolbar;
    CollapsingToolbarLayout collapsingToolbarLayout;
    TextView textView_faculty_description, textView_route_process, textView_faculty_dept;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faculty_details);

        //initialize all global variables
        initializeGlobalVariables();

        setSupportActionBar(toolbar);

        //back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setSubtitleTextColor(getResources().getColor(R.color.colorLight));
        collapsingToolbarLayout.setTitle(IDCollection.title);
        collapsingToolbarLayout.setExpandedTitleColor(getResources().getColor(R.color.colorLight));

        //get data from server and set it to layout
        getAndSetData();

        Picasso.get().load(APILink.image_url2).into(imageView_faculty_image);
   }

    private void initializeGlobalVariables(){
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        linearLayout_load_error = (LinearLayout) findViewById(R.id.linearLayout_load_error);
        coordinatorLayout_faculty_details = (CoordinatorLayout) findViewById(R.id.coordinatorLayout_faculty_details);

        imageView_faculty_image = (ImageView) findViewById(R.id.imageView_faculty_image);
        toolbar = (Toolbar) findViewById(R.id.custom_toolbar);
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.custom_collapsing_toolbar);

        textView_route_process = (TextView) findViewById(R.id.textView_route_process);
        textView_faculty_description = (TextView) findViewById(R.id.textView_faculty_description);
        textView_faculty_dept = (TextView) findViewById(R.id.textView_faculty_dept);

        coordinatorLayout_faculty_details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getAndSetData();
            }
        });

        imageView_faculty_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, PictureView.class));
            }
        });
    }

    private void getAndSetData(){
        showProgressBar();

        String url = APILink.faculty_details+IDCollection.facultyID+"/details";
        final RequestQueue requestQueue = Volley.newRequestQueue(context);
        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    textView_faculty_description.setText(response.getString("description"));
                    textView_faculty_dept.setText(response.getString("subject"));
                    textView_route_process.setText(response.getString("route_process"));

                    showFacultyDetails();
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
        coordinatorLayout_faculty_details.setVisibility(View.GONE);
        linearLayout_load_error.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
    }

    private void showError(){
        coordinatorLayout_faculty_details.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
        linearLayout_load_error.setVisibility(View.VISIBLE);
    }

    private void showFacultyDetails(){
        linearLayout_load_error.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
        coordinatorLayout_faculty_details.setVisibility(View.VISIBLE);
    }
}
