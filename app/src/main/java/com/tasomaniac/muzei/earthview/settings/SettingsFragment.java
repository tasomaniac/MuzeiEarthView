/*
 * Copyright 2013 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.tasomaniac.muzei.earthview.settings;

import android.Manifest;
import android.app.backup.BackupManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.view.View;

import com.google.android.apps.muzei.api.MuzeiArtSource;
import com.tasomaniac.android.widget.IntegrationPreference;
import com.tasomaniac.muzei.earthview.BuildConfig;
import com.tasomaniac.muzei.earthview.EarthViewArtSource;
import com.tasomaniac.muzei.earthview.R;
import com.tasomaniac.muzei.earthview.data.Injector;
import com.tasomaniac.muzei.earthview.data.prefs.RotateInterval;
import com.tasomaniac.muzei.earthview.data.prefs.StringPreference;

import javax.inject.Inject;

import timber.log.Timber;

public class SettingsFragment extends PreferenceFragmentCompat
        implements SharedPreferences.OnSharedPreferenceChangeListener,
        ActivityCompat.OnRequestPermissionsResultCallback,
        Preference.OnPreferenceClickListener {

    private static final String ACTION_HANDLE_COMMAND = "com.google.android.apps.muzei.api.action.HANDLE_COMMAND";
    private static final String EXTRA_COMMAND_ID = "com.google.android.apps.muzei.api.extra.COMMAND_ID";

    private static final String LAUNCHER_ACTIVITY_NAME = "com.tasomaniac.muzei.earthview.ui.MainActivity";

    /**
     * Id to identify a storage permission request.
     */
    private static final int REQUEST_STORAGE = 0;

    @Inject @RotateInterval
    StringPreference rotateIntervalPref;

    private IntegrationPreference muzeiPref;
    private IntegrationPreference permissionPref;
    private PreferenceCategory integrationsCategory;

    private boolean permissionDeniedDefinitely = false;
    private boolean forceShowPermissionDialog;

    public SettingsFragment() {
    }

    public static SettingsFragment newInstance(boolean forceShowPermission) {
        SettingsFragment fragment = new SettingsFragment();
        Bundle args = new Bundle();
        args.putBoolean(SettingsActivity.EXTRA_FROM_BACKGROUND, forceShowPermission);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        forceShowPermissionDialog = getArguments().getBoolean(SettingsActivity.EXTRA_FROM_BACKGROUND);
    }

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        Injector.obtain(getContext()).inject(this);
        addPreferencesFromResource(R.xml.pref_general);

        bindPreferenceSummaryToValue(
                findPreference(R.string.pref_key_rotate_interval), rotateIntervalPref);

        muzeiPref = (IntegrationPreference) findPreference(R.string.pref_key_muzei_integration);
        muzeiPref.setPersistent(true);
        integrationsCategory = (PreferenceCategory) findPreference(R.string.pref_key_category_integrations);
    }

    private void addPermissionPreference() {
        if (permissionPref == null) {
            permissionPref = new IntegrationPreference(getContext());
            permissionPref.setTitle(R.string.permission_storage_title);
            permissionPref.setSummaryOn(R.string.permission_storage_rationale);
            permissionPref.setChecked(true);
            permissionPref.setOnPreferenceClickListener(this);
            if (integrationsCategory != null) {
                integrationsCategory.addPreference(permissionPref);
            }
        }
    }

    private void requestStoragePermission() {
        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                REQUEST_STORAGE);
    }

    public Preference findPreference(@StringRes int keyResource) {
        return findPreference(getString(keyResource));
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceManager().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
        muzeiPref.resume();

        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            Timber.i("Storage permission has NOT been granted. Requesting permission.");

            boolean showRequestPermissionRationale = ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (showRequestPermissionRationale && !forceShowPermissionDialog) {
                // Provide an additional rationale to the user if the permission was not granted
                // and the user would benefit from additional context for the use of the permission.
                // For example if the user has previously denied the permission.
                Timber.i("Displaying storage permission rationale to provide additional context.");
                addPermissionPreference();
            } else {
                // Storage permission has not been granted yet. Request it directly.
                requestStoragePermission();
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceManager().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
        muzeiPref.pause();
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {

        if (permissionDeniedDefinitely) {
            Uri packageURI = Uri.parse("package:" + BuildConfig.APPLICATION_ID);
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, packageURI);
            startActivity(intent);
        } else {
            requestStoragePermission();
            permissionDeniedDefinitely = true;
        }


        return true;
    }

    @NonNull
    @Override
    @SuppressWarnings("ConstantConditions")
    public View getView() {
        return super.getView();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        new BackupManager(getActivity()).dataChanged();

        // Potentially enable/disable the launcher activity if the settings button
        // preference has changed.
        final String launcherIntentKey = getString(R.string.pref_key_launcher_intent);
        if (isAdded() && launcherIntentKey.equals(s)) {

            final boolean hideLauncher = sharedPreferences.getBoolean(launcherIntentKey, false);
            getActivity().getPackageManager().setComponentEnabledSetting(
                    new ComponentName(
                            getActivity().getPackageName(),
                            LAUNCHER_ACTIVITY_NAME),
                    hideLauncher
                            ? PackageManager.COMPONENT_ENABLED_STATE_DISABLED
                            : PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                    PackageManager.DONT_KILL_APP);
        }
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_STORAGE) {
            // Received permission result for storage permission.
            Timber.i("Received response for storage permission request.");

            // Check if the only required permission has been granted
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // storage permission has been granted, preview can be displayed
                Timber.i("storage permission has now been granted. Showing preview.");
                Snackbar.make(getView(), R.string.permision_available_storage,
                        Snackbar.LENGTH_SHORT).show();

                if (permissionPref != null) {
                    integrationsCategory.removePreference(permissionPref);
                    permissionPref = null;
                }

                forceNextMuzeiArtwork();

                if (forceShowPermissionDialog) {
                    getActivity().finish();
                }
            } else {
                Timber.i("storage permission was NOT granted.");
                addPermissionPreference();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
        forceShowPermissionDialog = false;
    }

    private void forceNextMuzeiArtwork() {
        getActivity().startService(new Intent(ACTION_HANDLE_COMMAND)
                .setComponent(new ComponentName(getActivity(), EarthViewArtSource.class))
                .putExtra(EXTRA_COMMAND_ID, MuzeiArtSource.BUILTIN_COMMAND_ID_NEXT_ARTWORK));
    }

    /**
     * A preference value change listener that updates the preference's summary to reflect its new
     * value.
     */
    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener
            = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();

            if (preference instanceof ListPreference) {
                // For list preferences, look up the correct display value in
                // the preference's 'entries' list.
                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(stringValue);

                // Set the summary to reflect the new value.
                preference.setSummary(
                        index >= 0
                                ? (listPreference.getEntries()[index])
                                .toString().replaceAll("%", "%%")
                                : null);

            } else {
                // For all other preferences, set the summary to the value's
                // simple string representation.
                preference.setSummary(stringValue);
            }
            return true;
        }
    };

    /**
     * Binds a preference's summary to its value. More specifically, when the preference's value is
     * changed, its summary (line of text below the preference title) is updated to reflect the
     * value. The summary is also immediately updated upon calling this method. The exact display
     * format is dependent on the type of preference.
     */
    public static void bindPreferenceSummaryToValue(Preference preference, StringPreference pref) {
        setAndCallPreferenceChangeListener(preference, sBindPreferenceSummaryToValueListener, pref);
    }

    /**
     * When the preference's value is changed, trigger the given listener. The listener is also
     * immediately called with the preference's current value upon calling this method.
     */
    public static void setAndCallPreferenceChangeListener(Preference preference,
                                                          Preference.OnPreferenceChangeListener listener,
                                                          StringPreference pref) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(listener);

        // Trigger the listener immediately with the preference's
        // current value.
        listener.onPreferenceChange(preference, pref.get());
    }

}
