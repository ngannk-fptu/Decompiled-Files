/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.webresource.cdn.CDNStrategy
 *  com.atlassian.plugin.webresource.prebake.PrebakeConfig
 */
package com.atlassian.plugins.impl;

import com.atlassian.plugin.webresource.cdn.CDNStrategy;
import com.atlassian.plugin.webresource.prebake.PrebakeConfig;
import java.util.Optional;

public class PrefixCDNStrategy
implements CDNStrategy {
    static final String PREFIX_SYSTEM_PROPERTY = "application.fixed.cdn.prefix";
    private final Optional<PrebakeConfig> prebakeConfig;
    private final String prefix;

    public PrefixCDNStrategy(String prefix, Optional<PrebakeConfig> prebakeConfig) {
        this.prebakeConfig = prebakeConfig;
        this.prefix = prefix;
    }

    public boolean supportsCdn() {
        return true;
    }

    public String transformRelativeUrl(String str) {
        return this.prefix + str;
    }

    public Optional<PrebakeConfig> getPrebakeConfig() {
        return this.prebakeConfig;
    }
}

