/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.tool.schema.internal.exec;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.relational.SqlStringGenerationContext;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;
import org.hibernate.resource.transaction.spi.DdlTransactionIsolator;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.tool.schema.extract.spi.ExtractionContext;

public class ImprovedExtractionContextImpl
implements ExtractionContext {
    private final ServiceRegistry serviceRegistry;
    private final JdbcEnvironment jdbcEnvironment;
    private final SqlStringGenerationContext sqlStringGenerationContext;
    private final DdlTransactionIsolator ddlTransactionIsolator;
    private final ExtractionContext.DatabaseObjectAccess databaseObjectAccess;
    private DatabaseMetaData jdbcDatabaseMetaData;

    public ImprovedExtractionContextImpl(ServiceRegistry serviceRegistry, JdbcEnvironment jdbcEnvironment, SqlStringGenerationContext sqlStringGenerationContext, DdlTransactionIsolator ddlTransactionIsolator, ExtractionContext.DatabaseObjectAccess databaseObjectAccess) {
        this.serviceRegistry = serviceRegistry;
        this.jdbcEnvironment = jdbcEnvironment;
        this.sqlStringGenerationContext = sqlStringGenerationContext;
        this.ddlTransactionIsolator = ddlTransactionIsolator;
        this.databaseObjectAccess = databaseObjectAccess;
    }

    @Override
    public ServiceRegistry getServiceRegistry() {
        return this.serviceRegistry;
    }

    @Override
    public JdbcEnvironment getJdbcEnvironment() {
        return this.jdbcEnvironment;
    }

    @Override
    public SqlStringGenerationContext getSqlStringGenerationContext() {
        return this.sqlStringGenerationContext;
    }

    @Override
    public Connection getJdbcConnection() {
        return this.ddlTransactionIsolator.getIsolatedConnection();
    }

    @Override
    public DatabaseMetaData getJdbcDatabaseMetaData() {
        if (this.jdbcDatabaseMetaData == null) {
            try {
                this.jdbcDatabaseMetaData = this.getJdbcConnection().getMetaData();
            }
            catch (SQLException e) {
                throw this.jdbcEnvironment.getSqlExceptionHelper().convert(e, "Unable to obtain JDBC DatabaseMetaData");
            }
        }
        return this.jdbcDatabaseMetaData;
    }

    @Override
    public Identifier getDefaultCatalog() {
        return this.sqlStringGenerationContext.getDefaultCatalog();
    }

    @Override
    public Identifier getDefaultSchema() {
        return this.sqlStringGenerationContext.getDefaultSchema();
    }

    @Override
    public ExtractionContext.DatabaseObjectAccess getDatabaseObjectAccess() {
        return this.databaseObjectAccess;
    }

    @Override
    public void cleanup() {
        if (this.jdbcDatabaseMetaData != null) {
            this.jdbcDatabaseMetaData = null;
        }
    }
}

