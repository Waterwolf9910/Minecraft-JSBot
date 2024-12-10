package com.waterwolfies.js_bot.screen;

import com.waterwolfies.js_bot.JSBot;
import com.waterwolfies.js_bot.network.NetInfo;
import com.waterwolfies.js_bot.screen.handler.JamesBlockScreenHandler;
import com.waterwolfies.js_bot.screen.widgets.InteractiveTexturedButtonWidget;
import com.waterwolfies.js_bot.screen.widgets.TextBoxWidget;

import java.util.Arrays;
import java.util.List;

import com.mojang.blaze3d.systems.RenderSystem;

import org.lwjgl.glfw.GLFW;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class JamesBlockScreen extends HandledScreen<JamesBlockScreenHandler> {

    public static Identifier save_button = Identifier.of(JSBot.MOD_ID, "textures/ui/save_button.png");
    public static Identifier pressed_save_button = Identifier.of(JSBot.MOD_ID, "textures/ui/save_button_pressed.png");

    public static Identifier exit_button = Identifier.of(JSBot.MOD_ID, "textures/ui/exit_button.png");
    public static Identifier pressed_exit_button = Identifier.of(JSBot.MOD_ID, "textures/ui/exit_button_pressed.png");

    public static Identifier run_button = Identifier.of(JSBot.MOD_ID, "textures/ui/run_button.png");
    public static Identifier pressed_run_button = Identifier.of(JSBot.MOD_ID, "textures/ui/run_button_pressed.png");

    public static Identifier background = Identifier.of(JSBot.MOD_ID, "textures/ui/jsbot_bg.png");

    // private final Screen parent;

    public TextBoxWidget command_line;
    public TextBoxWidget logs;
    public List<String> file_list;
    protected String file_name;
    protected Mode mode = Mode.COMMAND;

    public JamesBlockScreen(JamesBlockScreenHandler handler, PlayerInventory inventory, Text _t) {
        // super(handler, inventory, Text.translatable("container.jsbot.james_block"));
        super(handler, inventory, Text.literal(""));
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeInt(getID());
        ClientPlayNetworking.send(NetInfo.C2S.GRAB_FILE_LIST, buf);
        // this.parent = parent;
    }

    public void runCommand(String command_line) {
        String[] args = command_line.split(" ");
        String command = args[0];
        args = Arrays.copyOfRange(args, 1, args.length);

        switch (command) {
            case "edit": {
                if (args.length < 1) {
                    this.addLog("Missing file_name\n");
                }
                this.edit(args[0]);
                break;
            }
            case "run": {
                if (args.length < 1) {
                    this.addLog("Missing file_name\n");
                    return;
                }
                this.run(args[0]);
                break;
            }
            case "clear": {
                this.command_line.setText("");
                this.logs.setText("");
                break;
            }
            default: {
                this.addLog("No Such Command: " + command + "\n");
            }
        }
    }

    public void run(String file_name) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeInt(this.getID());
        buf.writeString(file_name);
        buf.writeBlockPos(this.handler.getPos());
        ClientPlayNetworking.send(NetInfo.C2S.RUN_JS_CODE, buf);
        this.mode = Mode.RUN;
    }

    public void edit(String file_name) {
        this.file_name = file_name;
        this.mode = Mode.EDIT;
        this.command_line.setCursor(0, 0);
        if (this.file_list != null && this.file_list.contains(file_name)) {
            this.command_line.setText("Loading...");
            this.mode = Mode.WAIT;
            PacketByteBuf buf = PacketByteBufs.create();
            buf.writeInt(this.getID());
            buf.writeString(file_name);
            ClientPlayNetworking.send(NetInfo.C2S.LOAD_FILE, buf);
            return;
        }
        this.command_line.setText("");
    }

    public void save() {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeInt(this.getID());
        buf.writeString(this.file_name);
        buf.writeString(this.command_line.getText());
        ClientPlayNetworking.send(NetInfo.C2S.SAVE_JS_FILE, buf);
    }

    public void cancel() {
        this.command_line.setText("");
        this.command_line.setCursor(0, 0);
        this.mode = Mode.COMMAND;
        this.file_name = null;
    }

    @Override
    protected void init() {
        super.init();
        this.playerInventoryTitle = Text.literal("");
        // this.playerInventoryTitle = Text.literal("");
        // try {
        //     for (Field field : HandledScreen.class.getFields()) {
        //         JSBot.LOGGER.info(field.getName());
        //         JSBot.LOGGER.info("{}", field.getType());
        //         JSBot.LOGGER.info("{}", this.playerInventoryTitle.getClass());
        //     }
        //     Field field = this.getClass().getField(FabricLoader.getInstance().isDevelopmentEnvironment() ? "playerInventoryTitle": "field_29347");
        //     field.setAccessible(true);
        //     field.set(this, Text.literal(""));
        // } catch (NoSuchFieldException | IllegalAccessException e) {e.printStackTrace();}
        
        String cmd_text = "";
        String log_text = "";
        if (this.command_line != null) {
            cmd_text = this.command_line.getText();
        }
        if (this.logs != null) {
            log_text = this.logs.getText();
        }
        
        // this.command_line.setX((int) ((backgroundWidth / 2) + (32 * (4 / 3d)) + 1));
        // this.command_line.setY((int) ((backgroundHeight / 2) - (32 * (4/3d)) + 4));
        // this.command_line.setWidth((int) (backgroundWidth - (32 * (3 / 4d)) + 14));
        int text_x = ((width - backgroundWidth) / 2 + 6);
        int text_y = ((height - backgroundHeight) / 2 + 7);
        int text_w = (int) (backgroundWidth - (32 * .35)) + 2;
        int cmd_height = (int) (backgroundHeight - (32 * .35) - 43);
        this.command_line = new TextBoxWidget(this.textRenderer, text_x, text_y, text_w, cmd_height,
            Text.literal("Type your command"), Text.literal(cmd_text));
        this.logs = new TextBoxWidget(this.textRenderer, text_x, text_y + cmd_height + 6, text_w, cmd_height - 77,
            Text.literal("edit [file] to edit\n" + "run [file] to run\n" + "clear | click c here to clear"), Text.literal(log_text));
        JSBot.LOGGER.info(this.logs.getText());
        // this.command_line = new TextBoxWidget(this.textRenderer, text_x, text_y + cmd_height + 6, text_w, cmd_height - 77,
        //     Text.literal("Type your command"), Text.literal(cmd_text));
        // this.logs = new TextBoxWidget(this.textRenderer, text_x, text_y, text_w, cmd_height,
        //     Text.literal(""), Text.literal(log_text));
        this.command_line.setKPListener((keyCode, scanCode, modifier) -> {
            if (mode == Mode.WAIT || mode == Mode.RUN) {
                return true;
            }
            if (keyCode == GLFW.GLFW_KEY_ENTER && mode == Mode.COMMAND) {
                // String[] split = this.command_line.getText().split("\n");
                // runCommand(split[split.length - 1]);
                var line = this.command_line.getLine(this.command_line.currentLine());
                if (line != null) {
                    runCommand(line);
                }
                return true;
            }
            if (keyCode == GLFW.GLFW_KEY_S && modifier == GLFW.GLFW_MOD_CONTROL && mode == Mode.EDIT) {
                save();
                return true;
            }
            if (keyCode == GLFW.GLFW_KEY_C && modifier == GLFW.GLFW_MOD_CONTROL && (mode == Mode.RUN || mode == Mode.EDIT)) {
                cancel();
                return true;
            }
            return false;
        });
        this.command_line.setCTListener((ch, mod) -> {
            if (mode == Mode.WAIT || mode == Mode.RUN) {
                return true;
            }
            if (ch == GLFW.GLFW_KEY_S && mod == GLFW.GLFW_MOD_CONTROL && mode == Mode.EDIT) {
                save();
                this.mode = Mode.COMMAND;
                this.command_line.setText("");
                this.command_line.setCursor(0, 0);
                return true;
            }
            return false;
        });
        this.logs.setKPListener((keyCode, scanCode, modifier) -> {
            if (keyCode == GLFW.GLFW_KEY_C) {
                this.logs.setText("");
                this.command_line.setCursor(0, 0);
            }
            return true;
        });
        this.logs.setCTListener((chr, mod) -> {
            return true;
        });
        // this.command_line.setX(text_x);
        // this.command_line.setY(text_y);
        // this.command_line.setWidth(text_w);
        // this.logs.setX(text_x);
        // this.logs.setY(text_y + cmd_height + 7);
        // this.logs.setWidth(text_w);
        // ((ClickableWidgetMixin) this.command_line).setHeight(cmd_height);
        // ((ClickableWidgetMixin) this.logs).setHeight((int) (backgroundHeight - (32 * .35) - 90));
        // ((ClickableWidgetMixin) this.command_line).setHeight((int) (backgroundHeight - (32 * .35) - 20));

        this.addDrawableChild(this.command_line);
        this.addDrawableChild(this.logs);
        
        this.addDrawableChild(
            new InteractiveTexturedButtonWidget(text_x, text_y-25, 35, 25, 5, 5, 0, save_button, 350, 190, button -> {
                if (mode != Mode.EDIT) {
                    this.addLog("Open a file first\n");
                    return;
                }
                save();
            }).setClickTexture(pressed_save_button)
        );
        this.addDrawableChild(
            new InteractiveTexturedButtonWidget(text_x + 35 + 3, text_y-25, 35, 25, 5, 5, 0, run_button, 350, 190, button -> {
                if (this.file_name == null) {
                    this.addLog("Open a file first\n");
                    return;
                }
                if (mode == Mode.EDIT) {
                    save();
                }
                this.command_line.setText("");
                this.command_line.setCursor(0, 0);
                run(this.file_name);
                this.file_name = null;
            }).setClickTexture(pressed_run_button)
        );
        this.addDrawableChild(
            new InteractiveTexturedButtonWidget(text_x + (35 * 2) + 6, text_y-25, 35, 25, 5, 5, 0, exit_button, 350, 190, button -> {
                if (mode == Mode.EDIT) {
                    cancel();
                }
            }).setClickTexture(pressed_exit_button)
        );
        
        JSBot.LOGGER.info("{} {}", this.width, this.backgroundWidth);
    }

    public void completed() {
        this.mode = Mode.COMMAND;
        this.command_line.setText("");
        this.command_line.setCursor(0, 0);
    }

    @Override
    public void close() {
        // client.setScreen(parent);
        // this.client.getNetworkHandler().send
        super.close();
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        RenderSystem.setShaderTexture(0, background);
        // int x = (width - ba)
        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;
        // context.drawTexture(background, x, y, 0, 0, backgroundWidth, backgroundHeight, 1024 / (backgroundWidth / 4), 768 / (backgroundHeight / 3));
        // context.drawTexture(background, x, y, 0, 0, backgroundWidth, backgroundHeight, 1024, 768);
        // context.drawTexture(background, x, y, 0, 0, backgroundWidth, backgroundHeight);
        // context.drawTexture(background, x, y, 0, 0, 1024, 768);
        context.drawTexture(background, x, y, backgroundWidth, backgroundHeight, 0, 0, 1024, 768, 1024, 768);
    }

    

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        int text_y = ((height - backgroundHeight) / 2 + 7);
        int cmd_height = (int) (backgroundHeight - (32 * .35) - 43);
        super.render(context, mouseX, mouseY, delta);
        String text_mode = "";
        switch (this.mode) {
            case COMMAND: {
                text_mode = "command";
                break;
            }
            case EDIT: {
                text_mode = "edit";
                break;
            }
            case RUN: {
                text_mode = "run";
                break;
            }
            case WAIT: {
                text_mode = "wait";
                break;
            }
        }
        context.drawText(this.textRenderer, Text.literal("Mode: " + text_mode), (this.width - this.backgroundWidth) / 2 + 55, text_y + cmd_height -1, 0xFfFfFfFf, true);
        drawMouseoverTooltip(context, mouseX, mouseY);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (MinecraftClient.getInstance().options.inventoryKey.matchesKey(keyCode, scanCode)) {
            return true;
        };
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    // @Override
    // public void renderBackgroundTexture(DrawContext context) {
    //     context.drawTexture(background, 0, 0, 0, 0, this.width, this.height, 1024, 768);
    // }

    public void setFileData(String string) {
        if (this.mode == Mode.WAIT) {
            this.mode = Mode.EDIT;
            this.command_line.setText(string);
            this.addLog("Press CTRL+S to save\n");
            this.addLog("Press CTRL+C to cancel\n");
        }
    }

    public void addLog(String text) {
        this.logs.setText(this.logs.getText() + text);
    }

    public int getID() {
        return this.handler.getID();
    }

    public enum Mode {
        COMMAND,
        WAIT,
        EDIT,
        RUN
    }

}
