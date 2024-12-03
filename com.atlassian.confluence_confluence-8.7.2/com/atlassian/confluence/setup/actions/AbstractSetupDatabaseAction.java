/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.config.bootstrap.BootstrapException
 *  com.atlassian.config.db.DatabaseDetails
 *  com.atlassian.config.db.HibernateConfig
 *  com.atlassian.config.util.BootstrapUtils
 *  com.atlassian.xwork.ParameterSafe
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.setup.actions;

import com.atlassian.config.bootstrap.BootstrapException;
import com.atlassian.config.db.DatabaseDetails;
import com.atlassian.config.db.HibernateConfig;
import com.atlassian.config.util.BootstrapUtils;
import com.atlassian.confluence.setup.ConfluenceDatabaseDetails;
import com.atlassian.confluence.setup.ConfluenceDatabaseDetailsBuilder;
import com.atlassian.confluence.setup.DatabaseEnum;
import com.atlassian.confluence.setup.DatabaseVerifier;
import com.atlassian.confluence.setup.DatabaseVerifyException;
import com.atlassian.confluence.setup.actions.AbstractSetupAction;
import com.atlassian.confluence.setup.settings.DatabaseErrorMessageConverter;
import com.atlassian.confluence.setup.settings.DatabaseTestResult;
import com.atlassian.xwork.ParameterSafe;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractSetupDatabaseAction
extends AbstractSetupAction {
    private static final Logger log = LoggerFactory.getLogger(AbstractSetupDatabaseAction.class);
    protected static final String DATASOURCE_PREFIX = "java:comp/env/jdbc/";
    private String database;
    protected ConfluenceDatabaseDetails dbDetails = new ConfluenceDatabaseDetailsBuilder().build();
    private HibernateConfig hibernateConfig;
    private DatabaseVerifier databaseVerifier;

    public HibernateConfig getHibernateConfig() {
        if (this.hibernateConfig == null) {
            this.hibernateConfig = BootstrapUtils.getBootstrapManager().getHibernateConfig();
        }
        return this.hibernateConfig;
    }

    public void setHibernateConfig(HibernateConfig hibernateConfig) {
        this.hibernateConfig = hibernateConfig;
    }

    public void setDatabaseVerifier(DatabaseVerifier databaseVerifier) {
        this.databaseVerifier = databaseVerifier;
    }

    public String getDatabase() {
        return this.database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }

    protected String detectDatabaseType(Connection connection) throws SQLException {
        String productName = connection.getMetaData().getDatabaseProductName();
        int majorVersion = connection.getMetaData().getDatabaseMajorVersion();
        return DatabaseEnum.getDatabaseType(productName, majorVersion);
    }

    @ParameterSafe
    public ConfluenceDatabaseDetails getDbConfigInfo() {
        return this.dbDetails;
    }

    public void setDbConfigInfo(ConfluenceDatabaseDetails dbDetails) {
        this.dbDetails = dbDetails;
    }

    protected boolean checkDriver(DatabaseDetails dbDetails) {
        try {
            Class.forName(dbDetails.getDriverClassName());
            return true;
        }
        catch (ClassNotFoundException e) {
            this.addActionError(this.getText("driver.class.not.found"));
            return false;
        }
    }

    protected boolean checkDatabaseURL(DatabaseDetails dbDetails) {
        if ("other".equals(this.getDatabase())) {
            return true;
        }
        ArrayList<String> dbURLPrefixes = new ArrayList<String>();
        dbURLPrefixes.add("jdbc:jtds:sqlserver://");
        dbURLPrefixes.add("jdbc:sqlserver://");
        dbURLPrefixes.add("jdbc:mysql://");
        dbURLPrefixes.add("jdbc:postgresql://");
        dbURLPrefixes.add("jdbc:oracle:thin:");
        for (String prefix : dbURLPrefixes) {
            if (!dbDetails.getDatabaseUrl().startsWith(prefix)) continue;
            return true;
        }
        this.addActionError(this.getText("database.url.invalid.setup.message", new String[]{dbDetails.getDatabaseUrl()}));
        return false;
    }

    protected List<String> findDatasourceNames() {
        return Collections.emptyList();
    }

    protected DatabaseTestResult testConnection(String databaseType, Connection connection) {
        try {
            this.databaseVerifier.verifyDatabase(databaseType, connection);
        }
        catch (DatabaseVerifyException e) {
            log.warn("Failed when verifying the database connection, the error message is : " + e.getMessage(), (Throwable)e);
            return new DatabaseTestResult(false, this.getText(e.getTitleKey()), this.getText(e.getKey(), e.getParameters()));
        }
        catch (SQLException e) {
            log.warn("Failed when testing the database connection, the error message is : " + e.getMessage(), (Throwable)e);
            SQLException cause = this.returnRootCauseIfExist(e);
            return new DatabaseTestResult(false, this.getText(DatabaseErrorMessageConverter.getMessageKey(databaseType, cause)), cause);
        }
        return new DatabaseTestResult(true, this.getText("setup.database.test.connection.success"));
    }

    protected DatabaseTestResult convertBootstrapException(String databaseType, BootstrapException e) {
        if (e.getCause() instanceof SQLException) {
            SQLException sqlException = (SQLException)e.getCause();
            return new DatabaseTestResult(false, this.getText(DatabaseErrorMessageConverter.getMessageKey(databaseType, sqlException)), sqlException);
        }
        return new DatabaseTestResult(false, this.getText("setup.database.test.connection.failed.generic"), e);
    }

    private SQLException returnRootCauseIfExist(SQLException e) {
        if (e.getSQLState() == null && e.getCause() instanceof SQLException) {
            return (SQLException)e.getCause();
        }
        return e;
    }
}

