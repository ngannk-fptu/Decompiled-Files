/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.config.util.BootstrapUtils
 *  com.atlassian.core.exception.InfrastructureException
 *  com.google.common.collect.ImmutableMap
 *  javax.persistence.PersistenceException
 *  org.apache.commons.lang3.StringUtils
 *  org.hibernate.JDBCException
 *  org.hibernate.Session
 *  org.hibernate.SessionFactory
 *  org.hibernate.boot.model.naming.Identifier
 *  org.hibernate.boot.model.relational.Namespace$Name
 *  org.hibernate.boot.model.relational.SqlStringGenerationContext
 *  org.hibernate.boot.registry.StandardServiceRegistry
 *  org.hibernate.engine.spi.SessionFactoryImplementor
 *  org.hibernate.engine.spi.SessionImplementor
 *  org.hibernate.resource.transaction.spi.DdlTransactionIsolator
 *  org.hibernate.service.ServiceRegistry
 *  org.hibernate.tool.schema.extract.spi.DatabaseInformation
 *  org.hibernate.tool.schema.extract.spi.TableInformation
 *  org.hibernate.tool.schema.internal.Helper
 *  org.hibernate.tool.schema.internal.HibernateSchemaManagementTool
 *  org.hibernate.tool.schema.internal.exec.JdbcContext
 *  org.hibernate.tool.schema.spi.SchemaManagementTool
 *  org.springframework.jdbc.UncategorizedSQLException
 *  org.springframework.jdbc.core.JdbcTemplate
 *  org.springframework.transaction.PlatformTransactionManager
 *  org.springframework.transaction.TransactionDefinition
 *  org.springframework.transaction.interceptor.DefaultTransactionAttribute
 *  org.springframework.transaction.support.TransactionTemplate
 */
package com.atlassian.confluence.upgrade.upgradetask;

import com.atlassian.config.util.BootstrapUtils;
import com.atlassian.confluence.setup.BootstrapManager;
import com.atlassian.core.exception.InfrastructureException;
import com.google.common.collect.ImmutableMap;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import javax.persistence.PersistenceException;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.JDBCException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.relational.Namespace;
import org.hibernate.boot.model.relational.SqlStringGenerationContext;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.resource.transaction.spi.DdlTransactionIsolator;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.tool.schema.extract.spi.DatabaseInformation;
import org.hibernate.tool.schema.extract.spi.TableInformation;
import org.hibernate.tool.schema.internal.Helper;
import org.hibernate.tool.schema.internal.HibernateSchemaManagementTool;
import org.hibernate.tool.schema.internal.exec.JdbcContext;
import org.hibernate.tool.schema.spi.SchemaManagementTool;
import org.springframework.jdbc.UncategorizedSQLException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.interceptor.DefaultTransactionAttribute;
import org.springframework.transaction.support.TransactionTemplate;

public final class DataAccessUtils {
    private DataAccessUtils() {
    }

    @Deprecated
    public static JdbcTemplate getJdbcTemplate(Session session) {
        return com.atlassian.confluence.impl.hibernate.DataAccessUtils.getJdbcTemplate(session);
    }

    private static Connection getConnection(Session session) {
        return ((SessionImplementor)session).connection();
    }

    public static Set<String> filterToExistingTables(Set<String> tableNames, PlatformTransactionManager transactionManager, SessionFactory sessionFactory) {
        return DataAccessUtils.runWithNewConnection(transactionManager, sessionFactory, connection -> DataAccessUtils.filterToExistingTables(tableNames, connection, sessionFactory));
    }

    private static Set<String> filterToExistingTables(Set<String> tableNames, Connection connection, SessionFactory sessionFactory) {
        DatabaseInformation databaseInformation;
        HashSet<String> presentTables = new HashSet<String>(tableNames);
        try {
            databaseInformation = DataAccessUtils.getDatabaseInformation(connection, sessionFactory);
        }
        catch (SQLException | PersistenceException e) {
            throw new InfrastructureException(e);
        }
        Iterator tableNameIt = presentTables.iterator();
        while (tableNameIt.hasNext()) {
            try {
                if (databaseInformation.getTableInformation(DataAccessUtils.getNamespaceName(connection), Identifier.toIdentifier((String)((String)tableNameIt.next()))) != null) continue;
                tableNameIt.remove();
            }
            catch (SQLException | PersistenceException e) {
                throw new InfrastructureException(e);
            }
        }
        return presentTables;
    }

