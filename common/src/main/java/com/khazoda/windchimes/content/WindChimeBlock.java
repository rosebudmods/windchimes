package com.khazoda.windchimes.content;

import com.khazoda.windchimes.registry.MainRegistry;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
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
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
//? if >= 1.21.2 {
/*import net.minecraft.world.level.redstone.Orientation;
*///?}
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class WindChimeBlock extends BaseEntityBlock {
  public static final MapCodec<WindChimeBlock> CODEC = simpleCodec(properties -> new WindChimeBlock(ChimeType.INVALID, properties));
  private static final VoxelShape SHAPE = Block.box(4.0, 8.0, 4.0, 12.0, 16.0, 12.0);
  private static final BooleanProperty POWERED = BlockStateProperties.POWERED;
  private final ChimeType chimeType;

  public WindChimeBlock(ChimeType chimeType, Properties properties) {
    super(properties);
    this.chimeType = chimeType;
    registerDefaultState(defaultBlockState().setValue(POWERED, false));
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
    float direction = (float) Mth.atan2(hit.getLocation().z - player.getZ(), hit.getLocation().x - player.getX());
    boolean rang = level.getBlockEntity(pos) instanceof WindChimeBlockEntity chime && chime.tryRing(!player.isShiftKeyDown(), direction);
    //? if >= 1.21.2 {
    /*return rang ? InteractionResult.SUCCESS_SERVER : InteractionResult.CONSUME;
    *///?} else {
    return rang ? InteractionResult.SUCCESS : InteractionResult.CONSUME;
    //?}
  }

  @Override
  protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighbor, /*? if >=1.21.2 {*//*Orientation orientation*//*?} else {*/BlockPos neighborPos/*?}*/, boolean movedByPiston) {
    updatePower(state, level, pos);
  }

  private void updatePower(BlockState state, Level level, BlockPos pos) {
    boolean powered = level.hasNeighborSignal(pos);
    if (powered == state.getValue(POWERED)) return;
    level.setBlock(pos, state.setValue(POWERED, powered), Block.UPDATE_CLIENTS);
    if (!powered || !(level.getBlockEntity(pos) instanceof WindChimeBlockEntity chime)) return;

    Direction source = Direction.UP;
    int strongestSignal = level.getBestNeighborSignal(pos);
    for (Direction direction : Direction.values()) {
      if (level.getSignal(pos.relative(direction), direction) == strongestSignal) {
        source = direction;
        break;
      }
    }
    float direction = source.getStepY() == 0
        ? (float) Mth.atan2(-source.getStepZ(), -source.getStepX())
        : level.random.nextFloat() * Mth.TWO_PI;
    chime.tryRing(true, direction);
  }

  @Override
  protected boolean hasAnalogOutputSignal(BlockState state) {
    return true;
  }

  @Override
  protected int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos/*? if >=1.21.9 {*//*, Direction direction*//*?}*/) {
    return level.getBlockEntity(pos) instanceof WindChimeBlockEntity chime && chime.isRinging() ? 15 : 0;
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
  protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
    builder.add(POWERED);
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