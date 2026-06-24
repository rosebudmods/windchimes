package com.khazoda.windchimes;

import com.khazoda.windchimes.content.WindChimeBlockEntityRenderer;
import com.khazoda.windchimes.registry.MainRegistry;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

public final class WindChimesNeoForgeClient {

  public static void register(IEventBus eventBus) {
    eventBus.addListener(WindChimesNeoForgeClient::registerRenderers);
  }

  private WindChimesNeoForgeClient() {
  }

  private static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
    event.registerBlockEntityRenderer(MainRegistry.CHIME_BLOCK_ENTITY.get(), WindChimeBlockEntityRenderer::new);
  }
}