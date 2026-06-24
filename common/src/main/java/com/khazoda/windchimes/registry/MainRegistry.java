package com.khazoda.windchimes.registry;

public final class MainRegistry {
  private static boolean initialized;

  private MainRegistry() {
  }

  public static void init() {
    if (initialized) {
      return;
    }
    initialized = true;
  }
}