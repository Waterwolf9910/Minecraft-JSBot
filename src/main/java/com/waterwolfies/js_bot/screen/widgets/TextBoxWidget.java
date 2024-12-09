package com.waterwolfies.js_bot.screen.widgets;

import java.lang.reflect.Field;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.EditBox;
import net.minecraft.client.gui.widget.EditBoxWidget;
import net.minecraft.client.input.CursorMovement;
import net.minecraft.text.Text;

public class TextBoxWidget extends EditBoxWidget {
    
    protected KeyPressedListener kp_listener;
    protected CharTypedListener ct_listener;

    public TextBoxWidget(TextRenderer textRenderer, int x, int y, int width, int height, Text placeholder, Text message) {
        super(textRenderer, x, y, width, height, placeholder, message);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (kp_listener != null && kp_listener.onKeyPress(keyCode, scanCode, modifiers)) {
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        if (ct_listener != null && ct_listener.onCharType(chr, modifiers)) {
            return true;
        }
        return super.charTyped(chr, modifiers);
    }

    public void setCTListener(CharTypedListener listener) {
        this.ct_listener = listener;
    }

    public void setKPListener(KeyPressedListener listener) {
        this.kp_listener = listener;
    }

    public EditBox getEditBox() {
        try {
            Field edit_bot_field = EditBoxWidget.class.getDeclaredField("editBox");
            edit_bot_field.setAccessible(true);
            return (EditBox) edit_bot_field.get(this);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void moveCursor(CursorMovement move, int amount) {
        EditBox edit_box = getEditBox();
        if (edit_box == null) {
            return;
        }

        edit_box.moveCursor(amount, amount);
    }

    public void setCursor(int x, int y) {
        EditBox edit_box = getEditBox();
        if (edit_box == null) {
            return;
        }

        edit_box.moveCursor(x, y);
    }

    public int currentLine() {
        EditBox edit_box = getEditBox();
        if (edit_box == null) {
            return -1;
        }

        return edit_box.getCurrentLineIndex();
    }

    public String getLine(int line) {
        EditBox edit_box = getEditBox();
        if (edit_box == null) {
            return null;
        }

        try {
            var ret = edit_box.getLine(line);
            Class<?> clazz = null;
            for (Class<?> c : edit_box.getClass().getClasses()) {
                if (c.isInstance(ret)) {
                    clazz = c;
                    break;
                }
            }
            if (clazz == null) {
                throw new NoSuchFieldException("Unable to find class def");
            }
            Field begin = clazz.getField("beginIndex");
            Field end = clazz.getField("endIndex");
            begin.setAccessible(true);
            end.setAccessible(true);
            return edit_box.getText().substring((int) begin.get(ret), (int) end.get(ret));
        } catch (IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
        }
        
        return null;
    }

    public interface KeyPressedListener {
        /**
         * @return true to interupt normal operation
         */
        public boolean onKeyPress(int keyCode, int scanCode, int modifier);
    }

    public interface CharTypedListener {
        /**
         * @return true to interupt normal operation
         */
        public boolean onCharType(char chr, int modifiers);
    }
}
