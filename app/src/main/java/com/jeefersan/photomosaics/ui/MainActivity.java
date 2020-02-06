package com.jeefersan.photomosaics.ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.jeefersan.photomosaics.R;
import com.jeefersan.photomosaics.utils.Loader;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by JeeferSan on 4-2-20.
 */

/* TODO: architecture
         Fix UI
         switch fix, buttons and chunksize input
         Show menu to choose between searchfromgallery of take image
         Save output to gallery in specific map
         Fetch drawables and save permanently in app
         Let user choose own drawables
         Share button : to whatsapp, facebook or email etc

 */

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.imageView)
    ImageView mSelectedImg;

    @BindView(R.id.photoview)
    ImageView mOutput;

    @OnClick(R.id.start)
    void click() {
        if (mViewModel.getmBitmap() != null) {
            mViewModel.getOutput();
            observe();
        }
    }

    @OnClick(R.id.browse)
    void onClick() {
        pickFromGallery();
    }

    private MainViewModel mViewModel;
    private Switch mSwitch;


    private final String TAG = "MainActivity";

    private static final int CREATE_REQUEST_CODE = 10;
    private static final int OPEN_REQUEST_CODE = 11;
    private static final int SAVE_REQUEST_CODE = 12;
    private static final int REQUEST_GALLERY_PHOTO = 13;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mViewModel = ViewModelProviders.of(this).get(MainViewModel.class);

//        mLoader = new Loader(getApplication());

        mSwitch = findViewById(R.id.switch1);
        mSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
//            mLoader.setPixel(isChecked);
            mViewModel.setPixel(isChecked);
        });

    }

    private void pickFromGallery() {
        Intent pickImg = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickImg.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivityForResult(pickImg, REQUEST_GALLERY_PHOTO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            switch (requestCode) {

                case REQUEST_GALLERY_PHOTO:
                    Uri selectedImg;
                    if (data == null) {
                        break;
                    }
                    selectedImg = data.getData();
                    Glide.with(this)
                            .asBitmap()
                            .load(selectedImg)
                            .into(new CustomTarget<Bitmap>() {
                                @Override
                                public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                    mViewModel.setBitmap(resource);
                                    mSelectedImg.setImageBitmap(resource);
                                }

                                @Override
                                public void onLoadCleared(@Nullable Drawable placeholder) {
                                    Log.d(TAG, "onLoadCleared");
                                }
                            });


                    break;

            }


        }

    }


    private void observe() {

        mViewModel.outputLiveData.observe(this, bitmap -> {
            if (bitmap != null) {
                mOutput.setImageBitmap(bitmap);
            }
        });


    }
}