package com.tasomaniac.muzei.earthview;

import android.app.Application;
import android.content.pm.PackageManager;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

import java.util.Random;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * A module for Android-specific dependencies which require a Context to create.
 *
 * Created by Said Tahsin Dane on 17/03/15.
 */
@Module
final class AppModule {
    private final App app;

    AppModule(App app) {
        this.app = app;
    }

    @Provides @Singleton
    Application application() {
        return app;
    }

    @Provides @Singleton
    PackageManager providePackageManager() {
        return app.getPackageManager();
    }

    @Provides @Singleton Analytics provideAnalytics() {
        if (BuildConfig.DEBUG) {
            return new Analytics.DebugAnalytics();
        }

        GoogleAnalytics googleAnalytics = GoogleAnalytics.getInstance(app);
        Tracker tracker = googleAnalytics.newTracker(BuildConfig.ANALYTICS_KEY);
        tracker.setSessionTimeout(300); // ms? s? better be s.
        return new Analytics.AnalyticsImpl(tracker);
    }

    @Provides @Singleton
    Random provideRandom() {
        return new Random();
    }
}