    public static boolean isTablePresent(String tableName, PlatformTransactionManager transactionManager, SessionFactory sessionFactory) {
        if (StringUtils.isBlank((CharSequence)tableName)) {
            return false;
        }
        HashSet<String> tables = new HashSet<String>(1);
        tables.add(tableName);
        return !DataAccessUtils.filterToExistingTables(tables, transactionManager, sessionFactory).isEmpty();
    }

    public static String getPrimaryKeyColumnName(PlatformTransactionManager transactionManager, SessionFactory sessionFactory, String tableName) {
        return DataAccessUtils.runWithNewConnection(transactionManager, sessionFactory, connection -> {
            try {
                DatabaseMetaData metaData = connection.getMetaData();
                String tableNameIdentifier = tableName;
                if (metaData.storesLowerCaseIdentifiers()) {
                    tableNameIdentifier = tableName.toLowerCase();
                }
                if (metaData.storesUpperCaseIdentifiers()) {
                    tableNameIdentifier = tableName.toUpperCase();
                }
                try (ResultSet rs = metaData.getPrimaryKeys(null, null, tableNameIdentifier);){
                    ArrayList<String> primaryKeyColumnNames = new ArrayList<String>();
                    while (rs.next()) {
                        primaryKeyColumnNames.add(rs.getString("COLUMN_NAME"));
                    }
                    switch (primaryKeyColumnNames.size()) {
                        case 0: {
                            throw new IllegalStateException("Table '" + tableName + "' has no primary key");
                        }
                        case 1: {
                            String string = (String)primaryKeyColumnNames.get(0);
                            return string;
                        }
                    }
                    throw new IllegalStateException("Table '" + tableName + "' has a multi-column primary key on " + primaryKeyColumnNames);
                }
            }
            catch (SQLException e) {
                throw new UncategorizedSQLException("Failed to retrieve primary column name for table " + tableName, "", e);
            }
        });
    }

    public static boolean isColumnPresent(String tableName, String columnName, PlatformTransactionManager transactionManager, SessionFactory sessionFactory, BootstrapManager bootstrapManager) throws InfrastructureException {
        return DataAccessUtils.runWithNewConnection(transactionManager, sessionFactory, connection -> {
            try {
                DatabaseInformation databaseInformation = DataAccessUtils.getDatabaseInformation(connection, sessionFactory);
                TableInformation tableInformation = databaseInformation.getTableInformation(DataAccessUtils.getNamespaceName(connection), Identifier.toIdentifier((String)tableName));
                if (tableInformation == null) {
                    return false;
                }
                return tableInformation.getColumn(Identifier.toIdentifier((String)columnName)) != null;
            }
            catch (SQLException | PersistenceException e) {
                throw new InfrastructureException(e);
            }
        });
    }

    public static <T> T runWithNewConnection(PlatformTransactionManager transactionManager, SessionFactory sessionFactory, Function<Connection, T> callback) {
        return (T)new TransactionTemplate(transactionManager, (TransactionDefinition)new DefaultTransactionAttribute(3)).execute(status -> {
            Session session = sessionFactory.getCurrentSession();
            Connection connection = DataAccessUtils.getConnection(session);
            return callback.apply(connection);
        });
    }

    public static DatabaseInformation getDatabaseInformation(Connection connection, SessionFactory sessionFactory) throws JDBCException, SQLException {
        StandardServiceRegistry serviceRegistry = sessionFactory.getSessionFactoryOptions().getServiceRegistry();
        HibernateSchemaManagementTool tool = (HibernateSchemaManagementTool)serviceRegistry.getService(SchemaManagementTool.class);
        JdbcContext jdbcContext = tool.resolveJdbcContext((Map)ImmutableMap.builder().putAll((Map)BootstrapUtils.getBootstrapManager().getHibernateProperties()).put((Object)"jakarta.persistence.schema-generation-connection", (Object)connection).build());
        connection.setAutoCommit(false);
        DdlTransactionIsolator ddlTransactionIsolator = tool.getDdlTransactionIsolator(jdbcContext);
        return Helper.buildDatabaseInformation((ServiceRegistry)serviceRegistry, (DdlTransactionIsolator)ddlTransactionIsolator, (SqlStringGenerationContext)((SessionFactoryImplementor)sessionFactory).getSqlStringGenerationContext(), (SchemaManagementTool)tool);
    }

    private static Namespace.Name getNamespaceName(Connection connection) throws SQLException {
        return new Namespace.Name(Identifier.toIdentifier((String)connection.getCatalog()), Identifier.toIdentifier((String)connection.getSchema()));
    }
}

