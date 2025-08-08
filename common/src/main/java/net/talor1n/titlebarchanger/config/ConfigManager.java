package net.talor1n.titlebarchanger.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import lombok.Getter;
import lombok.Setter;
import net.talor1n.titlebarchanger.TitlebarChanger;
import net.talor1n.titlebarchanger.utils.color.RGB;
import net.talor1n.titlebarchanger.utils.color.RGBAdapter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Thread-safe configuration manager for TitlebarChanger application.
 * <p>
 * This enum singleton provides centralized configuration management with
 * automatic backup creation, validation, and robust error handling.
 *
 * <h3>Features:</h3>
 * <ul>
 *   <li>Thread-safe operations with read-write locks</li>
 *   <li>Automatic backup creation for corrupted configs</li>
 *   <li>Modern NIO.2 file operations</li>
 *   <li>Comprehensive validation and error handling</li>
 *   <li>Support for RGB color objects</li>
 * </ul>
 *
 * <h3>Usage:</h3>
 * <pre>
 * // Initialize with titlebarChangerConfig file path
 * ConfigManager.INSTANCE.initialize(Paths.get("titlebarChangerConfig.json"));
 *
 * // Access configuration
 * TitlebarConfig titlebarChangerConfig = ConfigManager.INSTANCE.getTitlebarConfig();
 *
 * // Update and save
 * ConfigManager.INSTANCE.updateAndSave(newConfig);
 *
 * // Reload from file
 * ConfigManager.INSTANCE.reload();
 * </pre>
 */
public enum ConfigManager {
    /**
     * Singleton instance
     */
    INSTANCE;

    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .serializeNulls()
            .registerTypeAdapter(RGB.class, new RGBAdapter())
            .create();

    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    @Getter
    @Setter
    private Path configPath;

    @Getter
    private volatile TitlebarConfig titlebarChangerConfig;

