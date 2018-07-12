package com.tasomaniac.muzei.earthview;

import com.google.android.apps.muzei.api.provider.MuzeiArtProvider;
import dagger.android.AndroidInjection;

public class EarthViewArtProvider extends MuzeiArtProvider {

    @Override public boolean onCreate() {
        AndroidInjection.inject(this);
        return super.onCreate();
    }

    @Override protected void onLoadRequested(boolean initial) {

    }
}
