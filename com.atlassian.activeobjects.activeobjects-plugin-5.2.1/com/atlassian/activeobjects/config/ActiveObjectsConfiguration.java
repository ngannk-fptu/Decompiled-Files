/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.activeobjects.config;

import com.atlassian.activeobjects.config.PluginKey;
import com.atlassian.activeobjects.external.ActiveObjectsUpgradeTask;
import com.atlassian.activeobjects.internal.DataSourceType;
import com.atlassian.activeobjects.internal.Prefix;
import java.util.List;
import java.util.Set;
import net.java.ao.RawEntity;
import net.java.ao.SchemaConfiguration;
import net.java.ao.schema.NameConverters;

public interface ActiveObjectsConfiguration {
    public static final String AO_TABLE_PREFIX = "AO";

    public PluginKey getPluginKey();

    public DataSourceType getDataSourceType();

    public Prefix getTableNamePrefix();

    public NameConverters getNameConverters();

    public SchemaConfiguration getSchemaConfiguration();

    public Set<Class<? extends RawEntity<?>>> getEntities();

    public List<ActiveObjectsUpgradeTask> getUpgradeTasks();
}

