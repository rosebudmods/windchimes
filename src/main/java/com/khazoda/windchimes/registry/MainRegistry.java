package com.khazoda.windchimes.registry;

//? if >= 1.21.2 {
/*import com.khazoda.windchimes.Constants;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
*///?}

import com.khazoda.windchimes.content.ChimeType;
import com.khazoda.windchimes.content.WindChimeBlock;
import com.khazoda.windchimes.content.WindChimeBlockEntity;
import com.khazoda.windchimes.platform.ChimeBlockEntityTypes;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.function.Supplier;

public final class MainRegistry {
  public static Supplier<WindChimeBlock> IRON_CHIME;
  public static Supplier<WindChimeBlock> BAMBOO_CHIME;
  public static Supplier<WindChimeBlock> COPPER_CHIME;
  public static Supplier<Item> IRON_CHIME_ITEM;
  public static Supplier<Item> BAMBOO_CHIME_ITEM;
  public static Supplier<Item> COPPER_CHIME_ITEM;
  public static Supplier<CreativeModeTab> WINDCHIMES_TAB;
  public static Supplier<BlockEntityType<WindChimeBlockEntity>> CHIME_BLOCK_ENTITY;
  private static boolean initialized;

  private MainRegistry() {
  }

  public static void register(Reg r) {
    if (initialized) return;
    IRON_CHIME = r.register(BuiltInRegistries.BLOCK, "iron_chime", () -> new WindChimeBlock(ChimeType.IRON, blockSettings("iron_chime", ChimeType.defaultSettings(SoundType.METAL, Blocks.IRON_BLOCK.defaultMapColor()))));
    BAMBOO_CHIME = r.register(BuiltInRegistries.BLOCK, "bamboo_chime", () -> new WindChimeBlock(ChimeType.BAMBOO, blockSettings("bamboo_chime", ChimeType.defaultSettings(SoundType.BAMBOO, Blocks.BAMBOO.defaultMapColor()))));
    COPPER_CHIME = r.register(BuiltInRegistries.BLOCK, "copper_chime", () -> new WindChimeBlock(ChimeType.COPPER, blockSettings("copper_chime", ChimeType.defaultSettings(SoundType.COPPER, /*? if >= 26.2 {*//*Blocks.COPPER_BLOCK.weathering().unaffected()*//*?} else {*/Blocks.COPPER_BLOCK/*?}*/.defaultMapColor()))));
    IRON_CHIME_ITEM = r.register(BuiltInRegistries.ITEM, "iron_chime", () -> new BlockItem(IRON_CHIME.get(), itemSettings("iron_chime")));
    BAMBOO_CHIME_ITEM = r.register(BuiltInRegistries.ITEM, "bamboo_chime", () -> new BlockItem(BAMBOO_CHIME.get(), itemSettings("bamboo_chime")));
    COPPER_CHIME_ITEM = r.register(BuiltInRegistries.ITEM, "copper_chime", () -> new BlockItem(COPPER_CHIME.get(), itemSettings("copper_chime")));

    //? if >= 26.1 {
    /*WINDCHIMES_TAB = r.register(BuiltInRegistries.CREATIVE_MODE_TAB, "main", () -> new CreativeModeTab.Builder(CreativeModeTab.Row.BOTTOM, 7)
        .title(Component.translatable("itemGroup.windchimes.main"))
        .icon(() -> COPPER_CHIME_ITEM.get().getDefaultInstance())
        .build());*/
    //?} else {
    WINDCHIMES_TAB = r.register(BuiltInRegistries.CREATIVE_MODE_TAB, "main", () -> new CreativeModeTab.Builder(CreativeModeTab.Row.TOP, 0)
        .title(Component.translatable("itemGroup.windchimes.main"))
        .icon(() -> COPPER_CHIME_ITEM.get().getDefaultInstance())
        .displayItems((parameters, output) -> {
          output.accept(BAMBOO_CHIME_ITEM.get());
          output.accept(COPPER_CHIME_ITEM.get());
          output.accept(IRON_CHIME_ITEM.get());
        })
        .build());
    //?}

    r.register(BuiltInRegistries.SOUND_EVENT, "chime.iron.loud", () -> ChimeType.IRON_LOUD_SOUND);
    r.register(BuiltInRegistries.SOUND_EVENT, "chime.iron.quiet", () -> ChimeType.IRON_QUIET_SOUND);
    r.register(BuiltInRegistries.SOUND_EVENT, "chime.bamboo.loud", () -> ChimeType.BAMBOO_LOUD_SOUND);
    r.register(BuiltInRegistries.SOUND_EVENT, "chime.bamboo.quiet", () -> ChimeType.BAMBOO_QUIET_SOUND);
    r.register(BuiltInRegistries.SOUND_EVENT, "chime.copper.loud", () -> ChimeType.COPPER_LOUD_SOUND);
    r.register(BuiltInRegistries.SOUND_EVENT, "chime.copper.quiet", () -> ChimeType.COPPER_QUIET_SOUND);

    CHIME_BLOCK_ENTITY = r.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, "chime", () -> ChimeBlockEntityTypes.create(IRON_CHIME.get(), BAMBOO_CHIME.get(), COPPER_CHIME.get()));
    initialized = true;
  }

  private static BlockBehaviour.Properties blockSettings(String name, BlockBehaviour.Properties properties) {
    //? if >= 1.21.2 {
    /*return properties.setId(ResourceKey.create(Registries.BLOCK, Constants.ID(name)));*/
    //?} else {
    return properties;
    //?}
  }

  private static Item.Properties itemSettings(String name) {
    //? if >= 1.21.2 {
    /*return new Item.Properties().setId(ResourceKey.create(Registries.ITEM, Constants.ID(name))).useBlockDescriptionPrefix();*/
    //?} else {
    return new Item.Properties();
    //?}
  }

  public interface Reg {
    <T, V extends T> Supplier<V> register(Registry<T> registry, String name, Supplier<V> supplier);
  }
}