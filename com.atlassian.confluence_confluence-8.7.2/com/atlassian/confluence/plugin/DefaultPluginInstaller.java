/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.PluginArtifact
 *  com.atlassian.plugin.PluginInstaller
 */
package com.atlassian.confluence.plugin;

import com.atlassian.confluence.plugin.persistence.PluginData;
import com.atlassian.confluence.plugin.persistence.PluginDataDao;
import com.atlassian.confluence.plugin.persistence.PluginDataWithoutBinary;
import com.atlassian.plugin.PluginArtifact;
import com.atlassian.plugin.PluginInstaller;

public class DefaultPluginInstaller
implements PluginInstaller {
    private PluginDataDao pluginDataDao;

    public void setPluginDataDao(PluginDataDao pluginDataDao) {
        this.pluginDataDao = pluginDataDao;
    }

    public void installPlugin(String key, PluginArtifact pluginArtifact) {
        PluginData pluginData = new PluginData();
        if (this.pluginDataDao.pluginDataExists(key)) {
            PluginDataWithoutBinary oldPluginData = this.pluginDataDao.getPluginDataWithoutBinary(key);
            pluginData.setId(oldPluginData.getId());
        }
        pluginData.setKey(key);
        pluginData.setFileName(pluginArtifact.getName());
        pluginData.setData(pluginArtifact.getInputStream());
        this.pluginDataDao.saveOrUpdate(pluginData);
    }
}

