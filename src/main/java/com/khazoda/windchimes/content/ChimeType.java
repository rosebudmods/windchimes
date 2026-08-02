package com.khazoda.windchimes.content;

import com.khazoda.windchimes.Constants;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;

public record ChimeType(SoundEvent loudSound, SoundEvent quietSound, ResourceLocation textureId, float floatiness) {
  public static final ResourceLocation IRON_LOUD_SOUND_ID = Constants.ID("chime.iron.loud");
  public static final ResourceLocation IRON_QUIET_SOUND_ID = Constants.ID("chime.iron.quiet");
  public static final ResourceLocation BAMBOO_LOUD_SOUND_ID = Constants.ID("chime.bamboo.loud");
  public static final ResourceLocation BAMBOO_QUIET_SOUND_ID = Constants.ID("chime.bamboo.quiet");
  public static final ResourceLocation COPPER_LOUD_SOUND_ID = Constants.ID("chime.copper.loud");
  public static final ResourceLocation COPPER_QUIET_SOUND_ID = Constants.ID("chime.copper.quiet");

  public static final SoundEvent IRON_LOUD_SOUND = SoundEvent.createFixedRangeEvent(IRON_LOUD_SOUND_ID, 48.0F);
  public static final SoundEvent IRON_QUIET_SOUND = SoundEvent.createFixedRangeEvent(IRON_QUIET_SOUND_ID, 24.0F);
  public static final SoundEvent BAMBOO_LOUD_SOUND = SoundEvent.createFixedRangeEvent(BAMBOO_LOUD_SOUND_ID, 48.0F);
  public static final SoundEvent BAMBOO_QUIET_SOUND = SoundEvent.createFixedRangeEvent(BAMBOO_QUIET_SOUND_ID, 24.0F);
  public static final SoundEvent COPPER_LOUD_SOUND = SoundEvent.createFixedRangeEvent(COPPER_LOUD_SOUND_ID, 48.0F);
  public static final SoundEvent COPPER_QUIET_SOUND = SoundEvent.createFixedRangeEvent(COPPER_QUIET_SOUND_ID, 24.0F);

  public static final ChimeType IRON = new ChimeType(IRON_LOUD_SOUND, IRON_QUIET_SOUND, Constants.ID("textures/iron_chime.png"), 0.7F);
  public static final ChimeType BAMBOO = new ChimeType(BAMBOO_LOUD_SOUND, BAMBOO_QUIET_SOUND, Constants.ID("textures/bamboo_chime.png"), 1.3F);
  public static final ChimeType COPPER = new ChimeType(COPPER_LOUD_SOUND, COPPER_QUIET_SOUND, Constants.ID("textures/copper_chime.png"), 1.0F);
  public static final ChimeType INVALID = new ChimeType(SoundEvents.PIGLIN_AMBIENT, SoundEvents.PIGLIN_HURT, ResourceLocation.withDefaultNamespace("textures/block/red_concrete.png"), 1.0F);

  public static BlockBehaviour.Properties defaultSettings(SoundType soundType, MapColor mapColor) {
    return BlockBehaviour.Properties.of()
        .strength(0.0F)
        .sound(soundType)
        .mapColor(mapColor)
        .noCollission()
        .noOcclusion();
  }
}