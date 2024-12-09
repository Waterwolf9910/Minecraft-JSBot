package com.waterwolfies.js_bot.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.gui.widget.ClickableWidget;

@Mixin(ClickableWidget.class)
public interface ClickableWidgetIMixin {
    
    @Accessor("height")
    public void setHeight(int height);
}