    /**
     * Initializes the configuration manager with the specified titlebarChangerConfig file path.
     *
     * @param configPath path to the configuration file
     * @throws IllegalArgumentException if configPath is null
     */
    public void initialize(Path configPath) {
        if (configPath == null) {
            throw new IllegalArgumentException("TitlebarConfig path cannot be null");
        }

        lock.writeLock().lock();
        try {
            this.configPath = configPath;
            loadConfig();
            TitlebarChanger.LOGGER.info("ConfigManager initialized with path: {}", configPath);
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Loads configuration from the file system.
     * <p>
     * If the titlebarChangerConfig file doesn't exist, creates a default configuration.
     * If the file is corrupted, creates a backup and uses default settings.
     *
     * @throws IllegalStateException if not initialized
     */
    public void loadConfig() {
        ensureInitialized();

        lock.writeLock().lock();
        try {
            if (Files.exists(configPath)) {
                loadFromFile();
            } else {
                createDefaultConfig();
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Loads configuration from the existing file.
     */
    private void loadFromFile() {
        try {
            String json = Files.readString(configPath);
            TitlebarConfig loadedTitlebarConfig = GSON.fromJson(json, TitlebarConfig.class);

            if (isValidConfig(loadedTitlebarConfig)) {
                this.titlebarChangerConfig = loadedTitlebarConfig;
                TitlebarChanger.LOGGER.info("Configuration loaded successfully from: {}", configPath);
            } else {
                TitlebarChanger.LOGGER.warn("Invalid configuration detected, creating backup and using defaults");
                createBackupAndDefault();
            }
        } catch (IOException e) {
            TitlebarChanger.LOGGER.error("Failed to read titlebarChangerConfig file: {} - {}", configPath, e.getMessage());
            createDefaultConfig();
        } catch (JsonSyntaxException e) {
            TitlebarChanger.LOGGER.error("Malformed JSON in titlebarChangerConfig file: {} - {}", configPath, e.getMessage());
            createBackupAndDefault();
        } catch (Exception e) {
            TitlebarChanger.LOGGER.error("Unexpected error loading titlebarChangerConfig: {} - {}", configPath, e.getMessage());
            createDefaultConfig();
        }
    }

    /**
     * Creates a default configuration and saves it.
     */
    private void createDefaultConfig() {
        this.titlebarChangerConfig = TitlebarConfig.builder().build(); // Use builder for default values
        saveConfig();
        TitlebarChanger.LOGGER.info("Created default configuration at: {}", configPath);
    }

    /**
     * Creates a backup of the corrupted titlebarChangerConfig file and creates a default titlebarChangerConfig.
     */
    private void createBackupAndDefault() {
        try {
            Path backupPath = configPath.resolveSibling(
                    configPath.getFileName() + ".backup." + System.currentTimeMillis()
            );
            Files.copy(configPath, backupPath, StandardCopyOption.REPLACE_EXISTING);
            TitlebarChanger.LOGGER.info("Corrupted titlebarChangerConfig backed up to: {}", backupPath);
        } catch (IOException e) {
            TitlebarChanger.LOGGER.warn("Failed to create backup: {}", e.getMessage());
        }
        createDefaultConfig();
    }

    /**
     * Saves the current configuration to file.
     *
     * @throws IllegalStateException if not initialized or titlebarChangerConfig is null
     */
    public void saveConfig() {
        ensureInitialized();

        if (titlebarChangerConfig == null) {
            TitlebarChanger.LOGGER.warn("Cannot save: configuration is null");
            return;
        }

        lock.readLock().lock();
        try {
            // Ensure parent directories exist
            if (configPath.getParent() != null) {
                Files.createDirectories(configPath.getParent());
            }

            String json = GSON.toJson(titlebarChangerConfig);
            Files.writeString(configPath, json);

            TitlebarChanger.LOGGER.debug("Configuration saved to: {}", configPath);
        } catch (IOException e) {
            TitlebarChanger.LOGGER.error("Failed to save titlebarChangerConfig to: {} - {}", configPath, e.getMessage());
        } finally {
            lock.readLock().unlock();
        }
    }


    /**
     * Reloads configuration from the file system.
     *
     * @throws IllegalStateException if not initialized
     */
    public void reload() {
        ensureInitialized();
        TitlebarChanger.LOGGER.info("Reloading configuration from: {}", configPath);
        loadConfig();
    }

    /**
     * Checks if the configuration manager is properly initialized.
     *
     * @return true if initialized with a valid path and titlebarChangerConfig
     */
    public boolean isInitialized() {
        return configPath != null && titlebarChangerConfig != null;
    }

    /**
     * Validates that a configuration object has valid values.
     *
     * @param titlebarChangerConfig the configuration to validate
     * @return true if the configuration is valid
     */
    private boolean isValidConfig(TitlebarConfig titlebarChangerConfig) {
        if (titlebarChangerConfig == null) {
            return false;
        }

        try {
            // Check for null theme and corner preferences
            if (titlebarChangerConfig.getTheme() == null || titlebarChangerConfig.getCorner() == null) {
                TitlebarChanger.LOGGER.debug("TitlebarConfig validation failed: null theme or corner preference");
                return false;
            }

            // Validate color objects are not null (they can be RGB.EMPTY for "no color")
            if (titlebarChangerConfig.getCaptionColor() == null ||
                    titlebarChangerConfig.getBorderColor() == null ||
                    titlebarChangerConfig.getTextColor() == null) {
                TitlebarChanger.LOGGER.debug("TitlebarConfig validation failed: null color values");
                return false;
            }

            // All validations passed
            return true;

        } catch (Exception e) {
            TitlebarChanger.LOGGER.debug("TitlebarConfig validation failed with exception: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Resets the configuration to default values and saves it.
     *
     * @throws IllegalStateException if not initialized
     */
    public void resetToDefaults() {
        ensureInitialized();

        lock.writeLock().lock();
        try {
            this.titlebarChangerConfig = TitlebarConfig.builder().build();
            saveConfig();
            TitlebarChanger.LOGGER.info("Configuration reset to defaults");
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Ensures the manager is initialized before performing operations.
     *
     * @throws IllegalStateException if not initialized
     */
    private void ensureInitialized() {
        if (configPath == null) {
            throw new IllegalStateException("ConfigManager not initialized. Call initialize(Path) first.");
        }
    }
}