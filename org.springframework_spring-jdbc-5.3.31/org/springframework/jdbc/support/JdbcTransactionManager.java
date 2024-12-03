/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.core.SpringProperties
 *  org.springframework.dao.DataAccessException
 *  org.springframework.lang.Nullable
 */
package org.springframework.jdbc.support;

import java.sql.SQLException;
import javax.sql.DataSource;
import org.springframework.core.SpringProperties;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.support.SQLErrorCodeSQLExceptionTranslator;
import org.springframework.jdbc.support.SQLExceptionSubclassTranslator;
import org.springframework.jdbc.support.SQLExceptionTranslator;
import org.springframework.lang.Nullable;

public class JdbcTransactionManager
extends DataSourceTransactionManager {
    private static final boolean shouldIgnoreXml = SpringProperties.getFlag((String)"spring.xml.ignore");
    @Nullable
    private volatile SQLExceptionTranslator exceptionTranslator;
    private boolean lazyInit = true;

    public JdbcTransactionManager() {
    }

    public JdbcTransactionManager(DataSource dataSource) {
        this();
        this.setDataSource(dataSource);
        this.afterPropertiesSet();
    }

    public void setDatabaseProductName(String dbName) {
        if (!shouldIgnoreXml) {
            this.exceptionTranslator = new SQLErrorCodeSQLExceptionTranslator(dbName);
        }
    }

    public void setExceptionTranslator(SQLExceptionTranslator exceptionTranslator) {
        this.exceptionTranslator = exceptionTranslator;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public SQLExceptionTranslator getExceptionTranslator() {
        SQLExceptionTranslator exceptionTranslator = this.exceptionTranslator;
        if (exceptionTranslator != null) {
            return exceptionTranslator;
        }
        JdbcTransactionManager jdbcTransactionManager = this;
        synchronized (jdbcTransactionManager) {
            exceptionTranslator = this.exceptionTranslator;
            if (exceptionTranslator == null) {
                exceptionTranslator = shouldIgnoreXml ? new SQLExceptionSubclassTranslator() : new SQLErrorCodeSQLExceptionTranslator(this.obtainDataSource());
                this.exceptionTranslator = exceptionTranslator;
            }
            return exceptionTranslator;
        }
    }

    public void setLazyInit(boolean lazyInit) {
        this.lazyInit = lazyInit;
    }

    public boolean isLazyInit() {
        return this.lazyInit;
    }

    @Override
    public void afterPropertiesSet() {
        super.afterPropertiesSet();
        if (!this.isLazyInit()) {
            this.getExceptionTranslator();
        }
    }

    @Override
    protected RuntimeException translateException(String task, SQLException ex) {
        DataAccessException dae = this.getExceptionTranslator().translate(task, null, ex);
        if (dae != null) {
            return dae;
        }
        return super.translateException(task, ex);
    }
}

