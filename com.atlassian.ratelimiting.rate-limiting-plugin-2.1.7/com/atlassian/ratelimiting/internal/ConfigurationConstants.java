/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.ratelimiting.internal;

import javax.annotation.Nonnull;

public class ConfigurationConstants {
    public final String pluginKey;
    public final Long exemptionsLimit;

    public ConfigurationConstants(@Nonnull String pluginKey, @Nonnull Long exemptionsLimit) {
        this.pluginKey = pluginKey;
        this.exemptionsLimit = exemptionsLimit;
    }
}

