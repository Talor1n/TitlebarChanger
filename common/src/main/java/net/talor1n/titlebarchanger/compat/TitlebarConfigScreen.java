package net.talor1n.titlebarchanger.compat;

import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.api.Requirement;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.talor1n.titlebarchanger.TitlebarChanger;
import net.talor1n.titlebarchanger.config.ConfigManager;
import net.talor1n.titlebarchanger.utils.color.RGB;
import net.talor1n.titlebarchanger.utils.win32.DwmWindowCornerPreference;
import net.talor1n.titlebarchanger.utils.win32.DwmWindowThemeAttribute;

import java.util.function.IntConsumer;

import static net.talor1n.titlebarchanger.compat.TitlebarConfigLimitedTheme.fromLimited;
import static net.talor1n.titlebarchanger.compat.TitlebarConfigLimitedTheme.toLimited;

/**
 * Builds the Cloth Config screen for TitlebarChanger.
 * <p>
 * Features:
 * <ul>
 *     <li>Platform gating: non-Windows -> shows a single “unsupported” message.</li>
 *     <li>Windows 10 gating: CUSTOM theme is disallowed; theme selector is limited to LIGHT/DARK.</li>
 *     <li>Dynamic requirements: corner selector is disabled on Win10 and when theme is LIGHT.</li>
 *     <li>Color pickers are available only on Win11+ and only when theme is CUSTOM.</li>
 *     <li>Shared helpers (color picker factory, hex defaults, predicates).</li>
 * </ul>
 */
@SuppressWarnings("UnstableApiUsage")
public final class TitlebarConfigScreen {
    private TitlebarConfigScreen() {}

    // region i18n keys
    private static final String T_TITLE                 = "title.titlebarchanger.config";
    private static final String T_CAT_GENERAL           = "category.titlebarchanger.general";
    private static final String T_INFO_WIN10            = "titlebarchanger.info.win10";
    private static final String T_UNSUPPORTED_BODY      = "titlebarchanger.unsupported.body";
    private static final String T_THEME                 = "titlebarchanger.theme";
    private static final String T_THEME_TOOLTIP         = "titlebarchanger.theme.tooltip";
    private static final String T_CORNER                = "titlebarchanger.corner";
    private static final String T_CORNER_TOOLTIP        = "titlebarchanger.corner.tooltip";
    private static final String T_CAPTION_COLOR         = "titlebarchanger.captionColor";
    private static final String T_CAPTION_COLOR_TOOLTIP = "titlebarchanger.captionColor.tooltip";
    private static final String T_BORDER_COLOR          = "titlebarchanger.borderColor";
    private static final String T_BORDER_COLOR_TOOLTIP  = "titlebarchanger.borderColor.tooltip";
    private static final String T_TEXT_COLOR            = "titlebarchanger.textColor";
    private static final String T_TEXT_COLOR_TOOLTIP    = "titlebarchanger.textColor.tooltip";
    // endregion

    /**
     * Creates the config screen (single category) with all dynamic requirements.
     *
     * @return ready-to-open {@link Screen}
     */
    public static Screen createConfigScreen() {
        final var cfg        = ConfigManager.INSTANCE.getTitlebarChangerConfig();
        final boolean windows   = isWindows();
        final boolean windows10 = windows && isWindows10();

        final ConfigBuilder builder = ConfigBuilder.create()
                .setTitle(Component.translatable(T_TITLE))
                .setSavingRunnable(() -> {
                    try {
                        ConfigManager.INSTANCE.saveConfig();
                        TitlebarChanger.api.loadStyle();
                    } catch (Exception e) {
                        TitlebarChanger.LOGGER.error("Failed to save config", e);
                    }
                })
                .transparentBackground();

        final ConfigEntryBuilder eb = builder.entryBuilder();
        final ConfigCategory cat = builder.getOrCreateCategory(Component.translatable(T_CAT_GENERAL));

        // Non-Windows: just show a message and exit.
        if (!windows) {
            addUnsupportedMessage(cat, eb);
            return builder.build();
        }

        // Win10: force theme from CUSTOM to DARK (CUSTOM is not supported there).
        if (windows10 && cfg.getTheme() == DwmWindowThemeAttribute.CUSTOM) {
            cfg.setTheme(DwmWindowThemeAttribute.DARK);
        }

        addWin10InfoBanner(cat, eb, windows10);

        // Build theme entries (full vs limited) and get common predicates.
        final var theme = buildThemeEntries(cat, eb, cfg, windows10);

        // Corner entry (disabled on Win10 and when theme is LIGHT)
        cat.addEntry(buildCornerEntry(eb, cfg, theme, windows10));

        // Colors — available only on Win11+ and only for CUSTOM theme.
        buildColorEntries(cat, eb, cfg, theme, windows10);

        return builder.build();
    }

