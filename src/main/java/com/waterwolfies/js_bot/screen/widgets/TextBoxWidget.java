package com.waterwolfies.js_bot.screen.widgets;

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

    // public EditBox getEditBox() {
    //     try {
    //         Field edit_bot_field = EditBoxWidget.class.getDeclaredField(FabricLoader.getInstance().isDevelopmentEnvironment() ? "editBox" : "field_39509");
    //         edit_bot_field.setAccessible(true);
    //         return (EditBox) edit_bot_field.get(this);
    //     } catch (NoSuchFieldException | IllegalAccessException e) {
    //         e.printStackTrace();
    //     }
    //     return null;
    // }

    public void moveCursor(CursorMovement move, int amount) {
        // EditBox edit_box = getEditBox();
        // if (edit_box == null) {
        //     return;
        // }

        this.editBox.moveCursor(amount, amount);
    }

    public void setCursor(int x, int y) {
        // EditBox edit_box = getEditBox();
        // if (edit_box == null) {
        //     return;
        // }

        this.editBox.moveCursor(x, y);
    }

    public int currentLine() {
        // EditBox edit_box = getEditBox();
        // if (edit_box == null) {
        //     return -1;
        // }

        return this.editBox.getCurrentLineIndex();
    }

    public String getLine(int line) {
        // EditBox edit_box = getEditBox();
        // if (edit_box == null) {
        //     return null;
        // }

        // try {
        //     Class<?> clazz = null;
        //     for (Class<?> c : edit_box.getClass().getClasses()) {
        //         if (c.isInstance(ret)) {
        //             clazz = c;
        //             break;
        //         }
        //     }
        //     if (clazz == null) {
        //         throw new NoSuchFieldException("Unable to find class def");
        //     }
        //     Field begin = clazz.getField(FabricLoader.getInstance().isDevelopmentEnvironment() ? "beginIndex" : "comp_862");
        //     Field end = clazz.getField(FabricLoader.getInstance().isDevelopmentEnvironment() ? "endIndex" : "comp_863");
        //     begin.setAccessible(true);
        //     end.setAccessible(true);
        //     return edit_box.getText().substring((int) begin.get(ret), (int) end.get(ret));
        // } catch (IllegalAccessException | NoSuchFieldException e) {
        //     e.printStackTrace();
        // }
        
        EditBox.Substring sub = this.editBox.getLine(line);
        return this.editBox.getText().substring(sub.beginIndex(), sub.endIndex());
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
