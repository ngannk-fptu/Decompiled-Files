/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.config.db.HibernateConfig
 *  com.atlassian.confluence.upgrade.AbstractUpgradeTask
 *  com.atlassian.confluence.upgrade.DatabaseUpgradeTask
 */
package com.atlassian.confluence.upgrade.upgradetask;

import com.atlassian.config.db.HibernateConfig;
import com.atlassian.confluence.pages.persistence.dao.DbEscapeHelper;
import com.atlassian.confluence.upgrade.AbstractUpgradeTask;
import com.atlassian.confluence.upgrade.DatabaseUpgradeTask;
import com.atlassian.confluence.upgrade.ddl.CreateIndexCommand;
import com.atlassian.confluence.upgrade.ddl.DdlExecutor;
import java.util.ArrayList;

public class SynchronyEvictionEventsPostSchemaUpgradeTask
extends AbstractUpgradeTask
implements DatabaseUpgradeTask {
    private static final String BUILD_NUMBER = "8202";
    private final DdlExecutor ddlExecutor;
    private final HibernateConfig config;
    private final DbEscapeHelper escapeHelper;

    public SynchronyEvictionEventsPostSchemaUpgradeTask(DdlExecutor ddlExecutor, HibernateConfig config, DbEscapeHelper escapeHelper) {
        this.ddlExecutor = ddlExecutor;
        this.config = config;
        this.escapeHelper = escapeHelper;
    }

    public boolean runOnSpaceImport() {
        return false;
    }

    public boolean breaksBackwardCompatibility() {
        return false;
    }

    public String getBuildNumber() {
        return BUILD_NUMBER;
    }

    public void doUpgrade() throws Exception {
        ArrayList<CreateIndexCommand> commands = new ArrayList<CreateIndexCommand>();
        if (!this.config.isOracle()) {
            commands.add(this.ddlExecutor.createCreateIndexCommand("e_h_r_idx", this.escapeHelper.escapeIdentifier("EVENTS"), true, this.escapeHelper.escapeIdentifier("history"), this.escapeHelper.escapeIdentifier("rev")));
        }
        commands.add(this.ddlExecutor.createCreateIndexCommand("e_h_p_s_idx", this.escapeHelper.escapeIdentifier("EVENTS"), true, this.escapeHelper.escapeIdentifier("history"), this.escapeHelper.escapeIdentifier("partition"), this.escapeHelper.escapeIdentifier("sequence")));
        commands.add(this.ddlExecutor.createCreateIndexCommand("e_c_i_idx", this.escapeHelper.escapeIdentifier("EVENTS"), false, this.escapeHelper.escapeIdentifier("contentid"), this.escapeHelper.escapeIdentifier("inserted")));
        commands.add(this.ddlExecutor.createCreateIndexCommand("e_i_c_idx", this.escapeHelper.escapeIdentifier("EVENTS"), false, this.escapeHelper.escapeIdentifier("inserted"), this.escapeHelper.escapeIdentifier("contentid")));
        this.ddlExecutor.executeDdl(commands);
    }
}

