/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.core.SpringProperties
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 */
package org.springframework.jdbc.support;

import javax.sql.DataSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.SpringProperties;
import org.springframework.jdbc.support.SQLErrorCodeSQLExceptionTranslator;
import org.springframework.jdbc.support.SQLExceptionSubclassTranslator;
import org.springframework.jdbc.support.SQLExceptionTranslator;
import org.springframework.jdbc.support.SQLStateSQLExceptionTranslator;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

public abstract class JdbcAccessor
implements InitializingBean {
    private static final boolean shouldIgnoreXml = SpringProperties.getFlag((String)"spring.xml.ignore");
    protected final Log logger = LogFactory.getLog(this.getClass());
    @Nullable
    private DataSource dataSource;
    @Nullable
    private volatile SQLExceptionTranslator exceptionTranslator;
    private boolean lazyInit = true;

    public void setDataSource(@Nullable DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Nullable
    public DataSource getDataSource() {
        return this.dataSource;
    }

    protected DataSource obtainDataSource() {
        DataSource dataSource = this.getDataSource();
        Assert.state((dataSource != null ? 1 : 0) != 0, (String)"No DataSource set");
        return dataSource;
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
        JdbcAccessor jdbcAccessor = this;
        synchronized (jdbcAccessor) {
            exceptionTranslator = this.exceptionTranslator;
            if (exceptionTranslator == null) {
                DataSource dataSource = this.getDataSource();
                exceptionTranslator = shouldIgnoreXml ? new SQLExceptionSubclassTranslator() : (dataSource != null ? new SQLErrorCodeSQLExceptionTranslator(dataSource) : new SQLStateSQLExceptionTranslator());
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

    public void afterPropertiesSet() {
        if (this.getDataSource() == null) {
            throw new IllegalArgumentException("Property 'dataSource' is required");
        }
        if (!this.isLazyInit()) {
            this.getExceptionTranslator();
        }
    }
}

