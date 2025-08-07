package net.talor1n.titlebarchanger.ui.color;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.talor1n.titlebarchanger.Mode;
import net.talor1n.titlebarchanger.TitlebarChanger;

public class SelectTitleBarStrokeColor extends SelectColor {
    public SelectTitleBarStrokeColor(Screen lastScreen, Component title) {
        super(lastScreen, title);
    }

    protected void saveConfig() {
        this.configManager.getConfig().getTitleBarStrokeColor().setR(this.redValue);
        this.configManager.getConfig().getTitleBarStrokeColor().setB(this.blueValue);
        this.configManager.getConfig().getTitleBarStrokeColor().setG(this.greenValue);
        this.configManager.saveConfig();
        TitlebarChanger.mode = Mode.TITLE_BAR_STROKE_COLOR_MODE;
        TitlebarChanger.applyChanges();
    }

    protected void loadConfig() {
        this.redValue = this.configManager.getConfig().getTitleBarStrokeColor().getR();
        this.blueValue = this.configManager.getConfig().getTitleBarStrokeColor().getB();
        this.greenValue = this.configManager.getConfig().getTitleBarStrokeColor().getG();
    }
}
