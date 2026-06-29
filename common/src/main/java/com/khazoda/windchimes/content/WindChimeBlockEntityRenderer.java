package com.khazoda.windchimes.content;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.MultiBufferSource;
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
*///?}
//? if >= 1.21.5 {
/*import net.minecraft.world.phys.Vec3;
*///?}

public class WindChimeBlockEntityRenderer implements BlockEntityRenderer<WindChimeBlockEntity/*? if >=1.21.9 {*//*, WindChimeBlockEntityRenderer.RenderState*//*?}*/> {
  private final ModelPart platform;
  private final ModelPart rods1;
  private final ModelPart rods2;
  private final ModelPart clapper;

  //? if >= 1.21.9 {
  /*public static final class RenderState extends BlockEntityRenderState {
    private long gameTime;
    private float partialTick;
    private int ringingTicks;
    private float strengthDivisor;
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

  //? if < 1.21.9
  @Override
  public void render(WindChimeBlockEntity entity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay/*? if >=1.21.5 {*//*, Vec3 cameraPos*//*?}*/) {
    Level level = entity.getLevel();
    if (level != null) {
      setupModel(level.getGameTime(), partialTick, entity.ringingTicks, entity.strengthDivisor);
    }

    VertexConsumer consumer = bufferSource.getBuffer(RenderType.entityCutout(entity.getChimeType().textureId));
    renderModel(poseStack, consumer, packedLight, packedOverlay);
  }

  //? if >= 1.21.9 {
  /*@Override
  public RenderState createRenderState() {
    return new RenderState();
  }

  @Override
  public void extractRenderState(WindChimeBlockEntity entity, RenderState state, float partialTick, Vec3 cameraPos, ModelFeatureRenderer.CrumblingOverlay crumblingOverlay) {
    BlockEntityRenderer.super.extractRenderState(entity, state, partialTick, cameraPos, crumblingOverlay);
    Level level = entity.getLevel();
    state.gameTime = level == null ? 0L : level.getGameTime();
    state.partialTick = partialTick;
    state.ringingTicks = entity.ringingTicks;
    state.strengthDivisor = entity.strengthDivisor;
    state.textureId = entity.getChimeType().textureId;
  }

  @Override
  public void submit(RenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState cameraState) {
    collector.submitCustomGeometry(poseStack, RenderType.entityCutout(state.textureId), (pose, consumer) -> {
      setupModel(state.gameTime, state.partialTick, state.ringingTicks, state.strengthDivisor);
      PoseStack submittedPose = new PoseStack();
      submittedPose.last().set(pose);
      renderModel(submittedPose, consumer, state.lightCoords, OverlayTexture.NO_OVERLAY);
    });
  }
  *///?}

  private void setupModel(long gameTime, float partialTick, int ringingTicks, float strengthDivisor) {
    float correctedTicks = (gameTime % 314.15F) + partialTick;
    platform.xRot = Mth.sin(correctedTicks * 0.04F) * 0.06F;
    platform.zRot = Mth.sin(correctedTicks * 0.06F) * 0.04F;

    float sway = ringingTicks + 1.0F;
    float strength = ringingTicks / strengthDivisor;
    float animationTick = ((gameTime % 628.3F) + partialTick - sway) * 0.1F;
    float animationTick7 = animationTick * 0.7F;
    float animationTick3 = animationTick * 0.3F;

    rods1.xRot = Mth.sin(animationTick) * 0.07F * strength;
    rods1.zRot = Mth.cos(animationTick7) * 0.07F * strength;
    rods1.yRot = Mth.cos(animationTick3) * 0.5F * (strength + 1.0F);
    rods2.xRot = Mth.cos(animationTick7) * 0.07F * strength;
    rods2.zRot = Mth.sin(animationTick) * 0.07F * strength;
    rods2.yRot = Mth.sin(animationTick3) * 0.5F * (strength + 1.0F);
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