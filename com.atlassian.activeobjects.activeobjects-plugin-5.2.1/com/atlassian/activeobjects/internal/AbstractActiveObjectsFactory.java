/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.beehive.ClusterLock
 *  com.atlassian.beehive.ClusterLockService
 *  com.atlassian.sal.api.transaction.TransactionTemplate
 *  com.google.common.base.Preconditions
 *  com.google.common.base.Supplier
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.activeobjects.internal;

import com.atlassian.activeobjects.config.ActiveObjectsConfiguration;
import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.activeobjects.internal.ActiveObjectUpgradeManager;
import com.atlassian.activeobjects.internal.ActiveObjectsFactory;
import com.atlassian.activeobjects.internal.ActiveObjectsInitException;
import com.atlassian.activeobjects.internal.DataSourceType;
import com.atlassian.beehive.ClusterLock;
import com.atlassian.beehive.ClusterLockService;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.google.common.base.Preconditions;
import com.google.common.base.Supplier;
import java.util.Collection;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import net.java.ao.RawEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

abstract class AbstractActiveObjectsFactory
implements ActiveObjectsFactory {
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());
    private static final String LOCK_TIMEOUT_PROPERTY = "ao-plugin.upgrade.task.lock.timeout";
    private static final String LOCK_PREFIX = "ao-plugin.upgrade.";
    protected static final int LOCK_TIMEOUT_SECONDS = Integer.getInteger("ao-plugin.upgrade.task.lock.timeout", 300000);
    private final DataSourceType supportedDataSourceType;
    protected final ActiveObjectUpgradeManager aoUpgradeManager;
    protected final TransactionTemplate transactionTemplate;
    private final ClusterLockService clusterLockService;

    AbstractActiveObjectsFactory(DataSourceType dataSourceType, ActiveObjectUpgradeManager aoUpgradeManager, TransactionTemplate transactionTemplate, ClusterLockService clusterLockService) {
        this.supportedDataSourceType = (DataSourceType)((Object)Preconditions.checkNotNull((Object)((Object)dataSourceType)));
        this.aoUpgradeManager = (ActiveObjectUpgradeManager)Preconditions.checkNotNull((Object)aoUpgradeManager);
        this.transactionTemplate = (TransactionTemplate)Preconditions.checkNotNull((Object)transactionTemplate);
        this.clusterLockService = (ClusterLockService)Preconditions.checkNotNull((Object)clusterLockService);
    }

    @Override
    public final boolean accept(ActiveObjectsConfiguration configuration) {
        return this.supportedDataSourceType.equals((Object)configuration.getDataSourceType());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public final ActiveObjects create(ActiveObjectsConfiguration configuration) {
        if (!this.accept(configuration)) {
            throw new IllegalStateException(configuration + " is not supported. Did you can #accept(ActiveObjectConfiguration) before calling me?");
        }
        String lockName = LOCK_PREFIX + configuration.getPluginKey().asString();
        ClusterLock lock = this.clusterLockService.getLockForName(lockName);
        try {
            if (!lock.tryLock((long)LOCK_TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
                throw new ActiveObjectsInitException("unable to acquire cluster lock named '" + lockName + "' after waiting " + LOCK_TIMEOUT_SECONDS + " seconds; note that this timeout may be adjusted via the system property '" + LOCK_TIMEOUT_PROPERTY + "'");
            }
        }
        catch (InterruptedException e) {
            throw new ActiveObjectsInitException("interrupted while trying to acquire cluster lock named '" + lockName + "'", e);
        }
        try {
            this.upgrade(configuration);
            ActiveObjects ao = this.doCreate(configuration);
            Set<Class<? extends RawEntity<?>>> entitiesToMigrate = configuration.getEntities();
            ActiveObjects activeObjects = (ActiveObjects)this.transactionTemplate.execute(() -> {
                this.logger.debug("Created active objects instance with configuration {}, now migrating entities {}", (Object)configuration, (Object)entitiesToMigrate);
                ao.migrate(this.asArray(entitiesToMigrate));
                return ao;
            });
            return activeObjects;
        }
        finally {
            lock.unlock();
        }
    }

    protected void upgrade(ActiveObjectsConfiguration configuration) {
        this.aoUpgradeManager.upgrade(configuration.getTableNamePrefix(), configuration.getUpgradeTasks(), (Supplier<ActiveObjects>)((Supplier)() -> this.doCreate(configuration)));
    }

    private Class<? extends RawEntity<?>>[] asArray(Collection<Class<? extends RawEntity<?>>> classes) {
        return classes.toArray(new Class[classes.size()]);
    }

    protected abstract ActiveObjects doCreate(ActiveObjectsConfiguration var1);
}

