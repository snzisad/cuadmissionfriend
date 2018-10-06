package com.adnroid.bstech.cuadmissionfriend;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.adnroid.bstech.cuadmissionfriend.HelperClass.APILink;
import com.adnroid.bstech.cuadmissionfriend.HelperClass.IDCollection;
import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Blog extends AppCompatActivity {
    LinearLayout linearLayout_blog, linearLayout_load_error;
    ProgressBar progressBar_blog ,progressBar_new_post;
    ScrollView scrollView_blog;

    EditText editText_user_name, editText_post_title, editText_post_content;
    ImageView imageView_close;
    Button button_submit_post;

    List<String> user_name, post_title, post_id;

    int response_code;
    FloatingActionButton fab;
    private final Context context = Blog.this;
    AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blog);
        super.setTitle("ব্লগ");

        //initialize all global variables
        initializeGlobalVariables();

        //get data from server and set in layout
        getDataFromServer();
    }

    private void initializeGlobalVariables(){
        linearLayout_blog = (LinearLayout) findViewById(R.id.linearLayout_blog);
        linearLayout_load_error = (LinearLayout) findViewById(R.id.linearLayout_load_error);
        progressBar_blog = (ProgressBar) findViewById(R.id.progressBar_blog);
        scrollView_blog = (ScrollView) findViewById(R.id.scrollView_blog);

        fab = (FloatingActionButton) findViewById(R.id.fab_add_post);

        linearLayout_load_error.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDataFromServer();
            }
        });
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showNewPostDialog();
            }
        });
    }

    private void showNewPostDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        View view = getLayoutInflater().inflate(R.layout.layout_new_post, null, false);

        //initialize all variables
        editText_user_name = (EditText) view.findViewById(R.id.editText_user_name);
        editText_post_title = (EditText) view.findViewById(R.id.editText_post_title);
        editText_post_content = (EditText) view.findViewById(R.id.editText_post_content);

        imageView_close = (ImageView) view.findViewById(R.id.imageView_close);
        progressBar_new_post = (ProgressBar) view.findViewById(R.id.progressBar_new_post);
        button_submit_post = (Button) view.findViewById(R.id.button_submit_post);

        imageView_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.cancel();
            }
        });

        button_submit_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkEditText()){
                    //first unsubscribe to topic so that the user who are posting can't receive his own post's notification
                    FirebaseMessaging.getInstance().unsubscribeFromTopic("Post");

                    sendDataToServer();
                }
            }
        });

        builder.setView(view);

        builder.create();
        alertDialog=builder.show();
    }

    private void getDataFromServer(){
        showProgressBar();

        user_name = new ArrayList<>();
        post_title = new ArrayList<>();
        post_id = new ArrayList<>();

        final RequestQueue requestQueue = Volley.newRequestQueue(context);

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, APILink.blog_post_list, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject chield = response.getJSONObject(i);

                        user_name.add(chield.getString("user_name"));
                        post_title.add(chield.getString("title"));
                        post_id.add(chield.getString("id"));
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

    private void sendDataToServer(){
        progressBar_new_post.setVisibility(View.VISIBLE);
        button_submit_post.setVisibility(View.GONE);

        final RequestQueue requestQueue = Volley.newRequestQueue(context);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, APILink.add_post, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject message = new JSONObject(response);
                    Toast.makeText(context, message.getString("message"), Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                getDataFromServer();
                alertDialog.cancel();
                requestQueue.stop();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                progressBar_new_post.setVisibility(View.GONE);
                button_submit_post.setVisibility(View.VISIBLE);

                Toast.makeText(context, "Something went wrong. Please try again", Toast.LENGTH_SHORT).show();
                requestQueue.stop();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> param = new HashMap<>();
                param.put("user_name", editText_user_name.getText().toString());
                param.put("title", editText_post_title.getText().toString());
                param.put("post", editText_post_content.getText().toString());

                return param;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> header = new HashMap<>();
                header.put("Accept","application/json");

                return header;
            }
        };

        requestQueue.add(stringRequest);

        //again subscribe to topic
        FirebaseMessaging.getInstance().subscribeToTopic("Post");
    }

    private void addDataInLayout(){
        linearLayout_blog.removeAllViews();

        for(int i=0; i<user_name.size(); i++){
            View v = getLayoutInflater().inflate(R.layout.layout_blog_post, null);

            CardView cardView_post_list = v.findViewById(R.id.cardView_post_list);
            TextView textView_user_name = v.findViewById(R.id.textView_user_name);
            TextView textView_post_title = v.findViewById(R.id.textView_post_title);

            textView_user_name.setText(user_name.get(i));
            textView_post_title.setText(post_title.get(i));

            final int j = i;
            cardView_post_list.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    IDCollection.postID = post_id.get(j);
                    IDCollection.title = post_title.get(j);
                    startActivity(new Intent(context, PostView.class));
                }
            });

            linearLayout_blog.addView(v);
        }
        showScrollView();
    }

    private void showProgressBar(){
        scrollView_blog.setVisibility(View.GONE);
        linearLayout_load_error.setVisibility(View.GONE);
        progressBar_blog.setVisibility(View.VISIBLE);
    }

    private void showError(){
        scrollView_blog.setVisibility(View.GONE);
        progressBar_blog.setVisibility(View.GONE);
        linearLayout_load_error.setVisibility(View.VISIBLE);
    }

    private void showScrollView(){
        linearLayout_load_error.setVisibility(View.GONE);
        progressBar_blog.setVisibility(View.GONE);
        scrollView_blog.setVisibility(View.VISIBLE);
    }

    private boolean checkEditText(){
        boolean result = true;
        if(TextUtils.isEmpty(editText_user_name.getText().toString())){
            editText_user_name.setError("Required");
            result = false;
        }
        if(TextUtils.isEmpty(editText_post_title.getText().toString())){
            editText_post_title.setError("Required");
            result = false;
        }
        if(TextUtils.isEmpty(editText_post_content.getText().toString())){
            editText_post_content.setError("Required");
            result = false;
        }

        return result;
    }


    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
