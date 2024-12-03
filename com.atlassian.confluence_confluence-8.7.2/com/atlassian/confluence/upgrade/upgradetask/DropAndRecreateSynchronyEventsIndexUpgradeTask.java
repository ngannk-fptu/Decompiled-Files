/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.upgrade.AbstractUpgradeTask
 *  com.atlassian.confluence.upgrade.DatabaseUpgradeTask
 *  com.google.common.collect.Lists
 *  org.apache.commons.lang3.StringUtils
 *  org.springframework.dao.DataAccessException
 */
package com.atlassian.confluence.upgrade.upgradetask;

import com.atlassian.confluence.core.persistence.hibernate.HibernateDatabaseCapabilities;
import com.atlassian.confluence.upgrade.AbstractUpgradeTask;
import com.atlassian.confluence.upgrade.DatabaseUpgradeTask;
import com.atlassian.confluence.upgrade.ddl.CreateIndexCommand;
import com.atlassian.confluence.upgrade.ddl.DdlExecutor;
import com.atlassian.confluence.upgrade.ddl.DropIndexCommand;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import org.apache.commons.lang3.StringUtils;
import org.springframework.dao.DataAccessException;

public class DropAndRecreateSynchronyEventsIndexUpgradeTask
extends AbstractUpgradeTask
implements DatabaseUpgradeTask {
    private final DdlExecutor ddlExecutor;
    private final HibernateDatabaseCapabilities databaseCapabilities;

    public DropAndRecreateSynchronyEventsIndexUpgradeTask(DdlExecutor ddlExecutor, HibernateDatabaseCapabilities databaseCapabilities) {
        this.ddlExecutor = ddlExecutor;
        this.databaseCapabilities = databaseCapabilities;
    }

    public String getBuildNumber() {
        return "7109";
    }

    public boolean runOnSpaceImport() {
        return false;
    }

    public boolean breaksBackwardCompatibility() {
        return false;
    }

    public String getShortDescription() {
        return "Drop and update some low performance indexes on EVENTS table for Synchrony";
    }

    public void doUpgrade() throws Exception {
        this.executeDropIndexSilently(this.ddlExecutor.createDropIndexCommand("e_r_h_idx", DropAndRecreateSynchronyEventsIndexUpgradeTask.quote(this.databaseCapabilities, "EVENTS")), this.ddlExecutor.createDropIndexCommand("e_h_r_idx", DropAndRecreateSynchronyEventsIndexUpgradeTask.quote(this.databaseCapabilities, "EVENTS")), this.ddlExecutor.createDropIndexCommand("e_h_p_s_idx", DropAndRecreateSynchronyEventsIndexUpgradeTask.quote(this.databaseCapabilities, "EVENTS")));
        log.info("Recreating indexes on the EVENTS table. This may take a few minutes if the table is large...");
        ArrayList<CreateIndexCommand> recreateIndexes = new ArrayList<CreateIndexCommand>();
        if (!this.databaseCapabilities.isOracle()) {
            recreateIndexes.add(this.ddlExecutor.createCreateIndexCommand("e_h_r_idx", DropAndRecreateSynchronyEventsIndexUpgradeTask.quote(this.databaseCapabilities, "EVENTS"), true, DropAndRecreateSynchronyEventsIndexUpgradeTask.quote(this.databaseCapabilities, "history"), DropAndRecreateSynchronyEventsIndexUpgradeTask.quote(this.databaseCapabilities, "rev")));
        }
        recreateIndexes.add(this.ddlExecutor.createCreateIndexCommand("e_h_p_s_idx", DropAndRecreateSynchronyEventsIndexUpgradeTask.quote(this.databaseCapabilities, "EVENTS"), true, DropAndRecreateSynchronyEventsIndexUpgradeTask.quote(this.databaseCapabilities, "history"), DropAndRecreateSynchronyEventsIndexUpgradeTask.quote(this.databaseCapabilities, "partition"), DropAndRecreateSynchronyEventsIndexUpgradeTask.quote(this.databaseCapabilities, "sequence")));
        this.ddlExecutor.executeDdl(recreateIndexes);
    }

    private static String quote(HibernateDatabaseCapabilities hibernateConfig, String name) {
        if (hibernateConfig.isMySql()) {
            return StringUtils.wrap((String)name, (String)"`");
        }
        return StringUtils.wrap((String)name, (String)"\"");
    }

    private void executeDropIndexSilently(DropIndexCommand ... commands) {
        for (DropIndexCommand cmd : commands) {
            try {
                this.ddlExecutor.executeDdl(Lists.newArrayList((Object[])new DropIndexCommand[]{cmd}));
            }
            catch (DataAccessException e) {
                log.info("Safely ignoring [{}]. : {}", (Object)cmd.getStatement(), (Object)e.getMessage());
            }
        }
    }
}

