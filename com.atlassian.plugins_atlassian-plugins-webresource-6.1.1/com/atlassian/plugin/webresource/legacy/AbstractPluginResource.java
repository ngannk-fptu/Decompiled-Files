/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugin.webresource.legacy;

import com.atlassian.plugin.webresource.legacy.PluginResource;
import java.util.Set;

public abstract class AbstractPluginResource
implements PluginResource {
    protected final Set<String> completeKeys;

    protected AbstractPluginResource(Set<String> completeKeys) {
        this.completeKeys = completeKeys;
    }
}

