package com.khazoda.windchimes.content;

import com.khazoda.windchimes.registry.MainRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class WindChimeBlockEntity extends BlockEntity {
  private static final int DIRECTION_STEPS = 64; //5.625 degree precision
  private long ringStartTick;
  private int ringTicks;
  private int ambientDelay;
  float previousSwingStrength;
  float swingStrength;
  float ringDirection;
  int seedForAnimation;

  public WindChimeBlockEntity(BlockPos pos, BlockState state) {
    super(MainRegistry.CHIME_BLOCK_ENTITY.get(), pos, state);
    ringStartTick = -animationOffset(pos);
  }

  public static void tick(Level level, BlockPos pos, BlockState state, WindChimeBlockEntity chime) {
    if (level.isClientSide) {
      int ambientTicks = level.isThundering() ? 30 : level.isRaining() ? 15 : level.isDay() ? 0 : 5;
      chime.ringTicks = Math.max(ambientTicks, chime.ringTicks - 1);
      chime.previousSwingStrength = chime.swingStrength;
      float target = chime.ringTicks / 35.0F; //smaller divisor = bigger swings
      chime.swingStrength = Mth.lerp(target > chime.swingStrength ? 0.5F : 0.25F, chime.swingStrength, target);
      return;
    }

    if (chime.ringTicks > 0 && --chime.ringTicks == 0) level.updateNeighbourForOutputSignal(pos, state.getBlock());
    if (chime.ambientDelay == 0) chime.resetAmbientDelay(level);
    else if (--chime.ambientDelay == 0) {
      chime.resetAmbientDelay(level);
      boolean loud = level.isThundering() ? level.random.nextInt(4) != 0 : level.random.nextInt(level.isRaining() ? 3 : 5) == 0;
      chime.ring(level, loud, 0, 0);
    }
  }

  boolean tryRing(boolean loud, float direction) {
    Level level = this.level;
    if (level == null || level.isClientSide || ringTicks > 0) return false;
    resetAmbientDelay(level);
    int directionStep = Mth.floor((direction + Mth.PI / 2.0F) * DIRECTION_STEPS / Mth.TWO_PI) + level.random.nextIntBetweenInclusive(-2, 2);
    ring(level, loud, Mth.positiveModulo(directionStep, DIRECTION_STEPS) + 1, level.random.nextIntBetweenInclusive(1, 255));
    return true;
  }

  private void resetAmbientDelay(Level level) {
    int min = level.isThundering() ? 60 : level.isRaining() ? 120 : level.isDay() ? 300 : 200;
    int range = level.isThundering() ? 140 : level.isRaining() ? 380 : level.isDay() ? 800 : 600;
    ambientDelay = min + level.random.nextInt(range);
  }

  private void ring(Level level, boolean loud, int direction, int seed) {
    ringTicks = loud ? 140 : 60;
    level.updateNeighbourForOutputSignal(worldPosition, getBlockState().getBlock());
    level.blockEvent(worldPosition, getBlockState().getBlock(), seed, (direction << 1) | (loud ? 1 : 0));
  }

  @Override
  public boolean triggerEvent(int seed, int data) {
    if (level == null) return super.triggerEvent(seed, data);

    boolean loud = (data & 1) != 0;
    if (level.isClientSide) {
      ringTicks = loud ? 140 : 60;
      ringStartTick = level.getGameTime();
      seedForAnimation = seed;
      if (data > 1) ringDirection = ((data >>> 1) - 1) * Mth.TWO_PI / DIRECTION_STEPS;
    } else {
      level.playSound(null, worldPosition, loud ? getChimeType().loudSound : getChimeType().quietSound, SoundSource.BLOCKS, 0.9F + level.random.nextFloat() * 0.2F, 0.8F + level.random.nextFloat() * 0.4F);
    }
    return true;
  }

  ChimeType getChimeType() {
    return ((WindChimeBlock) getBlockState().getBlock()).getChimeType();
  }

  boolean isRinging() {
    return ringTicks > 0;
  }

  float getSwingStrength(float partialTick) {
    return Mth.lerp(partialTick, previousSwingStrength, swingStrength);
  }

  float getSwingTime(float partialTick) {
    return ((level == null ? 0L : level.getGameTime()) - ringStartTick + partialTick) * 0.2F;
  }

  /* world position dependent number to for staggering animations */
  static int animationOffset(BlockPos pos) {
    return Math.floorMod(pos.hashCode(), 120);
  }
}