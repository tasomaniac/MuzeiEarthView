package com.tasomaniac.muzei.earthview;

import android.util.Log;
import com.crashlytics.android.Crashlytics;
import dagger.android.AndroidInjector;
import dagger.android.support.DaggerApplication;
import io.fabric.sdk.android.Fabric;
import timber.log.Timber;

public class App extends DaggerApplication {

    @Override
    public void onCreate() {
        super.onCreate();

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        } else {
            Fabric.with(this, new Crashlytics());
            Timber.plant(new CrashReportingTree());
        }
    }

    @Override protected AndroidInjector<App> applicationInjector() {
        return DaggerAppComponent.builder().create(this);
    }

    /**
     * A tree which logs important information for crash reporting.
     */
    private static class CrashReportingTree extends Timber.Tree {
        @Override protected void log(int priority, String tag, String message, Throwable t) {
            if (priority == Log.VERBOSE || priority == Log.DEBUG) {
                return;
            }

            Crashlytics.log(priority, tag, message);
            if (t != null && priority >= Log.WARN) {
                Crashlytics.logException(t);
            }
        }
    }
}
