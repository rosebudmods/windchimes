package com.khazoda.windchimes.platform;

import com.khazoda.windchimes.content.WindChimeBlockEntity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
//? if fabric
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
//? if neoforge && < 1.21.2
//import java.util.Set;

public final class ChimeBlockEntityTypes {
  private ChimeBlockEntityTypes() {
  }

  @SuppressWarnings("deprecation")
  public static BlockEntityType<WindChimeBlockEntity> create(Block... blocks) {
    //? if fabric {
    return FabricBlockEntityTypeBuilder.create(WindChimeBlockEntity::new, blocks).build(null);
    //?} elif neoforge && >= 1.21.2 {
    /*return new BlockEntityType<>(WindChimeBlockEntity::new, blocks);*/
    //?} else {
    /*return new BlockEntityType<>(WindChimeBlockEntity::new, Set.of(blocks), null);
    *///?}
  }
}