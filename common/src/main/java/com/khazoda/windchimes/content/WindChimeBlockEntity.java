package com.khazoda.windchimes.content;

import com.khazoda.windchimes.registry.MainRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class WindChimeBlockEntity extends BlockEntity {
  protected final int tickDisplacement;
  public int ringingTicks;
  public float strengthDivisor = 35.0F;
  protected int ticksToNextRing;
  protected int baselineRingTicks;
  @Nullable
  protected ChimeType cachedType;
  protected boolean cachedTypeNeedsUpdate;

  public WindChimeBlockEntity(BlockPos pos, BlockState state) {
    super(MainRegistry.CHIME_BLOCK_ENTITY.get(), pos, state);
    ringingTicks = 0;
    ticksToNextRing = 40;
    baselineRingTicks = 0;
    cachedType = ((WindChimeBlock) state.getBlock()).getChimeType();
    cachedTypeNeedsUpdate = true;
    tickDisplacement = Math.abs(pos.getX() + pos.getY() + pos.getZ()) % 6;
  }

  public static void tick(Level level, BlockPos pos, BlockState state, WindChimeBlockEntity chime) {
    if (level.isClientSide) {
      if (chime.ringingTicks > chime.baselineRingTicks) {
        chime.ringingTicks--;
      }
      if (chime.ringingTicks < chime.baselineRingTicks) {
        chime.ringingTicks = chime.baselineRingTicks;
      }
      if (level.isRaining()) {
        chime.baselineRingTicks = level.isThundering() ? 26 : 12;
      } else {
        chime.baselineRingTicks = level.isDay() ? 0 : 6;
      }
      return;
    }

    chime.ticksToNextRing--;
    if (chime.ticksToNextRing <= 0 && level.getGameTime() % 6 == chime.tickDisplacement) {
      if (level.isRaining()) {
        if (level.isThundering()) {
          chime.ticksToNextRing = level.random.nextInt(200);
          chime.ring(level.random.nextInt(4) != 0);
        } else {
          chime.ticksToNextRing = 100 + level.random.nextInt(400);
          chime.ring(level.random.nextInt(3) == 0);
        }
      } else if (level.isDay()) {
        chime.ticksToNextRing = 200 + level.random.nextInt(900);
        chime.ring(level.random.nextInt(5) == 0);
      } else {
        chime.ticksToNextRing = 100 + level.random.nextInt(700);
        chime.ring(level.random.nextInt(5) == 0);
      }
    }
  }

  public void ring(boolean isLoud) {
    if (level != null && level.getBlockState(worldPosition.below()).isAir()) {
      level.blockEvent(worldPosition, level.getBlockState(worldPosition).getBlock(), 1, isLoud ? 1 : 0);
    }
  }

  @Override
  public boolean triggerEvent(int type, int data) {
    if (type == 1 && level != null) {
      if (data == 0) {
        ringingTicks = 60;
        strengthDivisor = 35.0F;
        level.playSound(null, worldPosition, getChimeType().quietSound, SoundSource.RECORDS,
            0.9F + level.random.nextFloat() * 0.2F,
            0.8F + level.random.nextFloat() * 0.4F);
      } else {
        ringingTicks = 140;
        strengthDivisor = 55.0F;
        level.playSound(null, worldPosition, getChimeType().loudSound, SoundSource.RECORDS,
            0.9F + level.random.nextFloat() * 0.2F,
            0.8F + level.random.nextFloat() * 0.4F);
      }
      return true;
    }
    return super.triggerEvent(type, data);
  }

  public ChimeType getChimeType() {
    if (cachedTypeNeedsUpdate) {
      cachedType = ((WindChimeBlock) getBlockState().getBlock()).getChimeType();
      cachedTypeNeedsUpdate = false;
    }
    return cachedType == null ? ChimeType.INVALID : cachedType;
  }
}