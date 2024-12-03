/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.springframework.dao.DataAccessResourceFailureException
 */
package org.springframework.jdbc.core.metadata;

import javax.sql.DataSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.jdbc.core.metadata.DerbyTableMetaDataProvider;
import org.springframework.jdbc.core.metadata.GenericTableMetaDataProvider;
import org.springframework.jdbc.core.metadata.HsqlTableMetaDataProvider;
import org.springframework.jdbc.core.metadata.OracleTableMetaDataProvider;
import org.springframework.jdbc.core.metadata.PostgresTableMetaDataProvider;
import org.springframework.jdbc.core.metadata.TableMetaDataContext;
import org.springframework.jdbc.core.metadata.TableMetaDataProvider;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.jdbc.support.MetaDataAccessException;

public final class TableMetaDataProviderFactory {
    private static final Log logger = LogFactory.getLog(TableMetaDataProviderFactory.class);

    private TableMetaDataProviderFactory() {
    }

    public static TableMetaDataProvider createMetaDataProvider(DataSource dataSource, TableMetaDataContext context) {
        try {
            return JdbcUtils.extractDatabaseMetaData(dataSource, databaseMetaData -> {
                String databaseProductName = JdbcUtils.commonDatabaseName(databaseMetaData.getDatabaseProductName());
                GenericTableMetaDataProvider provider = "Oracle".equals(databaseProductName) ? new OracleTableMetaDataProvider(databaseMetaData, context.isOverrideIncludeSynonymsDefault()) : ("PostgreSQL".equals(databaseProductName) ? new PostgresTableMetaDataProvider(databaseMetaData) : ("Apache Derby".equals(databaseProductName) ? new DerbyTableMetaDataProvider(databaseMetaData) : ("HSQL Database Engine".equals(databaseProductName) ? new HsqlTableMetaDataProvider(databaseMetaData) : new GenericTableMetaDataProvider(databaseMetaData))));
                if (logger.isDebugEnabled()) {
                    logger.debug((Object)("Using " + provider.getClass().getSimpleName()));
                }
                provider.initializeWithMetaData(databaseMetaData);
                if (context.isAccessTableColumnMetaData()) {
                    provider.initializeWithTableColumnMetaData(databaseMetaData, context.getCatalogName(), context.getSchemaName(), context.getTableName());
                }
                return provider;
            });
        }
        catch (MetaDataAccessException ex) {
            throw new DataAccessResourceFailureException("Error retrieving database meta-data", (Throwable)((Object)ex));
        }
    }
}

