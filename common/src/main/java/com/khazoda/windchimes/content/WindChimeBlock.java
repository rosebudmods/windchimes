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
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class WindChimeBlock extends BaseEntityBlock {
  public static final MapCodec<WindChimeBlock> CODEC = simpleCodec(properties -> new WindChimeBlock(ChimeType.INVALID, properties));

  private static final VoxelShape SHAPE = Block.box(4.0, 8.0, 4.0, 12.0, 16.0, 12.0);

  private final ChimeType chimeType;

  public WindChimeBlock(ChimeType chimeType, Properties properties) {
    super(properties);
    this.chimeType = chimeType;
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
  //? if >= 1.21.2 {
  /*protected BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess tickAccess, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, RandomSource random) {
    if (level.isEmptyBlock(pos.above())) {
      BlockEntity entity = level.getBlockEntity(pos);
      if (entity != null) {
        entity.setRemoved();
      }
      return Blocks.AIR.defaultBlockState();
    }
    return super.updateShape(state, level, tickAccess, pos, direction, neighborPos, neighborState, random);
  }
  *///?} else {
  protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
    if (level.isEmptyBlock(pos.above())) {
      BlockEntity entity = level.getBlockEntity(pos);
      if (entity != null) {
        entity.setRemoved();
      }
      return Blocks.AIR.defaultBlockState();
    }
    return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
  }
  //?}

  @Override
  protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
    return !level.isEmptyBlock(pos.above()) && level.isEmptyBlock(pos.below());
  }

  @Override
  protected RenderShape getRenderShape(BlockState state) {
    return RenderShape.INVISIBLE;
  }

  @Nullable
  @Override
  public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
    return new WindChimeBlockEntity(pos, state);
  }

  @Nullable
  @Override
  public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
    return createTickerHelper(type, MainRegistry.CHIME_BLOCK_ENTITY.get(), WindChimeBlockEntity::tick);
  }

  @Override
  protected boolean triggerEvent(BlockState state, Level level, BlockPos pos, int type, int data) {
    BlockEntity entity = level.getBlockEntity(pos);
    return entity != null && entity.triggerEvent(type, data);
  }

  @Override
  protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
    if (level.getBlockEntity(pos) instanceof WindChimeBlockEntity chime) {
      chime.ring(!player.isShiftKeyDown());
      chime.ticksToNextRing += 4;
    }
    //? if >= 1.21.2 {
    /*return InteractionResult.SUCCESS;
    *///?} else {
    return InteractionResult.sidedSuccess(level.isClientSide);
    //?}
  }

  public ChimeType getChimeType() {
    return chimeType;
  }
}