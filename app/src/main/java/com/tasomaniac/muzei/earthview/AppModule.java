package com.tasomaniac.muzei.earthview;

import android.app.Application;
import android.content.pm.PackageManager;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.ContentViewEvent;
import com.crashlytics.android.answers.CustomEvent;
import com.tasomaniac.muzei.earthview.data.Analytics;

import javax.inject.Singleton;
import java.util.Random;

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

    @Provides @Singleton
    Analytics provideAnalytics() {
        if (BuildConfig.DEBUG) {
            return new Analytics.DebugAnalytics();
        }
        return new AnswersAnalytics();
    }

    @Provides @Singleton
    Random provideRandom() {
        return new Random();
    }

    private static class AnswersAnalytics implements Analytics {
        private final Answers answers = Answers.getInstance();

        @Override
        public void sendScreenView(String screenName) {
            answers.logContentView(new ContentViewEvent().putContentName(screenName));
        }

        @Override
        public void sendEvent(String category, String action, String label) {
            answers.logCustom(new CustomEvent(category)
                                      .putCustomAttribute(action, label));
        }
    }
}
