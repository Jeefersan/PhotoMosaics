package com.jeefersan.photomosaics.utils;

import android.content.Context;
import android.os.Environment;
import android.widget.Toast;

import com.jeefersan.photomosaics.Constants;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by JeeferSan on 6-2-20.
 */
public class Utils {

    public static Boolean isInRange(int x) {
        if (x < Constants.MIN_VALUE || x > Constants.MAX_VALUE) {
            return false;
        }
        return true;
    }

    public static void showToast(Context context){
        Toast.makeText(context,"Please enter value between " + Constants.MIN_VALUE + "-" +
                Constants.MAX_VALUE, Toast.LENGTH_SHORT).show();
    }

    public static File createImg(Context context) throws IOException {
        String timeStamp = new SimpleDateFormat(Constants.DATE_FORMAT).format(new Date());
        String imgFileName = "PhotoMosaic_" + timeStamp + "_";
        File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File img = File.createTempFile(
                imgFileName,
                ".jpg",
                storageDir
        );

        return img;
    }



}
