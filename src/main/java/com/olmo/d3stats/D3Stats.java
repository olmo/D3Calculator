package com.olmo.d3stats;


import android.app.Application;

import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class D3Stats extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
            .cacheInMemory(true)
            .cacheOnDisc(true)
        .build();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
            .memoryCache(new LruMemoryCache(2 * 1024 * 1024))
            .memoryCacheSize(2 * 1024 * 1024)
            .defaultDisplayImageOptions(defaultOptions)
            .build();
        ImageLoader.getInstance().init(config);
    }
}