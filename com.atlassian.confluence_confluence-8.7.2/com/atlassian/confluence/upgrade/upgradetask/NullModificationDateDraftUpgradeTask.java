/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.upgrade.AbstractUpgradeTask
 *  com.atlassian.confluence.upgrade.DatabaseUpgradeTask
 *  com.google.common.annotations.VisibleForTesting
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.upgrade.upgradetask;

import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.upgrade.AbstractUpgradeTask;
import com.atlassian.confluence.upgrade.DatabaseUpgradeTask;
import com.google.common.annotations.VisibleForTesting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NullModificationDateDraftUpgradeTask
extends AbstractUpgradeTask
implements DatabaseUpgradeTask {
    private Logger LOG = LoggerFactory.getLogger(NullModificationDateDraftUpgradeTask.class);
    private PageManager pageManager;
    private int totalStaleDraftRemoved;

    public NullModificationDateDraftUpgradeTask(PageManager pageManager) {
        this.pageManager = pageManager;
    }

    @VisibleForTesting
    public int getTotalStaleDraftRemoved() {
        return this.totalStaleDraftRemoved;
    }

    public String getBuildNumber() {
        return "8501";
    }

    public boolean runOnSpaceImport() {
        return false;
    }

    public boolean breaksBackwardCompatibility() {
        return false;
    }

    public void doUpgrade() throws Exception {
        this.totalStaleDraftRemoved = this.pageManager.removeStaleSharedDrafts();
    }
}

