package com.tasomaniac.muzei.earthview.data;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import com.tasomaniac.muzei.earthview.App;
import com.tasomaniac.muzei.earthview.R;
import com.tasomaniac.muzei.earthview.data.api.EarthView;
import com.tasomaniac.muzei.earthview.data.prefs.BooleanPreference;
import com.tasomaniac.muzei.earthview.data.prefs.EarthViewPrefs;
import com.tasomaniac.muzei.earthview.data.prefs.RotateInterval;
import com.tasomaniac.muzei.earthview.data.prefs.StringPreference;
import com.tasomaniac.muzei.earthview.data.prefs.WiFiOnly;
import dagger.Module;
import dagger.Provides;
import dagger.Reusable;

@Module
public final class DataModule {
    private static final String DEFAULT_ROTATE_INTERVAL = "24";
    private static final EarthView FIRST_EARTH_VIEW
            = new EarthView("http://earthview.withgoogle.com/_api/istanbul-turkey-1888.json", null, null);

    @Provides
    static SharedPreferences provideSharedPreferences(App app) {
        return PreferenceManager.getDefaultSharedPreferences(app);
    }

    @Provides
    @Reusable
    @RotateInterval
    static StringPreference provideRotateIntervalPreference(App app, SharedPreferences prefs) {
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
    @WiFiOnly
    static BooleanPreference providesWiFiOnlyPreference(App app, SharedPreferences sharedPreferences) {
        return new BooleanPreference(sharedPreferences, app.getString(R.string.pref_key_wifi_only));
    }

    @Provides
    @WiFiOnly
    static Boolean providesWiFiOnly(@WiFiOnly BooleanPreference pref) {
        return pref.get();
    }

    @Provides
    static EarthViewPrefs earthViewPrefs(SharedPreferences sharedPreferences) {
        return new EarthViewPrefs(sharedPreferences, FIRST_EARTH_VIEW);
    }
}
