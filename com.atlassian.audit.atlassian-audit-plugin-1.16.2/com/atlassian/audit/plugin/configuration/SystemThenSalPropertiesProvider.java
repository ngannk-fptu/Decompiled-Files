/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.ApplicationProperties
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.audit.plugin.configuration;

import com.atlassian.audit.plugin.configuration.PropertiesProvider;
import com.atlassian.sal.api.ApplicationProperties;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class SystemThenSalPropertiesProvider
implements PropertiesProvider {
    private final ApplicationProperties applicationProperties;

    public SystemThenSalPropertiesProvider(ApplicationProperties applicationProperties) {
        this.applicationProperties = applicationProperties;
    }

    @Override
    @Nullable
    public String getProperty(@Nonnull String key) {
        Objects.requireNonNull(key, "key to get property");
        String systemPropertyValue = System.getProperty(key);
        if (systemPropertyValue != null) {
            return systemPropertyValue;
        }
        try {
            return this.applicationProperties.getPropertyValue(key);
        }
        catch (UnsupportedOperationException e) {
            return null;
        }
    }

    @Override
    public boolean getBoolean(@Nonnull String key) {
        Objects.requireNonNull(key, "key to get boolean property");
        return Boolean.parseBoolean(this.getProperty(key));
    }

    @Override
    @Nullable
    public String getProperty(@Nonnull String key, String defaultValue) {
        Objects.requireNonNull(key, "key to get property");
        String propertyValue = this.getProperty(key);
        return propertyValue == null ? defaultValue : propertyValue;
    }

    @Override
    public int getInteger(@Nonnull String key, int defaultValue) {
        Objects.requireNonNull(key, "key to get integer property");
        try {
            return Integer.parseInt(this.getProperty(key));
        }
        catch (NullPointerException | NumberFormatException e) {
            return defaultValue;
        }
    }

    @Override
    public long getLong(@Nonnull String key, long defaultValue) {
        Objects.requireNonNull(key, "key to get long int property");
        try {
            return Long.parseLong(this.getProperty(key));
        }
        catch (NullPointerException | NumberFormatException e) {
            return defaultValue;
        }
    }
}

