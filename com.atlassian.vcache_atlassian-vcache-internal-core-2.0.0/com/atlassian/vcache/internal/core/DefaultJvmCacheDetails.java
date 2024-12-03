/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.vcache.JvmCacheSettings
 *  com.atlassian.vcache.internal.JvmCacheDetails
 */
package com.atlassian.vcache.internal.core;

import com.atlassian.vcache.JvmCacheSettings;
import com.atlassian.vcache.internal.JvmCacheDetails;
import java.util.Objects;

public class DefaultJvmCacheDetails
implements JvmCacheDetails {
    private final String name;
    private final JvmCacheSettings settings;

    public DefaultJvmCacheDetails(String name, JvmCacheSettings settings) {
        this.name = Objects.requireNonNull(name);
        this.settings = Objects.requireNonNull(settings);
    }

    public String getName() {
        return this.name;
    }

    public JvmCacheSettings getSettings() {
        return this.settings;
    }
}

