package net.talor1n.titlebarchanger.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import lombok.Getter;
import lombok.Setter;
import net.talor1n.titlebarchanger.TitlebarChanger;
import net.talor1n.titlebarchanger.utils.color.RGBA;
import net.talor1n.titlebarchanger.utils.color.RGBAAdapter;

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
 *   <li>Support for RGBA color objects</li>
 * </ul>
 *
 * <h3>Usage:</h3>
 * <pre>
 * // Initialize with config file path
 * ConfigManager.INSTANCE.initialize(Paths.get("config.json"));
 *
 * // Access configuration
 * Config config = ConfigManager.INSTANCE.getConfig();
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
            .registerTypeAdapter(RGBA.class, new RGBAAdapter())
            .create();

    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    /**
     * -- GETTER --
     * Gets the current configuration file path.
     *
     * @return the path to the configuration file, or null if not initialized
     */
    @Getter
    @Setter
    private Path configPath;

    @Getter
    private volatile Config config;

    /**
     * Initializes the configuration manager with the specified config file path.
     *
     * @param configPath path to the configuration file
     * @throws IllegalArgumentException if configPath is null
     */
    public void initialize(Path configPath) {
        if (configPath == null) {
            throw new IllegalArgumentException("Config path cannot be null");
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
     * If the config file doesn't exist, creates a default configuration.
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
            Config loadedConfig = GSON.fromJson(json, Config.class);

            if (isValidConfig(loadedConfig)) {
                this.config = loadedConfig;
                TitlebarChanger.LOGGER.info("Configuration loaded successfully from: {}", configPath);
            } else {
                TitlebarChanger.LOGGER.warn("Invalid configuration detected, creating backup and using defaults");
                createBackupAndDefault();
            }
        } catch (IOException e) {
            TitlebarChanger.LOGGER.error("Failed to read config file: {} - {}", configPath, e.getMessage());
            createDefaultConfig();
        } catch (JsonSyntaxException e) {
            TitlebarChanger.LOGGER.error("Malformed JSON in config file: {} - {}", configPath, e.getMessage());
            createBackupAndDefault();
        } catch (Exception e) {
            TitlebarChanger.LOGGER.error("Unexpected error loading config: {} - {}", configPath, e.getMessage());
            createDefaultConfig();
        }
    }

    /**
     * Creates a default configuration and saves it.
     */
    private void createDefaultConfig() {
        this.config = Config.builder().build(); // Use builder for default values
        saveConfig();
        TitlebarChanger.LOGGER.info("Created default configuration at: {}", configPath);
    }

    /**
     * Creates a backup of the corrupted config file and creates a default config.
     */
    private void createBackupAndDefault() {
        try {
            Path backupPath = configPath.resolveSibling(
                    configPath.getFileName() + ".backup." + System.currentTimeMillis()
            );
            Files.copy(configPath, backupPath, StandardCopyOption.REPLACE_EXISTING);
            TitlebarChanger.LOGGER.info("Corrupted config backed up to: {}", backupPath);
        } catch (IOException e) {
            TitlebarChanger.LOGGER.warn("Failed to create backup: {}", e.getMessage());
        }
        createDefaultConfig();
    }

    /**
     * Saves the current configuration to file.
     *
     * @throws IllegalStateException if not initialized or config is null
     */
    public void saveConfig() {
        ensureInitialized();

        if (config == null) {
            TitlebarChanger.LOGGER.warn("Cannot save: configuration is null");
            return;
        }

        lock.readLock().lock();
        try {
            // Ensure parent directories exist
            if (configPath.getParent() != null) {
                Files.createDirectories(configPath.getParent());
            }

            String json = GSON.toJson(config);
            Files.writeString(configPath, json);

            TitlebarChanger.LOGGER.debug("Configuration saved to: {}", configPath);
        } catch (IOException e) {
            TitlebarChanger.LOGGER.error("Failed to save config to: {} - {}", configPath, e.getMessage());
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Saves a specific configuration to file without changing the current config.
     *
     * @param configToSave the configuration to save
     * @throws IllegalArgumentException if configToSave is null or invalid
     * @throws IllegalStateException    if not initialized
     */
    public void saveConfig(Config configToSave) {
        ensureInitialized();

        if (configToSave == null) {
            throw new IllegalArgumentException("Configuration to save cannot be null");
        }

        if (!isValidConfig(configToSave)) {
            throw new IllegalArgumentException("Invalid configuration provided");
        }

        lock.writeLock().lock();
        try {
            Config previousConfig = this.config;
            this.config = configToSave;
            saveConfig();
            TitlebarChanger.LOGGER.debug("External configuration saved");
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Updates the configuration and automatically saves it to file.
     *
     * @param newConfig the new configuration to set
     * @throws IllegalArgumentException if newConfig is null or invalid
     * @throws IllegalStateException    if not initialized
     */
    public void updateAndSave(Config newConfig) {
        ensureInitialized();

        if (newConfig == null) {
            throw new IllegalArgumentException("Configuration cannot be null");
        }

        lock.writeLock().lock();
        try {
            if (isValidConfig(newConfig)) {
                this.config = newConfig;
                saveConfig();
                TitlebarChanger.LOGGER.debug("Configuration updated and saved");
            } else {
                throw new IllegalArgumentException("Invalid configuration provided");
            }
        } finally {
            lock.writeLock().unlock();
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
     * @return true if initialized with a valid path and config
     */
    public boolean isInitialized() {
        return configPath != null && config != null;
    }

    /**
     * Validates that a configuration object has valid values.
     *
     * @param config the configuration to validate
     * @return true if the configuration is valid
     */
    private boolean isValidConfig(Config config) {
        if (config == null) {
            return false;
        }

        try {
            // Check for null theme and corner preferences
            if (config.getTheme() == null || config.getCorner() == null) {
                TitlebarChanger.LOGGER.debug("Config validation failed: null theme or corner preference");
                return false;
            }

            // Validate color objects are not null (they can be RGBA.EMPTY for "no color")
            if (config.getCaptionColor() == null ||
                    config.getBorderColor() == null ||
                    config.getTextColor() == null) {
                TitlebarChanger.LOGGER.debug("Config validation failed: null color values");
                return false;
            }

            // All validations passed
            return true;

        } catch (Exception e) {
            TitlebarChanger.LOGGER.debug("Config validation failed with exception: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Creates a copy of the current configuration for safe modification.
     *
     * @return a new Config instance with the same values as the current config
     * @throws IllegalStateException if not initialized or config is null
     */
    public Config copyCurrentConfig() {
        ensureInitialized();

        if (config == null) {
            throw new IllegalStateException("No configuration available to copy");
        }

        lock.readLock().lock();
        try {
            return Config.builder()
                    .theme(config.getTheme())
                    .corner(config.getCorner())
                    .captionColor(config.getCaptionColor())
                    .borderColor(config.getBorderColor())
                    .textColor(config.getTextColor())
                    .showTheMenu(config.isShowTheMenu())
                    .showWarnScreen(config.isShowWarnScreen())
                    .build();
        } finally {
            lock.readLock().unlock();
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
            this.config = Config.builder().build();
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