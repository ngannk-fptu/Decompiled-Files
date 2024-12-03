/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.fortuna.ical4j.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.Properties;
import net.fortuna.ical4j.util.ResourceLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Configurator {
    private static final Logger LOG = LoggerFactory.getLogger(Configurator.class);
    private static final Properties CONFIG = new Properties();

    private Configurator() {
    }

    public static Optional<String> getProperty(String key) {
        String property = CONFIG.getProperty(key);
        if (property == null) {
            property = System.getProperty(key);
        }
        return Optional.ofNullable(property);
    }

    public static Optional<Integer> getIntProperty(String key) {
        Optional<String> property = Configurator.getProperty(key);
        if (property.isPresent()) {
            try {
                int intValue = Integer.parseInt(property.get());
                return Optional.of(intValue);
            }
            catch (NumberFormatException nfe) {
                LOG.info(String.format("Invalid configuration value: %s", key), (Throwable)nfe);
                return Optional.empty();
            }
        }
        return Optional.empty();
    }

    public static <T extends Enum<T>> Optional<T> getEnumProperty(Class<T> clazz, String key) {
        Optional<String> property = Configurator.getProperty(key);
        if (property.isPresent()) {
            try {
                return Optional.of(Enum.valueOf(clazz, property.get()));
            }
            catch (IllegalArgumentException iae) {
                LOG.info(String.format("Invalid configuration value: %s", key), (Throwable)iae);
                return Optional.empty();
            }
        }
        return Optional.empty();
    }

    public static <T> Optional<T> getObjectProperty(String key) {
        Optional<String> property = Configurator.getProperty(key);
        if (property.isPresent()) {
            try {
                return Optional.of(Class.forName(property.get()).newInstance());
            }
            catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
                LOG.info(String.format("Invalid configuration value: %s", key), (Throwable)e);
                return Optional.empty();
            }
        }
        return Optional.empty();
    }

    static {
        try (InputStream in = ResourceLoader.getResourceAsStream("ical4j.properties");){
            CONFIG.load(in);
        }
        catch (IOException | NullPointerException e) {
            LOG.info("ical4j.properties not found.");
        }
    }
}

