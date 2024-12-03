/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.upgrade.AbstractUpgradeTask
 *  com.atlassian.confluence.upgrade.DatabaseUpgradeTask
 *  org.springframework.dao.DataAccessException
 */
package com.atlassian.confluence.upgrade.upgradetask;

import com.atlassian.confluence.upgrade.AbstractUpgradeTask;
import com.atlassian.confluence.upgrade.DatabaseUpgradeTask;
import com.atlassian.confluence.upgrade.ddl.CreateIndexCommand;
import com.atlassian.confluence.upgrade.ddl.DdlExecutor;
import com.atlassian.confluence.upgrade.ddl.DropIndexCommand;
import java.util.Arrays;
import org.springframework.dao.DataAccessException;

public class DropAndUpdateContentIndexesUpgradeTask
extends AbstractUpgradeTask
implements DatabaseUpgradeTask {
    private DdlExecutor ddlExecutor;

    public DropAndUpdateContentIndexesUpgradeTask(DdlExecutor ddlExecutor) {
        this.ddlExecutor = ddlExecutor;
    }

    public String getBuildNumber() {
        return "7105";
    }

    public String getShortDescription() {
        return "Drop or update some low performance indexes on CONTENT";
    }

    public boolean runOnSpaceImport() {
        return false;
    }

    public boolean breaksBackwardCompatibility() {
        return false;
    }

    public void doUpgrade() throws Exception {
        log.info("Update indexes for content table");
        this.executeDropIndexSilently(this.ddlExecutor.createDropIndexCommand("c_status_idx", "CONTENT"), this.ddlExecutor.createDropIndexCommand("c_contenttype_idx", "CONTENT"), this.ddlExecutor.createDropIndexCommand("c_si_ct_pv_cs_cd_idx", "CONTENT"));
        CreateIndexCommand createCmd = this.ddlExecutor.createCreateIndexCommand("c_si_ct_pv_cs_cd_idx", "CONTENT", "CREATIONDATE", "PREVVER", "SPACEID", "CONTENTTYPE", "CONTENT_STATUS");
        this.ddlExecutor.executeDdl(Arrays.asList(createCmd));
    }

    private void executeDropIndexSilently(DropIndexCommand ... commands) {
        for (DropIndexCommand cmd : commands) {
            try {
                this.ddlExecutor.executeDdl(Arrays.asList(cmd));
            }
            catch (DataAccessException e) {
                log.info("Index does not exist, probably dropped by user", (Object)e.getMessage());
            }
        }
    }
}

