package com.jeefersan.photomosaics.utils;

import android.app.Application;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.jeefersan.photomosaics.R;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;


public class Loader extends AndroidViewModel {
    private final String TAG = "Loader";
    private int chunkSize;

    private List<Bitmap> mBitmapChunks;

    private int[][] mColorAvgArray;

    private int w, h, xChunks, yChunks;

    private Bitmap mBitmap;
    private Drawable[] drawables;
    private int[][] colorAvgArray;
    private final HashMap<Integer, Bitmap> map;


    public Loader(@NonNull Application application) {
        super(application);
        map = BitmapUtils.bmpToMap(getDrawables());
    }

    private List<Bitmap> splitImage(Bitmap bitmap) {

        mBitmapChunks = new ArrayList<>();

        w = bitmap.getWidth();
        h = bitmap.getHeight();

        xChunks = (int) Math.ceil((double) w / (double) chunkSize);
        yChunks = (int) Math.ceil((double) h / (double) chunkSize);

        Log.d(TAG, "xchunks: " + xChunks);
        Log.d(TAG, "ychunks: " + yChunks);


        for (int y = 0; y < yChunks; y++) {
            for (int x = 0; x < xChunks; x++) {
                mBitmapChunks.add(Bitmap.createBitmap(bitmap, x * chunkSize, y * chunkSize, chunkSize, chunkSize));
            }
        }

        return mBitmapChunks;
    }

    private Drawable[] getDrawables() {
        if (drawables != null) {
            return drawables;
        }
        Field[] drawableFields = R.raw.class.getFields();

        Drawable[] drawables = new Drawable[drawableFields.length];

        for (int i = 0; i < drawableFields.length; i++) {
            try {
                drawables[i] = getApplication().getDrawable(drawableFields[i].getInt(null));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return drawables;
    }


    private Bitmap scaleImg(Bitmap bmp) {
        return Bitmap.createScaledBitmap(bmp, chunkSize, chunkSize, false);
    }

    private double getColorDiff(int c1, int c2) {
        int r1 = Color.red(c1);
        int r2 = Color.red(c2);
        int g1 = Color.green(c1);
        int g2 = Color.green(c2);
        int b1 = Color.blue(c1);
        int b2 = Color.blue(c2);

        return Math.pow(r2 - r1, 2) + Math.pow(g2 - g1, 2) + Math.pow(b2 - b1, 2);

    }

    private Bitmap[][] toClosestBmpArray(int[][] colorAvgArr, HashMap<Integer, Bitmap> map) {
        double smallest;
        double tmp;
        Bitmap bit;
        int key = -1;

        Bitmap[][] bmpArr = new Bitmap[colorAvgArr.length][colorAvgArr[0].length];

        for (int i = 0; i < bmpArr.length; i++) {
            for (int j = 0; j < bmpArr[0].length; j++) {
                smallest = Float.MAX_VALUE;

                for (int k : map.keySet()) {
                    tmp = getColorDiff(colorAvgArr[i][j], k);

                    if (tmp < smallest) {
                        smallest = tmp;
                        key = k;
                    }
                }
                if (key != -1) {
                    bit = scaleImg(map.get(key));
                    bmpArr[i][j] = bit;
                }
            }
        }
        return bmpArr;
    }


    private int[][] getColorAvgArray(List<Bitmap> list) {
        mColorAvgArray = new int[yChunks][xChunks];

        int i = 0;
        for (int y = 0; y < yChunks; y++) {
            for (int x = 0; x < xChunks; x++) {
                mColorAvgArray[y][x] = BitmapUtils.getAvgColorInt(list.get(i));
                i++;
            }
        }

        return mColorAvgArray;
    }

    private Bitmap toMosaic(Bitmap[][] bmpArr) {
        Bitmap[][] finalBmpArr = bmpArr;

        Bitmap mosaic = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);

        Callable<Bitmap> callable = () -> {
            Bitmap m = mosaic;
            Canvas canvas = new Canvas(mosaic);
            Paint paint = new Paint();
            Bitmap bmp;

            for (int i = 0; i < finalBmpArr.length; i++) {
                for (int j = 0; j < finalBmpArr[0].length; j++) {
                    bmp = finalBmpArr[i][j];
                    canvas.drawBitmap(bmp, j * chunkSize, i * chunkSize, paint);
                }
            }
            return m;
        };

        FutureTask<Bitmap> future = new FutureTask<Bitmap>(callable);
        future.run();

        Bitmap bit = null;

        try {
            bit = future.get();

        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return bit;

    }

    public Bitmap getOutput(Bitmap b, int chunkSize, boolean bool) {
        this.chunkSize = chunkSize;
        if (mBitmap != b) {
            this.mBitmap = b;
            colorAvgArray = getColorAvgArray(splitImage(BitmapUtils.resize(b, chunkSize)));
        }

        if (bool) {
            return toPixelate(colorAvgArray);
        }

        Bitmap[][] bmpArray = toClosestBmpArray(colorAvgArray, map);
        return toMosaic(bmpArray);

    }

    private Bitmap toPixelate(int[][] avgArray) {
        int[][] colorArr = avgArray;

        Bitmap image = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);

        Callable<Bitmap> callable = () -> {
            Bitmap s = image;
            Canvas canvas = new Canvas(s);
            canvas.drawARGB(255, 0, 0, 0);

            Paint paint = new Paint();
            paint.setAntiAlias(true);
            int a = chunkSize;
            for (int y = 0; y < colorArr.length; y++) {
                int b = y * chunkSize;
                for (int x = 0; x < colorArr[0].length; x++) {
                    paint.setColor(colorArr[y][x]);
                    int left = x * a;
                    int top = y * a;
                    int right = x * a + a;
                    int bottom = b + y * a;
                    canvas.drawRect(left, top, right, bottom, paint);
                }
            }
            return s;
        };

        FutureTask<Bitmap> future = new FutureTask<Bitmap>(callable);
        future.run();

        Bitmap beet = null;


        try {
            beet = future.get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        return beet;
    }

}