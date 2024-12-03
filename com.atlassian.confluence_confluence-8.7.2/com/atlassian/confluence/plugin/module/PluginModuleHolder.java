/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.ModuleDescriptor
 *  com.atlassian.plugin.StateAware
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugin.module;

import com.atlassian.confluence.plugin.module.DefaultPluginModuleFactory;
import com.atlassian.confluence.plugin.module.PluginModuleFactory;
import com.atlassian.plugin.ModuleDescriptor;
import com.atlassian.plugin.StateAware;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PluginModuleHolder<T> {
    private static final Logger log = LoggerFactory.getLogger(PluginModuleHolder.class);
    private final PluginModuleFactory<? extends T> pluginModuleFactory;
    private T module;
    private boolean enabled;

    public static <T> PluginModuleHolder<T> getInstanceWithDefaultFactory(ModuleDescriptor<? extends T> moduleDescriptor) {
        return new PluginModuleHolder<T>(new DefaultPluginModuleFactory<T>(moduleDescriptor));
    }

    public static <T> PluginModuleHolder<T> getInstance(PluginModuleFactory<? extends T> pluginModuleFactory) {
        return new PluginModuleHolder<T>(pluginModuleFactory);
    }

    private PluginModuleHolder(PluginModuleFactory<? extends T> pluginModuleFactory) {
        this.pluginModuleFactory = pluginModuleFactory;
    }

    public void enabled(Class<T> moduleClass) {
        if (this.enabled) {
            return;
        }
        if (moduleClass != null && StateAware.class.isAssignableFrom(moduleClass)) {
            if (this.module == null) {
                this.module = this.createModule();
            }
            ((StateAware)this.module).enabled();
        }
        this.enabled = true;
    }

    private T createModule() {
        log.debug("Instantiating plugin module: " + this);
        return this.pluginModuleFactory.createModule();
    }

    public void disabled() {
        if (this.enabled && this.module instanceof StateAware) {
            ((StateAware)this.module).disabled();
        }
        this.enabled = false;
        this.module = null;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public T getModule() throws IllegalStateException {
        if (!this.enabled) {
            throw new IllegalStateException("Cannot retrieve plugin module before it is enabled: " + this);
        }
        if (this.module == null) {
            this.module = this.createModule();
        }
        return this.module;
    }

    public String toString() {
        if (this.module == null) {
            return "PluginModuleHolder[(unknown; not enabled)]";
        }
        return "PluginModuleHolder[" + this.module + "]";
    }
}

