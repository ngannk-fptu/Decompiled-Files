/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.upgrade.AbstractUpgradeTask
 *  com.atlassian.confluence.upgrade.DatabaseUpgradeTask
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.dao.DataAccessException
 */
package com.atlassian.confluence.upgrade.upgradetask;

import com.atlassian.confluence.upgrade.AbstractUpgradeTask;
import com.atlassian.confluence.upgrade.DatabaseUpgradeTask;
import com.atlassian.confluence.upgrade.ddl.CreateIndexCommand;
import com.atlassian.confluence.upgrade.ddl.DdlExecutor;
import com.atlassian.confluence.upgrade.ddl.DropIndexCommand;
import com.atlassian.confluence.upgrade.upgradetask.AddMissingUnmanagedUniqueConstraintsUpgradeTask;
import java.util.Collections;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;

public class AddMissingOsPropertyIndexUpgradeTask
extends AbstractUpgradeTask
implements DatabaseUpgradeTask {
    private static final Logger log = LoggerFactory.getLogger(AddMissingUnmanagedUniqueConstraintsUpgradeTask.class);
    private static final String BUILD_NUMBER = "8301";
    private final DdlExecutor ddlExecutor;

    public AddMissingOsPropertyIndexUpgradeTask(DdlExecutor ddlExecutor) {
        this.ddlExecutor = Objects.requireNonNull(ddlExecutor);
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

    public String getShortDescription() {
        return "Create index ospe_entityid_idx on table OS_PROPERTYENTRY";
    }

    public void doUpgrade() throws Exception {
        DropIndexCommand dropIndexCommand = this.ddlExecutor.createDropIndexCommand("ospe_entityid_idx", "OS_PROPERTYENTRY");
        try {
            log.info("Dropping index [ospe_entityid_idx] from table [OS_PROPERTYENTRY]");
            this.ddlExecutor.executeDdl(Collections.singletonList(dropIndexCommand));
            log.info("Index dropped");
        }
        catch (DataAccessException dae) {
            log.debug("Error deleting index ospe_entityid_idx from table OS_PROPERTYENTRY", (Throwable)dae);
        }
        CreateIndexCommand createIndexCommand = this.ddlExecutor.createCreateIndexCommand("ospe_entityid_idx", "OS_PROPERTYENTRY", "entity_id");
        try {
            log.info("Creating index [ospe_entityid_idx] on table [OS_PROPERTYENTRY]");
            this.ddlExecutor.executeDdl(Collections.singletonList(createIndexCommand));
            log.info("Index created");
        }
        catch (DataAccessException dae) {
            log.warn("Cannot add index ospe_entityid_idx to table OS_PROPERTYENTRY", (Throwable)dae);
        }
    }
}

