package com.khazoda.windchimes.content;

import com.khazoda.windchimes.registry.MainRegistry;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
//? if >= 1.21.2 {
/*import net.minecraft.util.RandomSource;
*///?} else {
import net.minecraft.world.level.LevelAccessor;
//?}
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
//? if >= 1.21.2 {
/*import net.minecraft.world.level.ScheduledTickAccess;
*///?}
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class WindChimeBlock extends BaseEntityBlock {
  public static final MapCodec<WindChimeBlock> CODEC = simpleCodec(properties -> new WindChimeBlock(ChimeType.INVALID, properties));
  private static final VoxelShape SHAPE = Block.box(4.0, 8.0, 4.0, 12.0, 16.0, 12.0);
  private final ChimeType chimeType;

  public WindChimeBlock(ChimeType chimeType, Properties properties) {
    super(properties);
    this.chimeType = chimeType;
  }

  @Override
  protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
    return !level.isEmptyBlock(pos.above()) && level.isEmptyBlock(pos.below());
  }

  @Override
  //? if >= 1.21.2 {
  /*protected BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess tickAccess, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, RandomSource random) {
    return state.canSurvive(level, pos) ? state : Blocks.AIR.defaultBlockState();
  }
  *///?} else {
  protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
    return state.canSurvive(level, pos) ? state : Blocks.AIR.defaultBlockState();
  }
  //?}

  @Override
  protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
    if (level.getBlockEntity(pos) instanceof WindChimeBlockEntity chime) {
      chime.interact(!player.isShiftKeyDown());
    }
    //? if >= 1.21.2 {
    /*return InteractionResult.SUCCESS;
    *///?} else {
    return InteractionResult.sidedSuccess(level.isClientSide);
    //?}
  }

  ChimeType getChimeType() {
    return chimeType;
  }

  @Override
  public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
    return new WindChimeBlockEntity(pos, state);
  }

  @Override
  protected MapCodec<? extends WindChimeBlock> codec() {
    return CODEC;
  }

  @Override
  protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
    return SHAPE;
  }

  @Override
  public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
    return createTickerHelper(type, MainRegistry.CHIME_BLOCK_ENTITY.get(), WindChimeBlockEntity::tick);
  }
}