package com.jeefersan.photomosaics.utils;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;

import com.jeefersan.photomosaics.Configs;

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

    public static Bitmap resize(Bitmap b) {

        int w2 = b.getWidth() - (b.getWidth() % Configs.CHUNKSIZE);
        int h2 = b.getHeight() - (b.getHeight() % Configs.CHUNKSIZE);

        return Bitmap.createScaledBitmap(b, w2, h2, false);

    }
}

