/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.engine.jdbc.env.internal;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.hibernate.HibernateException;
import org.hibernate.boot.model.source.internal.hbm.CommaSeparatedStringHelper;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.jdbc.connections.spi.JdbcConnectionAccess;
import org.hibernate.engine.jdbc.cursor.internal.StandardRefCursorSupport;
import org.hibernate.engine.jdbc.env.spi.ExtractedDatabaseMetaData;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;
import org.hibernate.engine.jdbc.env.spi.SQLStateType;
import org.hibernate.tool.schema.extract.spi.ExtractionContext;
import org.hibernate.tool.schema.extract.spi.SequenceInformation;

public class ExtractedDatabaseMetaDataImpl
implements ExtractedDatabaseMetaData {
    private final JdbcEnvironment jdbcEnvironment;
    private final JdbcConnectionAccess connectionAccess;
    private final String connectionCatalogName;
    private final String connectionSchemaName;
    private final boolean supportsRefCursors;
    private final boolean supportsNamedParameters;
    private final boolean supportsScrollableResults;
    private final boolean supportsGetGeneratedKeys;
    private final boolean supportsBatchUpdates;
    private final boolean supportsDataDefinitionInTransaction;
    private final boolean doesDataDefinitionCauseTransactionCommit;
    private final SQLStateType sqlStateType;
    private final boolean jdbcMetadataAccessible;
    private List<SequenceInformation> sequenceInformationList;

    private ExtractedDatabaseMetaDataImpl(JdbcEnvironment jdbcEnvironment, JdbcConnectionAccess connectionAccess, String connectionCatalogName, String connectionSchemaName, boolean supportsRefCursors, boolean supportsNamedParameters, boolean supportsScrollableResults, boolean supportsGetGeneratedKeys, boolean supportsBatchUpdates, boolean supportsDataDefinitionInTransaction, boolean doesDataDefinitionCauseTransactionCommit, SQLStateType sqlStateType, boolean jdbcMetadataIsAccessible) {
        this.jdbcEnvironment = jdbcEnvironment;
        this.connectionAccess = connectionAccess;
        this.connectionCatalogName = connectionCatalogName;
        this.connectionSchemaName = connectionSchemaName;
        this.supportsRefCursors = supportsRefCursors;
        this.supportsNamedParameters = supportsNamedParameters;
        this.supportsScrollableResults = supportsScrollableResults;
        this.supportsGetGeneratedKeys = supportsGetGeneratedKeys;
        this.supportsBatchUpdates = supportsBatchUpdates;
        this.supportsDataDefinitionInTransaction = supportsDataDefinitionInTransaction;
        this.doesDataDefinitionCauseTransactionCommit = doesDataDefinitionCauseTransactionCommit;
        this.sqlStateType = sqlStateType;
        this.jdbcMetadataAccessible = jdbcMetadataIsAccessible;
    }

    @Override
    public boolean supportsRefCursors() {
        return this.supportsRefCursors;
    }

    @Override
    public JdbcEnvironment getJdbcEnvironment() {
        return this.jdbcEnvironment;
    }

    @Override
    public boolean supportsNamedParameters() {
        return this.supportsNamedParameters;
    }

    @Override
    public boolean supportsScrollableResults() {
        return this.supportsScrollableResults;
    }

    @Override
    public boolean supportsGetGeneratedKeys() {
        return this.supportsGetGeneratedKeys;
    }

    @Override
    public boolean supportsBatchUpdates() {
        return this.supportsBatchUpdates;
    }

    @Override
    public boolean supportsDataDefinitionInTransaction() {
        return this.supportsDataDefinitionInTransaction;
    }

    @Override
    public boolean doesDataDefinitionCauseTransactionCommit() {
        return this.doesDataDefinitionCauseTransactionCommit;
    }

    @Override
    public SQLStateType getSqlStateType() {
        return this.sqlStateType;
    }

    @Override
    public String getConnectionCatalogName() {
        return this.connectionCatalogName;
    }

    @Override
    public String getConnectionSchemaName() {
        return this.connectionSchemaName;
    }

    @Override
    public synchronized List<SequenceInformation> getSequenceInformationList() {
        if (this.jdbcMetadataAccessible) {
            if (this.sequenceInformationList == null) {
                this.sequenceInformationList = this.sequenceInformationList();
            }
            return this.sequenceInformationList;
        }
        return Collections.emptyList();
    }

    private List<SequenceInformation> sequenceInformationList() {
        final JdbcEnvironment jdbcEnvironment = this.jdbcEnvironment;
        Dialect dialect = this.jdbcEnvironment.getDialect();
        Connection connection = null;
        try {
            final Connection c = connection = this.connectionAccess.obtainConnection();
            Iterable<SequenceInformation> sequenceInformationIterable = dialect.getSequenceInformationExtractor().extractMetadata(new ExtractionContext.EmptyExtractionContext(){

                @Override
                public Connection getJdbcConnection() {
                    return c;
                }

                @Override
                public JdbcEnvironment getJdbcEnvironment() {
                    return jdbcEnvironment;
                }
            });
            List<SequenceInformation> list = StreamSupport.stream(sequenceInformationIterable.spliterator(), false).collect(Collectors.toList());
            return list;
        }
        catch (SQLException e) {
            throw new HibernateException("Could not fetch the SequenceInformation from the database", e);
        }
        finally {
            if (connection != null) {
                try {
                    this.connectionAccess.releaseConnection(connection);
                }
                catch (SQLException sQLException) {}
            }
        }
    }

    public static class Builder {
        private final JdbcEnvironment jdbcEnvironment;
        private final boolean jdbcMetadataIsAccessible;
        private final JdbcConnectionAccess connectionAccess;
        private String connectionSchemaName;
        private String connectionCatalogName;
        private boolean supportsRefCursors;
        private boolean supportsNamedParameters;
        private boolean supportsScrollableResults;
        private boolean supportsGetGeneratedKeys;
        private boolean supportsBatchUpdates = true;
        private boolean supportsDataDefinitionInTransaction;
        private boolean doesDataDefinitionCauseTransactionCommit;
        private SQLStateType sqlStateType;

        public Builder(JdbcEnvironment jdbcEnvironment, boolean jdbcMetadataIsAccessible, JdbcConnectionAccess connectionAccess) {
            this.jdbcEnvironment = jdbcEnvironment;
            this.jdbcMetadataIsAccessible = jdbcMetadataIsAccessible;
            this.connectionAccess = connectionAccess;
        }

        public Builder apply(DatabaseMetaData databaseMetaData) throws SQLException {
            this.connectionCatalogName = databaseMetaData.getConnection().getCatalog();
            this.supportsRefCursors = StandardRefCursorSupport.supportsRefCursors(databaseMetaData);
            this.supportsNamedParameters = databaseMetaData.supportsNamedParameters();
            this.supportsScrollableResults = databaseMetaData.supportsResultSetType(1004);
            this.supportsGetGeneratedKeys = databaseMetaData.supportsGetGeneratedKeys();
            this.supportsBatchUpdates = databaseMetaData.supportsBatchUpdates();
            this.supportsDataDefinitionInTransaction = !databaseMetaData.dataDefinitionIgnoredInTransactions();
            this.doesDataDefinitionCauseTransactionCommit = databaseMetaData.dataDefinitionCausesTransactionCommit();
            this.sqlStateType = SQLStateType.interpretReportedSQLStateType(databaseMetaData.getSQLStateType());
            return this;
        }

        private Set<String> parseKeywords(String extraKeywordsString) {
            return CommaSeparatedStringHelper.split(extraKeywordsString);
        }

        public Builder setConnectionSchemaName(String connectionSchemaName) {
            this.connectionSchemaName = connectionSchemaName;
            return this;
        }

        public Builder setConnectionCatalogName(String connectionCatalogName) {
            this.connectionCatalogName = connectionCatalogName;
            return this;
        }

        public Builder setSupportsRefCursors(boolean supportsRefCursors) {
            this.supportsRefCursors = supportsRefCursors;
            return this;
        }

        public Builder setSupportsNamedParameters(boolean supportsNamedParameters) {
            this.supportsNamedParameters = supportsNamedParameters;
            return this;
        }

        public Builder setSupportsScrollableResults(boolean supportsScrollableResults) {
            this.supportsScrollableResults = supportsScrollableResults;
            return this;
        }

        public Builder setSupportsGetGeneratedKeys(boolean supportsGetGeneratedKeys) {
            this.supportsGetGeneratedKeys = supportsGetGeneratedKeys;
            return this;
        }

        public Builder setSupportsBatchUpdates(boolean supportsBatchUpdates) {
            this.supportsBatchUpdates = supportsBatchUpdates;
            return this;
        }

        public Builder setSupportsDataDefinitionInTransaction(boolean supportsDataDefinitionInTransaction) {
            this.supportsDataDefinitionInTransaction = supportsDataDefinitionInTransaction;
            return this;
        }

        public Builder setDoesDataDefinitionCauseTransactionCommit(boolean doesDataDefinitionCauseTransactionCommit) {
            this.doesDataDefinitionCauseTransactionCommit = doesDataDefinitionCauseTransactionCommit;
            return this;
        }

        public Builder setSqlStateType(SQLStateType sqlStateType) {
            this.sqlStateType = sqlStateType;
            return this;
        }

        public ExtractedDatabaseMetaDataImpl build() {
            return new ExtractedDatabaseMetaDataImpl(this.jdbcEnvironment, this.connectionAccess, this.connectionCatalogName, this.connectionSchemaName, this.supportsRefCursors, this.supportsNamedParameters, this.supportsScrollableResults, this.supportsGetGeneratedKeys, this.supportsBatchUpdates, this.supportsDataDefinitionInTransaction, this.doesDataDefinitionCauseTransactionCommit, this.sqlStateType, this.jdbcMetadataIsAccessible);
        }
    }
}

