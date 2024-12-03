/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.pluginsettings.PluginSettings
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.plugins.authentication.impl.config;

import com.atlassian.sal.api.pluginsettings.PluginSettings;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class PluginSettingsUtil {
    private static final Logger logger = LoggerFactory.getLogger(PluginSettingsUtil.class);

    private PluginSettingsUtil() {
    }

    public static String getFullKey(String key) {
        if ("client-secret".equals(key)) {
            return "License.com.atlassian.plugins.authentication.sso.config." + key;
        }
        return "com.atlassian.plugins.authentication.sso.config." + key;
    }

    public static Object getRawValue(@Nonnull PluginSettings settings, @Nonnull String key) {
        return settings.get(PluginSettingsUtil.getFullKey(key));
    }

    @Nullable
    public static String getStringValue(@Nonnull PluginSettings settings, @Nonnull String key) {
        Object value = PluginSettingsUtil.getRawValue(settings, key);
        return value == null ? null : String.valueOf(value);
    }

    @Nullable
    public static Long getLongValue(@Nonnull PluginSettings settings, @Nonnull String key) {
        String value = PluginSettingsUtil.getStringValue(settings, key);
        return value == null ? null : Long.valueOf(value);
    }

    @Nullable
    public static ZonedDateTime getDateValue(@Nonnull PluginSettings settings, @Nonnull String key, ZoneId zone) {
        Long value = PluginSettingsUtil.getLongValue(settings, key);
        return value == null ? null : Instant.ofEpochMilli(value).atZone(zone);
    }

    public static boolean getBooleanValue(@Nonnull PluginSettings settings, @Nonnull String key, boolean defaultValue) {
        String storedValue = PluginSettingsUtil.getStringValue(settings, key);
        return storedValue == null ? defaultValue : Boolean.parseBoolean(storedValue);
    }

    public static Optional<Boolean> getBooleanValue(@Nonnull PluginSettings settings, @Nonnull String key) {
        String storedValue = PluginSettingsUtil.getStringValue(settings, key);
        return storedValue == null ? Optional.empty() : Optional.of(Boolean.parseBoolean(storedValue));
    }

    public static void removeValue(@Nonnull PluginSettings settings, @Nonnull String key) {
        settings.remove(PluginSettingsUtil.getFullKey(key));
    }

    public static void setStringValue(@Nonnull PluginSettings settings, @Nonnull String key, @Nullable String value) {
        settings.put(PluginSettingsUtil.getFullKey(key), (Object)value);
    }

    public static void setLongValue(@Nonnull PluginSettings settings, @Nonnull String key, @Nullable Long value) {
        settings.put(PluginSettingsUtil.getFullKey(key), (Object)(value == null ? null : value.toString()));
    }

    public static void setDateValue(@Nonnull PluginSettings settings, @Nonnull String key, @Nullable ZonedDateTime value) {
        PluginSettingsUtil.setLongValue(settings, key, value == null ? null : Long.valueOf(value.toInstant().toEpochMilli()));
    }

    public static void setBooleanValue(@Nonnull PluginSettings settings, @Nonnull String key, boolean value) {
        PluginSettingsUtil.setStringValue(settings, key, Boolean.toString(value));
    }

    public static void setListValue(@Nonnull PluginSettings settings, @Nonnull String key, @Nullable List<String> value) {
        settings.put(PluginSettingsUtil.getFullKey(key), (Object)(value == null ? null : new ArrayList<String>(value)));
    }

    @Nonnull
    public static <T> List<T> getListValue(@Nonnull PluginSettings settings, @Nonnull String key, @Nonnull Function<Object, T> elementMapper) {
        Object value = PluginSettingsUtil.getRawValue(settings, key);
        if (value == null) {
            return Collections.emptyList();
        }
        if (value instanceof Iterable) {
            return StreamSupport.stream(((Iterable)value).spliterator(), false).map(elementMapper).collect(Collectors.toList());
        }
        logger.warn("Invalid type for key {}, expected {}, was {}", new Object[]{PluginSettingsUtil.getFullKey(key), Iterable.class, value.getClass()});
        return Collections.emptyList();
    }
}

