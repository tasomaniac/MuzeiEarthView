package com.tasomaniac.muzei.earthview;

import android.app.Application;
import android.support.annotation.NonNull;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.tasomaniac.muzei.earthview.data.Injector;

import hugo.weaving.DebugLog;
import io.fabric.sdk.android.Fabric;
import timber.log.Timber;

public class App extends Application {

    private AppComponent component;

    @Override
    public void onCreate() {
        super.onCreate();

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        } else {
            Fabric.with(this, new Crashlytics());
            Timber.plant(new CrashReportingTree());
        }

        buildComponentAndInject();
    }

    @DebugLog // Extracted for debugging.
    public void buildComponentAndInject() {
        component = AppComponent.Initializer.init(this);
        component.inject(this);
    }

    @Override
    public Object getSystemService(@NonNull String name) {
        if (Injector.matchesService(name)) {
            return component;
        }
        return super.getSystemService(name);
    }

    /** A tree which logs important information for crash reporting. */
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
