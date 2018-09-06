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

public class Faculty extends AppCompatActivity {
    LinearLayout linearLayout_faculty, linearLayout_load_error;
    ProgressBar progressBar_faculty;

    Context context = Faculty.this;
    ScrollView scrollView_faculty_list;
    List<String> faculty_name, faculty_id, department;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faculty);
        super.setTitle("ফ্যাকাল্টি");

        //initialize all global variables
        initializeGlobalVariables();

        //get data from server and set to layout
        getDataFromServer();
    }

    private void initializeGlobalVariables(){
        linearLayout_faculty = (LinearLayout) findViewById(R.id.linearLayout_faculty);
        linearLayout_load_error = (LinearLayout) findViewById(R.id.linearLayout_load_error);
        progressBar_faculty = (ProgressBar) findViewById(R.id.progressBar_faculty);

        scrollView_faculty_list = (ScrollView) findViewById(R.id.scrollView_faculty_list);
        progressBar_faculty = (ProgressBar) findViewById(R.id.progressBar_faculty);

        linearLayout_load_error.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDataFromServer();
            }
        });

    }

    private void getDataFromServer(){
        showProgressBar();

        faculty_name = new ArrayList<>();
        faculty_id = new ArrayList<>();
        department = new ArrayList<>();

        final RequestQueue requestQueue = Volley.newRequestQueue(context);

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, APILink.faculty_list, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject chield = response.getJSONObject(i);
                        faculty_name.add(chield.getString("name"));
                        faculty_id.add(chield.getString("id"));
                        department.add(chield.getString("subject"));
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

    private void addDataInLayout(){
        linearLayout_faculty.removeAllViews();

        for(int i=0; i<faculty_name.size(); i++){
            View v = getLayoutInflater().inflate(R.layout.layout_faculty_list, null);

            CardView cardView_faculty_list = v.findViewById(R.id.cardView_faculty_list);
            TextView textView_faculty_name = v.findViewById(R.id.textView_faculty_name);
            TextView textView_department_name = v.findViewById(R.id.textView_department_name);

            textView_faculty_name.setText(faculty_name.get(i));
            textView_department_name.setText(department.get(i));

            final int j = i;
            cardView_faculty_list.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    IDCollection.facultyID = faculty_id.get(j);
                    IDCollection.title = faculty_name.get(j);
                    startActivity(new Intent(context, FacultyDetails.class));
                }
            });

            linearLayout_faculty.addView(v);
        }
        showScrollView();
    }

    private void showProgressBar(){
        scrollView_faculty_list.setVisibility(View.GONE);
        linearLayout_load_error.setVisibility(View.GONE);
        progressBar_faculty.setVisibility(View.VISIBLE);
    }

    private void showError(){
        scrollView_faculty_list.setVisibility(View.GONE);
        progressBar_faculty.setVisibility(View.GONE);
        linearLayout_load_error.setVisibility(View.VISIBLE);
    }

    private void showScrollView(){
        linearLayout_load_error.setVisibility(View.GONE);
        progressBar_faculty.setVisibility(View.GONE);
        scrollView_faculty_list.setVisibility(View.VISIBLE);
    }
}
