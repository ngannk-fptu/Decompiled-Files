/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.pluginsettings.PluginSettings
 *  com.atlassian.sal.api.pluginsettings.PluginSettingsFactory
 */
package com.atlassian.activeobjects.internal;

import com.atlassian.activeobjects.external.ModelVersion;
import com.atlassian.activeobjects.internal.ActiveObjectsSettingKeys;
import com.atlassian.activeobjects.internal.ModelVersionManager;
import com.atlassian.activeobjects.internal.Prefix;
import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import java.util.Objects;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public final class SalModelVersionManager
implements ModelVersionManager {
    private final ActiveObjectsSettingKeys settingKeys;
    private final PluginSettingsFactory pluginSettingsFactory;
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    public SalModelVersionManager(PluginSettingsFactory pluginSettingsFactory, ActiveObjectsSettingKeys settingKeys) {
        this.pluginSettingsFactory = Objects.requireNonNull(pluginSettingsFactory);
        this.settingKeys = Objects.requireNonNull(settingKeys);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public ModelVersion getCurrent(Prefix tableNamePrefix) {
        Lock read = this.lock.readLock();
        read.lock();
        try {
            ModelVersion modelVersion = ModelVersion.valueOf((String)this.getPluginSettings().get(this.settingKeys.getModelVersionKey(tableNamePrefix)));
            return modelVersion;
        }
        finally {
            read.unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void update(Prefix tableNamePrefix, ModelVersion version) {
        Lock write = this.lock.writeLock();
        write.lock();
        try {
            this.getPluginSettings().put(this.settingKeys.getModelVersionKey(tableNamePrefix), (Object)version.toString());
        }
        finally {
            write.unlock();
        }
    }

    private PluginSettings getPluginSettings() {
        return this.pluginSettingsFactory.createGlobalSettings();
    }
}

