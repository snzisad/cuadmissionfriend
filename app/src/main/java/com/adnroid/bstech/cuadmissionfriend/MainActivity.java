package com.adnroid.bstech.cuadmissionfriend;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.adnroid.bstech.cuadmissionfriend.HelperClass.APILink;
import com.adnroid.bstech.cuadmissionfriend.HelperClass.IDCollection;
import com.adnroid.bstech.cuadmissionfriend.HelperClass.LocationDatabase;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    TextView textView_latest_post_title, textView_view_full_post;
    CardView cardView_latest_post_layout, cardView_latest_post_load_error_layout, cardView_circular_button, cardView_process_button,
        cardView_faculty_button, cardView_blog_button, cardView_route_button, cardView_spot_button;
    ProgressBar progressBar_latest_post;
    private final Context context = MainActivity.this;
    LocationDatabase locationDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // All global variables initialization
        initialize_global_variables();

        // Handle all item click
        handle_on_click();

        //get the latest post
        getLatestPost();

        //get all location details
//        getLocationInfo();

        //subscribe to toic to get notification
        FirebaseMessaging.getInstance().subscribeToTopic("Post");
    }

    private void initialize_global_variables(){
        textView_latest_post_title = (TextView)findViewById(R.id.textView_latest_post_title);
        textView_view_full_post = (TextView)findViewById(R.id.textView_view_full_post);

        cardView_latest_post_layout = (CardView) findViewById(R.id.cardView_latest_post_layout);
        cardView_latest_post_load_error_layout = (CardView) findViewById(R.id.cardView_latest_post_load_error_layout);
        cardView_circular_button = (CardView) findViewById(R.id.cardView_circular_button);
        cardView_process_button = (CardView) findViewById(R.id.cardView_process_button);
        cardView_faculty_button = (CardView) findViewById(R.id.cardView_faculty_button);
        cardView_blog_button = (CardView) findViewById(R.id.cardView_blog_button);
        cardView_route_button = (CardView) findViewById(R.id.cardView_route_button);
        cardView_spot_button = (CardView) findViewById(R.id.cardView_spot_button);

        progressBar_latest_post = (ProgressBar) findViewById(R.id.progressBar_latest_post);
    }

    private void handle_on_click(){
        cardView_latest_post_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, PostView.class));
            }
        });

        cardView_circular_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(context, Circular.class));
            }
        });

        cardView_process_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(context, Process.class));
            }
        });

        cardView_faculty_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(context, Faculty.class));
            }
        });

        cardView_blog_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(context, Blog.class));
            }
        });

        cardView_route_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(context, LocationMap.class));
            }
        });

        cardView_spot_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(context, TouristSpot.class));
            }
        });

        cardView_latest_post_load_error_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLatestPost();
            }
        });
    }

    private void getLatestPost(){
        progressBar_latest_post.setVisibility(View.VISIBLE);
        cardView_latest_post_load_error_layout.setVisibility(View.GONE);
        cardView_latest_post_layout.setVisibility(View.GONE);

        final RequestQueue requestQueue = Volley.newRequestQueue(context);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, APILink.latest_post, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    //save data from server to variable
                    String id = response.getString("id");
                    String title = response.getString("title");

                    //set variable data to textview
                    textView_latest_post_title.setText(title);
                    IDCollection.postID = id;
                    IDCollection.title = title;

                    //hide or show content
                    progressBar_latest_post.setVisibility(View.GONE);
                    cardView_latest_post_load_error_layout.setVisibility(View.GONE);
                    cardView_latest_post_layout.setVisibility(View.VISIBLE);

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

        requestQueue.add(request);
    }

    private void showError(){
        progressBar_latest_post.setVisibility(View.GONE);
        cardView_latest_post_layout.setVisibility(View.GONE);
        cardView_latest_post_load_error_layout.setVisibility(View.VISIBLE);
    }

    private void getLocationInfo(){
        final RequestQueue requestQueue = Volley.newRequestQueue(context);
        locationDatabase = new LocationDatabase(this);
        locationDatabase.empty();

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, APILink.location, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {

                    for(int i = 0 ; i<response.length(); i++){
                        JSONObject child = response.getJSONObject(i);
                        locationDatabase.add(child.getString("name"), child.getString("latitude"), child.getString("longitude"));
                    }

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

        requestQueue.add(request);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.about:
                startActivity(new Intent(this, About.class));
                break;

            case R.id.share:
                shereToFriend ();
                break;

            case R.id.rate:
                try{
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.adnroid.bstech.cuadmissionfriend")));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.adnroid.bstech.cuadmissionfriend")));
                }
                break;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        System.exit(0);
        finish();
    }

    private void shereToFriend(){
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        String shareBodyText = "চট্টগ্রাম বিশ্ববিদ্যালয়য়ের ভর্তি পরীক্ষা নিয়ে দারুন একটা অ্যাপ প্লেস্টোরে পাওয়া যাচ্ছে। এখনই ডাউনলোড করে ৫* রেটিং দিন এবং সবার মাঝে ছড়িয়ে দিন। ডাউনলোড করতে ক্লিক করুনঃ https://play.google.com/store/apps/details?id=com.adnroid.bstech.cuadmissionfriend";
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,"ডাউনলোড করুন এখনই");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBodyText);
        startActivity(Intent.createChooser(sharingIntent, "শেয়ার করুন"));
    }
}
