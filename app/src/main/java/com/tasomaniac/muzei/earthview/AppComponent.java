package com.tasomaniac.muzei.earthview;

import com.tasomaniac.muzei.earthview.data.DataModule;
import com.tasomaniac.muzei.earthview.data.analytics.AnalyticsModule;
import com.tasomaniac.muzei.earthview.data.api.ApiModule;
import dagger.Component;
import dagger.android.AndroidInjector;
import dagger.android.support.AndroidSupportInjectionModule;

import javax.inject.Singleton;

@Singleton
@Component(modules = {
        AndroidSupportInjectionModule.class,
        DataModule.class,
        ApiModule.class,
        AnalyticsModule.class,
        BindingModule.class
})
public interface AppComponent extends AndroidInjector<App> {

    @Component.Builder
    abstract class Builder extends AndroidInjector.Builder<App> {
    }
}
