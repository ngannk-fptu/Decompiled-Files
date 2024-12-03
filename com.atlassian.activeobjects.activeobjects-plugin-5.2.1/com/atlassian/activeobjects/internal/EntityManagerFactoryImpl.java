/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.activeobjects.spi.DatabaseType
 *  com.google.common.base.Preconditions
 */
package com.atlassian.activeobjects.internal;

import com.atlassian.activeobjects.config.ActiveObjectsConfiguration;
import com.atlassian.activeobjects.internal.DatabaseProviderFactory;
import com.atlassian.activeobjects.internal.EntityManagerFactory;
import com.atlassian.activeobjects.spi.DatabaseType;
import com.google.common.base.Preconditions;
import javax.sql.DataSource;
import net.java.ao.EntityManager;
import net.java.ao.EntityManagerConfiguration;
import net.java.ao.SchemaConfiguration;
import net.java.ao.schema.NameConverters;
import net.java.ao.schema.info.CachingEntityInfoResolverFactory;
import net.java.ao.schema.info.EntityInfoResolverFactory;

public final class EntityManagerFactoryImpl
implements EntityManagerFactory {
    private final DatabaseProviderFactory databaseProviderFactory;

    public EntityManagerFactoryImpl(DatabaseProviderFactory databaseProviderFactory) {
        this.databaseProviderFactory = (DatabaseProviderFactory)Preconditions.checkNotNull((Object)databaseProviderFactory);
    }

    @Override
    public EntityManager getEntityManager(DataSource dataSource, DatabaseType databaseType, String schema, ActiveObjectsConfiguration configuration) {
        DataSourceEntityManagerConfiguration entityManagerConfiguration = new DataSourceEntityManagerConfiguration(configuration.getNameConverters(), configuration.getSchemaConfiguration(), new CachingEntityInfoResolverFactory());
        return new EntityManager(this.databaseProviderFactory.getDatabaseProvider(dataSource, databaseType, schema), entityManagerConfiguration);
    }

    private static class DataSourceEntityManagerConfiguration
    implements EntityManagerConfiguration {
        private final NameConverters nameConverters;
        private final SchemaConfiguration schemaConfiguration;
        private final EntityInfoResolverFactory entityInfoResolverFactory;

        DataSourceEntityManagerConfiguration(NameConverters nameConverters, SchemaConfiguration schemaConfiguration, EntityInfoResolverFactory entityInfoResolverFactory) {
            this.nameConverters = nameConverters;
            this.schemaConfiguration = schemaConfiguration;
            this.entityInfoResolverFactory = entityInfoResolverFactory;
        }

        @Override
        public boolean useWeakCache() {
            return true;
        }

        @Override
        public NameConverters getNameConverters() {
            return this.nameConverters;
        }

        @Override
        public SchemaConfiguration getSchemaConfiguration() {
            return this.schemaConfiguration;
        }

        @Override
        public EntityInfoResolverFactory getEntityInfoResolverFactory() {
            return this.entityInfoResolverFactory;
        }
    }
}

