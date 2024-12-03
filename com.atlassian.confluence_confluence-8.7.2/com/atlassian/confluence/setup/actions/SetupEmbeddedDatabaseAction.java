/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.config.ConfigurationException
 *  com.atlassian.config.bootstrap.BootstrapException
 *  com.atlassian.config.db.DatabaseDetails
 *  com.atlassian.sal.api.websudo.WebSudoRequired
 *  com.atlassian.xwork.HttpMethod
 *  com.atlassian.xwork.PermittedMethods
 *  com.atlassian.xwork.RequireSecurityToken
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.setup.actions;

import com.atlassian.config.ConfigurationException;
import com.atlassian.config.bootstrap.BootstrapException;
import com.atlassian.config.db.DatabaseDetails;
import com.atlassian.confluence.impl.security.SystemAdminOnly;
import com.atlassian.confluence.setup.ConfluenceDatabaseDetailsBuilder;
import com.atlassian.confluence.setup.actions.AbstractDatabaseCreationAction;
import com.atlassian.confluence.util.HtmlUtil;
import com.atlassian.sal.api.websudo.WebSudoRequired;
import com.atlassian.xwork.HttpMethod;
import com.atlassian.xwork.PermittedMethods;
import com.atlassian.xwork.RequireSecurityToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebSudoRequired
@SystemAdminOnly
public class SetupEmbeddedDatabaseAction
extends AbstractDatabaseCreationAction {
    private static final Logger log = LoggerFactory.getLogger(SetupEmbeddedDatabaseAction.class);
    public static final String DEFAULT_EVAL_DB = "h2";

    @Override
    @PermittedMethods(value={HttpMethod.GET, HttpMethod.POST})
    @RequireSecurityToken(value=true)
    public String execute() throws ConfigurationException {
        return super.execute();
    }

    @Override
    String setupDatabase() throws ConfigurationException {
        this.setDatabase(DEFAULT_EVAL_DB);
        this.setDbConfigInfo(new ConfluenceDatabaseDetailsBuilder().databaseType(DEFAULT_EVAL_DB).build(DatabaseDetails.getDefaults((String)DEFAULT_EVAL_DB)));
        if ("install".equals(this.getSetupPersister().getSetupType()) || "custom".equals(this.getSetupPersister().getSetupType())) {
            return this.createDatabase();
        }
        throw new IllegalStateException("Unrecognised setup type: " + this.getSetupPersister().getSetupType());
    }

    public String createDatabase() {
        if (!this.checkDriver(this.getDbConfigInfo())) {
            return "error";
        }
        try {
            this.getDbConfigInfo().setupForDatabase(this.getDatabase());
            this.bootstrapConfigurer().bootstrapDatabase(this.getDbConfigInfo(), true);
        }
        catch (BootstrapException e) {
            this.addActionError(this.getText("configuring.db.failed"));
            this.addActionError(HtmlUtil.htmlEncode(e.getMessage()));
            log.error("Unable to bootstrap embedded database", (Throwable)e);
            return "error";
        }
        return "success";
    }
}

