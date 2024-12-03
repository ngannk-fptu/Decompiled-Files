/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugin.webresource.legacy;

import com.atlassian.plugin.webresource.legacy.AbstractPluginResource;
import java.util.HashSet;
import java.util.SortedSet;
import java.util.TreeSet;

public class SuperBatchPluginResource
extends AbstractPluginResource {
    private final SortedSet<String> excludedKeys = new TreeSet<String>();

    protected SuperBatchPluginResource() {
        super(new HashSet<String>());
    }

    public void addBatchedWebResourceDescriptor(String key) {
        this.completeKeys.add(key);
    }

    public void addExcludedContext(String key) {
        this.excludedKeys.add(key);
    }

    public Iterable<String> getExcludedContexts() {
        return this.excludedKeys;
    }

    public boolean isEmpty() {
        return this.completeKeys.isEmpty();
    }
}

