/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.upgrade.AbstractUpgradeTask
 */
package com.atlassian.confluence.internal.index.opensearch;

import com.atlassian.confluence.upgrade.AbstractUpgradeTask;

public class NoopSplitIndexUpgradeTask
extends AbstractUpgradeTask {
    public String getBuildNumber() {
        return "8503";
    }

    public boolean runOnSpaceImport() {
        return false;
    }

    public boolean breaksBackwardCompatibility() {
        return false;
    }

    public void doUpgrade() throws Exception {
    }
}

