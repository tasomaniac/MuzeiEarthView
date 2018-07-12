package com.tasomaniac.muzei.earthview.data.prefs;

import android.content.SharedPreferences;
import androidx.annotation.NonNull;

public class StringPreference {
    private final SharedPreferences preferences;
    private final String key;
    private final String defaultValue;

    public StringPreference(@NonNull SharedPreferences preferences, @NonNull String key) {
        this(preferences, key, null);
    }

    public StringPreference(@NonNull SharedPreferences preferences, @NonNull String key, String defaultValue) {
        this.preferences = preferences;
        this.key = key;
        this.defaultValue = defaultValue;
    }

    public String get() {
        return preferences.getString(key, defaultValue);
    }

    public boolean isSet() {
        return preferences.contains(key);
    }

    public void set(String value) {
        preferences.edit().putString(key, value).apply();
    }

    public void delete() {
        preferences.edit().remove(key).apply();
    }
}
