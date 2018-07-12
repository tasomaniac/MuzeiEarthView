package com.tasomaniac.muzei.earthview;

import com.tasomaniac.muzei.earthview.settings.SettingsFragment;
import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
interface BindingModule {

    @ContributesAndroidInjector EarthViewArtSource earthViewArtSource();

    @ContributesAndroidInjector SettingsFragment settingsFragment();

    @ContributesAndroidInjector EarthViewArtProvider earthViewArtProvider();
}
