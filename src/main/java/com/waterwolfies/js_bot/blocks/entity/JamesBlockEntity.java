package com.waterwolfies.js_bot.blocks.entity;

import com.waterwolfies.js_bot.JSBot;
import com.waterwolfies.js_bot.JSBotPersistanceState;
import com.waterwolfies.js_bot.blocks.JamesBlock;
import com.waterwolfies.js_bot.screen.handler.JamesBlockScreenHandler;
import com.waterwolfies.js_bot.utils.IInventory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

// TODO: Implement Energy (prob with coal)
public class JamesBlockEntity extends BlockEntity implements ExtendedScreenHandlerFactory, IInventory {

    private int id = -1;

    protected DefaultedList<ItemStack> items = DefaultedList.ofSize(5, ItemStack.EMPTY);
    public int[] redstone_sides = new int[] { 0, 0, 0, 0, 0, 0 };

    public JamesBlockEntity(BlockPos pos, BlockState state) {
        super(JSBotEntityTypes.JAMES_BLOCK_ENTITY_TYPE, pos, state);
        // ServerPlayNetworking.registerGlobalReceiver(NetInfo.C2S.RUN_JS_CODE, null)
    }

    public static void tick(World world, BlockPos pos, BlockState state, JamesBlockEntity be) {
        if (world.isClient) {
            return;
        }
        if (be.id == -1) {
            be.id = JSBotPersistanceState.getServerState(world.getServer()).last_computer_id.getAndIncrement();
        }
        Path computer_path = JSBot.base_computer_dir.resolve("computer_" + be.id);
        if (!Files.exists(computer_path)) {
            try {
                Files.createDirectories(computer_path);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        nbt.putInt("comp_id", id);
        nbt.putIntArray("redstone_sides", redstone_sides);
        Inventories.writeNbt(nbt, this.items);
        super.writeNbt(nbt);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        this.id = nbt.getInt("comp_id");
        this.redstone_sides = nbt.getIntArray("redstone_sides");
        Inventories.readNbt(nbt, items);
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return items;
    }

    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return new JamesBlockScreenHandler(syncId, playerInventory, this);
    }

    @Override
    public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
        buf.writeBlockPos(this.pos);
        buf.writeInt(this.id);
    }

    protected JamesBlock getCachedBlock() {
        return (JamesBlock) this.getCachedState().getBlock();
    }

    public int getID() {
        return this.id;
    }

    @Override
    public Text getDisplayName() {
        return Text.literal("");
    }

    public Direction getFacing() {
        return this.world.getBlockState(this.pos).get(Properties.FACING);
    }

    public void setFacing(Direction dir) {
        this.world.setBlockState(pos, this.world.getBlockState(this.pos).with(Properties.FACING, dir), Block.NOTIFY_LISTENERS);
    }

    public Direction getCachedFacing() {
        return this.getCachedState().get(Properties.FACING);
    }
}
