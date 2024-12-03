/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.config.db.HibernateConfig
 *  com.google.common.collect.ImmutableMap
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.hibernate.SharedSessionContract
 *  org.hibernate.boot.model.naming.Identifier
 *  org.hibernate.boot.model.relational.SqlStringGenerationContext
 *  org.hibernate.dialect.Dialect
 *  org.hibernate.engine.jdbc.env.spi.JdbcEnvironment
 *  org.hibernate.engine.spi.SessionFactoryImplementor
 *  org.hibernate.engine.spi.SessionImplementor
 *  org.hibernate.resource.transaction.spi.DdlTransactionIsolator
 *  org.hibernate.service.ServiceRegistry
 *  org.hibernate.service.spi.ServiceRegistryImplementor
 *  org.hibernate.tool.schema.extract.internal.DatabaseInformationImpl
 *  org.hibernate.tool.schema.internal.HibernateSchemaManagementTool
 *  org.hibernate.tool.schema.internal.exec.JdbcConnectionAccessProvidedConnectionImpl
 *  org.hibernate.tool.schema.internal.exec.JdbcContext
 *  org.hibernate.tool.schema.spi.SchemaManagementTool
 */
package com.atlassian.confluence.core.persistence.schema.hibernate;

import com.atlassian.config.db.HibernateConfig;
import com.atlassian.confluence.core.persistence.schema.api.SchemaInformationService;
import com.google.common.collect.ImmutableMap;
import java.sql.SQLException;
import java.util.Map;
import java.util.Objects;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.hibernate.SharedSessionContract;
import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.relational.SqlStringGenerationContext;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.resource.transaction.spi.DdlTransactionIsolator;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.spi.ServiceRegistryImplementor;
import org.hibernate.tool.schema.extract.internal.DatabaseInformationImpl;
import org.hibernate.tool.schema.internal.HibernateSchemaManagementTool;
import org.hibernate.tool.schema.internal.exec.JdbcConnectionAccessProvidedConnectionImpl;
import org.hibernate.tool.schema.internal.exec.JdbcContext;
import org.hibernate.tool.schema.spi.SchemaManagementTool;

public class HibernateSchemaInformationService
implements SchemaInformationService {
    private final SessionFactoryImplementor sessionFactory;
    private final HibernateConfig hibernateConfig;

    public HibernateSchemaInformationService(SessionFactoryImplementor sessionFactory, HibernateConfig hibernateConfig) {
        this.sessionFactory = Objects.requireNonNull(sessionFactory);
        this.hibernateConfig = Objects.requireNonNull(hibernateConfig);
    }

    @Override
    public @NonNull SchemaInformationService.CloseableDatabaseInformation getDatabaseInformation() throws SQLException {
        SessionImplementor session = (SessionImplementor)this.sessionFactory.openSession();
        SqlStringGenerationContext sqlStringGenerationContext = this.sessionFactory.getSqlStringGenerationContext();
        ServiceRegistryImplementor serviceRegistry = this.sessionFactory.getServiceRegistry();
        JdbcEnvironment jdbcEnvironment = (JdbcEnvironment)serviceRegistry.getService(JdbcEnvironment.class);
        HibernateSchemaManagementTool schemaManagementTool = (HibernateSchemaManagementTool)serviceRegistry.getService(SchemaManagementTool.class);
        JdbcContext jdbcContext = this.buildJdbcContext(session, schemaManagementTool);
        DdlTransactionIsolator ddlTransactionIsolator = schemaManagementTool.getDdlTransactionIsolator(jdbcContext);
        return new CloseableDatabaseInformationImpl(serviceRegistry, jdbcEnvironment, sqlStringGenerationContext, ddlTransactionIsolator, schemaManagementTool, (SharedSessionContract)session);
    }

    private JdbcContext buildJdbcContext(SessionImplementor session, HibernateSchemaManagementTool schemaManagementTool) {
        JdbcContext jdbcContext = schemaManagementTool.resolveJdbcContext((Map)ImmutableMap.builder().putAll(this.hibernateConfig.getApplicationConfig().getProperties()).put((Object)"jakarta.persistence.schema-generation-connection", (Object)session.connection()).build());
        if (!(jdbcContext.getJdbcConnectionAccess() instanceof JdbcConnectionAccessProvidedConnectionImpl)) {
            throw new IllegalStateException("JdbcConnectionAccess must be a JdbcConnectionAccessProvidedConnectionImpl");
        }
        return jdbcContext;
    }

    @Override
    public Dialect getDialect() {
        return this.getJdbcEnvironment().getDialect();
    }

    @Override
    public Identifier getCurrentCatalog() {
        return this.getJdbcEnvironment().getCurrentCatalog();
    }

    @Override
    public Identifier getCurrentSchema() {
        return this.getJdbcEnvironment().getCurrentSchema();
    }

    private JdbcEnvironment getJdbcEnvironment() {
        return (JdbcEnvironment)this.sessionFactory.getServiceRegistry().getService(JdbcEnvironment.class);
    }

    private static final class CloseableDatabaseInformationImpl
    extends DatabaseInformationImpl
    implements SchemaInformationService.CloseableDatabaseInformation {
        private final DdlTransactionIsolator ddlTransactionIsolator;
        private final SharedSessionContract session;

        public CloseableDatabaseInformationImpl(ServiceRegistryImplementor serviceRegistry, JdbcEnvironment jdbcEnvironment, SqlStringGenerationContext sqlStringGenerationContext, DdlTransactionIsolator ddlTransactionIsolator, HibernateSchemaManagementTool schemaManagementTool, SharedSessionContract session) throws SQLException {
            super((ServiceRegistry)serviceRegistry, jdbcEnvironment, sqlStringGenerationContext, ddlTransactionIsolator, (SchemaManagementTool)schemaManagementTool);
            this.ddlTransactionIsolator = ddlTransactionIsolator;
            this.session = session;
        }

        @Override
        public void close() {
            super.cleanup();
            this.ddlTransactionIsolator.release();
            this.session.close();
        }
    }
}

