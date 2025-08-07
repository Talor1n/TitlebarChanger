package net.talor1n.titlebarchanger.ui.color;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.function.Consumer;

public abstract class SelectColor extends Screen {
    protected int redValue;
    protected int greenValue;
    protected int blueValue;
    private final Screen lastScreen;

    public SelectColor(Screen lastScreen, Component title) {
        super(title);
        this.lastScreen = lastScreen;
    }

    protected void init() {
        this.clearWidgets();
        ColorSlider slider1 = new ColorSlider(this.width / 2 - 155, this.height / 6 + 48 - 6, 310, 20, Component.translatable("gui.red"), 255, redValue, newValue -> redValue = newValue);
        ColorSlider slider2 = new ColorSlider(this.width / 2 - 155, this.height / 6 + 72 - 6, 310, 20, Component.translatable("gui.green"), 255, greenValue, newValue -> greenValue = newValue);
        ColorSlider slider3 = new ColorSlider(this.width / 2 - 155, this.height / 6 + 96 - 6, 310, 20, Component.translatable("gui.blue"), 255, blueValue, newValue -> blueValue = newValue);
        this.addRenderableWidget(slider1);
        this.addRenderableWidget(slider2);
        this.addRenderableWidget(slider3);
        this.addRenderableWidget(Button.builder(Component.translatable("gui.done"), (button) -> {

            Minecraft.getInstance().setScreen(this.lastScreen);
        }).bounds(this.width / 2 - 100, this.height / 6 + 168, 200, 20).build());
        super.init();
    }

    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        super.render(guiGraphics, mouseX, mouseY, delta);
        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, 20, 16777215);
    }

    private static class ColorSlider extends AbstractSliderButton {
        private final int maxValue;
        private final Consumer<Integer> onValueChange;

        public ColorSlider(int x, int y, int width, int height, Component text, int maxValue, int initialValue, Consumer<Integer> onValueChange) {
            super(x, y, width, height, text, (double) initialValue / (double) maxValue);
            this.maxValue = maxValue;
            this.onValueChange = onValueChange;
            this.updateMessage();
        }

        protected void updateMessage() {
            String message = this.getMessage().getString().split(":")[0];
            this.setMessage(Component.literal(message + ": " + (int) (this.value * (double) this.maxValue)));
        }

        protected void applyValue() {
            int intValue = (int) (this.value * (double) this.maxValue);
            this.onValueChange.accept(intValue);
        }

        public double getValue() {
            return this.value;
        }
    }
}
