package com.adnroid.bstech.cuadmissionfriend;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
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
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PostView extends AppCompatActivity {

    LinearLayout linearLayout_comment_list, linearLayout_load_error;
    ProgressBar progressBar_post, progressBar_new_comment;

    TextView textView_post_user_name, textView_post_title, textView_post_details, textView_comment_user_name, textView_comment_text;
    EditText editText_comment_user_name, editText_comment_content;
    ImageView imageView_btn_close;
    Button button_submit_comment;

    List<String> user_name, comment, comment_id;

    CardView cardView_comment_list, cardView_post;
    FloatingActionButton fab;
    private final Context context = PostView.this;
    AlertDialog alertDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_view);
        super.setTitle(IDCollection.title);

        //initialize all global variables
        initializeGlobalVariables();

        //get post from server and set in layout
        getPostFromServer();

    }

    private void initializeGlobalVariables(){
        linearLayout_comment_list = (LinearLayout) findViewById(R.id.linearLayout_comment_list);
        linearLayout_load_error = (LinearLayout) findViewById(R.id.linearLayout_load_error);
        cardView_post = (CardView) findViewById(R.id.cardView_post);
        textView_post_user_name = (TextView) findViewById(R.id.textView_post_user_name);
        textView_post_title = (TextView) findViewById(R.id.textView_post_title);
        textView_post_details = (TextView) findViewById(R.id.textView_post_details);

        progressBar_post = (ProgressBar) findViewById(R.id.progressBar_post);
        fab = (FloatingActionButton) findViewById(R.id.fab_add_comment);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showNewCommentDialog();
            }
        });

        linearLayout_load_error.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getPostFromServer();
            }
        });
    }

    private void showNewCommentDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        View view = getLayoutInflater().inflate(R.layout.layout_new_comment, null, false);

        //initialize all variables
        editText_comment_user_name = (EditText) view.findViewById(R.id.editText_comment_user_name);
        editText_comment_content = (EditText) view.findViewById(R.id.editText_comment_content);
        imageView_btn_close = (ImageView) view.findViewById(R.id.imageView_btn_close);
        progressBar_new_comment = (ProgressBar) view.findViewById(R.id.progressBar_new_comment);
        button_submit_comment = (Button) view.findViewById(R.id.button_submit_comment);

        imageView_btn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.cancel();
            }
        });

        button_submit_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkEditText()){
                    submitCommentToServer();
                }
            }
        });

        builder.setView(view);

        builder.create();
        alertDialog=builder.show();
    }

    private void getPostFromServer(){
        showProgressBar();
        cardView_post.setVisibility(View.GONE);

        final RequestQueue requestQueue = Volley.newRequestQueue(context);
        String post_url = APILink.post_details+IDCollection.postID+"/details";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, post_url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    textView_post_user_name.setText(response.getString("user_name"));
                    textView_post_title.setText(response.getString("title"));
                    textView_post_details.setText(response.getString("post"));

                    requestQueue.stop();
                    cardView_post.setVisibility(View.VISIBLE);
                    getCommentFromServer();

                } catch (Exception e) {
                    showError();
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

    private void getCommentFromServer(){
        showProgressBar();

        user_name = new ArrayList<>();
        comment = new ArrayList<>();
        comment_id = new ArrayList<>();

        final RequestQueue requestQueue = Volley.newRequestQueue(context);
        String comment_url = APILink.get_comment+IDCollection.postID+"/comments";

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, comment_url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject chield = response.getJSONObject(i);

                        user_name.add(chield.getString("user_name"));
                        comment.add(chield.getString("comment"));
                        comment_id.add(chield.getString("id"));
                    }
                    requestQueue.stop();
                    addCommentInLayout();

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

    private void submitCommentToServer(){
        button_submit_comment.setVisibility(View.GONE);
        progressBar_new_comment.setVisibility(View.VISIBLE);

        String add_comment_url = APILink.add_comment+IDCollection.postID+"/add";
        final RequestQueue requestQueue = Volley.newRequestQueue(context);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, add_comment_url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                alertDialog.cancel();
                getCommentFromServer();
                requestQueue.stop();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                button_submit_comment.setVisibility(View.VISIBLE);
                progressBar_new_comment.setVisibility(View.GONE);
                requestQueue.stop();

                Toast.makeText(context, "Sorry! Something went wrong. Please try again", Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> param = new HashMap<>();

                param.put("user_name", editText_comment_user_name.getText().toString());
                param.put("comment", editText_comment_content.getText().toString());

                return param;
            }
        };

        requestQueue.add(stringRequest);
    }

    private boolean checkEditText(){
        boolean result = true;

        if(TextUtils.isEmpty(editText_comment_user_name.getText().toString())){
            editText_comment_user_name.setError("Required");
            result = false;
        }

        if(TextUtils.isEmpty(editText_comment_content.getText().toString())){
            editText_comment_content.setError("Required");
            result = false;
        }

        return result;
    }

    private void showProgressBar(){
        linearLayout_load_error.setVisibility(View.GONE);
        linearLayout_comment_list.setVisibility(View.GONE);
        progressBar_post.setVisibility(View.VISIBLE);
    }

    private void showError(){
        linearLayout_comment_list.setVisibility(View.GONE);
        progressBar_post.setVisibility(View.GONE);
        cardView_post.setVisibility(View.GONE);
        linearLayout_load_error.setVisibility(View.VISIBLE);
    }

    private void showComment(){
        linearLayout_load_error.setVisibility(View.GONE);
        progressBar_post.setVisibility(View.GONE);
        linearLayout_comment_list.setVisibility(View.VISIBLE);
    }

    private void addCommentInLayout(){
        linearLayout_comment_list.removeAllViews();

        for(int i=0; i<user_name.size(); i++){
            View v = getLayoutInflater().inflate(R.layout.layout_comment, null);

            TextView textView_comment_user_name = v.findViewById(R.id.textView_comment_user_name);
            TextView textView_comment_text = v.findViewById(R.id.textView_comment_text);

            textView_comment_user_name.setText(user_name.get(i));
            textView_comment_text.setText(comment.get(i));

            linearLayout_comment_list.addView(v);
        }

        showComment();
    }

}
