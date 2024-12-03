/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugin.webresource.impl;

import com.atlassian.plugin.webresource.impl.snapshot.resource.Resource;

public class ResourceKey {
    private final String key;
    private final String name;

    public ResourceKey(Resource resource) {
        this(resource.getKey(), resource.getName());
    }

    public ResourceKey(String key, String name) {
        this.key = key;
        this.name = name;
    }

    public String getKey() {
        return this.key;
    }

    public String getName() {
        return this.name;
    }
}

