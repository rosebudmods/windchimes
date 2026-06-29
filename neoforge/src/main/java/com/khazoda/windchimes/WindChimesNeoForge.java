package com.khazoda.windchimes;

import com.khazoda.windchimes.registry.MainRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

@Mod(Constants.MOD_ID)
public class WindChimesNeoForge {
  private static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(Constants.MOD_ID);
  private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Constants.MOD_ID);
  private static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(BuiltInRegistries.CREATIVE_MODE_TAB, Constants.MOD_ID);
  private static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(BuiltInRegistries.SOUND_EVENT, Constants.MOD_ID);
  private static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, Constants.MOD_ID);

  public WindChimesNeoForge(IEventBus eventBus) {
    MainRegistry.register(new MainRegistry.Reg() {
      @Override
      @SuppressWarnings("unchecked")
      public <T, V extends T> Supplier<V> register(Registry<T> registry, String name, Supplier<V> supplier) {
        if (registry == BuiltInRegistries.BLOCK)
          return (Supplier<V>) BLOCKS.register(name, (Supplier<? extends Block>) supplier);
        if (registry == BuiltInRegistries.ITEM)
          return (Supplier<V>) ITEMS.register(name, (Supplier<? extends Item>) supplier);
        if (registry == BuiltInRegistries.CREATIVE_MODE_TAB)
          return (Supplier<V>) TABS.register(name, (Supplier<? extends CreativeModeTab>) supplier);
        if (registry == BuiltInRegistries.SOUND_EVENT)
          return (Supplier<V>) SOUNDS.register(name, (Supplier<? extends SoundEvent>) supplier);
        if (registry == BuiltInRegistries.BLOCK_ENTITY_TYPE)
          return (Supplier<V>) BLOCK_ENTITY_TYPES.register(name, (Supplier<? extends BlockEntityType<?>>) supplier);
        throw new IllegalArgumentException("Unsupported registry");
      }
    });

    BLOCKS.register(eventBus);
    ITEMS.register(eventBus);
    TABS.register(eventBus);
    SOUNDS.register(eventBus);
    BLOCK_ENTITY_TYPES.register(eventBus);

    if (/*? if >= 1.21.9 {*//*FMLEnvironment.getDist()*//*?} else {*/FMLEnvironment.dist/*?}*/ == Dist.CLIENT) WindChimesNeoForgeClient.register(eventBus);

    WindChimesCommon.init();
  }
}