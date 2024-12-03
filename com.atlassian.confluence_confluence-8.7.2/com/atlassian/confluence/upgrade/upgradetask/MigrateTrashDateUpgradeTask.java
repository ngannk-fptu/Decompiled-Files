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

import com.atlassian.confluence.internal.pages.TrashManagerInternal;
import com.atlassian.confluence.upgrade.AbstractUpgradeTask;
import com.atlassian.confluence.upgrade.DatabaseUpgradeTask;
import java.time.Instant;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MigrateTrashDateUpgradeTask
extends AbstractUpgradeTask
implements DatabaseUpgradeTask {
    private static final Logger log = LoggerFactory.getLogger(MigrateTrashDateUpgradeTask.class);
    private final TrashManagerInternal trashManagerInternal;

    public MigrateTrashDateUpgradeTask(TrashManagerInternal trashManagerInternal) {
        this.trashManagerInternal = Objects.requireNonNull(trashManagerInternal);
    }

    public String getBuildNumber() {
        return "8802";
    }

    public String getShortDescription() {
        return "Set trash date for existing items in the trash";
    }

    public boolean runOnSpaceImport() {
        return false;
    }

    public boolean breaksBackwardCompatibility() {
        return false;
    }

    public void doUpgrade() throws Exception {
        log.info("Setting trash date for existing trash");
        this.trashManagerInternal.migrateTrashDate(Instant.now());
    }
}

