package com.tasomaniac.muzei.earthview.data;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.tasomaniac.muzei.earthview.R;
import com.tasomaniac.muzei.earthview.data.prefs.BooleanPreference;
import com.tasomaniac.muzei.earthview.data.prefs.DownloadUrl;
import com.tasomaniac.muzei.earthview.data.prefs.NextEarthView;
import com.tasomaniac.muzei.earthview.data.prefs.RotateInterval;
import com.tasomaniac.muzei.earthview.data.prefs.StringPreference;
import com.tasomaniac.muzei.earthview.data.prefs.WiFiOnly;

import dagger.Module;
import dagger.Provides;
import dagger.Reusable;

@Module
public final class DataModule {
    private static final String DEFAULT_ROTATE_INTERVAL = "24";

    private static final String PREF_KEY_NEXT_EARTH_VIEW = "next_earth_view";
    private static final String PREF_KEY_MAPS_LINK = "maps_link";
    private static final String PREF_KEY_DOWNLOAD_URL = "download_url";

    private static final String FIRST_EARTH_VIEW
            = "http://earthview.withgoogle.com/_api/istanbul-turkey-1888.json";

    @Provides
    static SharedPreferences provideSharedPreferences(Application app) {
        return PreferenceManager.getDefaultSharedPreferences(app);
    }

    @Provides
    @Reusable
    @RotateInterval
    static StringPreference provideRotateIntervalPreference(Application app,
                                                     SharedPreferences prefs) {
        return new StringPreference(
                prefs,
                app.getString(R.string.pref_key_rotate_interval),
                DEFAULT_ROTATE_INTERVAL
        );
    }

    @Provides
    @RotateInterval
    static String provideRotateInterval(@RotateInterval StringPreference pref) {
        return pref.get();
    }

    @Provides
    @Reusable
    @DownloadUrl
    static StringPreference provideDownloadUrlPreference(SharedPreferences prefs) {
        return new StringPreference(prefs, PREF_KEY_DOWNLOAD_URL);
    }

    @Provides
    @Reusable
    @MapsLink
    static StringPreference provideMapsLinkPreference(SharedPreferences prefs) {
        return new StringPreference(prefs, PREF_KEY_MAPS_LINK);
    }

    @Provides
    @Reusable
    @NextEarthView
    static StringPreference provideNextEarthViewPreference(SharedPreferences prefs) {
        return new StringPreference(prefs, PREF_KEY_NEXT_EARTH_VIEW, FIRST_EARTH_VIEW);
    }

    @Provides
    @Reusable
    @WiFiOnly
    static BooleanPreference providesWiFiOnlyPreference(Application app, SharedPreferences sharedPreferences) {
        return new BooleanPreference(sharedPreferences, app.getString(R.string.pref_key_wifi_only));
    }

    @Provides
    @WiFiOnly
    static Boolean providesWiFiOnly(@WiFiOnly BooleanPreference pref) {
        return pref.get();
    }
}
