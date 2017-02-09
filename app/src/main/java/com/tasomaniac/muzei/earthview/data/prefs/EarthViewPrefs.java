package com.tasomaniac.muzei.earthview.data.prefs;

import android.content.SharedPreferences;

import com.tasomaniac.muzei.earthview.data.api.EarthView;

public class EarthViewPrefs {

    private static final String PREF_KEY_NEXT_EARTH_VIEW = "next_earth_view";
    private static final String PREF_KEY_MAPS_LINK = "maps_link";
    private static final String PREF_KEY_DOWNLOAD_URL = "download_url";

    private final SharedPreferences preferences;
    private final EarthView defaultValue;

    public EarthViewPrefs(SharedPreferences preferences) {
        this(preferences, null);
    }

    public EarthViewPrefs(SharedPreferences preferences, EarthView defaultValue) {
        this.preferences = preferences;
        this.defaultValue = defaultValue;
    }

    public EarthView get() {
        if (isSet()) {
            String nextApi = preferences.getString(PREF_KEY_NEXT_EARTH_VIEW, null);
            String mapsLink = preferences.getString(PREF_KEY_MAPS_LINK, null);
            String downloadUrl = preferences.getString(PREF_KEY_DOWNLOAD_URL, null);
            return new EarthView(nextApi, mapsLink, downloadUrl);
        } else {
            return defaultValue;
        }
    }

    public boolean isSet() {
        return preferences.contains(PREF_KEY_NEXT_EARTH_VIEW);
    }

    public void set(EarthView value) {
        preferences.edit()
                .putString(PREF_KEY_NEXT_EARTH_VIEW, value.getNextApi())
                .putString(PREF_KEY_MAPS_LINK, value.getMapsLink())
                .putString(PREF_KEY_DOWNLOAD_URL, value.getDownloadUrl())
                .apply();
    }

    public void delete() {
        preferences.edit()
                .remove(PREF_KEY_NEXT_EARTH_VIEW)
                .remove(PREF_KEY_MAPS_LINK)
                .remove(PREF_KEY_DOWNLOAD_URL)
                .apply();
    }
}
