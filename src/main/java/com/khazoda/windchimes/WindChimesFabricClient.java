package com.khazoda.windchimes;

//? if fabric {
import com.khazoda.windchimes.content.WindChimeBlockEntityRenderer;
import com.khazoda.windchimes.registry.MainRegistry;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
//?}

public class WindChimesFabricClient implements ClientModInitializer {
  @Override
  @SuppressWarnings("deprecation")
  public void onInitializeClient() {
    //? if >= 1.21.9 {
    /*BlockEntityRenderers.register(MainRegistry.CHIME_BLOCK_ENTITY.get(), WindChimeBlockEntityRenderer::new);
    *///?} else {
    BlockEntityRendererRegistry.register(MainRegistry.CHIME_BLOCK_ENTITY.get(), WindChimeBlockEntityRenderer::new);
    //?}
  }
}
//?}