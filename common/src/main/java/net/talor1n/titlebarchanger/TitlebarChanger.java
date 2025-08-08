package net.talor1n.titlebarchanger;

import com.sun.jna.Memory;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinNT;
import me.shedaniel.clothconfig2.ClothConfigInitializer;
import net.minecraft.client.Minecraft;
import net.talor1n.titlebarchanger.api.TitlebarChangerApi;
import net.talor1n.titlebarchanger.compat.ClothConfigCompat;
import net.talor1n.titlebarchanger.config.TitlebarChangerConfig;
import net.talor1n.titlebarchanger.config.ConfigManager;
import net.talor1n.titlebarchanger.utils.PlatformHelper;
import net.talor1n.titlebarchanger.utils.color.RGBA;
import net.talor1n.titlebarchanger.utils.win32.DwmWindowAttribute;
import net.talor1n.titlebarchanger.utils.win32.DwmWindowCornerPreference;
import net.talor1n.titlebarchanger.utils.win32.DwmWindowThemeAttribute;
import net.talor1n.titlebarchanger.utils.win32.SystemStatus;
import net.talor1n.titlebarchanger.win32.DwmApi;
import net.talor1n.titlebarchanger.win32.Ntdll;
import org.lwjgl.glfw.GLFWNativeWin32;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.function.Consumer;

import static net.talor1n.titlebarchanger.utils.win32.DwmWindowThemeAttribute.CUSTOM;
import static net.talor1n.titlebarchanger.utils.win32.SystemStatus.NOT_SUITABLE;

public final class TitlebarChanger {
    public static final String MOD_ID = "titlebarchanger";
    public static final Logger LOGGER = LoggerFactory.getLogger(TitlebarChanger.MOD_ID);
    public static TitlebarChangerApi api = new TitlebarChangerApiImpl();

    public static void init(Path configPath) {
        ConfigManager.INSTANCE.initialize(configPath);
        Minecraft.getInstance().execute(api::loadStyle);
    }

    static class TitlebarChangerApiImpl implements TitlebarChangerApi {
        private long windowHandle() {
            long glfwWindow = Minecraft.getInstance().getWindow().getWindow();
            return GLFWNativeWin32.glfwGetWin32Window(glfwWindow);
        }

        private WinDef.HWND windowHwnd() {
            return new WinDef.HWND(Pointer.createConstant(windowHandle()));
        }

        private ConfigManager configManager() {
            return ConfigManager.INSTANCE;
        }

        private TitlebarChangerConfig getConfig() {
            return configManager().getTitlebarChangerConfig();
        }

        @Override
        public SystemStatus checkSystem() {
            // Debug Mode :)
            var debug = System.getProperty("titlebarchanger.forceSystem");
            if (debug != null) {
                LOGGER.warn("Forced system status via property: {}", debug);
                return SystemStatus.valueOf(debug.toUpperCase());
            }

            String osName = System.getProperty("os.name", "").toLowerCase();
            if (!osName.contains("windows")) {
                LOGGER.debug("TitlebarChanger only supports Windows, detected: {}", osName);
                return NOT_SUITABLE;
            }

            try {
                return Ntdll.checkWindowsVer();
            } catch (Exception e) {
                LOGGER.error("Failed to check Windows version", e);
                return NOT_SUITABLE;
            }
        }

        @Override
        public boolean setWindowAttribute(DwmWindowAttribute attribute, int value) {
            if (isOtherPlatform()) {
                LOGGER.debug("Skipping window attribute change - unsupported platform");
                return false;
            }

            if (attribute == null) {
                throw new IllegalArgumentException("Attribute cannot be null");
            }

            try {
                // Prepare a 4-byte memory block containing our integer
                Memory mem = new Memory(4);
                mem.setInt(0, value);

                // Invoke the DWM API call
                WinNT.HRESULT result = DwmApi.INSTANCE.DwmSetWindowAttribute(
                        windowHwnd(),
                        new WinDef.DWORD(attribute.getValue()),
                        new WinDef.LPVOID(mem),
                        new WinDef.DWORD(4)
                );

                boolean success = result.intValue() == 0;
                if (!success) {
                    LOGGER.warn("Failed to set window attribute {}: HRESULT = 0x{}",
                            attribute, Integer.toHexString(result.intValue()));
                }
                return success;

            } catch (Exception e) {
                LOGGER.error("Exception setting window attribute {}", attribute, e);
                return false;
            }
        }

