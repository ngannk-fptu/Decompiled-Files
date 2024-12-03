/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.springframework.dao.DataAccessResourceFailureException
 */
package org.springframework.jdbc.core.metadata;

import java.util.Arrays;
import java.util.List;
import javax.sql.DataSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.jdbc.core.metadata.CallMetaDataContext;
import org.springframework.jdbc.core.metadata.CallMetaDataProvider;
import org.springframework.jdbc.core.metadata.Db2CallMetaDataProvider;
import org.springframework.jdbc.core.metadata.DerbyCallMetaDataProvider;
import org.springframework.jdbc.core.metadata.GenericCallMetaDataProvider;
import org.springframework.jdbc.core.metadata.HanaCallMetaDataProvider;
import org.springframework.jdbc.core.metadata.OracleCallMetaDataProvider;
import org.springframework.jdbc.core.metadata.PostgresCallMetaDataProvider;
import org.springframework.jdbc.core.metadata.SqlServerCallMetaDataProvider;
import org.springframework.jdbc.core.metadata.SybaseCallMetaDataProvider;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.jdbc.support.MetaDataAccessException;

public final class CallMetaDataProviderFactory {
    public static final List<String> supportedDatabaseProductsForProcedures = Arrays.asList("Apache Derby", "DB2", "Informix Dynamic Server", "MariaDB", "Microsoft SQL Server", "MySQL", "Oracle", "PostgreSQL", "Sybase");
    public static final List<String> supportedDatabaseProductsForFunctions = Arrays.asList("MariaDB", "Microsoft SQL Server", "MySQL", "Oracle", "PostgreSQL");
    private static final Log logger = LogFactory.getLog(CallMetaDataProviderFactory.class);

    private CallMetaDataProviderFactory() {
    }

    public static CallMetaDataProvider createMetaDataProvider(DataSource dataSource, CallMetaDataContext context) {
        try {
            return JdbcUtils.extractDatabaseMetaData(dataSource, databaseMetaData -> {
                String databaseProductName = JdbcUtils.commonDatabaseName(databaseMetaData.getDatabaseProductName());
                boolean accessProcedureColumnMetaData = context.isAccessCallParameterMetaData();
                if (context.isFunction()) {
                    if (!supportedDatabaseProductsForFunctions.contains(databaseProductName)) {
                        if (logger.isInfoEnabled()) {
                            logger.info((Object)(databaseProductName + " is not one of the databases fully supported for function calls -- supported are: " + supportedDatabaseProductsForFunctions));
                        }
                        if (accessProcedureColumnMetaData) {
                            logger.info((Object)"Metadata processing disabled - you must specify all parameters explicitly");
                            accessProcedureColumnMetaData = false;
                        }
                    }
                } else if (!supportedDatabaseProductsForProcedures.contains(databaseProductName)) {
                    if (logger.isInfoEnabled()) {
                        logger.info((Object)(databaseProductName + " is not one of the databases fully supported for procedure calls -- supported are: " + supportedDatabaseProductsForProcedures));
                    }
                    if (accessProcedureColumnMetaData) {
                        logger.info((Object)"Metadata processing disabled - you must specify all parameters explicitly");
                        accessProcedureColumnMetaData = false;
                    }
                }
                GenericCallMetaDataProvider provider = "Oracle".equals(databaseProductName) ? new OracleCallMetaDataProvider(databaseMetaData) : ("PostgreSQL".equals(databaseProductName) ? new PostgresCallMetaDataProvider(databaseMetaData) : ("Apache Derby".equals(databaseProductName) ? new DerbyCallMetaDataProvider(databaseMetaData) : ("DB2".equals(databaseProductName) ? new Db2CallMetaDataProvider(databaseMetaData) : ("HDB".equals(databaseProductName) ? new HanaCallMetaDataProvider(databaseMetaData) : ("Microsoft SQL Server".equals(databaseProductName) ? new SqlServerCallMetaDataProvider(databaseMetaData) : ("Sybase".equals(databaseProductName) ? new SybaseCallMetaDataProvider(databaseMetaData) : new GenericCallMetaDataProvider(databaseMetaData)))))));
                if (logger.isDebugEnabled()) {
                    logger.debug((Object)("Using " + provider.getClass().getName()));
                }
                provider.initializeWithMetaData(databaseMetaData);
                if (accessProcedureColumnMetaData) {
                    provider.initializeWithProcedureColumnMetaData(databaseMetaData, context.getCatalogName(), context.getSchemaName(), context.getProcedureName());
                }
                return provider;
            });
        }
        catch (MetaDataAccessException ex) {
            throw new DataAccessResourceFailureException("Error retrieving database meta-data", (Throwable)((Object)ex));
        }
    }
}

