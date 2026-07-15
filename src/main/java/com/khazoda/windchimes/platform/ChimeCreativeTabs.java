package com.khazoda.windchimes.platform;

//? if fabric && >= 26.1 {
/*import com.khazoda.windchimes.Constants;
import com.khazoda.windchimes.registry.MainRegistry;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.fabricmc.fabric.api.creativetab.v1.FabricCreativeModeTab;
import net.fabricmc.fabric.api.creativetab.v1.FabricCreativeModeTabOutput;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
*///?} elif fabric {
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
//?}
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;

import java.util.function.Supplier;

public final class ChimeCreativeTabs {
  private ChimeCreativeTabs() {
  }

  public static CreativeModeTab create(Supplier<Item> icon, Item... items) {
    CreativeModeTab.Builder builder = builder(icon);
    //? if >= 26.1 {
    /*return builder.build();*/
    //?} else {
    return builder
        .displayItems((parameters, output) -> {
          for (Item item : items) output.accept(item);
        })
        .build();
    //?}
  }

  //? if fabric && >= 26.1 {
  /*public static void registerItems() {
    CreativeModeTabEvents.modifyOutputEvent(ResourceKey.create(Registries.CREATIVE_MODE_TAB, Constants.ID("main"))).register(output -> {
      addItem(output, MainRegistry.BAMBOO_CHIME_ITEM.get());
      addItem(output, MainRegistry.COPPER_CHIME_ITEM.get());
      addItem(output, MainRegistry.IRON_CHIME_ITEM.get());
    });
  }

  private static void addItem(FabricCreativeModeTabOutput output, Item item) {
    output.accept(item.getDefaultInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
  }
  *///?}

  private static CreativeModeTab.Builder builder(Supplier<Item> icon) {
    //? if fabric && >= 26.1 {
    /*return FabricCreativeModeTab.builder()
        .title(title())
        .icon(() -> icon.get().getDefaultInstance());*/
    //?} elif fabric {
    return FabricItemGroup.builder()
        .title(title())
        .icon(() -> icon.get().getDefaultInstance());
    //?} else {
    /*return new CreativeModeTab.Builder(CreativeModeTab.Row.TOP, 0)
        .title(title())
        .icon(() -> icon.get().getDefaultInstance());
    *///?}
  }

  private static Component title() {
    return Component.translatable("itemGroup.windchimes.main");
  }
}
