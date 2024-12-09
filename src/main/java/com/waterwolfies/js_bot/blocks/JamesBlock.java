package com.waterwolfies.js_bot.blocks;

import com.waterwolfies.js_bot.blocks.entity.JSBotEntityTypes;
import com.waterwolfies.js_bot.blocks.entity.JamesBlockEntity;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.Blocks;
import net.minecraft.block.MapColor;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.enums.Instrument;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.state.StateManager.Builder;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class JamesBlock extends BlockWithEntity {
    
    protected boolean is_opened = false;
    
    public JamesBlock() {
        super(Settings
            .create()
            .allowsSpawning((a, b , c, d) -> false)
            .suffocates((a, b, c) -> false)
            .instrument(Instrument.PLING)
            .solid()
            .noCollision()
            .pistonBehavior(PistonBehavior.PUSH_ONLY)
            .dropsLike(Blocks.ANVIL)
            .strength(5.0f, 9f)
            .mapColor(MapColor.GRAY)
            // .pistonBehavior(PistonBehavior.DESTROY)
        );
        setDefaultState(getDefaultState().with(Properties.FACING, Direction.NORTH));
    }

    @Override
    protected void appendProperties(Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(Properties.FACING);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return super.getPlacementState(ctx).with(Properties.FACING, ctx.getPlayerLookDirection().getOpposite());
    }
    
    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        // super.onUse(state, world, pos, player, hand, hit);
        // is_opened.

        if (!world.isClient()) {
            NamedScreenHandlerFactory factory = state.createScreenHandlerFactory(world, pos);

            if (factory != null) {
                player.openHandledScreen(factory);
            }
        }

        return ActionResult.SUCCESS;
    }
    
    @Override
    public void appendTooltip(ItemStack stack, BlockView world, List<Text> tooltip, TooltipContext options) {
        NbtCompound nbt = BlockItem.getBlockEntityNbt(stack);
        if (nbt == null) {
            return;
        }

        tooltip.add(Text.literal("Computer ID: " + nbt.getInt("id")));
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new JamesBlockEntity(pos, state);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return checkType(type, JSBotEntityTypes.JAMES_BLOCK_ENTITY_TYPE, JamesBlockEntity::tick);
    }
    
    @Override
    public boolean emitsRedstonePower(BlockState state) {
        return true;
    }

    @Override
    public int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        return ((JamesBlockEntity) world.getBlockEntity(pos)).redstone_sides[direction.getId()];
        // return super.getWeakRedstonePower(state, world, pos, direction);
    }

}
