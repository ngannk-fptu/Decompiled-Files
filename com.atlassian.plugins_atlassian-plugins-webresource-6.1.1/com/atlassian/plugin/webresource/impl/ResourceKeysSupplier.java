/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugin.webresource.impl;

import com.atlassian.plugin.webresource.impl.ResourceKey;
import java.util.List;

public class ResourceKeysSupplier {
    private final List<ResourceKey> keys;

    public ResourceKeysSupplier(List<ResourceKey> keys) {
        this.keys = keys;
    }

    public List<ResourceKey> getKeys() {
        return this.keys;
    }
}

