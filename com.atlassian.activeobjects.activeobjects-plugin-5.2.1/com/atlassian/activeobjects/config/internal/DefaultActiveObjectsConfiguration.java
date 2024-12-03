/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  org.apache.commons.lang3.builder.EqualsBuilder
 *  org.apache.commons.lang3.builder.HashCodeBuilder
 */
package com.atlassian.activeobjects.config.internal;

import com.atlassian.activeobjects.config.ActiveObjectsConfiguration;
import com.atlassian.activeobjects.config.PluginKey;
import com.atlassian.activeobjects.external.ActiveObjectsUpgradeTask;
import com.atlassian.activeobjects.internal.DataSourceType;
import com.atlassian.activeobjects.internal.DataSourceTypeResolver;
import com.atlassian.activeobjects.internal.Prefix;
import com.google.common.base.Preconditions;
import java.util.List;
import java.util.Set;
import net.java.ao.RawEntity;
import net.java.ao.SchemaConfiguration;
import net.java.ao.schema.NameConverters;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public final class DefaultActiveObjectsConfiguration
implements ActiveObjectsConfiguration {
    private final PluginKey pluginKey;
    private final DataSourceTypeResolver dataSourceTypeResolver;
    private Prefix tableNamePrefix;
    private NameConverters nameConverters;
    private SchemaConfiguration schemaConfiguration;
    private Set<Class<? extends RawEntity<?>>> entities;
    private List<ActiveObjectsUpgradeTask> upgradeTasks;

    public DefaultActiveObjectsConfiguration(PluginKey pluginKey, DataSourceTypeResolver dataSourceTypeResolver) {
        this.pluginKey = (PluginKey)Preconditions.checkNotNull((Object)pluginKey);
        this.dataSourceTypeResolver = (DataSourceTypeResolver)Preconditions.checkNotNull((Object)dataSourceTypeResolver);
    }

    @Override
    public PluginKey getPluginKey() {
        return this.pluginKey;
    }

    @Override
    public DataSourceType getDataSourceType() {
        return this.dataSourceTypeResolver.getDataSourceType(this.getTableNamePrefix());
    }

    @Override
    public Prefix getTableNamePrefix() {
        return this.tableNamePrefix;
    }

    public void setTableNamePrefix(Prefix tableNamePrefix) {
        this.tableNamePrefix = tableNamePrefix;
    }

    @Override
    public NameConverters getNameConverters() {
        return this.nameConverters;
    }

    public void setNameConverters(NameConverters nameConverters) {
        this.nameConverters = nameConverters;
    }

    @Override
    public SchemaConfiguration getSchemaConfiguration() {
        return this.schemaConfiguration;
    }

    public void setSchemaConfiguration(SchemaConfiguration schemaConfiguration) {
        this.schemaConfiguration = schemaConfiguration;
    }

    @Override
    public Set<Class<? extends RawEntity<?>>> getEntities() {
        return this.entities;
    }

    public void setEntities(Set<Class<? extends RawEntity<?>>> entities) {
        this.entities = entities;
    }

    @Override
    public List<ActiveObjectsUpgradeTask> getUpgradeTasks() {
        return this.upgradeTasks;
    }

    public void setUpgradeTasks(List<ActiveObjectsUpgradeTask> upgradeTasks) {
        this.upgradeTasks = upgradeTasks;
    }

    public final int hashCode() {
        return new HashCodeBuilder(5, 13).append((Object)this.pluginKey).toHashCode();
    }

    public final boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (o == this) {
            return true;
        }
        if (o.getClass() != this.getClass()) {
            return false;
        }
        DefaultActiveObjectsConfiguration configuration = (DefaultActiveObjectsConfiguration)o;
        return new EqualsBuilder().append((Object)this.pluginKey, (Object)configuration.pluginKey).isEquals();
    }
}

