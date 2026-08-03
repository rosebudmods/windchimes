package com.khazoda.windchimes.content;

import com.khazoda.windchimes.registry.MainRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class WindChimeBlockEntity extends BlockEntity {
  private static final int DIRECTION_STEPS = 64; //5.625 degree precision
  private static final AABB CONTACT_BOX = new AABB(4.0 / 16.0, -5.0 / 16.0, 4.0 / 16.0, 12.0 / 16.0, 1.0, 12.0 / 16.0); // projectile and entity collision detection
  private static final double MIN_CONTACT_SPEED = 0.01;
  float previousSwingStrength;
  float swingStrength;
  float ringDirection;
  int seedForAnimation;
  private long ringStartTick;
  private int ringTicks;
  private int ambientDelay;
  //? if =1.21.1 {
  /*private WindChimeSable sable;
  *///?}

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
      //? if =1.21.1 {
      /*if (chime.sable == null) chime.sable = new WindChimeSable();
      chime.sable.tick(chime);
      *///?}
      return;
    }

    if (chime.ringTicks > 0 && --chime.ringTicks == 0) level.updateNeighbourForOutputSignal(pos, state.getBlock());
    if (chime.ringTicks == 0 && chime.handleContact(level)) return;
    if (chime.ambientDelay == 0) chime.resetAmbientDelay(level);
    else if (--chime.ambientDelay == 0) {
      chime.resetAmbientDelay(level);
      boolean loud = level.isThundering() ? level.random.nextInt(4) != 0
          : level.random.nextInt(level.isRaining() ? 3 : 5) == 0;
      chime.ring(level, loud ? RingStrength.LOUD : RingStrength.MEDIUM, 0, 0);
    }
  }

  /* world position dependent number to for staggering animations */
  static int animationOffset(BlockPos pos) {
    return Math.floorMod(pos.hashCode(), 120);
  }

  boolean tryRing(RingStrength strength, float direction) {
    Level level = this.level;
    if (level == null || level.isClientSide || ringTicks > 0) return false;
    resetAmbientDelay(level);
    int directionStep = Mth.floor((direction + Mth.PI / 2.0F) * DIRECTION_STEPS / Mth.TWO_PI) + level.random.nextIntBetweenInclusive(-2, 2);
    ring(level, strength, Mth.positiveModulo(directionStep, DIRECTION_STEPS) + 1, level.random.nextIntBetweenInclusive(1, 255));
    return true;
  }

  private boolean ringFromMovement(Vec3 movement, RingStrength strength) {
    Level level = this.level;
    if (level == null) return false;

    float direction = movement.horizontalDistanceSqr() == 0.0
        ? level.random.nextFloat() * Mth.TWO_PI
        : (float) Mth.atan2(movement.z, movement.x);
    //? if =1.21.1 {
    /*direction = WindChimeSable.directionFromMovement(this, movement, direction);
    *///?}
    return tryRing(strength, direction);
  }

  private boolean handleContact(Level level) {
    AABB contactBox = CONTACT_BOX.move(worldPosition);
    //? if =1.21.1 {
    /*contactBox = WindChimeSable.worldContactBox(this, contactBox);
    *///?}
    for (LivingEntity entity : level.getEntitiesOfClass(LivingEntity.class, contactBox, entity -> !entity.isSpectator())) {
      Vec3 movement = entity.getKnownMovement();
      //? if =1.21.1 {
      /*movement = WindChimeSable.relativeMovement(this, entity, movement);
      *///?}
      if (movement.lengthSqr() < MIN_CONTACT_SPEED * MIN_CONTACT_SPEED) continue;
      RingStrength strength = entity.isCrouching() ? RingStrength.QUIET
          : entity.isSprinting() ? RingStrength.LOUD : RingStrength.MEDIUM;
      if (ringFromMovement(movement, strength)) return true;
    }

    // inflate search radius so projectiles aren't missed between ticks
    for (Projectile projectile : level.getEntitiesOfClass(Projectile.class, contactBox.inflate(4.0))) {
      Vec3 end = projectile.position();
      Vec3 start = new Vec3(projectile.xo, projectile.yo, projectile.zo);
      double radius = projectile.getBbWidth() * 0.5;
      AABB pathTarget = contactBox.inflate(radius, 0.0, radius).expandTowards(0.0, -projectile.getBbHeight(), 0.0);
      if (!pathTarget.contains(start) && pathTarget.clip(start, end).isEmpty()) continue;

      Vec3 movement = end.subtract(start);
      if (movement.lengthSqr() == 0.0) movement = projectile.getDeltaMovement();
      if (movement.lengthSqr() != 0.0 && ringFromMovement(movement, RingStrength.LOUD)) return true;
    }
    return false;
  }

  private void resetAmbientDelay(Level level) {
    int min = level.isThundering() ? 60 : level.isRaining() ? 120 : level.isDay() ? 300 : 200;
    int range = level.isThundering() ? 140 : level.isRaining() ? 380 : level.isDay() ? 800 : 600;
    ambientDelay = min + level.random.nextInt(range);
  }

  private void ring(Level level, RingStrength strength, int direction, int seed) {
    ringTicks = strength.duration;
    level.updateNeighbourForOutputSignal(worldPosition, getBlockState().getBlock());
    level.blockEvent(worldPosition, getBlockState().getBlock(), seed, direction * 3 + strength.ordinal());
  }

  @Override
  public boolean triggerEvent(int seed, int data) {
    if (level == null) return super.triggerEvent(seed, data);

    RingStrength strength = switch (data % 3) {
      case 0 -> RingStrength.QUIET;
      case 1 -> RingStrength.MEDIUM;
      default -> RingStrength.LOUD;
    };
    if (level.isClientSide) {
      ringTicks = strength.duration;
      ringStartTick = level.getGameTime();
      seedForAnimation = seed;
      int direction = data / 3;
      if (direction > 0) ringDirection = (direction - 1) * Mth.TWO_PI / DIRECTION_STEPS;
    } else {
      level.playSound(null, worldPosition,
          strength == RingStrength.LOUD ? getChimeType().loudSound() : getChimeType().softSound(),
          SoundSource.BLOCKS, strength.volume + level.random.nextFloat() * 0.2F,
          0.8F + level.random.nextFloat() * 0.4F);
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

  enum RingStrength {
    QUIET(35, 0.35F), MEDIUM(75, 0.9F), LOUD(140, 0.9F);

    final int duration;
    final float volume;

    RingStrength(int duration, float volume) {
      this.duration = duration;
      this.volume = volume;
    }
  }

  //? if =1.21.1 {
  /*WindChimeSable getSableMotion() {
    return sable;
  }
  *///?}
}