/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.persistence.EntityManagerProvider
 *  com.atlassian.confluence.status.service.SystemInformationService
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.sal.api.rdbms.TransactionalExecutorFactory
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.base.Preconditions
 *  com.google.common.base.Strings
 *  com.google.common.base.Supplier
 *  com.google.common.base.Suppliers
 *  net.java.ao.DatabaseProvider
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.jdbc.core.JdbcTemplate
 *  org.springframework.jdbc.datasource.SingleConnectionDataSource
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.extra.calendar3.querydsl;

import com.atlassian.confluence.extra.calendar3.events.ActiveObjectsInitializedEvent;
import com.atlassian.confluence.extra.calendar3.querydsl.DatabaseNameHelper;
import com.atlassian.confluence.persistence.EntityManagerProvider;
import com.atlassian.confluence.status.service.SystemInformationService;
import com.atlassian.event.api.EventListener;
import com.atlassian.sal.api.rdbms.TransactionalExecutorFactory;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import javax.sql.DataSource;
import net.java.ao.DatabaseProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.stereotype.Component;

@Component(value="databaseNameHelper")
public class DatabaseNameHelperImpl
implements DatabaseNameHelper {
    public static final String TABLE_NAME_COLUMN_KEY = "TABLE_NAME";
    public static final String COLUMN_NAME_COLUMN_KEY = "COLUMN_NAME";
    private DatabaseProvider databaseProvider;
    private final EntityManagerProvider entityManagerProvider;
    private final TransactionalExecutorFactory transactionalExecutorFactory;
    private volatile Supplier<Map<NameKey, String>> names;
    private volatile boolean activeObjectsInitialized = false;

    @VisibleForTesting
    public void setActiveObjectsInitialized(boolean activeObjectsInitialized) {
        this.activeObjectsInitialized = activeObjectsInitialized;
    }

    @Autowired
    public DatabaseNameHelperImpl(EntityManagerProvider entityManagerProvider, SystemInformationService systemInformationService, TransactionalExecutorFactory transactionalExecutorFactory) {
        this.entityManagerProvider = entityManagerProvider;
        this.transactionalExecutorFactory = transactionalExecutorFactory;
        this.names = Suppliers.memoize(this.newNamesSupplier());
    }

    @VisibleForTesting
    public DatabaseNameHelperImpl(DatabaseProvider databaseProvider) {
        this.databaseProvider = databaseProvider;
        this.entityManagerProvider = null;
        this.transactionalExecutorFactory = null;
        this.names = Suppliers.memoize(this.newNamesSupplier());
    }

    private Connection getConnection() {
        try {
            return this.databaseProvider.getConnection();
        }
        catch (SQLException e) {
            return null;
        }
    }

    @EventListener
    public void onActiveObjectsInitialized(ActiveObjectsInitializedEvent event) {
        this.initializeNames();
    }

    private synchronized void initializeNames() {
        this.names = Suppliers.memoize(this.newNamesSupplier());
    }

    @Override
    public boolean isQueryDslReady() {
        return this.activeObjectsInitialized;
    }

    @Override
    public String getCaseSensitiveTableName(String tableName) {
        return this.getCaseSensitiveName(tableName);
    }

    @Override
    public String getCaseSensitiveColumnName(String tableName, String columnName) {
        return this.getCaseSensitiveName(tableName, columnName);
    }

    private String getCaseSensitiveName(String ... qualifiedName) {
        if (!this.activeObjectsInitialized) {
            this.initializeNames();
        }
        if (qualifiedName == null || qualifiedName.length == 0) {
            throw new IllegalArgumentException("qualifiedName cannot be null or empty");
        }
        if (qualifiedName.length > 2) {
            throw new UnsupportedOperationException("Only 2 arguments are supported at this present time: table name and column name.");
        }
        String tableName = qualifiedName[0];
        Preconditions.checkNotNull((Object)tableName);
        String columnName = qualifiedName.length == 2 ? qualifiedName[1] : null;
        String caseSensitiveName = (String)((Map)this.names.get()).get(new NameKey(tableName, columnName));
        if (Strings.isNullOrEmpty((String)caseSensitiveName)) {
            throw new RuntimeException("names do not exist: (" + tableName + ", " + Strings.nullToEmpty((String)columnName) + ")");
        }
        return caseSensitiveName;
    }

    private Supplier<Map<NameKey, String>> newNamesSupplier() {
        return () -> {
            HashMap result = new HashMap();
            if (this.transactionalExecutorFactory != null) {
                this.transactionalExecutorFactory.create().execute(connection -> this.findNames(result, connection));
            } else {
                JdbcTemplate jdbcTemplate = this.newJdbcTemplate();
                jdbcTemplate.execute(connection -> this.findNames(result, connection));
            }
            this.activeObjectsInitialized = true;
            return result;
        };
    }

    private Object findNames(Map<NameKey, String> result, Connection connection) {
        DatabaseMetaData metaData = null;
        try {
            metaData = connection.getMetaData();
            this.findTableNames(metaData, result);
            this.findColumnNames(metaData, result);
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void findColumnNames(DatabaseMetaData metaData, Map<NameKey, String> result) throws SQLException {
        try (ResultSet columnResultSet = metaData.getColumns(null, null, null, null);){
            while (columnResultSet.next()) {
                String tableName = columnResultSet.getString(TABLE_NAME_COLUMN_KEY);
                String columnName = columnResultSet.getString(COLUMN_NAME_COLUMN_KEY);
                result.put(new NameKey(tableName, columnName), columnName);
            }
        }
    }

    private void findTableNames(DatabaseMetaData metaData, Map<NameKey, String> result) throws SQLException {
        try (ResultSet resultSet = metaData.getTables(null, null, null, null);){
            while (resultSet.next()) {
                String tableName = resultSet.getString(TABLE_NAME_COLUMN_KEY);
                result.put(new NameKey(tableName), tableName);
            }
        }
    }

    private JdbcTemplate newJdbcTemplate() {
        SingleConnectionDataSource dataSource = new SingleConnectionDataSource(this.getConnection(), true);
        return new JdbcTemplate((DataSource)dataSource);
    }

    private static final class NameKey {
        private final String tableName;
        private final String columnName;

        private NameKey(String tableName) {
            this(tableName, null);
        }

        private NameKey(String tableName, String columnName) {
            Preconditions.checkNotNull((Object)tableName);
            this.tableName = tableName.toLowerCase();
            this.columnName = columnName != null ? columnName.toLowerCase() : null;
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            NameKey nameKey = (NameKey)o;
            if (this.columnName != null ? !this.columnName.equals(nameKey.columnName) : nameKey.columnName != null) {
                return false;
            }
            return this.tableName.equals(nameKey.tableName);
        }

        public int hashCode() {
            int result = this.tableName.hashCode();
            result = 31 * result + (this.columnName != null ? this.columnName.hashCode() : 0);
            return result;
        }
    }
}