        @Override
        public boolean setWindowCornerPreference(DwmWindowCornerPreference cornerPreference) {
            if (isNotWindows11()) return false;
            var success = setWindowAttribute(DwmWindowAttribute.DWMWA_WINDOW_CORNER_PREFERENCE, cornerPreference.getValue());
            return saveConfigIfSuccess(success, titlebarChangerConfig -> titlebarChangerConfig.setCorner(cornerPreference));
        }

        @Override
        public boolean setDarkMode(DwmWindowThemeAttribute attribute) {
            if (isOtherPlatform()) return false;

            setCaptionColor(EMPTY);
            setBorderColor(EMPTY);
            setTextColor(EMPTY);

            var success = setWindowAttribute(DwmWindowAttribute.DWMWA_USE_IMMERSIVE_DARK_MODE,
                    attribute == DwmWindowThemeAttribute.DARK ? 1 : 0);
            return saveConfigIfSuccess(success, titlebarChangerConfig -> titlebarChangerConfig.setTheme(attribute));
        }

        @Override
        public boolean setCaptionColor(RGBA color) {
            if (isNotWindows11()) return false;
            if (!isCustomThemeAttribute(getConfig())) return false;
            var success = setWindowAttribute(DwmWindowAttribute.DWMWA_CAPTION_COLOR, color.toHexInt());
            return saveConfigIfSuccess(success, titlebarChangerConfig -> titlebarChangerConfig.setCaptionColor(color));
        }

        @Override
        public boolean setBorderColor(RGBA color) {
            if (isNotWindows11()) return false;
            if (!isCustomThemeAttribute(getConfig())) return false;
            var success = setWindowAttribute(DwmWindowAttribute.DWMWA_BORDER_COLOR, color.toHexInt());
            return saveConfigIfSuccess(success, titlebarChangerConfig -> titlebarChangerConfig.setBorderColor(color));
        }

        @Override
        public boolean setTextColor(RGBA color) {
            if (isNotWindows11()) return false;
            if (!isCustomThemeAttribute(getConfig())) return false;
            var success = setWindowAttribute(DwmWindowAttribute.DWMWA_TEXT_COLOR, color.toHexInt());
            return saveConfigIfSuccess(success, titlebarChangerConfig -> titlebarChangerConfig.setTextColor(color));
        }

        @Override
        public boolean loadStyle() {
            var config = getConfig();
            if (config == null) {
                throw new IllegalArgumentException("TitlebarChangerConfig cannot be null");
            }

            boolean allSuccess = true;

            try {
                if (isOtherPlatform()) return false;

                if (!setDarkMode(config.getTheme())) {
                    LOGGER.warn("Failed to apply dark mode setting: {}", config.getTheme());
                    allSuccess = false;
                }

                if (isNotWindows11()) return allSuccess;

                if (!setWindowCornerPreference(config.getCorner())) {
                    LOGGER.warn("Failed to apply corner preference: {}", config.getCorner());
                    allSuccess = false;
                }

                if (config.getTheme() == CUSTOM) {
                    if (config.getCaptionColor() != null &&
                            !setCaptionColor(config.getCaptionColor())) {
                        LOGGER.warn("Failed to apply caption color: {}", config.getCaptionColor());
                        allSuccess = false;
                    }

                    if (config.getBorderColor() != null &&
                            !setBorderColor(config.getBorderColor())) {
                        LOGGER.warn("Failed to apply border color: {}", config.getBorderColor());
                        allSuccess = false;
                    }

                    if (config.getTextColor() != null &&
                            !setTextColor(config.getTextColor())) {
                        LOGGER.warn("Failed to apply text color: {}", config.getTextColor());
                        allSuccess = false;
                    }
                }

                LOGGER.debug("Style loading completed. Success: {}", allSuccess);
                return allSuccess;

            } catch (Exception e) {
                LOGGER.error("Exception occurred while loading style", e);
                return false;
            }
        }

        @Override
        public boolean saveConfigIfSuccess(boolean success, Consumer<TitlebarChangerConfig> config) {
            if (!success) return false;
            config.accept(getConfig());
            configManager().saveConfig();
            return true;
        }
    }
}
