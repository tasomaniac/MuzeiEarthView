package com.tasomaniac.muzei.earthview.data.api;

import android.app.Application;

import javax.inject.Singleton;
import java.io.File;

import dagger.Module;
import dagger.Provides;
import okhttp3.Cache;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.moshi.MoshiConverterFactory;

@Module
public final class ApiModule {
    public static final String BASE_URL = "http://earthview.withgoogle.com";
    private static final int DISK_CACHE_SIZE = 5 * 1024 * 1024;

    @Provides
    @Singleton
    OkHttpClient provideOkHttpClient(Application app) {
        File cacheDir = new File(app.getCacheDir(), "http");
        Cache cache = new Cache(cacheDir, DISK_CACHE_SIZE);

        return new OkHttpClient.Builder()
                .cache(cache)
                .build();
    }

    @Provides
    @Singleton
    EarthViewApi provideEarthViewApi(OkHttpClient client) {
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(MoshiConverterFactory.create())
                .baseUrl(BASE_URL)
                .client(client)
                .build();
        return retrofit.create(EarthViewApi.class);
    }
}
