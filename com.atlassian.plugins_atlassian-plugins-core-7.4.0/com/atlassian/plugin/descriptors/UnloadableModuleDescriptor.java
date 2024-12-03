/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.Plugin
 */
package com.atlassian.plugin.descriptors;

import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.descriptors.AbstractNoOpModuleDescriptor;

public final class UnloadableModuleDescriptor
extends AbstractNoOpModuleDescriptor<Void> {
    @Override
    protected void loadClass(Plugin plugin, String clazz) {
    }
}

