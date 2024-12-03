/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.websudo.WebSudoRequired
 *  com.atlassian.xwork.HttpMethod
 *  com.atlassian.xwork.PermittedMethods
 *  com.atlassian.xwork.RequireSecurityToken
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.setup.actions;

import com.atlassian.confluence.impl.security.SystemAdminOnly;
import com.atlassian.confluence.setup.actions.AbstractSetupDatabaseAction;
import com.atlassian.confluence.setup.actions.DatabaseList;
import com.atlassian.sal.api.websudo.WebSudoRequired;
import com.atlassian.xwork.HttpMethod;
import com.atlassian.xwork.PermittedMethods;
import com.atlassian.xwork.RequireSecurityToken;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

@WebSudoRequired
@SystemAdminOnly
public class SetupDBChoiceAction
extends AbstractSetupDatabaseAction {
    private static final String DATASOURCE = "datasource";
    private static final String EMBEDDED_DB = "embedded";
    private DatabaseList databases = new DatabaseList();
    private String dbChoice;
    private boolean thisNodeClustered;
    private boolean datasourceDetected;
    boolean isSingleNodeDataCenter;

    @Override
    public String doDefault() throws Exception {
        String configuredDatabase = (String)this.getBootstrapStatusProvider().getProperty("confluence.database.choice");
        if (StringUtils.isNotEmpty((CharSequence)configuredDatabase)) {
            this.setDatabase(configuredDatabase);
            return "skipToNextStep";
        }
        boolean bl = this.datasourceDetected = this.findDatasourceNames().size() > 0;
        if (this.isThisNodeClustered() && !this.datasourceDetected) {
            return "skipToNextStepCluster";
        }
        return super.doDefault();
    }

    @PermittedMethods(value={HttpMethod.POST})
    @RequireSecurityToken(value=true)
    public String execute() throws Exception {
        if (DATASOURCE.equals(this.dbChoice)) {
            return "setupdatasource";
        }
        if (EMBEDDED_DB.equals(this.dbChoice)) {
            return "quick-setup";
        }
        return this.thisNodeClustered ? "setupdbcluster" : "setupdb";
    }

    public List getDatabases() {
        return this.databases.getDatabases();
    }

    public String getDbChoice() {
        return this.dbChoice;
    }

    public void setDbChoice(String dbChoice) {
        this.dbChoice = dbChoice;
    }

    public boolean isThisNodeClustered() {
        Object isClustered = this.getBootstrapStatusProvider().getProperty("confluence.cluster");
        return this.thisNodeClustered || "true".equals(isClustered);
    }

    public void setThisNodeClustered(boolean thisNodeClustered) {
        this.thisNodeClustered = thisNodeClustered;
    }

    public boolean isDatasourceDetected() {
        return this.datasourceDetected;
    }
}

