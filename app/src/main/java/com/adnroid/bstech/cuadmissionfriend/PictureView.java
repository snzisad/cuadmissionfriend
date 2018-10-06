package com.adnroid.bstech.cuadmissionfriend;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.adnroid.bstech.cuadmissionfriend.HelperClass.APILink;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import uk.co.senab.photoview.PhotoViewAttacher;

public class PictureView extends AppCompatActivity {
    ImageView imageView_picture;
    ProgressBar progressBar_picture;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture_view);

        imageView_picture = findViewById(R.id.imageView_picture);

        progressBar_picture = findViewById(R.id.progressBar_picture);

        Picasso.get()
                .load(APILink.image_url2)
                .into(imageView_picture, new Callback() {
                    @Override
                    public void onSuccess() {
                        progressBar_picture.setVisibility(View.GONE);
                        imageView_picture.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onError(Exception e) {

                    }
                });

        PhotoViewAttacher photoViewAttacher = new PhotoViewAttacher(imageView_picture);
        photoViewAttacher.update();
    }
}
