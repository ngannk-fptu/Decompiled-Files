/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugin.webresource.cdn;

import com.atlassian.plugin.webresource.cdn.CdnResourceUrlTransformer;
import com.atlassian.plugin.webresource.impl.config.Config;

public class CdnResourceUrlTransformerImpl
implements CdnResourceUrlTransformer {
    private final Config config;

    public CdnResourceUrlTransformerImpl(Config config) {
        this.config = config;
    }

    @Override
    public String getResourceCdnPrefix(String url) {
        return this.config.getResourceCdnPrefix(url);
    }
}

