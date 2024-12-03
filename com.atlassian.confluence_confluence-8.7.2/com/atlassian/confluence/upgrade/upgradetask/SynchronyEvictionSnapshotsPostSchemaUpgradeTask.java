/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.upgrade.AbstractUpgradeTask
 *  com.atlassian.confluence.upgrade.DatabaseUpgradeTask
 */
package com.atlassian.confluence.upgrade.upgradetask;

import com.atlassian.confluence.pages.persistence.dao.DbEscapeHelper;
import com.atlassian.confluence.upgrade.AbstractUpgradeTask;
import com.atlassian.confluence.upgrade.DatabaseUpgradeTask;
import com.atlassian.confluence.upgrade.ddl.CreateIndexCommand;
import com.atlassian.confluence.upgrade.ddl.DdlExecutor;
import java.util.ArrayList;

public class SynchronyEvictionSnapshotsPostSchemaUpgradeTask
extends AbstractUpgradeTask
implements DatabaseUpgradeTask {
    private static final String BUILD_NUMBER = "8202";
    private final DdlExecutor ddlExecutor;
    private final DbEscapeHelper escapeHelper;

    public SynchronyEvictionSnapshotsPostSchemaUpgradeTask(DdlExecutor ddlExecutor, DbEscapeHelper escapeHelper) {
        this.ddlExecutor = ddlExecutor;
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
        ArrayList<CreateIndexCommand> indexCreationCommands = new ArrayList<CreateIndexCommand>();
        indexCreationCommands.add(this.ddlExecutor.createCreateIndexCommand("s_c_i_idx", this.escapeHelper.escapeIdentifier("SNAPSHOTS"), false, this.escapeHelper.escapeIdentifier("contentid"), this.escapeHelper.escapeIdentifier("inserted")));
        indexCreationCommands.add(this.ddlExecutor.createCreateIndexCommand("s_i_c_idx", this.escapeHelper.escapeIdentifier("SNAPSHOTS"), false, this.escapeHelper.escapeIdentifier("inserted"), this.escapeHelper.escapeIdentifier("contentid")));
        this.ddlExecutor.executeDdl(indexCreationCommands);
    }
}

