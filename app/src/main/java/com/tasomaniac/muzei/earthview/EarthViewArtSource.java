package com.tasomaniac.muzei.earthview;

import android.Manifest;
import android.app.DownloadManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.content.ContextCompat;
import android.text.format.DateUtils;
import android.widget.Toast;

import com.google.android.apps.muzei.api.Artwork;
import com.google.android.apps.muzei.api.RemoteMuzeiArtSource;
import com.google.android.apps.muzei.api.UserCommand;
import com.tasomaniac.muzei.earthview.data.Injector;
import com.tasomaniac.muzei.earthview.data.MapsLink;
import com.tasomaniac.muzei.earthview.data.api.ApiModule;
import com.tasomaniac.muzei.earthview.data.api.EarthView;
import com.tasomaniac.muzei.earthview.data.api.EarthViewApi;
import com.tasomaniac.muzei.earthview.data.prefs.DownloadUrl;
import com.tasomaniac.muzei.earthview.data.prefs.NextEarthView;
import com.tasomaniac.muzei.earthview.data.prefs.RotateInterval;
import com.tasomaniac.muzei.earthview.data.prefs.StringPreference;
import com.tasomaniac.muzei.earthview.data.prefs.WiFiOnly;
import com.tasomaniac.muzei.earthview.settings.SettingsActivity;

import javax.inject.Inject;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Response;

public class EarthViewArtSource extends RemoteMuzeiArtSource {
    private static final String SOURCE_NAME = "EarthViewArtSource";

    private static final int COMMAND_ID_SHARE = 1;
    private static final int COMMAND_ID_DOWNLOAD = 2;
    private static final int COMMAND_ID_VIEW_IN_GOOGLE_MAPS = 3;
    private static final int COMMAND_ID_EARTH_VIEW_MAIN = 4;
    private static final int COMMAND_ID_DEBUG_INFO = 51;

    @Inject @RotateInterval String rotateInterval;
    @Inject @DownloadUrl StringPreference downloadUrlPref;
    @Inject @MapsLink StringPreference mapsLinkPref;
    @Inject @NextEarthView StringPreference nextEarthViewPref;
    @Inject @WiFiOnly Boolean wifiOnly;

    @Inject EarthViewApi earthViewApi;

    public EarthViewArtSource() {
        super(SOURCE_NAME);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Injector.obtain(this).inject(this);

        List<UserCommand> commands = new ArrayList<>();
        commands.add(new UserCommand(BUILTIN_COMMAND_ID_NEXT_ARTWORK));

        commands.add(new UserCommand(COMMAND_ID_SHARE, getString(R.string.action_share_artwork)));
        commands.add(new UserCommand(COMMAND_ID_DOWNLOAD, getString(R.string.action_download)));
        commands.add(new UserCommand(COMMAND_ID_VIEW_IN_GOOGLE_MAPS, getString(R.string.action_view_in_google_maps)));
        commands.add(new UserCommand(COMMAND_ID_EARTH_VIEW_MAIN, getString(R.string.action_open_earth_view)));
        if (BuildConfig.DEBUG) {
            commands.add(new UserCommand(COMMAND_ID_DEBUG_INFO, "Debug info"));
        }
        setUserCommands(commands);
    }

    @Override
    protected void onTryUpdate(int reason) throws RetryException {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            //Start Settings Activity to request permissions.
            startActivity(new Intent(this, SettingsActivity.class)
                    .putExtra(SettingsActivity.EXTRA_FROM_BACKGROUND, true)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            return;
        }

        // Skip the update only when
        // it is a scheduled request,
        // wifi only setting is on
        // and user is not connected to wifi
        if (reason == UPDATE_REASON_SCHEDULED && wifiOnly && !isWifi()) {
            scheduleUpdate(System.currentTimeMillis() + DateUtils.HOUR_IN_MILLIS);
            return;
        }

        loadNextEarthView(nextEarthViewPref.get());
    }

    private void loadNextEarthView(String nextEarthViewUrl) throws RetryException {
        try {
            final Response<EarthView> response = earthViewApi.nextEarthView(nextEarthViewUrl).execute();

            if (response.isSuccessful()) {
                EarthView earthView = response.body();

                updatePrefsForNextEarthView(earthView);

                publishArtwork(createArtworkFrom(earthView));

                long nextUpdateTimeInMillis = nextUpdateTimeInMillis();
                if (nextUpdateTimeInMillis != 0) {
                    scheduleUpdate(System.currentTimeMillis() + nextUpdateTimeInMillis);
                } else {
                    unscheduleUpdate();
                }
            }
        } catch (IOException e) {
            throw new RetryException(e);
        }
    }