    // region Sections

    /**
     * Adds a single unsupported message for non-Windows platforms.
     */
    private static void addUnsupportedMessage(ConfigCategory cat, ConfigEntryBuilder eb) {
        cat.addEntry(
                eb.startTextDescription(
                        Component.translatable(T_UNSUPPORTED_BODY).withStyle(ChatFormatting.RED, ChatFormatting.BOLD)
                ).build()
        );
    }

    /**
     * Adds an info banner (text) that shows only on Windows 10.
     */
    private static void addWin10InfoBanner(ConfigCategory cat, ConfigEntryBuilder eb, boolean windows10) {
        cat.addEntry(
                eb.startTextDescription(
                        Component.translatable(T_INFO_WIN10).withStyle(ChatFormatting.RED, ChatFormatting.BOLD)
                ).setDisplayRequirement(() -> windows10).build()
        );
    }

    /**
     * Builds the theme entries and returns a struct with predicates
     * that can be reused by other entries (LIGHT/CUSTOM checks).
     */
    private static ThemePredicates buildThemeEntries(
            ConfigCategory cat,
            ConfigEntryBuilder eb,
            net.talor1n.titlebarchanger.config.TitlebarConfig cfg,
            boolean windows10
    ) {
        // Full selector (LIGHT/DARK/CUSTOM) — only when NOT Windows 10.
        final var themeFull = eb.startEnumSelector(
                        Component.translatable(T_THEME),
                        DwmWindowThemeAttribute.class,
                        cfg.getTheme()
                )
                .setDefaultValue(DwmWindowThemeAttribute.DARK)
                .setTooltip(Component.translatable(T_THEME_TOOLTIP))
                .setSaveConsumer(cfg::setTheme)
                .setDisplayRequirement(() -> !windows10)
                .build();

        // Limited selector (LIGHT/DARK) — only on Windows 10.
        final var themeLimited = eb.startEnumSelector(
                        Component.translatable(T_THEME),
                        TitlebarConfigLimitedTheme.class,
                        toLimited(clampToAllowed(cfg.getTheme()))
                )
                .setDefaultValue(TitlebarConfigLimitedTheme.DARK)
                .setTooltip(Component.translatable(T_THEME_TOOLTIP))
                .setSaveConsumer(lt -> cfg.setTheme(fromLimited(lt)))
                .setDisplayRequirement(() -> windows10)
                .build();

        cat.addEntry(themeFull);
        cat.addEntry(themeLimited);

        // Compose reusable predicates:
        final var themeIsLight = Requirement.any(
                Requirement.isValue(themeFull, DwmWindowThemeAttribute.LIGHT),
                Requirement.isValue(themeLimited, TitlebarConfigLimitedTheme.LIGHT)
        );
        final var themeIsCustom = Requirement.isValue(themeFull, DwmWindowThemeAttribute.CUSTOM);

        return new ThemePredicates(themeIsLight, themeIsCustom);
    }

