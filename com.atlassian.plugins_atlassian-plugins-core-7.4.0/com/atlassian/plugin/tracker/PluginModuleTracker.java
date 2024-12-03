/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.ModuleDescriptor
 */
package com.atlassian.plugin.tracker;

import com.atlassian.plugin.ModuleDescriptor;

public interface PluginModuleTracker<M, T extends ModuleDescriptor<M>> {
    public Iterable<T> getModuleDescriptors();

    public Iterable<M> getModules();

    public int size();

    public void close();

    public static interface Customizer<M, T extends ModuleDescriptor<M>> {
        public T adding(T var1);

        public void removed(T var1);
    }
}

