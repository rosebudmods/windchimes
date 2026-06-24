package com.khazoda.windchimes;

import com.khazoda.windchimes.registry.MainRegistry;
import net.fabricmc.api.ModInitializer;
import net.minecraft.core.Registry;

import java.util.function.Supplier;

public class WindChimesFabric implements ModInitializer {

  @Override
  public void onInitialize() {
    WindChimesCommon.init();
    MainRegistry.register(new MainRegistry.Reg() {
      @Override
      public <T, V extends T> Supplier<V> register(Registry<T> registry, String name, Supplier<V> supplier) {
        V value = supplier.get();
        Registry.register(registry, Constants.ID(name), value);
        return () -> value;
      }
    });
  }
}