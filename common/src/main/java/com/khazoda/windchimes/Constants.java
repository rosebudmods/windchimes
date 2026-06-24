package com.khazoda.windchimes;

import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Constants {

  public static final String MOD_ID = "windchimes";
  public static final String MOD_NAME = "Wind Chimes";
  public static final Logger LOG = LoggerFactory.getLogger(MOD_NAME);

  private Constants() {
  }

  public static ResourceLocation ID(String path) {
    return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
  }
}