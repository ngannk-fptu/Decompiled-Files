/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.beehive.ClusterLock
 *  com.atlassian.beehive.ClusterLockService
 *  com.atlassian.sal.api.pluginsettings.PluginSettings
 *  com.atlassian.sal.api.pluginsettings.PluginSettingsFactory
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.upm;

import com.atlassian.beehive.ClusterLock;
import com.atlassian.beehive.ClusterLockService;
import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.upm.UpmSettings;
import com.atlassian.upm.core.impl.NamespacedPluginSettings;
import com.atlassian.upm.impl.Locks;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SysPersisted {
    private static final Logger log = LoggerFactory.getLogger(SysPersisted.class);
    private final PluginSettingsFactory pluginSettingsFactory;
    private final ClusterLockService lockService;

    public SysPersisted(PluginSettingsFactory pluginSettingsFactory, ClusterLockService lockService) {
        this.pluginSettingsFactory = Objects.requireNonNull(pluginSettingsFactory, "pluginSettingsFactory");
        this.lockService = Objects.requireNonNull(lockService, "lockService");
    }

    public boolean is(UpmSettings setting) {
        return this.getStoredBoolean(setting);
    }

    public void set(UpmSettings setting, Boolean value) {
        this.setBoolean(setting, value);
    }

    public boolean isPluginRequestFunctionalityDisabled() {
        return this.is(UpmSettings.REQUESTS_DISABLED);
    }

    private PluginSettings getPluginSettings() {
        return new NamespacedPluginSettings(this.pluginSettingsFactory.createGlobalSettings(), SysPersisted.class.getName() + ":properties:");
    }

    private boolean getStoredBoolean(UpmSettings setting) {
        String propertyKey = setting.getSysPropertyKey();
        try {
            Object storedValue = this.getPluginSettings().get(propertyKey);
            if (storedValue != null) {
                return Boolean.valueOf(storedValue.toString());
            }
        }
        catch (Exception e) {
            log.warn("Invalid persisted property detected: " + propertyKey, (Throwable)e);
            this.removeInvalidProperty(setting);
        }
        return Boolean.getBoolean(propertyKey);
    }

    private void removeInvalidProperty(UpmSettings setting) {
        Locks.writeWithLock(this.getLock(setting), () -> this.getPluginSettings().remove(setting.getSysPropertyKey()));
    }

    private void setBoolean(UpmSettings setting, boolean propertyValue) {
        Locks.writeWithLock(this.getLock(setting), () -> this.getPluginSettings().put(setting.getSysPropertyKey(), (Object)Boolean.toString(propertyValue)));
    }

    private ClusterLock getLock(UpmSettings setting) {
        return Locks.getLock(this.lockService, this.getClass(), setting.getKey());
    }
}

