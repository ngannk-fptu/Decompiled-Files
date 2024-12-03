/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugin.module;

import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.module.LegacyModuleFactory;

public interface ModuleFactory {
    public static final ModuleFactory LEGACY_MODULE_FACTORY = new LegacyModuleFactory();

    public <T> T createModule(String var1, ModuleDescriptor<T> var2);
}

