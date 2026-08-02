package com.khazoda.windchimes.content;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
//? if < 1.21.9 {
import net.minecraft.client.renderer.MultiBufferSource;
//?}
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
//? if >= 1.21.9 {
/*import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
*///?}
//? if >= 1.21.5 {
/*import net.minecraft.world.phys.Vec3;*/
//?}

//? if >= 1.21.9 {
/*public class WindChimeBlockEntityRenderer implements BlockEntityRenderer<WindChimeBlockEntity, WindChimeBlockEntityRenderer.RenderState> {
*///?} else {
public class WindChimeBlockEntityRenderer implements BlockEntityRenderer<WindChimeBlockEntity> {
//?}
private static final float AMBIENT_LOOP_TICKS = Mth.TWO_PI * 100.0F;
  private static final float[] ROD_SWING_SPEEDS = {0.75F, 1.05F, 0.90F, 0.85F};
  private static final float[] ROD_HEIGHTS = {16.0F, 17.5F, 16.5F, 17.0F};
  private final ModelPart model;
  private final ModelPart platform;
  private final ModelPart[] rods;
  private final ModelPart clapper;
  //? if >= 1.21.9 {
  /*private final PoseStack submittedPose = new PoseStack();*/
  //?}

  public WindChimeBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
    MeshDefinition mesh = new MeshDefinition();
    PartDefinition root = mesh.getRoot();
    root.addOrReplaceChild("platform", CubeListBuilder.create().texOffs(18, 3)
        .addBox(-0.5F, -1.0F, -0.5F, 1.0F, 1.0F, 1.0F).texOffs(0, 0)
        .addBox(-3.0F, -2.0F, -3.0F, 6.0F, 1.0F, 6.0F), PartPose.offset(8.0F, 16.0F, 8.0F));
    root.addOrReplaceChild("rod1", CubeListBuilder.create().texOffs(0, 7)
        .addBox(-0.5F, -21.0F, -0.5F, 1.0F, 15.0F, 1.0F), PartPose.offset(6.5F, ROD_HEIGHTS[0], 6.5F));
    root.addOrReplaceChild("rod2", CubeListBuilder.create().texOffs(12, 7)
        .addBox(-0.5F, -15.0F, -0.5F, 1.0F, 9.0F, 1.0F), PartPose.offset(9.5F, ROD_HEIGHTS[1], 9.5F));
    root.addOrReplaceChild("rod3", CubeListBuilder.create().texOffs(8, 7)
        .addBox(-0.5F, -17.0F, -0.5F, 1.0F, 11.0F, 1.0F), PartPose.offset(9.5F, ROD_HEIGHTS[2], 6.5F));
    root.addOrReplaceChild("rod4", CubeListBuilder.create().texOffs(4, 7)
        .addBox(-0.5F, -19.0F, -0.5F, 1.0F, 13.0F, 1.0F), PartPose.offset(6.5F, ROD_HEIGHTS[3], 9.5F));
    root.addOrReplaceChild("clapper", CubeListBuilder.create().texOffs(18, 0)
        .addBox(-1.0F, -13.0F, -1.0F, 2.0F, 1.0F, 2.0F), PartPose.offset(8.0F, 17.0F, 8.0F));
    model = root.bake(32, 32);
    platform = model.getChild("platform");
    clapper = model.getChild("clapper");
    rods = new ModelPart[4];
    for (int i = 0; i < rods.length; i++) rods[i] = model.getChild("rod" + (i + 1));
  }

  private static float ambientTime(WindChimeBlockEntity entity, float partialTick) {
    Level level = entity.getLevel();
    return ((level == null ? 0L : level.getGameTime()) % AMBIENT_LOOP_TICKS) + partialTick + WindChimeBlockEntity.animationOffset(entity.getBlockPos());
  }
  //? if >= 1.21.9 {
  /*public static final class RenderState extends BlockEntityRenderState {
    private ResourceLocation textureId;
    private float ambientTime;
    private float swingTime;
    private float swingStrength;
    private float ringDirection;
    private float floatiness;
    private int seedForAnimation;
  }*/
  //?}

  //? if >= 1.21.9 {
  /*@Override
  public RenderState createRenderState() {
    return new RenderState();
  }

  @Override
  public void extractRenderState(WindChimeBlockEntity entity, RenderState state, float partialTick, Vec3 cameraPos, @Nullable ModelFeatureRenderer.CrumblingOverlay crumblingOverlay) {
    BlockEntityRenderer.super.extractRenderState(entity, state, partialTick, cameraPos, crumblingOverlay);
    state.ambientTime = ambientTime(entity, partialTick);
    state.swingTime = entity.getSwingTime(partialTick);
    state.swingStrength = entity.getSwingStrength(partialTick);
    state.ringDirection = entity.ringDirection;
    state.floatiness = entity.getChimeType().floatiness();
    state.seedForAnimation = entity.seedForAnimation;
    state.textureId = entity.getChimeType().textureId();
  }

  @Override
  public void submit(RenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState cameraState) {
    collector.submitCustomGeometry(poseStack, RenderType.entityCutout(state.textureId), (pose, consumer) -> {
      int heightPermutation = Math.floorMod(state.blockPos.hashCode(), 24);
      animateModel(state.ambientTime, state.swingTime, state.swingStrength, state.ringDirection, state.seedForAnimation, heightPermutation);
      scaleMotion(state.floatiness);
      submittedPose.last().set(pose);
      model.render(submittedPose, consumer, state.lightCoords, OverlayTexture.NO_OVERLAY);
    });
  }*/
  //?}

  private static void animateRod(ModelPart rod, float time, float direction, int seed, int index, float swingStrength) {
    float variation = Mth.sin(seed + index * 2.0F);
    float staggeredTime = Math.max(0.0F, time - index * 0.15F);
    float swing = pendulumSwing(staggeredTime, ROD_SWING_SPEEDS[index] + variation * 0.07F) * (0.08F + variation * 0.007F) * swingStrength;
    float rodDirection = direction + (index - 1.5F) * 0.12F;
    rod.xRot = swing * Mth.cos(rodDirection);
    rod.zRot = swing * Mth.sin(rodDirection);
    float spinRamp = staggeredTime / (staggeredTime + 0.4F);
    rod.yRot = Mth.sin(staggeredTime * (0.3F + variation * 0.08F) + seed + index)
        * (0.08F + swingStrength * (0.05F + variation * 0.01F)) * spinRamp * spinRamp;
  }

  private static float pendulumSwing(float time, float speed) {
    return Mth.sin(time * speed) * time / (time + 0.4F);
  }

  //? if < 1.21.9 {
  @Override
  //? if >= 1.21.5 {
  /*public void render(WindChimeBlockEntity entity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay, Vec3 cameraPos) {*/
  //?} else {
  public void render(WindChimeBlockEntity entity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
  //?}
    int heightPermutation = Math.floorMod(entity.getBlockPos().hashCode(), 24);
    animateModel(ambientTime(entity, partialTick), entity.getSwingTime(partialTick), entity.getSwingStrength(partialTick), entity.ringDirection, entity.seedForAnimation, heightPermutation);
    scaleMotion(entity.getChimeType().floatiness());
    //? if =1.21.1 {
    /*applySableMotion(entity, partialTick);
    *///?}
    model.render(poseStack, bufferSource.getBuffer(RenderType.entityCutout(entity.getChimeType().textureId())), packedLight, packedOverlay);
  }
  //?}

  //? if =1.21.1 {
  /*private void applySableMotion(WindChimeBlockEntity entity, float partialTick) {
    WindChimeSable motion = entity.getSableMotion();
    if (motion == null) return;

    platform.xRot += motion.xRot(WindChimeSable.PLATFORM, partialTick);
    platform.zRot += motion.zRot(WindChimeSable.PLATFORM, partialTick);
    for (int i = 0; i < rods.length; i++) {
      rods[i].xRot += motion.xRot(WindChimeSable.FIRST_ROD + i, partialTick);
      rods[i].zRot += motion.zRot(WindChimeSable.FIRST_ROD + i, partialTick);
    }
    clapper.xRot += motion.xRot(WindChimeSable.CLAPPER, partialTick);
    clapper.zRot += motion.zRot(WindChimeSable.CLAPPER, partialTick);
  }
  *///?}

  private void scaleMotion(float scale) {
    platform.xRot *= scale;
    platform.zRot *= scale;
    for (ModelPart rod : rods) {
      rod.xRot *= scale;
      rod.yRot *= scale;
      rod.zRot *= scale;
    }
    clapper.xRot *= scale;
    clapper.zRot *= scale;
  }

  private void animateModel(float ambientTime, float swingTime, float swingStrength, float direction, int seed, int heightPermutation) {
    float platformSwing = pendulumSwing(swingTime, 0.7F) * 0.025F * swingStrength;
    platform.xRot = Mth.sin(ambientTime * 0.02F) * 0.03F + platformSwing * Mth.cos(direction);
    platform.zRot = Mth.sin(ambientTime * 0.03F) * 0.02F + platformSwing * Mth.sin(direction);

    /* Rod heights randomized between 4 preset heights in ROD_HEIGHTS for 24 possible combinations */
    for (int i = 0; i < rods.length; i++) rods[i].y = ROD_HEIGHTS[i];
    for (int i = 0; i < rods.length - 1; i++) {
      int swap = i + heightPermutation % (rods.length - i);
      float height = rods[i].y;
      rods[i].y = rods[swap].y;
      rods[swap].y = height;
      heightPermutation /= rods.length - i;
    }
    for (int i = 0; i < rods.length; i++) animateRod(rods[i], swingTime, direction, seed, i, swingStrength);

    float swing = pendulumSwing(swingTime, 0.9F) * 0.055F * swingStrength;
    float crossSwing = pendulumSwing(swingTime, 1.8F) * 0.006F * swingStrength;
    clapper.xRot = swing * Mth.cos(direction) - crossSwing * Mth.sin(direction);
    clapper.zRot = swing * Mth.sin(direction) + crossSwing * Mth.cos(direction);
  }
}