/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  org.apache.commons.lang3.StringUtils
 *  org.osgi.framework.Bundle
 */
package com.atlassian.activeobjects.config.internal;

import com.atlassian.activeobjects.ao.ConverterUtils;
import com.atlassian.activeobjects.ao.PrefixedSchemaConfiguration;
import com.atlassian.activeobjects.config.ActiveObjectsConfiguration;
import com.atlassian.activeobjects.config.ActiveObjectsConfigurationFactory;
import com.atlassian.activeobjects.config.PluginKey;
import com.atlassian.activeobjects.config.internal.DefaultActiveObjectsConfiguration;
import com.atlassian.activeobjects.external.ActiveObjectsUpgradeTask;
import com.atlassian.activeobjects.internal.DataSourceTypeResolver;
import com.atlassian.activeobjects.internal.Prefix;
import com.atlassian.activeobjects.internal.SimplePrefix;
import com.atlassian.activeobjects.internal.config.NameConvertersFactory;
import com.atlassian.activeobjects.util.Digester;
import com.google.common.base.Preconditions;
import java.util.List;
import java.util.Set;
import net.java.ao.RawEntity;
import net.java.ao.schema.NameConverters;
import org.apache.commons.lang3.StringUtils;
import org.osgi.framework.Bundle;

public final class DefaultActiveObjectsConfigurationFactory
implements ActiveObjectsConfigurationFactory {
    private final Digester digester;
    private final NameConvertersFactory nameConvertersFactory;
    private final DataSourceTypeResolver dataSourceTypeResolver;

    public DefaultActiveObjectsConfigurationFactory(Digester digester, NameConvertersFactory nameConvertersFactory, DataSourceTypeResolver dataSourceTypeResolver) {
        this.digester = (Digester)Preconditions.checkNotNull((Object)digester);
        this.nameConvertersFactory = (NameConvertersFactory)Preconditions.checkNotNull((Object)nameConvertersFactory);
        this.dataSourceTypeResolver = (DataSourceTypeResolver)Preconditions.checkNotNull((Object)dataSourceTypeResolver);
    }

    @Override
    public ActiveObjectsConfiguration getConfiguration(Bundle bundle, String namespace, Set<Class<? extends RawEntity<?>>> entities, List<ActiveObjectsUpgradeTask> upgradeTasks) {
        PluginKey pluginKey = PluginKey.fromBundle(bundle);
        Prefix tableNamePrefix = this.getTableNamePrefix(bundle, namespace);
        NameConverters nameConverters = this.nameConvertersFactory.getNameConverters(tableNamePrefix);
        DefaultActiveObjectsConfiguration defaultActiveObjectsConfiguration = new DefaultActiveObjectsConfiguration(pluginKey, this.dataSourceTypeResolver);
        defaultActiveObjectsConfiguration.setTableNamePrefix(tableNamePrefix);
        defaultActiveObjectsConfiguration.setNameConverters(nameConverters);
        defaultActiveObjectsConfiguration.setSchemaConfiguration(new PrefixedSchemaConfiguration(tableNamePrefix));
        defaultActiveObjectsConfiguration.setEntities(entities);
        defaultActiveObjectsConfiguration.setUpgradeTasks(upgradeTasks);
        return defaultActiveObjectsConfiguration;
    }

    private Prefix getTableNamePrefix(Bundle bundle, String namespace) {
        return this.getTableNamePrefix(StringUtils.isNotBlank((CharSequence)namespace) ? namespace : bundle.getSymbolicName());
    }

    private Prefix getTableNamePrefix(String namespace) {
        String hash = this.digester.digest(namespace, 6);
        return new SimplePrefix(ConverterUtils.toUpperCase("AO_" + hash), "_");
    }
}

