/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 *  com.atlassian.cache.CacheSettings
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.descriptors.AbstractModuleDescriptor
 *  com.atlassian.plugin.module.ModuleFactory
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  javax.annotation.Nonnull
 *  org.dom4j.Element
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.cache.osgi;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.cache.CacheSettings;
import com.atlassian.confluence.cache.osgi.ModuleCacheSettingsFactory;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.descriptors.AbstractModuleDescriptor;
import com.atlassian.plugin.module.ModuleFactory;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import javax.annotation.Nonnull;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ExperimentalApi
public final class CacheSettingsModuleDescriptor
extends AbstractModuleDescriptor<CacheSettings> {
    private static final Logger log = LoggerFactory.getLogger(CacheSettingsModuleDescriptor.class);
    private CacheSettings cacheSettings;

    public CacheSettingsModuleDescriptor(@ComponentImport ModuleFactory moduleFactory) {
        super(moduleFactory);
    }

    public void init(@Nonnull Plugin plugin, @Nonnull Element element) {
        super.init(plugin, element);
        log.debug("Initialising {}", (Object)this.getCompleteKey());
        this.cacheSettings = ModuleCacheSettingsFactory.buildCacheSettings(element);
    }

    public Class<CacheSettings> getModuleClass() {
        return CacheSettings.class;
    }

    public CacheSettings getModule() {
        return this.cacheSettings;
    }
}

