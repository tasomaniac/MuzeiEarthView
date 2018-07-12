package com.tasomaniac.muzei.earthview.data.analytics;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.ContentViewEvent;
import com.crashlytics.android.answers.CustomEvent;
import com.tasomaniac.muzei.earthview.BuildConfig;
import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;

@Module
public abstract class AnalyticsModule {

    @Provides @Singleton
    static Analytics provideAnalytics() {
        if (BuildConfig.DEBUG) {
            return new Analytics.DebugAnalytics();
        }
        return new AnswersAnalytics();
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
