package net.talor1n.titlebarchanger.ui.color;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.talor1n.titlebarchanger.Mode;
import net.talor1n.titlebarchanger.TitlebarChanger;

public class SelectTitleBarColor extends SelectColor {
    public SelectTitleBarColor(Screen lastScreen, Component title) {
        super(lastScreen, title);
    }

    protected void saveConfig() {
        this.configManager.getConfig().getTitleBarColor().setR(this.redValue);
        this.configManager.getConfig().getTitleBarColor().setB(this.blueValue);
        this.configManager.getConfig().getTitleBarColor().setG(this.greenValue);
        this.configManager.saveConfig();
        TitlebarChanger.mode = Mode.TITLE_BAR_COLOR_MODE;
        TitlebarChanger.applyChanges();
    }

    protected void loadConfig() {
        this.redValue = this.configManager.getConfig().getTitleBarColor().getR();
        this.blueValue = this.configManager.getConfig().getTitleBarColor().getB();
        this.greenValue = this.configManager.getConfig().getTitleBarColor().getG();
    }
}
