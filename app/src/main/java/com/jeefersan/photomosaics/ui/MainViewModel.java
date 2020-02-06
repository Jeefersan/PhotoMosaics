package com.jeefersan.photomosaics.ui;

import android.app.Application;
import android.graphics.Bitmap;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;


import com.jeefersan.photomosaics.utils.Loader;

public class MainViewModel extends AndroidViewModel {
    private final String TAG = "MainViewModel";
    private Loader mLoader;
    private Bitmap mBitmap;
    boolean isPixel;

    public MutableLiveData<Bitmap> outputLiveData = new MutableLiveData<>();

    public MainViewModel(@NonNull Application application) {
        super(application);
        mLoader = new Loader(application);
    }

    public void getOutput(){
       Bitmap bit = mLoader.getOutput(mBitmap, isPixel);
       outputLiveData.setValue(bit);
    }


    public void setPixel(boolean b) {
        isPixel = b;
    }

    public void setBitmap(Bitmap bitmap) {
        this.mBitmap = bitmap;
    }

    public Bitmap getmBitmap() {
        return mBitmap;
    }
}
