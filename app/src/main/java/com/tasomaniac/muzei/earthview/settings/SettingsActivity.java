package com.tasomaniac.muzei.earthview.settings;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

import com.tasomaniac.muzei.earthview.R;

public class SettingsActivity extends AppCompatActivity {

    public static final String EXTRA_REQUEST_PERMISSION = "request_permission";

    public static Intent createForPermissionRequest(Context context) {
        return new Intent(context, SettingsActivity.class)
                .putExtra(SettingsActivity.EXTRA_REQUEST_PERMISSION, true)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_action_done);
        toolbar.setNavigationContentDescription(R.string.done);
        setSupportActionBar(toolbar);

        CollapsingToolbarLayout collapsingToolbar = findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(getString(R.string.settings_label));

        if (savedInstanceState == null) {
            boolean forceShowPermission = getIntent().getBooleanExtra(EXTRA_REQUEST_PERMISSION, false);
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.fragment_container, SettingsFragment.newInstance(forceShowPermission))
                    .commitNow();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            if (prefs.getBoolean(getString(R.string.pref_key_muzei_integration), false)) {
                Toast.makeText(this, R.string.error_install_muzei, Toast.LENGTH_LONG).show();
            } else {
                finish();
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