    private void updatePrefsForNextEarthView(EarthView earthView) {
        nextEarthViewPref.set(earthView.getNextApi());
        mapsLinkPref.set(earthView.getMapsLink());
        downloadUrlPref.set(earthView.getDownloadUrl());
    }

    private Artwork createArtworkFrom(EarthView earthView) {
        return new Artwork.Builder()
                .title(earthView.getTitle())
                .byline(earthView.getAttribution())
                .imageUri(Uri.parse(earthView.getPhotoUrl()))
                .token(String.valueOf(earthView.getId()))
                .viewIntent(new Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse(ApiModule.BASE_URL + earthView.getUrl())
                ))
                .build();
    }

    private long nextUpdateTimeInMillis() {
        try {
            return Integer.parseInt(rotateInterval) * DateUtils.HOUR_IN_MILLIS;
        } catch (NumberFormatException e) {
            return DateUtils.DAY_IN_MILLIS;
        }
    }

    @Override
    protected void onCustomCommand(int id) {
        super.onCustomCommand(id);

        switch (id) {
            case COMMAND_ID_SHARE:
                shareArtwork();
                break;
            case COMMAND_ID_DOWNLOAD:
                downloadArtwork();
                break;
            case COMMAND_ID_VIEW_IN_GOOGLE_MAPS:
                openInGoogleMaps();
                break;
            case COMMAND_ID_EARTH_VIEW_MAIN:
                openInGoogleEarthView();
                break;
            case COMMAND_ID_DEBUG_INFO:
                displayDebugInfo();
                break;
        }
    }

    private void shareArtwork() {
        if (!checkValidArtwork(R.string.error_no_image_to_share)) {
            return;
        }

        String detailUrl = "https://g.co/ev/" + getCurrentArtwork().getToken();

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, "My Android wallpaper today is '"
                + getCurrentArtwork().getTitle().trim()
                + ". #MuzeiEarthView\n\n"
                + detailUrl);
        shareIntent = Intent.createChooser(shareIntent, "Share artwork");
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(shareIntent);
    }

    private void downloadArtwork() {
        if (!checkValidArtwork(R.string.error_no_image_to_download)) {
            return;
        }

        final String downloadUrl = downloadUrlPref.get();
        if (downloadUrl != null) {
            DownloadManager dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
            DownloadManager.Request request = new DownloadManager.Request(
                    Uri.parse(ApiModule.BASE_URL + downloadUrl))
                    .setDestinationInExternalPublicDir(
                            Environment.DIRECTORY_PICTURES,
                            getCurrentArtwork().getToken() + ".jpg")
                    .setMimeType("image/*")
                    .setVisibleInDownloadsUi(true)
                    .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            request.allowScanningByMediaScanner();
            dm.enqueue(request);
        }
    }

    private boolean checkValidArtwork(final int errorMessage) {
        Artwork currentArtwork = getCurrentArtwork();
        if (currentArtwork == null) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(EarthViewArtSource.this,
                                   errorMessage,
                                   Toast.LENGTH_SHORT).show();
                }
            });
            return false;
        }
        return true;
    }

    private void openInGoogleMaps() {
        final String mapsLink = mapsLinkPref.get();
        if (mapsLink != null) {
            Intent viewArchiveIntent = new Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(mapsLink)
            );
            viewArchiveIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            try {
                startActivity(viewArchiveIntent);
            } catch (ActivityNotFoundException ignored) {
            }
        }
    }

    private void openInGoogleEarthView() {
        Intent viewArchiveIntent = new Intent(
                Intent.ACTION_VIEW,
                Uri.parse("http://earthview.withgoogle.com/")
        );
        viewArchiveIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        try {
            startActivity(viewArchiveIntent);
        } catch (ActivityNotFoundException ignored) {
        }
    }

    private void displayDebugInfo() {
        long nextUpdateTimeMillis = getSharedPreferences()
                .getLong("scheduled_update_time_millis", 0);
        final String nextUpdateTime;
        if (nextUpdateTimeMillis > 0) {
            Date d = new Date(nextUpdateTimeMillis);
            nextUpdateTime = SimpleDateFormat.getDateTimeInstance().format(d);
        } else {
            nextUpdateTime = "None";
        }

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(EarthViewArtSource.this,
                               "Next update time: " + nextUpdateTime,
                               Toast.LENGTH_LONG).show();
            }
        });
    }

    public boolean isWifi() {
        NetworkInfo ni = ((ConnectivityManager) getSystemService(
                Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        return ni != null && ni.getType() == ConnectivityManager.TYPE_WIFI;
    }
}
