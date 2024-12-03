/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.config.bootstrap.BootstrapException
 *  com.atlassian.sal.api.websudo.WebSudoRequired
 *  com.atlassian.xwork.HttpMethod
 *  com.atlassian.xwork.PermittedMethods
 *  com.atlassian.xwork.RequireSecurityToken
 *  com.google.gson.Gson
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.setup.actions;

import com.atlassian.config.bootstrap.BootstrapException;
import com.atlassian.confluence.impl.security.SystemAdminOnly;
import com.atlassian.confluence.setup.actions.AbstractSetupDatabaseAction;
import com.atlassian.confluence.setup.settings.DatabaseTestResult;
import com.atlassian.sal.api.websudo.WebSudoRequired;
import com.atlassian.xwork.HttpMethod;
import com.atlassian.xwork.PermittedMethods;
import com.atlassian.xwork.RequireSecurityToken;
import com.google.gson.Gson;
import java.sql.Connection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebSudoRequired
@SystemAdminOnly
public class SetupDatasourceTestConnectionAction
extends AbstractSetupDatabaseAction {
    private static final Logger log = LoggerFactory.getLogger(SetupDatasourceTestConnectionAction.class);
    private String datasourceName = "java:comp/env/jdbc/";
    private DatabaseTestResult testResult;
    private Gson gson = new Gson();

    public String getDatasourceName() {
        return this.datasourceName;
    }

    public void setDatasourceName(String datasourceName) {
        this.datasourceName = datasourceName;
    }

    @PermittedMethods(value={HttpMethod.POST})
    @RequireSecurityToken(value=true)
    public String execute() throws Exception {
        String databaseType = "other";
        try (Connection connection = this.bootstrapConfigurer().getTestDatasourceConnection(this.datasourceName);){
            databaseType = this.detectDatabaseType(connection);
            this.testResult = this.testConnection(databaseType, connection);
        }
        catch (BootstrapException e) {
            log.warn("Failed when testing the datasource connection with error message: " + e.getMessage(), (Throwable)e);
            this.testResult = this.convertBootstrapException(databaseType, e);
        }
        return "json";
    }

    public String getJSONString() {
        return this.gson.toJson((Object)this.testResult);
    }
}

