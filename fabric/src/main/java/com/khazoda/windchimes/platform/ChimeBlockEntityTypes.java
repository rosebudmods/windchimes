package com.khazoda.windchimes.platform;

import com.khazoda.windchimes.content.WindChimeBlockEntity;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;

public final class ChimeBlockEntityTypes {
  private ChimeBlockEntityTypes() {
  }

  public static BlockEntityType<WindChimeBlockEntity> create(Block... blocks) {
    return FabricBlockEntityTypeBuilder.create(WindChimeBlockEntity::new, blocks).build();
  }
}