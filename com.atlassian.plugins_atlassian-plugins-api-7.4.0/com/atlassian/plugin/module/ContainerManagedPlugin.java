/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugin.module;

import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.module.ContainerAccessor;

public interface ContainerManagedPlugin
extends Plugin {
    public ContainerAccessor getContainerAccessor();
}

