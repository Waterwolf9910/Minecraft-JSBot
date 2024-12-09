package com.waterwolfies.js_bot.screen.widgets;

import java.lang.reflect.Field;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class InteractiveTexturedButtonWidget extends TexturedButtonWidget {

    protected Identifier init_texture;
    protected Identifier click_texture;

    public InteractiveTexturedButtonWidget(int x, int y, int width, int height, int u, int v, Identifier texture,
            ButtonWidget.PressAction pressAction) {
        this(x, y, width, height, u, v, height, texture, 256, 256, pressAction);
    }

    public InteractiveTexturedButtonWidget(int x, int y, int width, int height, int u, int v, int hoveredVOffset,
            Identifier texture, ButtonWidget.PressAction pressAction) {
        this(x, y, width, height, u, v, hoveredVOffset, texture, 256, 256, pressAction);
    }

    public InteractiveTexturedButtonWidget(
            int x,
            int y,
            int width,
            int height,
            int u,
            int v,
            int hoveredVOffset,
            Identifier texture,
            int textureWidth,
            int textureHeight,
            ButtonWidget.PressAction pressAction) {
        this(x, y, width, height, u, v, hoveredVOffset, texture, textureWidth, textureHeight, pressAction,
                ScreenTexts.EMPTY);
    }

    public InteractiveTexturedButtonWidget(
            int x,
            int y,
            int width,
            int height,
            int u,
            int v,
            int hoveredVOffset,
            Identifier texture,
            int textureWidth,
            int textureHeight,
            ButtonWidget.PressAction pressAction,
            Text message) {
        super(x, y, width, height, u, v, hoveredVOffset, texture, textureWidth, textureHeight, pressAction, message);
        this.init_texture = texture;
    }

    @Override
    public void onPress() {
        if (click_texture != null) {
            try {
                Field tex_field = this.getClass().getField("texture");
                tex_field.setAccessible(true);
                tex_field.set(this, click_texture);
            } catch (IllegalAccessException | NoSuchFieldException e) {
                e.printStackTrace();
            }
        } else {
            this.alpha = .5f;
        }
        super.onPress();
    }

    @Override
    public void onRelease(double mouseX, double mouseY) {
        this.resetTexture();
        // this.texture = init_texture;
        super.onRelease(mouseX, mouseY);
    }
    
    public void resetTexture() {
        try {
            Field tex_field = this.getClass().getField("texture");
            tex_field.setAccessible(true);
            tex_field.set(this, init_texture);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
        }
        this.alpha = 1f;
    }

    @Override
    public void renderButton(DrawContext context, int mouseX, int mouseY, float delta) {
        // JSBot.LOGGER.info("{} {} {} {} {}", this.isFocused(), this.isSelected(), this.isHovered(), this.active, this.hovered);
        if (!this.hovered && this.texture == this.click_texture) {
            this.resetTexture();
        }
        context.drawTexture(this.texture, this.getX(), this.getY(), this.width, this.height, this.u, this.v, this.textureWidth, this.textureHeight,
                this.textureWidth, this.textureHeight);
    }

    public InteractiveTexturedButtonWidget setClickTexture(Identifier click_texture) {
        this.click_texture = click_texture;
        return this;
    }
}
