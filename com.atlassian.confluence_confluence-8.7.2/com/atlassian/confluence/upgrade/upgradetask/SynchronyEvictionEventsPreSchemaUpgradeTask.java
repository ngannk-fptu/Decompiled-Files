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
import com.atlassian.confluence.upgrade.AbstractUpgradeTask;
import com.atlassian.confluence.upgrade.DatabaseUpgradeTask;
import com.atlassian.confluence.upgrade.ddl.DdlExecutor;
import com.atlassian.confluence.upgrade.ddl.DropTableIfExistsCommand;
import java.util.Collections;

public class SynchronyEvictionEventsPreSchemaUpgradeTask
extends AbstractUpgradeTask
implements DatabaseUpgradeTask {
    private static final String BUILD_NUMBER = "8202";
    private final DdlExecutor ddlExecutor;
    private final HibernateConfig config;

    public SynchronyEvictionEventsPreSchemaUpgradeTask(DdlExecutor ddlExecutor, HibernateConfig config) {
        this.ddlExecutor = ddlExecutor;
        this.config = config;
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
        DropTableIfExistsCommand dropEventsTable = DropTableIfExistsCommand.withEscapingTableName(this.config, "EVENTS");
        this.ddlExecutor.executeDdl(Collections.singletonList(dropEventsTable));
    }
}

