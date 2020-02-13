package com.jeefersan.photomosaics.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.lifecycle.ViewModelProviders;

import com.github.chrisbanes.photoview.PhotoView;
import com.jeefersan.photomosaics.Constants;
import com.jeefersan.photomosaics.R;
import com.jeefersan.photomosaics.utils.BitmapUtils;
import com.jeefersan.photomosaics.utils.IntentHelper;
import com.jeefersan.photomosaics.utils.Utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTouch;

/**
 * Created by JeeferSan on 4-2-20.
 */


public class MainActivity extends AppCompatActivity {
    private MainViewModel mViewModel;
    private SwitchCompat mSwitch;
    private Button mBrowseBtn;
    private IntentHelper intentHelper;

    private Uri mUri;

    private final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        intentHelper = new IntentHelper(this);
        initViews();
        observe();
    }

    private void initViews() {
        ButterKnife.bind(this);
        mViewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        mChunkInput.setText("" + Constants.CHUNK_SIZE);

        loadingView = findViewById(R.id.loading_view);
        mBrowseBtn = findViewById(R.id.browse);

        mSwitch = findViewById(R.id.switch1);
        mSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            mViewModel.setPixel(isChecked);
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
                        startIntent(Constants.REQUEST_GALLERY_PHOTO);
                        return true;

                    case R.id.take_new_photo:
                        startIntent(Constants.REQUEST_IMAGE_CAPTURE);
                        return true;
                }
                return false;
            });
            popupMenu.show();

        });
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
                    mViewModel.parseInput(intentHelper.getImgUri());
                    break;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.share_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.share) {
            if (mUri != null) {
                startIntent(Constants.REQUEST_SHARE);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void startIntent(int req) {
        Intent intent = intentHelper.getIntent(req, mUri);
        startActivityForResult(intent, req);

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
                mUri = BitmapUtils.bmpToUri(this, bitmap);
            }
        });
        mViewModel.isLoading.observe(this, isLoading -> {
            if (isLoading != null) {
                Log.d(TAG, "loading value: " + isLoading);
                loadingView.setVisibility(isLoading ? View.VISIBLE : View.GONE);
                if (isLoading) {
                    mOutput.setVisibility(View.GONE);
                }
            }
        });
    }

    @BindView(R.id.loading_view)
    ProgressBar loadingView;

    @BindView(R.id.inputview)
    ImageView mInput;

    @BindView(R.id.output)
    PhotoView mOutput;

    @BindView(R.id.chunkinput)
    TextView mChunkInput;

    @OnClick(R.id.startBtn)
    void start() {
        observe();

        if (mInput.getDrawable() != null) {
            mViewModel.setChunkSize(Integer.parseInt(mChunkInput.getText().toString()));
            mViewModel.getOutput();
        }
    }

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