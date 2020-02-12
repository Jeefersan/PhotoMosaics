package com.jeefersan.photomosaics.ui;

import android.app.Application;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;


import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.jeefersan.photomosaics.utils.BitmapUtils;
import com.jeefersan.photomosaics.utils.Loader;

public class MainViewModel extends AndroidViewModel {
    public MutableLiveData<Bitmap> outputLiveData = new MutableLiveData<>();
    public MutableLiveData<Bitmap> inputLiveData = new MutableLiveData<>();
    public MutableLiveData<Boolean> isLoading = new MutableLiveData<>();

    private final String TAG = "MainViewModel";
    private Loader mLoader;
    private Bitmap mBitmap;
    private boolean isPixel;
    private int chunkSize;

    public MainViewModel(@NonNull Application application) {
        super(application);
        mLoader = new Loader(application);
    }

    public void parseInput(Uri selectedImg) {
        Glide.with(getApplication())
                .asBitmap()
                .load(selectedImg)
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        Bitmap tmp = resource;
                        if (BitmapUtils.isTooBig(tmp)) {
                            BitmapUtils.scaleDown(tmp);
                        }
                        mBitmap = tmp;
                        inputLiveData.setValue(mBitmap);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                        Log.d(TAG, "onLoadCleared");
                    }
                });
    }


    public void getOutput() {
        isLoading.setValue(true);

        Bitmap bit = mLoader.getOutput(mBitmap, chunkSize, isPixel);
        Log.d(TAG, "getOutput w h = " + bit.getWidth() + ", " + mBitmap.getHeight());
        if (bit != null) {
            Log.d(TAG, "isLoading : " + isLoading.getValue());
            outputLiveData.setValue(bit);
        }


    }

    public Bitmap getmBitmap() {
        return mBitmap;
    }

    public void setPixel(boolean b) {
        isPixel = b;
    }

    public void setChunkSize(int chunkSize) {
        this.chunkSize = chunkSize;
    }
}
