/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugin.webresource.cdn;

import com.atlassian.plugin.webresource.cdn.CDNStrategy;
import java.util.Optional;

public interface CdnStrategyProvider {
    public Optional<CDNStrategy> getCdnStrategy();
}

