/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.business.insights.api.config.PropertiesProvider
 *  javax.annotation.Nonnull
 */
package com.atlassian.business.insights.core.config;

import com.atlassian.business.insights.api.config.PropertiesProvider;
import javax.annotation.Nonnull;

public class NoopPropertiesProvider
implements PropertiesProvider {
    public boolean getBoolean(@Nonnull String key) {
        return false;
    }

    public boolean getBoolean(@Nonnull String key, boolean defaultValue) {
        return false;
    }

    @Nonnull
    public String getProperty(@Nonnull String key, @Nonnull String defaultValue) {
        return defaultValue;
    }
}

