//? if =1.21.1 {
/*package com.khazoda.windchimes.content;

import dev.ryanhcode.sable.companion.SableCompanion;
import dev.ryanhcode.sable.companion.SubLevelAccess;
import dev.ryanhcode.sable.companion.math.Pose3dc;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;

final class WindChimeSable {

  // renderer part indices
  static final int PLATFORM = 0;
  static final int FIRST_ROD = 1;
  static final int CLAPPER = 5;

  // physics tuning and limits
  private static final double TICK_SECONDS = 0.05;
  private static final double GRAVITY = 9.81;
  private static final double MIN_PULL_STRENGTH = GRAVITY * 0.1;
  private static final double MAX_INERTIAL_ACCELERATION = GRAVITY * 3.0;
  private static final double MAX_AIR_DRAG_ACCELERATION = GRAVITY * 0.3;
  private static final double IMPACT_JOLT_THRESHOLD = GRAVITY * 2.0;
  private static final float MAX_ANGLE = 80.0F * Mth.DEG_TO_RAD;

  // individual part stiffness and damping
  private final PartMotion[] parts = {
      new PartMotion(3.0F, 0.72F),  // platform
      new PartMotion(3.2F, 0.62F),  // longest rod
      new PartMotion(4.1F, 0.62F),  // shortest rod
      new PartMotion(3.7F, 0.62F),  // second-shortest rod
      new PartMotion(3.45F, 0.62F), // second-longest rod
      new PartMotion(3.8F, 0.55F)   // clapper
  };

  // contraption motion history
  private final Vector3d lastVelocity = new Vector3d();
  private boolean hasVelocitySample;

  private final Vector3d smoothAcceleration = new Vector3d();
  private int impactCooldown;

  // reused multipurpose vector
  private final Vector3d temp = new Vector3d();

  void tick(WindChimeBlockEntity chime) {
    for (PartMotion part : parts) part.beginTick();
    float floatiness = chime.getChimeType().floatiness();

    SubLevelAccess subLevel = SableCompanion.INSTANCE.getContaining(chime);
    if (subLevel == null) {
      resetMotionHistory();
      updateParts(0.0F, 0.0F, 1.0F, floatiness);
      return;
    }

    Pose3dc pose = subLevel.logicalPose();
    temp.set(chime.getBlockPos().getX() + 0.5, chime.getBlockPos().getY() + 1.0, chime.getBlockPos().getZ() + 0.5);
    SableCompanion.INSTANCE.getVelocity(chime.getLevel(), subLevel, temp);

    if (hasInvalidValues(temp)) {
      resetMotionHistory();
      updateParts(0.0F, 0.0F, 1.0F, floatiness);
      return;
    }

    // discard motion if moved 8 blocks or more in a single tick
    boolean measuredAcceleration = false;
    if (temp.lengthSquared() * TICK_SECONDS * TICK_SECONDS > 64.0) {
      resetMotionHistory();
      temp.zero();
    } else {
      // acceleration derived from the change in contraption velocity
      measuredAcceleration = measureAcceleration();
    }
    capLength(temp, MAX_INERTIAL_ACCELERATION);

    // ring chime on sharp changes in acceleration
    if (impactCooldown > 0) impactCooldown--;
    double jolt = temp.distance(smoothAcceleration);
    if (measuredAcceleration && impactCooldown == 0 && jolt > IMPACT_JOLT_THRESHOLD) {
      float strength = Mth.clamp((float) ((jolt - IMPACT_JOLT_THRESHOLD) / (GRAVITY * 2.0)), 0.0F, 1.0F);
      playImpactSound(chime, strength);
      impactCooldown = 20;
    }
    smoothAcceleration.lerp(temp, jolt > IMPACT_JOLT_THRESHOLD ? 0.5 : 0.25);

    // slight air resistance while the contraption is moving
    temp.set(lastVelocity.x, 0.0, lastVelocity.z).mul(-0.2 * floatiness);
    capLength(temp, MAX_AIR_DRAG_ACCELERATION);
    double dragX = temp.x;
    double dragZ = temp.z;

    // combined pull from gravity, acceleration and air resistance
    temp.set(smoothAcceleration).mul(floatiness);
    capLength(temp, MAX_INERTIAL_ACCELERATION);
    temp.negate().add(dragX, -GRAVITY, dragZ);
    pose.transformNormalInverse(temp);
    swingTowards(temp, floatiness);
  }

  private boolean measureAcceleration() {
    if (!hasVelocitySample) {
      lastVelocity.set(temp);
      temp.zero();
      hasVelocitySample = true;
      return false;
    }

    double velocityX = temp.x;
    double velocityY = temp.y;
    double velocityZ = temp.z;
    temp.sub(lastVelocity).div(TICK_SECONDS);
    lastVelocity.set(velocityX, velocityY, velocityZ);
    return true;
  }

  private void swingTowards(Vector3d pull, float floatiness) {
    double pullStrength = pull.length();
    if (hasInvalidValues(pull) || pullStrength < MIN_PULL_STRENGTH) {
      updateParts(0.0F, 0.0F, 0.0F, floatiness);
      return;
    }

    float targetX = Mth.clamp((float) Math.atan2(-pull.z,
        Math.sqrt(pull.x * pull.x + pull.y * pull.y)), -MAX_ANGLE, MAX_ANGLE);
    float targetZ = Mth.clamp((float) Math.atan2(pull.x, -pull.y), -MAX_ANGLE, MAX_ANGLE);
    updateParts(targetX, targetZ, (float) Mth.clamp(pullStrength / GRAVITY, 0.0, 3.0), floatiness);
  }

  private void resetMotionHistory() {
    hasVelocitySample = false;
    lastVelocity.zero();
    smoothAcceleration.zero();
    impactCooldown = 0;
  }

  private void updateParts(float targetX, float targetZ, float pullScale, float floatiness) {
    float frequencyScale = 1.0F / (float) Math.sqrt(floatiness);
    for (PartMotion part : parts) part.update(targetX, targetZ, pullScale, frequencyScale);
  }

  float xRot(int part, float partialTick) {
    return parts[part].xRot(partialTick);
  }

  float zRot(int part, float partialTick) {
    return parts[part].zRot(partialTick);
  }

  static float interactionDirection(WindChimeBlockEntity chime, Player player, double hitX, double hitZ, float fallback) {
    SubLevelAccess subLevel = SableCompanion.INSTANCE.getContaining(chime);
    if (subLevel == null) return fallback;

    Vec3 eye = SableCompanion.INSTANCE.getEyePositionInterpolated(player, 1.0F);
    Vector3d localEye = new Vector3d(eye.x, eye.y, eye.z);
    subLevel.logicalPose().transformPositionInverse(localEye);
    double directionX = hitX - localEye.x;
    double directionZ = hitZ - localEye.z;
    return directionX * directionX + directionZ * directionZ < 1.0E-6
        ? fallback
        : (float) Mth.atan2(directionZ, directionX);
  }

  private static void playImpactSound(WindChimeBlockEntity chime, float strength) {
    Level level = chime.getLevel();
    if (level == null) return;
    BlockPos pos = chime.getBlockPos();
    level.playLocalSound(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
        chime.getChimeType().quietSound(), SoundSource.BLOCKS, Mth.lerp(strength, 0.55F, 0.9F),
        0.9F + level.random.nextFloat() * 0.2F, false);
  }

  private static boolean hasInvalidValues(Vector3d vector) {
    return !Double.isFinite(vector.x) || !Double.isFinite(vector.y) || !Double.isFinite(vector.z);
  }

  private static void capLength(Vector3d vector, double maximum) {
    double lengthSquared = vector.lengthSquared();
    if (lengthSquared > maximum * maximum) vector.mul(maximum / Math.sqrt(lengthSquared));
  }

  private static final class PartMotion {
    private final float frequency;
    private final float dampingRatio;

    private float previousX;
    private float previousZ;
    private float x;
    private float z;
    private float velocityX;
    private float velocityZ;

    private PartMotion(float frequency, float dampingRatio) {
      this.frequency = frequency;
      this.dampingRatio = dampingRatio;
    }

    private void beginTick() {
      previousX = x;
      previousZ = z;
    }

    private void update(float targetX, float targetZ, float pullScale, float frequencyScale) {
      float scaledFrequency = frequency * frequencyScale;
      float damping = 2.0F * dampingRatio * scaledFrequency;
      float stiffness = scaledFrequency * scaledFrequency * pullScale;
      float dt = (float) TICK_SECONDS;
      velocityX += ((targetX - x) * stiffness - velocityX * damping) * dt;
      velocityZ += ((targetZ - z) * stiffness - velocityZ * damping) * dt;
      x = Mth.clamp(x + velocityX * dt, -MAX_ANGLE, MAX_ANGLE);
      z = Mth.clamp(z + velocityZ * dt, -MAX_ANGLE, MAX_ANGLE);
    }

    private float xRot(float partialTick) {
      return Mth.lerp(partialTick, previousX, x);
    }

    private float zRot(float partialTick) {
      return Mth.lerp(partialTick, previousZ, z);
    }
  }
}
*///?}
