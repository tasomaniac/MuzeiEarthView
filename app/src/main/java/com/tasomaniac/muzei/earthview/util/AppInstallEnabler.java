/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.tasomaniac.muzei.earthview.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.NonNull;

import com.tasomaniac.muzei.earthview.ui.IntegrationPreference;


public final class AppInstallEnabler {

    private final Context mContext;
    private IntegrationPreference mPref;
    private final IntentFilter mIntentFilter;

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Broadcast receiver is always running on the UI thread here,
            // so we don't need consider thread synchronization.
            handleStateChanged();
        }
    };

    public AppInstallEnabler(@NonNull Context context,
                             @NonNull IntegrationPreference pref) {
        mContext = context;
        mPref = pref;

        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(Intent.ACTION_PACKAGE_ADDED);
        mIntentFilter.addAction(Intent.ACTION_PACKAGE_CHANGED);
        mIntentFilter.addAction(Intent.ACTION_PACKAGE_REPLACED);
        mIntentFilter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        mIntentFilter.addDataScheme("package");
    }

    public void resume() {

        handleStateChanged();
        mContext.registerReceiver(mReceiver, mIntentFilter);
    }

    public void pause() {
        mContext.unregisterReceiver(mReceiver);
    }

    void handleStateChanged() {
        mPref.checkState();
    }

}
