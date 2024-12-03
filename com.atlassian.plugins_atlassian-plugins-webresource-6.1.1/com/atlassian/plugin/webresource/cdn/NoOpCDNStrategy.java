/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.plugin.webresource.cdn;

import com.atlassian.plugin.webresource.cdn.CDNStrategy;
import com.atlassian.plugin.webresource.prebake.PrebakeConfig;
import java.util.Optional;
import javax.annotation.Nonnull;

public class NoOpCDNStrategy
implements CDNStrategy {
    private final Optional<PrebakeConfig> prebakeConfig;

    public NoOpCDNStrategy(@Nonnull Optional<PrebakeConfig> prebakeConfig) {
        this.prebakeConfig = prebakeConfig;
    }

    @Override
    public boolean supportsCdn() {
        return true;
    }

    @Override
    public String transformRelativeUrl(String s) {
        return s;
    }

    @Override
    public Optional<PrebakeConfig> getPrebakeConfig() {
        return this.prebakeConfig;
    }
}

