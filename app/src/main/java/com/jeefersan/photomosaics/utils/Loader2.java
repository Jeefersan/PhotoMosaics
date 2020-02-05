//package com.jeefersan.photomosaics.utils;
//
//import android.content.Context;
//import android.graphics.Bitmap;
//import android.graphics.Canvas;
//import android.graphics.Color;
//import android.graphics.Paint;
//import android.graphics.drawable.BitmapDrawable;
//import android.graphics.drawable.Drawable;
//import android.os.Handler;
//import android.os.Looper;
//import android.util.Log;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.lifecycle.MutableLiveData;
//
//import com.bumptech.glide.Glide;
//import com.bumptech.glide.request.Request;
//import com.bumptech.glide.request.target.CustomTarget;
//import com.bumptech.glide.request.target.SizeReadyCallback;
//import com.bumptech.glide.request.target.Target;
//import com.bumptech.glide.request.transition.Transition;
//import com.jeefersan.photomosaics.Configs;
//import com.jeefersan.photomosaics.R;
//import com.jeefersan.photomosaics.ui.MainActivity;
//
//import java.lang.reflect.Field;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.concurrent.Callable;
//import java.util.concurrent.ExecutionException;
//import java.util.concurrent.FutureTask;
//
//
//public class Loader {
//    public MutableLiveData<Bitmap> mutableLiveData = new MutableLiveData<>();
//    private MutableLiveData<Bitmap> output = new MutableLiveData<>();
//
//    private final String TAG = "Loader";
//    private static int CHUNKSIZE = Configs.CHUNKSIZE;
//
//    private Context mContext;
//    private List<Bitmap> mBitmapChunks;
//
//    private int[][] mColorAvgArray;
//    private Handler mUiHandler = new Handler(Looper.getMainLooper());
//
//    private int w, h, xChunks, yChunks;
//
//    private Field[] drawableFields;
//    private Drawable[] drawables;
//    private Bitmap mBitmap;
//
//    //image 900x1200
//    String imageUrl = "https://i.imgur.com/xdnMi.jpg";
//
//    //pikachu 600x600
////    String imageUrl = "https://i.imgur.com/2ETw6qO.jpg";
//
//    public Loader(Context mContext) {
//        this.mContext = mContext;
//    }
//
//    public MutableLiveData<Bitmap> getMutableLiveData() {
//        fetchImage();
//        return mutableLiveData;
//    }
//
//    public void fetchImage() {
//        Log.d(TAG, "fetchimg");
//
//        Glide.with(mContext)
//                .asBitmap()
//                .load(imageUrl)
//                .into(new CustomTarget<Bitmap>() {
//                    @Override
//                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
//                        mBitmap=resource;
//                        Log.d(TAG,"onResourecReady");
//                        getOutput(mBitmap,false);
//                    }
//
//                    @Override
//                    public void onLoadCleared(@Nullable Drawable placeholder) {
//                        Log.d(TAG,"onLoadCleared");
//                    }
//                });
//
//
//    }
//
//    private List<Bitmap> splitImage(Bitmap bitmap) {
//
//        mBitmapChunks = new ArrayList<>();
//
//        w = bitmap.getWidth();
//        h = bitmap.getHeight();
//
//        xChunks = (int) Math.ceil((double) w / (double) CHUNKSIZE);
//        yChunks = (int) Math.ceil((double) h / (double) CHUNKSIZE);
//
//        Log.d(TAG, "xchunks: " + xChunks);
//        Log.d(TAG, "ychunks: " + yChunks);
//
//
//        for (int y = 0; y < yChunks; y++) {
//            for (int x = 0; x < xChunks; x++) {
//                mBitmapChunks.add(Bitmap.createBitmap(bitmap, x * CHUNKSIZE, y * CHUNKSIZE, CHUNKSIZE, CHUNKSIZE));
//            }
//        }
//
//        return mBitmapChunks;
//    }
//
//    private Drawable[] getDrawables() {
//        drawableFields = R.raw.class.getFields();
//
//        if (drawables == null) {
//            drawables = new Drawable[drawableFields.length];
//        }
//        for (int i = 0; i < drawableFields.length; i++) {
//            try {
//                drawables[i] = mContext.getDrawable(drawableFields[i].getInt(null));
//            } catch (IllegalAccessException e) {
//                e.printStackTrace();
//            }
//
//        }
//        Log.d(TAG, "getDrawables: " + drawables[0]);
//
//        return drawables;
//    }
//
//    private HashMap<Integer, Bitmap> bmpToMap(Drawable[] drawablesArr) {
//        HashMap<Integer, Bitmap> map = new HashMap<>();
//        Bitmap[] bmpArr = toBitmapArr(drawablesArr);
//        int avgColor;
//
//        for (Bitmap b : bmpArr) {
//            avgColor = BitmapUtils.getAvgColorInt(b);
//            map.put(avgColor, b);
//        }
//
//        return map;
//    }
//
//    private Bitmap scaleImg(Bitmap bmp) {
//        return Bitmap.createScaledBitmap(bmp, CHUNKSIZE, CHUNKSIZE, false);
//    }
//
//    private double getColorDiff(int c1, int c2) {
//        int r1 = Color.red(c1);
//        int r2 = Color.red(c2);
//        int g1 = Color.green(c1);
//        int g2 = Color.green(c2);
//        int b1 = Color.blue(c1);
//        int b2 = Color.blue(c2);
//
//        return Math.pow(r2 - r1, 2) + Math.pow(g2 - g1, 2) + Math.pow(b2 - b1, 2);
//
//    }
//
//    private Bitmap[][] toClosestBmpArray(int[][] colorAvgArr, HashMap<Integer, Bitmap> map) {
//        double smallest;
//        double tmp;
//        Bitmap bit;
//        int key = -1;
//
//        Bitmap[][] bmpArr = new Bitmap[colorAvgArr.length][colorAvgArr[0].length];
//
//        for (int i = 0; i < bmpArr.length; i++) {
//            for (int j = 0; j < bmpArr[0].length; j++) {
//                smallest = Float.MAX_VALUE;
//
//                for (int k : map.keySet()) {
//                    tmp = getColorDiff(colorAvgArr[i][j], k);
//
//                    if (tmp < smallest) {
//                        smallest = tmp;
//                        key = k;
//                    }
//                }
//                if (key != -1) {
//                    bit = scaleImg(map.get(key));
//                    bmpArr[i][j] = bit;
//                }
//
//            }
//        }
//
//        return bmpArr;
//
//    }
//
//    private Bitmap[] toBitmapArr(Drawable[] drawablesArr) {
//        Bitmap[] bmpArr = new Bitmap[drawablesArr.length];
//
//        BitmapDrawable b = null;
//
//        for (int i = 0; i < bmpArr.length; i++) {
//            b = (BitmapDrawable) drawablesArr[i];
//            bmpArr[i] = b.getBitmap();
//        }
//
//        return bmpArr;
//    }
//
//
//    private int[][] getColorAvgArray(List<Bitmap> list) {
//
//
//        mColorAvgArray = new int[yChunks][xChunks];
//
//        int i = 0;
//        for (int y = 0; y < yChunks; y++) {
//            for (int x = 0; x < xChunks; x++) {
//                mColorAvgArray[y][x] = BitmapUtils.getAvgColorInt(list.get(i));
//                i++;
//            }
//        }
//
//        Log.d(TAG, "mColorAvgArray size = " + mColorAvgArray.length * mColorAvgArray[0].length);
//
//        return mColorAvgArray;
//    }
//
//    private void toMosaic(Bitmap[][] bmpArr) {
//        Bitmap[][] finalBmpArr = bmpArr;
//
//        Bitmap mosaic = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
//
//        Callable<Bitmap> callable = new Callable<Bitmap>() {
//            @Override
//            public Bitmap call() throws Exception {
//                Bitmap m = mosaic;
//                Canvas canvas = new Canvas(mosaic);
//                Paint paint = new Paint();
//                Bitmap bmp = null;
//
//                for (int i = 0; i < finalBmpArr.length; i++) {
//                    for (int j = 0; j < finalBmpArr[0].length; j++) {
//                        bmp = finalBmpArr[i][j];
//                        canvas.drawBitmap(bmp, j * CHUNKSIZE, i * CHUNKSIZE, paint);
//                    }
//                }
//
//                return m;
//            }
//        };
//
//        FutureTask<Bitmap> future = new FutureTask<Bitmap>(callable);
//        future.run();
//
//        Bitmap bit;
//
//        try {
//            bit = future.get();
//            if (bit != null) {
//                Log.d(TAG, "Succes");
//
//                mUiHandler.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        output.setValue(bit);
//                    }
//                });
//
//            }
//        } catch (ExecutionException e) {
//            e.printStackTrace();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//
//
//    }
//
//    public MutableLiveData<Bitmap> getOutput(Bitmap b, boolean isPix) {
//
//        Bitmap bitmap = b;
//
//
//        int[][] colorAvgArray = getColorAvgArray(splitImage(BitmapUtils.resize(bitmap)));
//
//        if (!isPix) {
//            Thread t = new Thread();
//
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    Drawable[] drawables = getDrawables();
//                    HashMap<Integer, Bitmap> map = bmpToMap(drawables);
//                    Bitmap[][] bmpArray = toClosestBmpArray(colorAvgArray, map);
//                    toMosaic(bmpArray);
//                    Log.d(TAG, "getOutput is ok");
//
//                }
//            }).start();
//
//            return output;
//
//        }
//        toPixelate(colorAvgArray);
//        return output;
//
//    }
//
//    private void toPixelate(int[][] avgArray) {
//        int[][] colorArr = avgArray;
//
//        Bitmap image = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
//
//        Callable<Bitmap> callable = () -> {
//            Bitmap s = image;
//            Canvas canvas = new Canvas(s);
//            canvas.drawARGB(255, 0, 0, 0);
//
//            Paint paint = new Paint();
//            paint.setAntiAlias(true);
//            int a = CHUNKSIZE;
//            for (int y = 0; y < colorArr.length; y++) {
//                int b = y * CHUNKSIZE;
//                for (int x = 0; x < colorArr[0].length; x++) {
//                    paint.setColor(colorArr[y][x]);
//                    int left = x * a;
//                    int top = y * a;
//                    int right = x * a + a;
//                    int bottom = b + y * a;
//                    canvas.drawRect(left, top, right, bottom, paint);
//                }
//            }
//            return s;
//        };
//
//        FutureTask<Bitmap> future = new FutureTask<Bitmap>(callable);
//        future.run();
//
//        if (future.isDone()) {
//            Log.d(TAG, "future is done");
//
//            Bitmap beet = null;
//
//            try {
//                beet = future.get();
//                output.setValue(beet);
//            } catch (ExecutionException e) {
//                e.printStackTrace();
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
//
//
//    }
//}