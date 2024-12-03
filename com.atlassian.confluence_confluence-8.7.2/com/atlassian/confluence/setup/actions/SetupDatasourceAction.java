/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.config.ConfigurationException
 *  com.atlassian.config.db.DatabaseDetails
 *  com.atlassian.sal.api.websudo.WebSudoRequired
 *  com.atlassian.xwork.HttpMethod
 *  com.atlassian.xwork.PermittedMethods
 *  com.atlassian.xwork.RequireSecurityToken
 *  org.hibernate.dialect.Dialect
 *  org.hibernate.engine.jdbc.dialect.internal.StandardDialectResolver
 *  org.hibernate.engine.jdbc.dialect.spi.DatabaseMetaDataDialectResolutionInfoAdapter
 *  org.hibernate.engine.jdbc.dialect.spi.DialectResolutionInfo
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.setup.actions;

import com.atlassian.config.ConfigurationException;
import com.atlassian.config.db.DatabaseDetails;
import com.atlassian.confluence.impl.security.SystemAdminOnly;
import com.atlassian.confluence.setup.ConfluenceDatabaseDetails;
import com.atlassian.confluence.setup.ConfluenceDatabaseDetailsBuilder;
import com.atlassian.confluence.setup.actions.AbstractDatabaseCreationAction;
import com.atlassian.confluence.util.HtmlUtil;
import com.atlassian.sal.api.websudo.WebSudoRequired;
import com.atlassian.xwork.HttpMethod;
import com.atlassian.xwork.PermittedMethods;
import com.atlassian.xwork.RequireSecurityToken;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.jdbc.dialect.internal.StandardDialectResolver;
import org.hibernate.engine.jdbc.dialect.spi.DatabaseMetaDataDialectResolutionInfoAdapter;
import org.hibernate.engine.jdbc.dialect.spi.DialectResolutionInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebSudoRequired
@SystemAdminOnly
public class SetupDatasourceAction
extends AbstractDatabaseCreationAction {
    private static final Logger log = LoggerFactory.getLogger(SetupDatasourceAction.class);
    private String datasourceName = "";
    private boolean multipleDatasources;
    private String dialect;
    private boolean forceOverwriteExistingData = false;

    public String getDatasourceName() {
        return this.datasourceName;
    }

    public void setDatasourceName(String datasourceName) {
        this.datasourceName = datasourceName;
    }

    public boolean isMultipleDatasources() {
        return this.multipleDatasources;
    }

    public String getDialect() {
        return this.dialect;
    }

    public void setDialect(String dialect) {
        this.dialect = dialect;
    }

    @Override
    public String doDefault() throws Exception {
        this.detectDatasource();
        this.dbDetails = new ConfluenceDatabaseDetailsBuilder().databaseType(this.getDatabase()).build();
        this.datasourceName = "java:comp/env/jdbc/" + this.datasourceName;
        return "input";
    }

    @Override
    @PermittedMethods(value={HttpMethod.POST})
    @RequireSecurityToken(value=true)
    public String execute() throws ConfigurationException {
        return super.execute();
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Override
    String setupDatabase() throws ConfigurationException {
        try (Connection connection = this.bootstrapConfigurer().getTestDatasourceConnection(this.datasourceName);){
            if (!this.forceOverwriteExistingData && this.getBootstrapStatusProvider().databaseContainsExistingData(connection)) {
                this.addActionError(this.getText("setup.database.tables.exist"));
                String string = "data-exists";
                return string;
            }
            this.dbDetails = this.setupDBDetails(connection);
        }
        catch (Exception e) {
            this.addActionError(this.getText("could.not.lookup.datasource") + ": " + HtmlUtil.htmlEncode(String.valueOf(e)));
            log.warn("Unable to look up datasource: " + e.getMessage(), (Throwable)e);
            return "error";
        }
        try {
            this.getBootstrapManager().bootstrapDatasource(this.datasourceName, this.dbDetails.getDialect());
            return "success";
        }
        catch (Exception e) {
            this.addActionError(this.getText("configuring.db.failed"));
            this.addActionError(e.getMessage());
            log.error("Unable to bootstrap datasource: " + e.getMessage(), (Throwable)e);
            return "error";
        }
    }

    public void setForceOverwriteExistingData(boolean forceOverwriteExistingData) {
        this.forceOverwriteExistingData = forceOverwriteExistingData;
    }

    private ConfluenceDatabaseDetails setupDBDetails(Connection connection) throws SQLException, ConfigurationException {
        String dbType = this.detectDatabaseType(connection);
        ConfluenceDatabaseDetails dbDetails = new ConfluenceDatabaseDetailsBuilder().databaseType(dbType).build(DatabaseDetails.getDefaults((String)dbType));
        dbDetails.setupForDatabase(dbType);
        if (dbDetails.getDialect() == null) {
            StandardDialectResolver resolver = new StandardDialectResolver();
            Dialect dialect = resolver.resolveDialect((DialectResolutionInfo)new DatabaseMetaDataDialectResolutionInfoAdapter(connection.getMetaData()));
            dbDetails.setDialect(dialect.getClass().getName());
        }
        return dbDetails;
    }

    protected void detectDatasource() {
        List<String> datasources = this.findDatasourceNames();
        if (datasources.size() == 1) {
            this.datasourceName = datasources.get(0);
        } else if (datasources.size() > 1) {
            this.multipleDatasources = true;
        }
    }
}

