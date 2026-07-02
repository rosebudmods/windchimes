package com.khazoda.windchimes.content;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
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
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
//? if >= 1.21.9 {
/*import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
*///?}
//? if >= 1.21.5 {
/*import net.minecraft.world.phys.Vec3;
*///?}

public class WindChimeBlockEntityRenderer implements BlockEntityRenderer<WindChimeBlockEntity/*? if >=1.21.9 {*//*, WindChimeBlockEntityRenderer.RenderState*//*?}*/> {
  private static final float LOOP_TICKS = Mth.TWO_PI * 100.0F;
  private final ModelPart platform;
  private final ModelPart rods1;
  private final ModelPart rods2;
  private final ModelPart clapper;

  //? if >= 1.21.9 {
  /*public static final class RenderState extends BlockEntityRenderState {
    private float time;
    private int ringTicks;
    private ResourceLocation textureId;
  }
  *///?}

  public WindChimeBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
    MeshDefinition platformMesh = new MeshDefinition();
    PartDefinition platformRoot = platformMesh.getRoot();
    platformRoot.addOrReplaceChild("hanger", CubeListBuilder.create().texOffs(18, 3).addBox(-0.5F, -1.0F, -0.5F, 1.0F, 1.0F, 1.0F), PartPose.ZERO);
    platformRoot.addOrReplaceChild("platform", CubeListBuilder.create().texOffs(0, 0).addBox(-3.0F, -2.0F, -3.0F, 6.0F, 1.0F, 6.0F), PartPose.ZERO);
    platform = platformRoot.bake(32, 32);
    platform.setPos(8.0F, 16.0F, 8.0F);

    MeshDefinition rods1Mesh = new MeshDefinition();
    PartDefinition rods1Root = rods1Mesh.getRoot();
    rods1Root.addOrReplaceChild("rod1", CubeListBuilder.create().texOffs(0, 7).addBox(-2.0F, -21.0F, -2.0F, 1.0F, 15.0F, 1.0F), PartPose.ZERO);
    rods1Root.addOrReplaceChild("rod2", CubeListBuilder.create().texOffs(12, 7).addBox(1.0F, -15.0F, 1.0F, 1.0F, 9.0F, 1.0F), PartPose.ZERO);
    rods1 = rods1Root.bake(32, 32);
    rods1.setPos(8.0F, 14.0F, 8.0F);

    MeshDefinition rods2Mesh = new MeshDefinition();
    PartDefinition rods2Root = rods2Mesh.getRoot();
    rods2Root.addOrReplaceChild("rod3", CubeListBuilder.create().texOffs(8, 7).addBox(1.0F, -17.0F, -2.0F, 1.0F, 11.0F, 1.0F), PartPose.ZERO);
    rods2Root.addOrReplaceChild("rod4", CubeListBuilder.create().texOffs(4, 7).addBox(-2.0F, -19.0F, 1.0F, 1.0F, 13.0F, 1.0F), PartPose.ZERO);
    rods2 = rods2Root.bake(32, 32);
    rods2.setPos(8.0F, 14.0F, 8.0F);

    MeshDefinition clapperMesh = new MeshDefinition();
    PartDefinition clapperRoot = clapperMesh.getRoot();
    clapperRoot.addOrReplaceChild("clapper", CubeListBuilder.create().texOffs(18, 0).addBox(-1.0F, -13.0F, -1.0F, 2.0F, 1.0F, 2.0F), PartPose.ZERO);
    clapper = clapperRoot.bake(32, 32);
    clapper.setPos(8.0F, 14.0F, 8.0F);
  }

  //? if < 1.21.9 {
  @Override
  public void render(WindChimeBlockEntity entity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay/*? if >=1.21.5 {*//*, Vec3 cameraPos*//*?}*/) {
    Level level = entity.getLevel();
    if (level != null) {
      setupModel((level.getGameTime() % LOOP_TICKS) + partialTick, phase(entity.getBlockPos()), entity.ringTicks);
    }

    VertexConsumer consumer = bufferSource.getBuffer(RenderType.entityCutout(entity.getChimeType().textureId));
    renderModel(poseStack, consumer, packedLight, packedOverlay);
  }
  //?}

  //? if >= 1.21.9 {
  /*@Override
  public RenderState createRenderState() {
    return new RenderState();
  }

  @Override
  public void extractRenderState(WindChimeBlockEntity entity, RenderState state, float partialTick, Vec3 cameraPos, ModelFeatureRenderer.CrumblingOverlay crumblingOverlay) {
    BlockEntityRenderer.super.extractRenderState(entity, state, partialTick, cameraPos, crumblingOverlay);
    Level level = entity.getLevel();
    state.time = ((level == null ? 0L : level.getGameTime()) % LOOP_TICKS) + partialTick;
    state.ringTicks = entity.ringTicks;
    state.textureId = entity.getChimeType().textureId;
  }

  @Override
  public void submit(RenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState cameraState) {
    collector.submitCustomGeometry(poseStack, RenderType.entityCutout(state.textureId), (pose, consumer) -> {
      setupModel(state.time, phase(state.blockPos), state.ringTicks);
      PoseStack submittedPose = new PoseStack();
      submittedPose.last().set(pose);
      renderModel(submittedPose, consumer, state.lightCoords, OverlayTexture.NO_OVERLAY);
    });
  }
  *///?}

  private static float phase(BlockPos pos) {
    return Math.floorMod(pos.getX() * 13 + pos.getY() * 17 + pos.getZ() * 23, 120);
  }

  private void setupModel(float time, float phase, int ringTicks) {
    float platformTime = (time % (LOOP_TICKS / 2.0F)) + phase;
    platform.xRot = Mth.sin(platformTime * 0.04F) * 0.06F;
    platform.zRot = Mth.sin(platformTime * 0.06F) * 0.04F;

    float ringTime = (time + phase - ringTicks) * 0.1F;
    float mediumTime = ringTime * 0.7F;
    float slowTime = ringTime * 0.3F;
    float strength = ringTicks / 50.0F;

    rods1.xRot = Mth.sin(ringTime) * 0.07F * strength;
    rods1.zRot = Mth.cos(mediumTime) * 0.07F * strength;
    rods1.yRot = Mth.cos(slowTime) * 0.5F * (strength + 1.0F);
    rods2.xRot = Mth.cos(mediumTime) * 0.07F * strength;
    rods2.zRot = Mth.sin(ringTime) * 0.07F * strength;
    rods2.yRot = Mth.sin(slowTime) * 0.5F * (strength + 1.0F);
    clapper.xRot = rods1.xRot + rods2.xRot;
    clapper.zRot = rods1.zRot + rods2.zRot;
    clapper.yRot = rods1.yRot + rods2.yRot;
  }

  private void renderModel(PoseStack poseStack, VertexConsumer consumer, int packedLight, int packedOverlay) {
    platform.render(poseStack, consumer, packedLight, packedOverlay);
    rods1.render(poseStack, consumer, packedLight, packedOverlay);
    rods2.render(poseStack, consumer, packedLight, packedOverlay);
    clapper.render(poseStack, consumer, packedLight, packedOverlay);
  }
}