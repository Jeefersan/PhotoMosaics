package com.jeefersan.photomosaics.utils;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;

import androidx.core.content.FileProvider;

import com.jeefersan.photomosaics.BuildConfig;
import com.jeefersan.photomosaics.Constants;

import java.io.File;
import java.io.IOException;

/**
 * Created by JeeferSan on 12-2-20.
 */
public class IntentHelper {
    private Context mContext;
    private Uri imgUri;

    public IntentHelper(Context c) {
        mContext=c;
    }

    public Intent getIntent(int requestCode, Uri uri) {
        Intent intent = null;

        if (requestCode == Constants.REQUEST_GALLERY_PHOTO) {
            intent = new Intent(Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }

        if (requestCode == Constants.REQUEST_IMAGE_CAPTURE) {
            intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (intent.resolveActivity(mContext.getPackageManager()) != null) {
                File imgFile = null;
                try {
                    imgFile = Utils.createImg(mContext);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (imgFile != null) {
                    imgUri = FileProvider.getUriForFile(mContext, BuildConfig.APPLICATION_ID + ".provider", imgFile);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, imgUri);

                }
            }

        }

        if(requestCode == Constants.REQUEST_SHARE){
            intent = new Intent(Intent.ACTION_SEND);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(uri,mContext.getContentResolver().getType(uri));
            intent.putExtra(Intent.EXTRA_STREAM,uri);
            intent.setType("image/png");

        }

        return intent;

    }

    public Uri getImgUri() {
        return imgUri;
    }
}