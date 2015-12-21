package com.tasomaniac.muzei.earthview.data;

import android.content.Context;

import com.tasomaniac.muzei.earthview.AppComponent;

public final class Injector {
  private static final String INJECTOR_SERVICE = "com.tasomaniac.injector";

  @SuppressWarnings({"ResourceType", "WrongConstant"}) // Explicitly doing a custom service.
  public static AppComponent obtain(Context context) {
    return (AppComponent) context.getSystemService(INJECTOR_SERVICE);
  }

  public static boolean matchesService(String name) {
    return INJECTOR_SERVICE.equals(name);
  }

  private Injector() {
    throw new AssertionError("No instances.");
  }
}
