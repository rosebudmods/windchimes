package com.khazoda.windchimes.platform;

import com.khazoda.windchimes.content.WindChimeBlockEntity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
//? if < 1.21.2 {
import java.util.Set;
//?}

public final class ChimeBlockEntityTypes {
  private ChimeBlockEntityTypes() {
  }

  public static BlockEntityType<WindChimeBlockEntity> create(Block... blocks) {
    //? if >= 1.21.2 {
    /*return new BlockEntityType<>(WindChimeBlockEntity::new, blocks);
    *///?} else {
    return new BlockEntityType<>(WindChimeBlockEntity::new, Set.of(blocks), null);
    //?}
  }
}