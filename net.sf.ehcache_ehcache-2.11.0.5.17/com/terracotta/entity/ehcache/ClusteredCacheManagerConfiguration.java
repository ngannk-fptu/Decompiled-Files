/*
 * Decompiled with CFR 0.152.
 */
package com.terracotta.entity.ehcache;

import com.terracotta.entity.EntityConfiguration;

public class ClusteredCacheManagerConfiguration
implements EntityConfiguration {
    private static final long serialVersionUID = 1L;
    private final String configurationText;

    public ClusteredCacheManagerConfiguration(String configurationText) {
        this.configurationText = configurationText;
    }

    public String getConfigurationAsText() {
        return this.configurationText;
    }
}

