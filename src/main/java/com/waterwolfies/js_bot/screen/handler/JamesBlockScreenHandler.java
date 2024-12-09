package com.waterwolfies.js_bot.screen.handler;

import com.waterwolfies.js_bot.blocks.entity.JamesBlockEntity;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.util.math.BlockPos;

public class JamesBlockScreenHandler extends ScreenHandler {

    protected BlockPos pos;
    protected int id; 
    private final Inventory inventory;

    // Client inventory gets synced
    public JamesBlockScreenHandler(int syncId, PlayerInventory player_inventory, PacketByteBuf buf) {
        this(syncId, player_inventory, new SimpleInventory(5));
        this.pos = buf.readBlockPos();
        this.id = buf.readInt();
    }

    public JamesBlockScreenHandler(int syncId, PlayerInventory player_inventory, Inventory inventory) {
        super(JSBotScreenHandlerTypes.JAMES_BLOCK_SCREEN_HANDER, syncId);
        if (inventory instanceof JamesBlockEntity e) { // On Server
            this.pos = e.getPos();
            this.id = e.getID();
        }
        this.inventory = inventory;
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int slot) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'quickMove'");
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return this.inventory.canPlayerUse(player);
    }

    public BlockPos getPos() {
        return this.pos;
    }

    public int getID() {
        return this.id;
    }
}
