package net.talor1n.titlebarchanger.ui.color;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.talor1n.titlebarchanger.Mode;
import net.talor1n.titlebarchanger.TitlebarChanger;

public class SelectTitleBarTextColor extends SelectColor {
    public SelectTitleBarTextColor(Screen lastScreen, Component title) {
        super(lastScreen, title);
    }

    protected void saveConfig() {
        this.configManager.getConfig().getTitleBarTextColor().setR(this.redValue);
        this.configManager.getConfig().getTitleBarTextColor().setB(this.blueValue);
        this.configManager.getConfig().getTitleBarTextColor().setG(this.greenValue);
        this.configManager.saveConfig();
        TitlebarChanger.mode = Mode.TITLE_BAR_TEXT_COLOR_MODE;
        TitlebarChanger.applyChanges();
    }

    protected void loadConfig() {
        this.redValue = this.configManager.getConfig().getTitleBarTextColor().getR();
        this.greenValue = this.configManager.getConfig().getTitleBarTextColor().getG();
        this.blueValue = this.configManager.getConfig().getTitleBarTextColor().getB();
    }
}
