/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugin.webresource.cdn;

import com.atlassian.plugin.webresource.prebake.PrebakeConfig;
import java.util.Optional;

public interface CDNStrategy {
    public boolean supportsCdn();

    public String transformRelativeUrl(String var1);

    default public String encodeConfigurationState() {
        return this.transformRelativeUrl("");
    }

    default public Optional<PrebakeConfig> getPrebakeConfig() {
        return Optional.empty();
    }
}

