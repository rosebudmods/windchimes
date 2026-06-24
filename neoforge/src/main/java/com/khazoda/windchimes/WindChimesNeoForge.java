package com.khazoda.windchimes;

import net.neoforged.fml.common.Mod;

@Mod(Constants.MOD_ID)
public class WindChimesNeoForge {

  public WindChimesNeoForge() {
    WindChimesCommon.init();
  }
}