package com.khazoda.windchimes;

//? if fabric {
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
    //? if >= 26.1 {
    /*registerCreativeTabItems();*/
    //?}
  }

  //? if >= 26.1 {
  /*private static void registerCreativeTabItems() {
    CreativeModeTabEvents.modifyOutputEvent(ResourceKey.create(Registries.CREATIVE_MODE_TAB, Constants.ID("main"))).register(output -> {
      addCreativeTabItem(output, MainRegistry.BAMBOO_CHIME_ITEM.get());
      addCreativeTabItem(output, MainRegistry.COPPER_CHIME_ITEM.get());
      addCreativeTabItem(output, MainRegistry.IRON_CHIME_ITEM.get());
    });
  }

  private static void addCreativeTabItem(FabricCreativeModeTabOutput output, Item item) {
    output.accept(item.getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
  }*/
  //?}
}
//?}