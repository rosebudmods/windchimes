package com.khazoda.windchimes;

import com.khazoda.windchimes.registry.MainRegistry;

public final class WindChimesCommon {
  private WindChimesCommon() {
  }

  public static void init() {
    MainRegistry.init();
  }
}