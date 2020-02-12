package com.jeefersan.photomosaics.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.FileProvider;
import androidx.lifecycle.ViewModelProviders;

import com.github.chrisbanes.photoview.PhotoView;
import com.jeefersan.photomosaics.BuildConfig;
import com.jeefersan.photomosaics.Constants;
import com.jeefersan.photomosaics.R;
import com.jeefersan.photomosaics.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnTouch;

/**
 * Created by JeeferSan on 4-2-20.
 */

/* TODO: architecture
         Save output to gallery in specific map
         Fetch drawables and save permanently in app
         Share mBrowseBtn : to whatsapp, facebook or email etc
 */

public class MainActivity extends AppCompatActivity {
    private MainViewModel mViewModel;
    private SwitchCompat mSwitch;
    private Button mBrowseBtn;
    private Button mStartBtn;
    private ProgressBar loadingView;
    private Uri imgUri;

    private final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();

        observe();

    }

    private void initViews() {
        ButterKnife.bind(this);
        mViewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        mChunkInput.setText("" + 30);

        loadingView = findViewById(R.id.loading_view);
        mBrowseBtn = findViewById(R.id.browse);
        mStartBtn = findViewById(R.id.startBtn);
        mSwitch = findViewById(R.id.switch1);
        mSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            mViewModel.setPixel(isChecked);
        });
        mStartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                observe();

                if (mInput.getDrawable() != null) {
                    mViewModel.setChunkSize(Integer.parseInt(mChunkInput.getText().toString()));
                    mViewModel.getOutput();
                }


            }
        });
        mBrowseBtn.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(getApplicationContext(), v);

            try {
                Method method = popupMenu.getMenu().getClass().getDeclaredMethod("setOptionalIconsVisible", boolean.class);
                method.setAccessible(true);
                method.invoke(popupMenu.getMenu(), true);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }

            popupMenu.getMenuInflater().inflate(R.menu.popup_menu, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                    case R.id.choose_from_gallery:
                        pickFromGallery();
                        return true;

                    case R.id.take_new_photo:
                        startCameraIntent();
                        return true;
                }
                return false;
            });
            popupMenu.show();

        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        initViews();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case Constants.REQUEST_GALLERY_PHOTO:
                    mViewModel.parseInput(data.getData());
                    break;
                case Constants.REQUEST_IMAGE_CAPTURE:
                    mViewModel.parseInput(imgUri);
                    break;
            }

        }

    }

    private void pickFromGallery() {
        Intent pickImg = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickImg.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivityForResult(pickImg, Constants.REQUEST_GALLERY_PHOTO);
    }

    private void startCameraIntent() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {

            File imgFile = null;
            try {
                imgFile = Utils.createImg(this);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (imgFile != null) {
                imgUri = FileProvider.getUriForFile(getApplicationContext(), BuildConfig.APPLICATION_ID + ".provider", imgFile);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imgUri);
                startActivityForResult(cameraIntent, Constants.REQUEST_IMAGE_CAPTURE);
            }

        }
    }


    private void observe() {
        mViewModel.inputLiveData.observe(this, bitmap -> {
            if (bitmap != null) {
                mInput.setImageBitmap(bitmap);
            }
        });
        mViewModel.outputLiveData.observe(this, bitmap -> {
            if (bitmap != null) {
                mOutput.setImageBitmap(bitmap);
                mOutput.setVisibility(View.VISIBLE);

            }
        });
        mViewModel.isLoading.observe(this, isLoading -> {
            if (isLoading != null) {
                Log.d(TAG, "loading value: " + isLoading);
                loadingView.setVisibility(isLoading ? View.VISIBLE : View.GONE);
                if(isLoading){
                    mOutput.setVisibility(View.GONE);
                }
            }

        });


    }


    @BindView(R.id.inputview)
    ImageView mInput;

    @BindView(R.id.output)
    PhotoView mOutput;

    @BindView(R.id.chunkinput)
    TextView mChunkInput;

    @OnTouch(R.id.plus_button)
    void plus() {
        if (!Utils.isInRange(Integer.parseInt(mChunkInput.getText().toString()) + 1)) {
            Utils.showToast(this);
        } else {
            mChunkInput.setText(String.valueOf(Integer.parseInt(mChunkInput.getText().toString()) + 1));
        }
    }


    @OnTouch(R.id.minus_button)
    void minus() {
        if (!Utils.isInRange(Integer.parseInt(mChunkInput.getText().toString()) - 1)) {
            Utils.showToast(this);
        } else {
            mChunkInput.setText(String.valueOf(Integer.parseInt(mChunkInput.getText().toString()) - 1));
        }
    }
}