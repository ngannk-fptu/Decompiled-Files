/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.module.ModuleFactory
 */
package com.atlassian.confluence.plugin;

import com.atlassian.plugin.module.ModuleFactory;

@Deprecated
public class AutodetectModuleFactoryHolder {
    private final ModuleFactory origin;

    public AutodetectModuleFactoryHolder(ModuleFactory origin) {
        this.origin = origin;
    }

    public ModuleFactory get() {
        return this.origin;
    }
}

