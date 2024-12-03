/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.upgrade.AbstractUpgradeTask
 */
package com.atlassian.confluence.upgrade.upgradetask;

import com.atlassian.confluence.upgrade.AbstractUpgradeTask;

public class AuditLoggingSchemaUpgradeTask
extends AbstractUpgradeTask {
    public boolean runOnSpaceImport() {
        return false;
    }

    public boolean breaksBackwardCompatibility() {
        return true;
    }

    public void doUpgrade() throws Exception {
    }

    public String getBuildNumber() {
        return "6427";
    }
}

