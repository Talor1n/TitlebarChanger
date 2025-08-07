package net.talor1n.titlebarchanger.ui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.talor1n.titlebarchanger.TitlebarChanger;
import net.talor1n.titlebarchanger.utils.SystemStatus;

import java.util.List;

public class WarnScreen extends Screen {
    private static final Component WARNING_TITLE = Component.translatable("gui.warning_title");
    private static Component WARNING_MESSAGE = Component.translatable("gui.warning_desc");
    private List<FormattedCharSequence> wrappedMessage;

    public WarnScreen() {
        super(WARNING_TITLE);
    }

    @Override
    protected void init() {
        if (TitlebarChanger.systemStatus == SystemStatus.LIMITED_SUITABILITY) {
            WARNING_MESSAGE = Component.translatable("gui.warning_desc");
        } else if (TitlebarChanger.systemStatus == SystemStatus.NOT_SUITABLE) {
            WARNING_MESSAGE = Component.translatable("gui.warning_desc_not_suitable");
        }

        this.wrappedMessage = this.font.split(WARNING_MESSAGE, this.width - 50);

        // Создаем кнопку "Закрыть"
        int buttonWidth = 300;
        int buttonHeight = 20;
        int buttonX = (this.width - buttonWidth) / 2;
        int buttonY = this.height / 2 + (this.wrappedMessage.size() * 9 / 2) + 30;

        this.addRenderableWidget(
                Button.builder(Component.translatable("gui.close"), button -> this.onClose())
                        .bounds(buttonX, buttonY, buttonWidth, buttonHeight)
                        .build()
        );
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        super.render(guiGraphics, mouseX, mouseY, delta);

        int titleX = this.width / 2;
        int titleY = this.height / 2 - 9 * 2 - 30;
        guiGraphics.drawCenteredString(this.font, WARNING_TITLE, titleX, titleY, 0xFF0000); // Красный цвет

        int messageY = this.height / 2 - (this.wrappedMessage.size() * 9 / 2);
        for (FormattedCharSequence line : this.wrappedMessage) {
            guiGraphics.drawCenteredString(this.font, line, this.width / 2, messageY, 0xFFFFFF); // Белый цвет
            messageY += 9 + 5;
        }
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return true;
    }

    @Override
    public void onClose() {
        TitlebarChanger.configManager.getConfig().setShowWarnScreen(false);
        TitlebarChanger.configManager.saveConfig();

        // Закрываем экран
        Minecraft.getInstance().setScreen(null);
    }
}