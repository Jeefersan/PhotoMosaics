package com.jeefersan.photomosaics.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.jeefersan.photomosaics.R;

/**
 * Created by JeeferSan on 13-2-20.
 */
public class SplashScreen extends AppCompatActivity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        startActivity(new Intent(this,MainActivity.class));
        finish();

    }
}
