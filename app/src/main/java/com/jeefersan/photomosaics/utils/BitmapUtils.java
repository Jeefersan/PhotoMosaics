package com.jeefersan.photomosaics.utils;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.jeefersan.photomosaics.Constants;

import java.util.HashMap;

public class BitmapUtils {

    private BitmapUtils() {
    }

    public static int getAvgColorInt(Bitmap bmp) {
        int w = bmp.getWidth();
        int h = bmp.getHeight();
        int[] pixels = new int[w * h];
        int size = pixels.length;

        bmp.getPixels(pixels, 0, w, 0, 0, w, h);

        long r = 0L;
        long g = 0L;
        long b = 0L;

        for (int p : pixels) {
            r += Color.red(p);
            g += Color.green(p);
            b += Color.blue(p);
        }

        return Color.rgb((int) r / size, (int) g / size, (int) b / size);
    }

    public static Bitmap scaleDown(Bitmap b) {
        Bitmap bit = b;
        double w = bit.getWidth();
        double h = bit.getHeight();
        Log.d("bmputils", "before mbitmap w h = " + bit.getWidth() + ", " + bit.getHeight());
        double scaleFactor = (w * h) / Constants.MAX_RESOLUTION;
        Log.d("Bmputils","scalefactor: " + scaleFactor);

        int w2 = (int)(w/scaleFactor);
        int h2 = (int)(h/scaleFactor);

        Log.d("BmpUtils", "scaling..");
        Bitmap.createScaledBitmap(bit, w2,h2, false);



        Log.d("bmputils", "after mbitmap w h = " + bit.getWidth() + ", " + bit.getHeight());
        return bit;
    }

    public static boolean isTooBig(Bitmap b) {
        if (b.getHeight() * b.getWidth() > Constants.MAX_RESOLUTION) {
            Log.d("BmpUtils", "is too big: true");
            return true;
        }
        return false;
    }

    public static Bitmap resize(Bitmap b, int chunkSize) {

        int w2 = b.getWidth() - (b.getWidth() % chunkSize);
        int h2 = b.getHeight() - (b.getHeight() % chunkSize);

        return Bitmap.createScaledBitmap(b, w2, h2, false);

    }

    public static Bitmap[] toBitmapArr(Drawable[] drawablesArr) {
        Bitmap[] bmpArr = new Bitmap[drawablesArr.length];

        BitmapDrawable b;

        for (int i = 0; i < bmpArr.length; i++) {
            b = (BitmapDrawable) drawablesArr[i];
            bmpArr[i] = b.getBitmap();
        }

        return bmpArr;
    }

    public static HashMap<Integer, Bitmap> bmpToMap(Drawable[] drawablesArr) {
        HashMap<Integer, Bitmap> map = new HashMap<>();
        Bitmap[] bmpArr = toBitmapArr(drawablesArr);
        int avgColor;

        for (Bitmap b : bmpArr) {
            avgColor = BitmapUtils.getAvgColorInt(b);
            map.put(avgColor, b);
        }

        return map;
    }


}

