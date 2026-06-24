package com.khazoda.windchimes;

import net.fabricmc.api.ModInitializer;

public class WindChimesFabric implements ModInitializer {

  @Override
  public void onInitialize() {
    WindChimesCommon.init();
  }
}