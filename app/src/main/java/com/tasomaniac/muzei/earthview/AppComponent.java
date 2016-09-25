package com.tasomaniac.muzei.earthview;

import android.app.Application;

import com.tasomaniac.muzei.earthview.data.DataModule;
import com.tasomaniac.muzei.earthview.data.analytics.AnalyticsModule;
import com.tasomaniac.muzei.earthview.data.api.ApiModule;
import com.tasomaniac.muzei.earthview.settings.SettingsFragment;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {
        AppModule.class,
        DataModule.class,
        ApiModule.class,
        AnalyticsModule.class
}, dependencies = Application.class)
public interface AppComponent {

    void inject(EarthViewArtSource fragment);

    void inject(SettingsFragment fragment);

}
