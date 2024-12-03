/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.vcache.ExternalCacheSettings
 *  com.atlassian.vcache.internal.ExternalCacheDetails
 *  com.atlassian.vcache.internal.ExternalCacheDetails$BufferPolicy
 */
package com.atlassian.vcache.internal.core;

import com.atlassian.vcache.ExternalCacheSettings;
import com.atlassian.vcache.internal.ExternalCacheDetails;
import java.util.Objects;

public class DefaultExternalCacheDetails
implements ExternalCacheDetails {
    private final String name;
    private final ExternalCacheDetails.BufferPolicy policy;
    private final ExternalCacheSettings settings;

    public DefaultExternalCacheDetails(String name, ExternalCacheDetails.BufferPolicy policy, ExternalCacheSettings settings) {
        this.name = Objects.requireNonNull(name);
        this.policy = Objects.requireNonNull(policy);
        this.settings = Objects.requireNonNull(settings);
    }

    public String getName() {
        return this.name;
    }

    public ExternalCacheDetails.BufferPolicy getPolicy() {
        return this.policy;
    }

    public ExternalCacheSettings getSettings() {
        return this.settings;
    }
}

