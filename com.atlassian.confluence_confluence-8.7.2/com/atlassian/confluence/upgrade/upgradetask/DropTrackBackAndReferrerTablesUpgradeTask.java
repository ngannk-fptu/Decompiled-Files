/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.upgrade.AbstractUpgradeTask
 *  com.atlassian.confluence.upgrade.DatabaseUpgradeTask
 *  com.atlassian.fugue.Pair
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.dao.DataAccessException
 */
package com.atlassian.confluence.upgrade.upgradetask;

import com.atlassian.confluence.upgrade.AbstractUpgradeTask;
import com.atlassian.confluence.upgrade.DatabaseUpgradeTask;
import com.atlassian.confluence.upgrade.ddl.DdlExecutor;
import com.atlassian.confluence.upgrade.ddl.DropTableCommand;
import com.atlassian.fugue.Pair;
import java.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;

public class DropTrackBackAndReferrerTablesUpgradeTask
extends AbstractUpgradeTask
implements DatabaseUpgradeTask {
    private static final Logger log = LoggerFactory.getLogger(DropTrackBackAndReferrerTablesUpgradeTask.class);
    public static final String TRACKBACK_TABLE_NAME = "TRACKBACKLINKS";
    public static final String REFFERAL_TABLE_NAME = "EXTRNLNKS";
    private final DdlExecutor ddlExecutor;

    public DropTrackBackAndReferrerTablesUpgradeTask(DdlExecutor ddlExecutor) {
        this.ddlExecutor = ddlExecutor;
    }

    public String getBuildNumber() {
        return "8201";
    }

    public String getShortDescription() {
        return "Drop unused table TRACKBACKLINKS, and table EXTRNLNKS";
    }

    public boolean runOnSpaceImport() {
        return false;
    }

    public boolean breaksBackwardCompatibility() {
        return false;
    }

    public void doUpgrade() throws Exception {
        log.info("Starting dropping unused table TRACKBACKLINKS, and table EXTRNLNKS");
        Arrays.asList(TRACKBACK_TABLE_NAME, REFFERAL_TABLE_NAME).stream().map(tableNameToBeDrop -> new Pair(tableNameToBeDrop, (Object)this.ddlExecutor.createDropTableCommand((String)tableNameToBeDrop))).forEach(commandPair -> {
            try {
                this.ddlExecutor.executeDdl(Arrays.asList((DropTableCommand)commandPair.right()));
            }
            catch (DataAccessException e) {
                log.warn("Cannot drop table [{}] because of: {}", commandPair.left(), (Object)e.getMessage());
            }
        });
        log.info("Finished dropping unused table TRACKBACKLINKS, and table EXTRNLNKS");
    }
}

