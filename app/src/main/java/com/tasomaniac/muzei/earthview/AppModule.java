package com.tasomaniac.muzei.earthview;

import javax.inject.Singleton;
import java.util.Random;

import dagger.Module;
import dagger.Provides;

@Module
final class AppModule {

    @Provides @Singleton
    Random provideRandom() {
        return new Random();
    }
}
