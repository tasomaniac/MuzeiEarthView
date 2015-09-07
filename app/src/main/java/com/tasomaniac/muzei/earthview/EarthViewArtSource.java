package com.tasomaniac.muzei.earthview;

import android.app.DownloadManager;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.google.android.apps.muzei.api.Artwork;
import com.google.android.apps.muzei.api.RemoteMuzeiArtSource;
import com.google.android.apps.muzei.api.UserCommand;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit.MoshiConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;
import timber.log.Timber;

public class EarthViewArtSource extends RemoteMuzeiArtSource {
    private static final String SOURCE_NAME = "EarthViewArtSource";

    private static final int COMMAND_ID_SHARE = 1;
    private static final int COMMAND_ID_DOWNLOAD = 2;
    private static final int COMMAND_ID_VIEW_IN_GOOGLE_MAPS = 3;
    private static final int COMMAND_ID_EARTH_VIEW_MAIN = 4;
    private static final int COMMAND_ID_DEBUG_INFO = 51;

    private static final int ROTATE_TIME_MILLIS = 24 * 60 * 60 * 1000; // rotate every 12 hours

    public static final String PREF_KEY_NEXT_EARTH_VIEW = "next_earth_view";
    public static final String PREF_KEY_MAPS_LINK = "maps_link";
    public static final String PREF_KEY_DOWNLOAD_URL = "download_url";

    SharedPreferences prefs;

    public EarthViewArtSource() {
        super(SOURCE_NAME);
    }

    @Override
    public void onCreate() {
        super.onCreate();
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

        prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
    }

    @Override
    protected void onTryUpdate(int reason) throws RetryException {

        final Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(MoshiConverterFactory.create())
                .baseUrl(EarthViewService.BASE_URL)
                .build();
        EarthViewService service = retrofit.create(EarthViewService.class);

        final String nextLink = prefs.getString(PREF_KEY_NEXT_EARTH_VIEW, EarthViewService.FIRST_EARTH_VIEW);
        try {
            final Response<EarthViewService.EartView> response = service.nextEartView(nextLink).execute();

            if (response.isSuccess()) {
                EarthViewService.EartView eartView = response.body();

                prefs.edit()
                        .putString(PREF_KEY_NEXT_EARTH_VIEW, eartView.getNextApi())
                        .putString(PREF_KEY_MAPS_LINK, eartView.getMapsLink())
                        .putString(PREF_KEY_DOWNLOAD_URL, eartView.getDownloadUrl())
                        .apply();

                publishArtwork(new Artwork.Builder()
                        .title(eartView.getTitle())
                        .byline(eartView.getAttribution())
                        .imageUri(Uri.parse(eartView.getPhotoUrl()))
                        .token(String.valueOf(eartView.getId()))
                        .viewIntent(new Intent(Intent.ACTION_VIEW,
                                Uri.parse(EarthViewService.BASE_URL + eartView.getUrl())))
                        .build());

                scheduleUpdate(System.currentTimeMillis() + ROTATE_TIME_MILLIS);
            }
        } catch (Exception e) {
            Timber.d(e, "Error getting the next link %s", nextLink);
            throw new RetryException(e);
        }
    }


    @Override
    protected void onCustomCommand(int id) {
        super.onCustomCommand(id);

        if (COMMAND_ID_SHARE == id) {
            Artwork currentArtwork = getCurrentArtwork();
            if (currentArtwork == null) {
                Timber.w("No current artwork, can't share.");
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(EarthViewArtSource.this,
                                R.string.error_no_image_to_share,
                                Toast.LENGTH_SHORT).show();
                    }
                });
                return;
            }

            String detailUrl = "https://g.co/ev/" + currentArtwork.getToken();

            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, "My Android wallpaper today is '"
                    + currentArtwork.getTitle().trim()
                    + ". #MuzeiEarthView\n\n"
                    + detailUrl);
            shareIntent = Intent.createChooser(shareIntent, "Share artwork");
            shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(shareIntent);

        } else if (COMMAND_ID_DOWNLOAD == id) {

            final String downloadUrl = prefs.getString(PREF_KEY_DOWNLOAD_URL, null);
            if (downloadUrl != null) {
                DownloadManager dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                DownloadManager.Request request = new DownloadManager.Request(
                        Uri.parse(EarthViewService.BASE_URL + downloadUrl))
                    .setDestinationInExternalPublicDir(Environment.DIRECTORY_PICTURES,
                            getCurrentArtwork().getToken() + ".jpg")
                    .setMimeType("image/*")
                    .setVisibleInDownloadsUi(true)
                    .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                request.allowScanningByMediaScanner();
                dm.enqueue(request);
            }

        } else if (COMMAND_ID_VIEW_IN_GOOGLE_MAPS == id) {

            final String mapsLink = prefs.getString(PREF_KEY_MAPS_LINK, null);
            if (mapsLink != null) {
                Intent viewArchiveIntent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse(mapsLink));
                viewArchiveIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                try {
                    startActivity(viewArchiveIntent);
                } catch (ActivityNotFoundException ignored) {
                }
            }

        } else if (COMMAND_ID_EARTH_VIEW_MAIN == id) {

            Intent viewArchiveIntent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://earthview.withgoogle.com/"));
            viewArchiveIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            try {
                startActivity(viewArchiveIntent);
            } catch (ActivityNotFoundException ignored) {
            }

        } else if (COMMAND_ID_DEBUG_INFO == id) {
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
    }
}

