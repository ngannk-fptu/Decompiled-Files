/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  javax.persistence.PersistenceException
 *  javax.persistence.Table
 *  org.hibernate.SessionFactory
 *  org.hibernate.boot.Metadata
 *  org.hibernate.boot.MetadataSources
 *  org.hibernate.boot.registry.BootstrapServiceRegistry
 *  org.hibernate.boot.registry.BootstrapServiceRegistryBuilder
 *  org.hibernate.boot.registry.StandardServiceRegistryBuilder
 *  org.hibernate.boot.registry.classloading.spi.ClassLoaderService
 *  org.hibernate.engine.jdbc.connections.spi.ConnectionProvider
 *  org.hibernate.service.ServiceRegistry
 *  org.hibernate.tool.schema.Action
 *  org.hibernate.tool.schema.spi.SchemaFilter
 *  org.hibernate.tool.schema.spi.SchemaFilterProvider
 */
package com.atlassian.migration.agent.store.jpa.impl;

import com.atlassian.migration.agent.Resources;
import com.atlassian.migration.agent.entity.ProductEntity;
import com.atlassian.migration.agent.store.jpa.SessionFactorySupplier;
import com.atlassian.migration.agent.store.jpa.impl.DefaultClassLoaderService;
import com.atlassian.migration.agent.store.jpa.impl.DialectResolver;
import com.atlassian.migration.agent.store.jpa.impl.ExcludingSchemaFilter;
import com.atlassian.migration.agent.store.jpa.impl.LiquibaseSchemaUpgrader;
import com.atlassian.migration.agent.store.jpa.impl.ThreadBoundSessionContext;
import com.google.common.annotations.VisibleForTesting;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.PersistenceException;
import javax.persistence.Table;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.BootstrapServiceRegistry;
import org.hibernate.boot.registry.BootstrapServiceRegistryBuilder;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.boot.registry.classloading.spi.ClassLoaderService;
import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.tool.schema.Action;
import org.hibernate.tool.schema.spi.SchemaFilter;
import org.hibernate.tool.schema.spi.SchemaFilterProvider;

public class DefaultSessionFactorySupplier
implements SessionFactorySupplier {
    private static final String JPA_ENTITIES_RESOURCE_PATH = "META-INF/jpa-entities";
    private static final String CHANGELOG_PATH = "META-INF/db-changelog/master.xml";
    private static final int HIBERNATE_BATCH_SIZE = 50;
    private static final boolean ORDER_INSERTS_AND_UPDATES_FLAG = true;
    private final ConnectionProvider connectionProvider;
    private final SessionFactory sessionFactory;
    private final DialectResolver dialectResolver;

    public DefaultSessionFactorySupplier(ConnectionProvider connectionProvider, DialectResolver dialectResolver) {
        this.connectionProvider = connectionProvider;
        this.dialectResolver = dialectResolver;
        this.sessionFactory = this.buildSessionFactory();
    }

    @VisibleForTesting
    DefaultSessionFactorySupplier(ConnectionProvider connectionProvider, DialectResolver dialectResolver, SessionFactory sessionFactory) {
        this.connectionProvider = connectionProvider;
        this.dialectResolver = dialectResolver;
        this.sessionFactory = sessionFactory;
    }

    @Override
    public SessionFactory get() {
        return this.sessionFactory;
    }

    private SessionFactory buildSessionFactory() {
        try {
            LiquibaseSchemaUpgrader.upgrade(this.connectionProvider.getConnection(), CHANGELOG_PATH);
        }
        catch (SQLException e) {
            throw new PersistenceException("Failed to get connection", (Throwable)e);
        }
        return this.buildMetadata(this.readEntityClassNames()).buildSessionFactory();
    }

    @VisibleForTesting
    List<String> readEntityClassNames() {
        try {
            return Resources.readLinesFromResources(JPA_ENTITIES_RESOURCE_PATH);
        }
        catch (IOException e) {
            throw new PersistenceException("Unable to load entity class names from META-INF/jpa-entities resource files", (Throwable)e);
        }
    }

    @VisibleForTesting
    Metadata buildMetadata(Collection<String> entityClassNames) {
        entityClassNames = this.sanitizeEntities(entityClassNames);
        DefaultClassLoaderService classLoaderService = new DefaultClassLoaderService();
        BootstrapServiceRegistry serviceRegistry = new BootstrapServiceRegistryBuilder().applyClassLoaderService((ClassLoaderService)new DefaultClassLoaderService()).build();
        StandardServiceRegistryBuilder registryBuilder = new StandardServiceRegistryBuilder(serviceRegistry);
        registryBuilder.applySetting("hibernate.connection.provider_class", (Object)this.connectionProvider);
        registryBuilder.applySetting("hibernate.hbm2ddl.auto", (Object)Action.VALIDATE);
        registryBuilder.applySetting("hibernate.current_session_context_class", (Object)ThreadBoundSessionContext.class.getName());
        registryBuilder.applySetting("hibernate.order_inserts", (Object)true);
        registryBuilder.applySetting("hibernate.order_updates", (Object)true);
        registryBuilder.applySetting("hibernate.jdbc.batch_size", (Object)50);
        this.dialectResolver.getCustomDialect().ifPresent(dialect -> registryBuilder.applySetting("hibernate.dialect", dialect));
        HashSet<String> excludedSchemaUpdateTables = new HashSet<String>();
        for (String entityClassName : entityClassNames) {
            Class entityClass = classLoaderService.classForName(entityClassName);
            ProductEntity productEntity = entityClass.getAnnotation(ProductEntity.class);
            if (productEntity == null) continue;
            Table table = entityClass.getAnnotation(Table.class);
            String tableName = table == null ? entityClass.getSimpleName() : table.name();
            excludedSchemaUpdateTables.add(tableName);
        }
        if (!excludedSchemaUpdateTables.isEmpty()) {
            final ExcludingSchemaFilter schemaFilter = new ExcludingSchemaFilter(excludedSchemaUpdateTables);
            registryBuilder.applySetting("hibernate.hbm2ddl.schema_filter_provider", (Object)new SchemaFilterProvider(){

                public SchemaFilter getCreateFilter() {
                    return schemaFilter;
                }

                public SchemaFilter getDropFilter() {
                    return schemaFilter;
                }

                public SchemaFilter getMigrateFilter() {
                    return schemaFilter;
                }

                public SchemaFilter getValidateFilter() {
                    return schemaFilter;
                }
            });
        }
        MetadataSources sources = new MetadataSources((ServiceRegistry)registryBuilder.build());
        entityClassNames.forEach(arg_0 -> ((MetadataSources)sources).addAnnotatedClassName(arg_0));
        return sources.getMetadataBuilder().build();
    }

    private List<String> sanitizeEntities(Collection<String> entityClassNames) {
        String poisonedEntityName = "com.atlassian.migration.agent.entity.RecentlyViewedEntity";
        return entityClassNames.stream().filter(className -> !poisonedEntityName.equals(className)).collect(Collectors.toList());
    }
}

