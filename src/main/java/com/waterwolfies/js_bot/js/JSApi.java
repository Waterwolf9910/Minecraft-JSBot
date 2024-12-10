package com.waterwolfies.js_bot.js;

import com.waterwolfies.js_bot.blocks.entity.JamesBlockEntity;
import com.waterwolfies.js_bot.mixin.WorldMixin;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class JSApi {

    protected JamesBlockEntity be;
    protected boolean moved = false;

    public JSApi(JamesBlockEntity be) {
        this.be = be;
    }

    public JSBlockPos getBlockPos() {
        return new JSBlockPos(this.be.getPos());
    }

    public String getFacing() {
        return this.be.getFacing().asString();
    }

    public boolean move(String dir) {
        
        BlockPos pos = this.be.getPos().offset(Direction.byName(dir.toLowerCase()));
        if (!this.be.getWorld().testBlockState(pos, state -> state.isOf(Blocks.AIR))) {
            return false;
        }
        ((WorldMixin) (Object) this.be.getWorld()).moveBlockEntity(this.be, pos);
        return true;
    }

    public void face(String dir) {
        this.be.setFacing(Direction.byName(dir.toLowerCase()));
    }

    public void setRedstoneOutput(String dir, int value) {
        this.be.redstone_sides[Direction.byName(dir.toLowerCase()).getId()] = Math.max(Math.min(value, 15), 0);
    }

    public BlockBreakState breakBlock() {
        World world = this.be.getWorld();
        BlockPos break_pos = this.be.getPos().offset(this.be.getFacing());
        BlockState state = world.getBlockState(break_pos);
        ItemStack equipped_item = this.be.getStack(0);
        
        if (canHarvest(state, equipped_item) && world.breakBlock(break_pos, false)) {
            return new BlockBreakState(false, "", equipped_item.getMaxDamage() - equipped_item.getDamage(), new JSBlockPos(break_pos));
        }

        int durability = 0;
        if (!equipped_item.damage(1, this.be.getWorld().random, null)) {
            durability = equipped_item.getMaxDamage() - equipped_item.getDamage();
        }
        return new BlockBreakState(true, BlockEntityType.getId(this.be.getType()).toString(), durability, new JSBlockPos(break_pos));
    }

    public BlockPlaceState placeBlock(int inventory_slot) {
        BlockPos place_pos = this.be.getPos().offset(this.be.getFacing());
        ItemStack stack = this.be.getStack(inventory_slot);
        World world = this.be.getWorld();
        if (!world.canSetBlock(place_pos) || stack == null) {
            return new BlockPlaceState(false, stack != null ? stack.getCount() : -1, new JSBlockPos(place_pos));
        }
        Block block = Block.getBlockFromItem(stack.getItem());

        if (block == null) {
            return new BlockPlaceState(false, stack.getCount(), new JSBlockPos(place_pos));
        }

        if (!world.canPlace(block.getDefaultState(), place_pos, ShapeContext.absent())) {
            return new BlockPlaceState(false, stack.getCount(), new JSBlockPos(place_pos));
        }

        stack.setCount(stack.getCount() - 1);

        return new BlockPlaceState(true, stack.getCount(), new JSBlockPos(place_pos));
    }

    public JSItemStack[] getInventory() {
        List<JSItemStack> list = new ArrayList<>();
        var items = this.be.getItems();
        for (ItemStack stack : items) {
            list.add(new JSItemStack(stack));
        }
        JSItemStack[] s = new JSItemStack[list.size()];
        return list.toArray(s);
    }

    // TODO: NBT -> JSON
    public class JSItemStack {
        public final String id;
        public final String name;
        public final int count;
        public final int max_count;
        public final int durability;
        public final boolean is_food;
        public final boolean is_enchantable;
        public final JSItemStack recipe_remainder;

        public JSItemStack(ItemStack stack) {
            this.id = Registries.ITEM.getId(stack.getItem()).toString();
            this.name = stack.getName().getString();
            this.count = stack.getCount();
            this.max_count = stack.getMaxCount();
            this.durability = stack.getMaxDamage() - stack.getDamage();
            this.is_food = stack.isFood();
            this.is_enchantable = stack.isEnchantable();
            ItemStack rr = stack.getRecipeRemainder();
            if (rr != null && !stack.isEmpty()) {
                recipe_remainder = new JSItemStack(rr);
            } else {
                recipe_remainder = null;
            }
        }

        @Override
        public String toString() {
            return this.id;
        }
    }

    public class JSBlockPos {
        public final int x;
        public final int y;
        public final int z;

        public JSBlockPos(BlockPos pos) {
            this.x = pos.getX();
            this.y = pos.getY();
            this.z = pos.getZ();
        }

        @Override
        public String toString() {
            return "(" + x + ", " + y + ", " + z + ")";
        }
    }

    protected boolean canHarvest(BlockState state, ItemStack stack) {
        return !state.isToolRequired() || stack.isSuitableFor(state);
    }

    public record BlockBreakState(boolean success, String id, int durability, JSBlockPos pos) {}

    public record BlockPlaceState(boolean success, int count_remaining, JSBlockPos pos) {}

}
