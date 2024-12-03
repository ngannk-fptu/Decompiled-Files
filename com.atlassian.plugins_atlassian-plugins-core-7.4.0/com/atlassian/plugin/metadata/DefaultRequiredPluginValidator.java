/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.PluginAccessor
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.plugin.metadata;

import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugin.metadata.RequiredPluginProvider;
import com.atlassian.plugin.metadata.RequiredPluginValidator;
import java.util.Collection;
import java.util.HashSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultRequiredPluginValidator
implements RequiredPluginValidator {
    private static final Logger log = LoggerFactory.getLogger(DefaultRequiredPluginValidator.class);
    private final PluginAccessor pluginAccessor;
    private final RequiredPluginProvider requiredPluginProvider;
    private final Collection<String> errors;

    public DefaultRequiredPluginValidator(PluginAccessor pluginAccessor, RequiredPluginProvider requiredPluginProvider) {
        this.pluginAccessor = pluginAccessor;
        this.requiredPluginProvider = requiredPluginProvider;
        this.errors = new HashSet<String>();
    }

    @Override
    public Collection<String> validate() {
        for (String key : this.requiredPluginProvider.getRequiredPluginKeys()) {
            if (this.pluginAccessor.isPluginEnabled(key)) continue;
            log.error("Plugin Not Enabled: {}", (Object)key);
            this.errors.add(key);
        }
        for (String key : this.requiredPluginProvider.getRequiredModuleKeys()) {
            if (this.pluginAccessor.isPluginModuleEnabled(key)) continue;
            log.error("Plugin Module Not Enabled: {}", (Object)key);
            this.errors.add(key);
        }
        return this.errors;
    }
}

