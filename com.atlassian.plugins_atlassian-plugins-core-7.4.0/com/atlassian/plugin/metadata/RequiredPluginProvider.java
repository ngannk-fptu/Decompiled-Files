/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugin.metadata;

import java.util.Set;

public interface RequiredPluginProvider {
    public Set<String> getRequiredPluginKeys();

    public Set<String> getRequiredModuleKeys();
}

