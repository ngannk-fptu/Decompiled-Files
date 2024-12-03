/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.upgrade.AbstractUpgradeTask
 *  com.atlassian.confluence.upgrade.DatabaseUpgradeTask
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.upgrade.upgradetask;

import com.atlassian.confluence.upgrade.AbstractUpgradeTask;
import com.atlassian.confluence.upgrade.DatabaseUpgradeTask;
import com.atlassian.confluence.upgrade.ddl.DdlExecutor;
import java.util.Collections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SynchronyEvictionPropertiesCleanupUpgradeTask
extends AbstractUpgradeTask
implements DatabaseUpgradeTask {
    private static final String BUILD_NUMBER = "8202";
    private static final String DELETE_SQL_QUERY = "DELETE FROM CONTENTPROPERTIES WHERE PROPERTYNAME = 'sync-rev'";
    private static final String UPDATE_SQL_QUERY = "UPDATE CONTENTPROPERTIES SET STRINGVAL = 'restored' WHERE PROPERTYNAME = 'sync-rev-source'";
    private static final Logger logger = LoggerFactory.getLogger(SynchronyEvictionPropertiesCleanupUpgradeTask.class);
    private final DdlExecutor ddlExecutor;

    public SynchronyEvictionPropertiesCleanupUpgradeTask(DdlExecutor ddlExecutor) {
        this.ddlExecutor = ddlExecutor;
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
        logger.info("Starting deletion of sync-rev and sync-rev-properties. This is expected to take some time on large databases (tens of minutes).");
        this.ddlExecutor.executeDdlStatements(Collections.singletonList(UPDATE_SQL_QUERY));
        this.ddlExecutor.executeDdlStatements(Collections.singletonList(DELETE_SQL_QUERY));
        logger.info("Finished deletion of synchrony related system properties.");
    }
}

