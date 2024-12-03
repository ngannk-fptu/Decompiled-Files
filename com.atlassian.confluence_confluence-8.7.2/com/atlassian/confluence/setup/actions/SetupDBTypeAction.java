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
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.struts2.ServletActionContext
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.setup.actions;

import com.atlassian.config.ConfigurationException;
import com.atlassian.config.db.DatabaseDetails;
import com.atlassian.confluence.impl.security.SystemAdminOnly;
import com.atlassian.confluence.setup.BootstrapManagerInternal;
import com.atlassian.confluence.setup.ConfluenceDatabaseDetails;
import com.atlassian.confluence.setup.ConfluenceDatabaseDetailsBuilder;
import com.atlassian.confluence.setup.DatabaseEnum;
import com.atlassian.confluence.setup.actions.AbstractDatabaseCreationAction;
import com.atlassian.confluence.util.HtmlUtil;
import com.atlassian.sal.api.websudo.WebSudoRequired;
import com.atlassian.xwork.HttpMethod;
import com.atlassian.xwork.PermittedMethods;
import com.atlassian.xwork.RequireSecurityToken;
import java.io.File;
import java.sql.Connection;
import java.util.List;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebSudoRequired
@SystemAdminOnly
public class SetupDBTypeAction
extends AbstractDatabaseCreationAction {
    private static final Logger log = LoggerFactory.getLogger(SetupDBTypeAction.class);
    private String dbChoiceSelect;
    private boolean missingMySQLDriver;
    private boolean missingOracleDriver;
    private String webRealPath;
    private boolean forceOverwriteExistingData = false;
    boolean thisNodeClustered;

    @Override
    public String doDefault() throws Exception {
        this.missingMySQLDriver = !this.isDriverPresent(DatabaseDetails.getDefaults((String)DatabaseEnum.MYSQL.getType()));
        this.missingOracleDriver = !this.isDriverPresent(DatabaseDetails.getDefaults((String)DatabaseEnum.ORACLE.getType()));
        this.webRealPath = new File(this.getCurrentSession().getServletContext().getRealPath("."), "/WEB-INF/lib").getPath();
        this.dbDetails = new ConfluenceDatabaseDetailsBuilder().databaseType(this.getDatabase()).build();
        String configuredDatabase = (String)this.getBootstrapStatusProvider().getProperty("confluence.database.choice");
        if (StringUtils.isNotEmpty((CharSequence)configuredDatabase)) {
            this.setDatabase(configuredDatabase);
            return "skipToNextStep";
        }
        return super.doDefault();
    }

    @Override
    @PermittedMethods(value={HttpMethod.GET, HttpMethod.POST})
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
        this.dbDetails.setupForDatabase(this.dbDetails.getDatabaseType());
        BootstrapManagerInternal bootstrapManager = (BootstrapManagerInternal)this.getBootstrapManager();
        Optional<DatabaseDetails> dbDetailsOptional = bootstrapManager.getDatabaseDetail(this.getDatabase());
        if (dbDetailsOptional.isPresent()) {
            this.dbDetails = (ConfluenceDatabaseDetails)dbDetailsOptional.get();
            bootstrapManager.performPersistenceUpgrade();
            this.dbDetails = (ConfluenceDatabaseDetails)bootstrapManager.getDatabaseDetail(this.getDatabase()).get();
            this.dbDetails.setSimple(false);
        }
        if (!this.checkDriver(this.getDbConfigInfo())) {
            return "error";
        }
        try (Connection connection = this.getBootstrapManager().getTestDatabaseConnection(this.dbDetails);){
            if (!this.forceOverwriteExistingData && this.getBootstrapStatusProvider().databaseContainsExistingData(connection)) {
                this.addActionError(this.getText("setup.database.tables.exist"));
                String string = "data-exists";
                return string;
            }
        }
        catch (Exception e) {
            this.addActionError(this.getText("configuring.db.failed"));
            this.checkDatabaseURL(this.getDbConfigInfo());
            this.addActionError(HtmlUtil.htmlEncode(e.getMessage()));
            log.error("Unable to connect to database: " + e.getMessage(), (Throwable)e);
            return "error";
        }
        try {
            if ("h2".equals(this.getDatabase().toLowerCase()) || "hsql".equals(this.getDatabase().toLowerCase())) {
                this.bootstrapConfigurer().bootstrapDatabase(this.dbDetails, true);
            } else {
                this.bootstrapConfigurer().bootstrapDatabase(this.dbDetails, false);
            }
        }
        catch (Exception e) {
            this.addActionError(this.getText("configuring.db.failed"));
            this.addActionError(HtmlUtil.htmlEncode(e.getMessage()));
            log.error("Unable to bootstrap standard database", (Throwable)e);
            return "error";
        }
        ServletActionContext.getContext().getSession().remove("dbConfig");
        return "success";
    }

    private boolean isDriverPresent(DatabaseDetails databaseDetails) {
        try {
            Class.forName(databaseDetails.getDriverClassName());
            return true;
        }
        catch (ClassNotFoundException e) {
            return false;
        }
    }

    public List getDatabases() {
        return this.dbDetails.getDatabases();
    }

    public boolean isMissingMySQLDriver() {
        return this.missingMySQLDriver;
    }

    public boolean isMissingOracleDriver() {
        return this.missingOracleDriver;
    }

    public String getWebRealPath() {
        return this.webRealPath;
    }

    public String getDbChoiceSelect() {
        return this.dbChoiceSelect;
    }

    public void setDbChoiceSelect(String dbChoiceSelect) {
        this.dbChoiceSelect = dbChoiceSelect;
    }

    public boolean isThisNodeClustered() {
        return this.thisNodeClustered;
    }

    public void setThisNodeClustered(boolean thisNodeClustered) {
        this.thisNodeClustered = thisNodeClustered;
    }

    public void setForceOverwriteExistingData(boolean forceOverwriteExistingData) {
        this.forceOverwriteExistingData = forceOverwriteExistingData;
    }
}

