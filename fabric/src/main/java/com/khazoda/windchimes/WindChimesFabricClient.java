package com.khazoda.windchimes;

import com.khazoda.windchimes.content.WindChimeBlockEntityRenderer;
import com.khazoda.windchimes.registry.MainRegistry;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;

public class WindChimesFabricClient implements ClientModInitializer {
  @Override
  public void onInitializeClient() {
    BlockEntityRenderers.register(MainRegistry.CHIME_BLOCK_ENTITY.get(), WindChimeBlockEntityRenderer::new);
  }
}