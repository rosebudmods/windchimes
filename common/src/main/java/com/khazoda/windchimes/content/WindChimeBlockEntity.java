package com.khazoda.windchimes.content;

import com.khazoda.windchimes.registry.MainRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class WindChimeBlockEntity extends BlockEntity {
  public int ringTicks;
  private int ringDelay;
  private long nextUse;

  public WindChimeBlockEntity(BlockPos pos, BlockState state) {
    super(MainRegistry.CHIME_BLOCK_ENTITY.get(), pos, state);
  }

  public static void tick(Level level, BlockPos pos, BlockState state, WindChimeBlockEntity chime) {
    if (level.isClientSide) {
      int ambientTicks = level.isThundering() ? 30 : level.isRaining() ? 15 : level.isDay() ? 0 : 5;
      chime.ringTicks = Math.max(ambientTicks, chime.ringTicks - 1);
      return;
    }

    if (chime.ringDelay == 0) {
      chime.resetRingDelay(level);
      return;
    }
    if (--chime.ringDelay > 0) return;

    chime.resetRingDelay(level);
    boolean loudly = level.isThundering()
        ? level.random.nextInt(4) != 0
        : level.random.nextInt(level.isRaining() ? 3 : 5) == 0;
    chime.ring(loudly);
  }

  void interact(boolean loudly) {
    if (level == null || level.isClientSide || level.getGameTime() < nextUse) return;
    nextUse = level.getGameTime() + 40;
    resetRingDelay(level);
    ring(loudly);
  }

  private void resetRingDelay(Level level) {
    int minDelay = level.isThundering() ? 60 : level.isRaining() ? 120 : level.isDay() ? 300 : 200;
    int randomDelay = level.isThundering() ? 140 : level.isRaining() ? 380 : level.isDay() ? 800 : 600;
    ringDelay = minDelay + level.random.nextInt(randomDelay);
  }

  private void ring(boolean loudly) {
    level.blockEvent(worldPosition, getBlockState().getBlock(), 1, loudly ? 1 : 0);
  }

  @Override
  public boolean triggerEvent(int type, int data) {
    if (type != 1 || level == null) return super.triggerEvent(type, data);

    boolean isLoud = data != 0;
    if (level.isClientSide) {
      ringTicks = isLoud ? 140 : 60;
    } else {
      level.playSound(null, worldPosition, isLoud ? getChimeType().loudSound : getChimeType().quietSound, SoundSource.BLOCKS,
          0.9F + level.random.nextFloat() * 0.2F,
          0.8F + level.random.nextFloat() * 0.4F);
    }
    return true;
  }

  ChimeType getChimeType() {
    return ((WindChimeBlock) getBlockState().getBlock()).getChimeType();
  }
}