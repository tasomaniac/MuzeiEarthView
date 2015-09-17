package com.tasomaniac.muzei.earthview;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import timber.log.Timber;

interface Analytics {

    void sendScreenView(String screenName);

    void sendEvent(String category, String action, String label, long value);

    void sendEvent(String category, String action, String label);

    class GoogleAnalytics implements Analytics {
        private final Tracker tracker;

        public GoogleAnalytics(Tracker tracker) {
            this.tracker = tracker;
        }

        @Override
        public void sendScreenView(String screenName) {
            tracker.setScreenName(screenName);
            tracker.send(new HitBuilders.AppViewBuilder().build());
        }

        @Override
        public void sendEvent(String category, String action, String label, long value) {
            tracker.send(new HitBuilders.EventBuilder()
                    .setCategory(category)
                    .setAction(action)
                    .setLabel(label)
                    .setValue(value)
                    .build());
        }

        @Override
        public void sendEvent(String category, String action, String label) {
            sendEvent(category, action, label, 0);
        }
    }

    class DebugAnalytics implements Analytics {

        @Override
        public void sendScreenView(String screenName) {
            Timber.tag("Analytics").d("Screen: " + screenName);
        }

        @Override
        public void sendEvent(String category, String action, String label) {
            sendEvent(category, action, label, 0);
        }

        @Override
        public void sendEvent(String category, String action, String label, long value) {
            Timber.tag("Analytics").d("Event recorded:"
                    + "\n\tCategory: " + category
                    + "\n\tAction: " + action
                    + "\n\tLabel: " + label
                    + "\n\tValue: " + value);
        }
    }
}