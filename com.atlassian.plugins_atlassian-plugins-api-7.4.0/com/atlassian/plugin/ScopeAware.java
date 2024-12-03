/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugin;

import java.util.Optional;

@Deprecated
public interface ScopeAware {
    default public Optional<String> getScopeKey() {
        return Optional.empty();
    }
}

