package com.jeefersan.photomosaics.ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.graphics.Bitmap;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by JeeferSan on 4-2-20.
 */

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.imageView)
    ImageView mSelectedImg;

    @BindView(R.id.photoview)
    SubsamplingScaleImageView mOutput;

    @OnClick(R.id.start)
    void click() {
        if (mBitmap != null) {
            observe();
        }
    }

    @OnClick(R.id.browse)
    void onClick() {
        pickFromGallery();
    }

    private MainViewModel mViewModel;
    private Switch mSwitch;
    private Bitmap mBitmap;

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

        mSwitch = findViewById(R.id.switch1);
        mSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            mViewModel.setPixel(isChecked);
        });

        Log.d(TAG, "onCreate");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        Log.d(TAG, "onPostResume");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
    }

    private void pickFromGallery() {
        Intent pickImg = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickImg.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivityForResult(pickImg, REQUEST_GALLERY_PHOTO);
    }

    private void newFile(View view) {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            switch (requestCode) {

                case REQUEST_GALLERY_PHOTO:
                    Uri selectedImg = null;
                    if (data != null) {
                        selectedImg = data.getData();
                    }

                    Glide.with(this)
                            .asBitmap()
                            .load(selectedImg)
                            .into(new CustomTarget<Bitmap>() {
                                @Override
                                public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                    mBitmap = resource;
                                    mViewModel.setBitmap(mBitmap);
                                    mSelectedImg.setImageBitmap(mBitmap);
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

        mViewModel.getOutput().observe(this, new Observer<Bitmap>() {
            @Override
            public void onChanged(Bitmap bitmap) {
                if (bitmap != null) {
                    mBitmap = bitmap;
                    mOutput.setImage(ImageSource.bitmap(mBitmap));
                }
            }
        });


    }
}