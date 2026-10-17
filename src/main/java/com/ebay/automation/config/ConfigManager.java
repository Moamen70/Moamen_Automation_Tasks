package com.ebay.automation.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/** Loads framework settings from config.properties; -D JVM properties override the file. */
public final class ConfigManager {
    private static final Properties PROPERTIES = new Properties();

    static {
        try (InputStream input = ConfigManager.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                throw new IllegalStateException("config.properties was not found on the classpath.");
            }
            PROPERTIES.load(input);
        } catch (IOException exception) {
            throw new ExceptionInInitializerError(exception);
        }
    }

    private ConfigManager() {
    }

    public static String get(String key) {
        String systemValue = System.getProperty(key);
        if (systemValue != null && !systemValue.isBlank()) {
            return systemValue.trim();
        }
        String fileValue = PROPERTIES.getProperty(key);
        if (fileValue == null || fileValue.isBlank()) {
            throw new IllegalArgumentException("Missing configuration property: " + key);
        }
        return fileValue.trim();
    }

    public static int getInt(String key) {
        return Integer.parseInt(get(key));
    }

    public static boolean getBoolean(String key) {
        return Boolean.parseBoolean(get(key));
    }
}