    /**
     * Builds the corner preference entry with dynamic requirements.
     */
    @SuppressWarnings("rawtypes")
    private static AbstractConfigListEntry buildCornerEntry(
            ConfigEntryBuilder eb,
            net.talor1n.titlebarchanger.config.TitlebarConfig cfg,
            ThemePredicates theme,
            boolean windows10
    ) {
        return eb.startEnumSelector(
                        Component.translatable(T_CORNER),
                        DwmWindowCornerPreference.class,
                        cfg.getCorner()
                )
                .setDefaultValue(DwmWindowCornerPreference.SYSTEM_DEFAULT)
                .setTooltip(Component.translatable(T_CORNER_TOOLTIP))
                .setSaveConsumer(cfg::setCorner)
                // Disabled: on Win10 OR when theme == LIGHT
                .setRequirement(Requirement.all(
                        Requirement.not(() -> windows10),
                        Requirement.not(theme.isLight)
                ))
                .build();
    }

    /**
     * Builds color entries (caption/border/text) and adds them to the category.
     * Colors are available on Win11+ and only when theme == CUSTOM.
     */
    private static void buildColorEntries(
            ConfigCategory cat,
            ConfigEntryBuilder eb,
            net.talor1n.titlebarchanger.config.TitlebarConfig cfg,
            ThemePredicates theme,
            boolean windows10
    ) {
        final Requirement colorReq = Requirement.all(
                Requirement.not(() -> windows10),
                theme.isCustom
        );

        cat.addEntry(colorPicker(
                eb,
                T_CAPTION_COLOR,
                T_CAPTION_COLOR_TOOLTIP,
                hexOrDefault(cfg.getCaptionColor(), RGB.of(5, 5, 5)),
                RGB.of(5, 5, 5).toHexInt(),
                v -> cfg.setCaptionColor(RGB.of(v)),
                colorReq
        ));

        cat.addEntry(colorPicker(
                eb,
                T_BORDER_COLOR,
                T_BORDER_COLOR_TOOLTIP,
                hexOrDefault(cfg.getBorderColor(), RGB.of(0, 255, 0)),
                RGB.of(0, 255, 0).toHexInt(),
                v -> cfg.setBorderColor(RGB.of(v)),
                colorReq
        ));

        cat.addEntry(colorPicker(
                eb,
                T_TEXT_COLOR,
                T_TEXT_COLOR_TOOLTIP,
                hexOrDefault(cfg.getTextColor(), RGB.of(0, 255, 0)),
                RGB.of(0, 255, 0).toHexInt(),
                v -> cfg.setTextColor(RGB.of(v)),
                colorReq
        ));
    }
    // endregion

    // region Helpers

    /** @return whether current platform is Windows. */
    private static boolean isWindows() {
        return !TitlebarChanger.api.isOtherPlatform();
    }

    /** @return whether current OS is Windows 10. */
    private static boolean isWindows10() {
        return TitlebarChanger.api.isWindows10();
    }

    /**
     * Ensures the theme is one of LIGHT/DARK; if not, returns DARK.
     */
    private static DwmWindowThemeAttribute clampToAllowed(DwmWindowThemeAttribute in) {
        return (in == DwmWindowThemeAttribute.DARK || in == DwmWindowThemeAttribute.LIGHT)
                ? in
                : DwmWindowThemeAttribute.DARK;
    }

    /**
     * Returns color's hex value, or the provided default when null.
     */
    private static int hexOrDefault(RGB value, RGB defRgb) {
        return (value != null ? value : defRgb).toHexInt();
    }

    /**
     * Factory for a color picker entry bound to an int (ARGB-free) value,
     * converting to/from {@link RGB}, and protected by a {@link Requirement}.
     */
    @SuppressWarnings("rawtypes")
    private static AbstractConfigListEntry colorPicker(
            ConfigEntryBuilder eb,
            String labelKey,
            String tooltipKey,
            int currentHex,
            int defaultHex,
            IntConsumer save,
            Requirement requirement
    ) {
        return eb.startColorField(Component.translatable(labelKey), currentHex)
                .setDefaultValue(defaultHex)
                .setTooltip(Component.translatable(tooltipKey))
                .setAlphaMode(false)
                .setSaveConsumer(save::accept)
                .setRequirement(requirement)
                .build();
    }
    // endregion

    /**
     * Bundle of common theme predicates used across entries.
     */
    private record ThemePredicates(Requirement isLight, Requirement isCustom) {}
}
