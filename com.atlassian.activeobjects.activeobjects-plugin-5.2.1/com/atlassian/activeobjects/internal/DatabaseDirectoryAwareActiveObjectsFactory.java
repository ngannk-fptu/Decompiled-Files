/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.activeobjects.spi.ActiveObjectsPluginConfiguration
 *  com.atlassian.activeobjects.spi.DatabaseType
 *  com.atlassian.beehive.ClusterLockService
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.atlassian.sal.api.transaction.TransactionTemplate
 *  com.google.common.base.Preconditions
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.activeobjects.internal;

import com.atlassian.activeobjects.ActiveObjectsPluginException;
import com.atlassian.activeobjects.config.ActiveObjectsConfiguration;
import com.atlassian.activeobjects.config.PluginKey;
import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.activeobjects.internal.AbstractActiveObjectsFactory;
import com.atlassian.activeobjects.internal.ActiveObjectUpgradeManager;
import com.atlassian.activeobjects.internal.DataSourceType;
import com.atlassian.activeobjects.internal.DatabaseDirectoryAware;
import com.atlassian.activeobjects.internal.EntityManagedActiveObjects;
import com.atlassian.activeobjects.internal.EntityManagedTransactionManager;
import com.atlassian.activeobjects.internal.TransactionManager;
import com.atlassian.activeobjects.spi.ActiveObjectsPluginConfiguration;
import com.atlassian.activeobjects.spi.DatabaseType;
import com.atlassian.beehive.ClusterLockService;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.google.common.base.Preconditions;
import java.io.File;
import net.java.ao.EntityManager;
import net.java.ao.builder.EntityManagerBuilder;
import net.java.ao.builder.EntityManagerBuilderWithDatabaseProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class DatabaseDirectoryAwareActiveObjectsFactory
extends AbstractActiveObjectsFactory {
    private static final String USER_NAME = "sa";
    private static final String PASSWORD = "";
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private final ApplicationProperties applicationProperties;
    private final ActiveObjectsPluginConfiguration dbConfiguration;

    public DatabaseDirectoryAwareActiveObjectsFactory(ActiveObjectUpgradeManager aoUpgradeManager, ApplicationProperties applicationProperties, ActiveObjectsPluginConfiguration dbConfiguration, TransactionTemplate transactionTemplate, ClusterLockService clusterLockService) {
        super(DataSourceType.HSQLDB, aoUpgradeManager, transactionTemplate, clusterLockService);
        this.applicationProperties = (ApplicationProperties)Preconditions.checkNotNull((Object)applicationProperties);
        this.dbConfiguration = (ActiveObjectsPluginConfiguration)Preconditions.checkNotNull((Object)dbConfiguration);
    }

    @Override
    protected ActiveObjects doCreate(ActiveObjectsConfiguration configuration) {
        File dbDir = this.getDatabaseDirectory(this.getDatabasesDirectory(this.getHomeDirectory()), configuration.getPluginKey());
        EntityManager entityManager = this.getEntityManager(dbDir, configuration);
        return new DatabaseDirectoryAwareEntityManagedActiveObjects(entityManager, new EntityManagedTransactionManager(entityManager));
    }

    private EntityManager getEntityManager(File dbDirectory, ActiveObjectsConfiguration configuration) {
        return ((EntityManagerBuilderWithDatabaseProperties)((EntityManagerBuilderWithDatabaseProperties)((EntityManagerBuilderWithDatabaseProperties)((EntityManagerBuilderWithDatabaseProperties)((EntityManagerBuilderWithDatabaseProperties)((EntityManagerBuilderWithDatabaseProperties)EntityManagerBuilder.url(DatabaseDirectoryAwareActiveObjectsFactory.getUri(dbDirectory)).username(USER_NAME).password(PASSWORD).auto().tableNameConverter(configuration.getNameConverters().getTableNameConverter())).fieldNameConverter(configuration.getNameConverters().getFieldNameConverter())).sequenceNameConverter(configuration.getNameConverters().getSequenceNameConverter())).triggerNameConverter(configuration.getNameConverters().getTriggerNameConverter())).indexNameConverter(configuration.getNameConverters().getIndexNameConverter())).schemaConfiguration(configuration.getSchemaConfiguration())).build();
    }

    private static String getUri(File dbDirectory) {
        return "jdbc:hsqldb:file:" + dbDirectory.getAbsolutePath() + "/db;hsqldb.default_table_type=cached";
    }

    private File getDatabaseDirectory(File databasesDirectory, PluginKey pluginKey) {
        File dbDir = new File(databasesDirectory, pluginKey.asString());
        if (!dbDir.exists() && !dbDir.mkdir()) {
            throw new ActiveObjectsPluginException("Could not create database directory for plugin <" + pluginKey + "> at  <" + dbDir.getAbsolutePath() + ">");
        }
        this.log.debug("Database directory {} initialised", (Object)dbDir);
        return dbDir;
    }

    private File getDatabasesDirectory(File home) {
        File dbDirectory;
        String path = this.dbConfiguration.getDatabaseBaseDirectory();
        if (path.startsWith("/")) {
            path = path.substring(1);
        }
        if ((dbDirectory = new File(home, path)).exists() && dbDirectory.isFile()) {
            throw new ActiveObjectsPluginException("Database directory already exists, but is a file, at <" + dbDirectory.getPath() + ">");
        }
        if (!dbDirectory.exists() && !dbDirectory.mkdirs()) {
            throw new ActiveObjectsPluginException("Could not create directory for database at <" + dbDirectory.getPath() + ">");
        }
        this.log.debug("ActiveObjects databases directory {} initialized", (Object)dbDirectory.getAbsolutePath());
        return dbDirectory;
    }

    private File getHomeDirectory() {
        File home = this.applicationProperties.getHomeDirectory();
        if (home == null) {
            throw new ActiveObjectsPluginException("Home directory undefined!");
        }
        if (!home.exists() || !home.isDirectory()) {
            throw new ActiveObjectsPluginException("The ActiveObjects plugin couldn't find a home directory at <" + home.getAbsolutePath() + ">");
        }
        return home;
    }

    private static final class DatabaseDirectoryAwareEntityManagedActiveObjects
    extends EntityManagedActiveObjects
    implements DatabaseDirectoryAware {
        DatabaseDirectoryAwareEntityManagedActiveObjects(EntityManager entityManager, TransactionManager transactionManager) {
            super(entityManager, transactionManager, DatabaseType.HSQL);
        }
    }
}

