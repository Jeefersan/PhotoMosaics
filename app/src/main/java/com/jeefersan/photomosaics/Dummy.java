package com.jeefersan.photomosaics;

import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.module.AppGlideModule;

@GlideModule
public class Dummy  extends AppGlideModule {
}


//    int i = 0;
//    int j = 0;
//                for (int y = 0; y < yChunks; y++) {
//
//        for (int x = 0; x < xChunks; x++) {
//        paint.setColor(lista[y][x]);
//        while (i < CHUNKSIZE) {
//        while (j < CHUNKSIZE) {
//        canvas.drawCircle((x*50) +(x+j), (y*50)+(y+i), 10, paint);
//        j++;
//        }
//        i++;
//        }
//        }
//        }