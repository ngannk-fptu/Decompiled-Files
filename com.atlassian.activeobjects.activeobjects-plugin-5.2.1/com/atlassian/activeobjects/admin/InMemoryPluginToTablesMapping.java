/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 */
package com.atlassian.activeobjects.admin;

import com.atlassian.activeobjects.admin.PluginInfo;
import com.atlassian.activeobjects.admin.PluginToTablesMapping;
import com.google.common.annotations.VisibleForTesting;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class InMemoryPluginToTablesMapping
implements PluginToTablesMapping {
    @VisibleForTesting
    final ConcurrentMap<String, PluginInfo> pluginInfoByTableName = new ConcurrentHashMap<String, PluginInfo>();

    @Override
    public void add(PluginInfo pluginInfo, List<String> tableNames) {
        for (String tableName : tableNames) {
            this.pluginInfoByTableName.put(tableName, pluginInfo);
        }
    }

    @Override
    public PluginInfo get(String tableName) {
        return (PluginInfo)this.pluginInfoByTableName.get(tableName);
    }
}

