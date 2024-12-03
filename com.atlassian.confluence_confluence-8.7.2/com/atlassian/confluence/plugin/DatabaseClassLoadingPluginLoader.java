/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.event.PluginEventManager
 *  com.atlassian.plugin.factories.PluginFactory
 *  com.atlassian.plugin.loaders.DiscardablePluginLoader
 *  com.atlassian.plugin.loaders.DynamicPluginLoader
 *  com.atlassian.plugin.loaders.ForwardingPluginLoader
 *  com.atlassian.plugin.loaders.PluginLoader
 *  com.atlassian.plugin.loaders.ScanningPluginLoader
 *  com.atlassian.plugin.loaders.classloading.Scanner
 */
package com.atlassian.confluence.plugin;

import com.atlassian.confluence.impl.plugin.TransactionalPluginLoader;
import com.atlassian.confluence.plugin.DatabasePluginScanner;
import com.atlassian.confluence.plugin.PluginDirectoryProvider;
import com.atlassian.confluence.plugin.persistence.PluginDataDao;
import com.atlassian.confluence.spring.transaction.interceptor.TransactionalHostContextAccessor;
import com.atlassian.confluence.tenant.TenantRegistry;
import com.atlassian.plugin.event.PluginEventManager;
import com.atlassian.plugin.factories.PluginFactory;
import com.atlassian.plugin.loaders.DiscardablePluginLoader;
import com.atlassian.plugin.loaders.DynamicPluginLoader;
import com.atlassian.plugin.loaders.ForwardingPluginLoader;
import com.atlassian.plugin.loaders.PluginLoader;
import com.atlassian.plugin.loaders.ScanningPluginLoader;
import com.atlassian.plugin.loaders.classloading.Scanner;
import java.util.List;

public class DatabaseClassLoadingPluginLoader
extends ForwardingPluginLoader {
    public static TransactionalPluginLoader create(PluginDirectoryProvider directoryProvider, PluginDataDao pluginDataDao, List<PluginFactory> listPluginFactories, PluginEventManager pluginEventManager, TransactionalHostContextAccessor hostContextAccessor) {
        DatabasePluginScanner scanner = new DatabasePluginScanner(pluginDataDao, directoryProvider.getPluginsCacheDirectory(), hostContextAccessor);
        ScanningPluginLoader loader = new ScanningPluginLoader((Scanner)scanner, listPluginFactories, pluginEventManager);
        return new TransactionalPluginLoader((DynamicPluginLoader)loader, (DiscardablePluginLoader)loader);
    }

    @Deprecated(forRemoval=true)
    public DatabaseClassLoadingPluginLoader(PluginDirectoryProvider directoryProvider, PluginDataDao pluginDataDao, TenantRegistry tenantRegistry, List<PluginFactory> listPluginFactories, PluginEventManager pluginEventManager, TransactionalHostContextAccessor hostContextAccessor) {
        super((PluginLoader)new ScanningPluginLoader((Scanner)new DatabasePluginScanner(pluginDataDao, directoryProvider.getPluginsCacheDirectory(), tenantRegistry, hostContextAccessor), listPluginFactories, pluginEventManager));
    }

    @Deprecated(forRemoval=true)
    public static TransactionalPluginLoader create(PluginDirectoryProvider directoryProvider, PluginDataDao pluginDataDao, TenantRegistry tenantRegistry, List<PluginFactory> listPluginFactories, PluginEventManager pluginEventManager, TransactionalHostContextAccessor hostContextAccessor) {
        ScanningPluginLoader loader = new ScanningPluginLoader((Scanner)new DatabasePluginScanner(pluginDataDao, directoryProvider.getPluginsCacheDirectory(), tenantRegistry, hostContextAccessor), listPluginFactories, pluginEventManager);
        return new TransactionalPluginLoader((DynamicPluginLoader)loader, (DiscardablePluginLoader)loader);
    }
}

