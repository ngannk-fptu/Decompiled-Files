/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.upgrade.AbstractDeferredRunUpgradeTask
 *  com.atlassian.confluence.upgrade.DatabaseUpgradeTask
 */
package com.atlassian.confluence.upgrade.upgradetask;

import com.atlassian.confluence.upgrade.AbstractDeferredRunUpgradeTask;
import com.atlassian.confluence.upgrade.DatabaseUpgradeTask;
import com.atlassian.confluence.user.UserVerificationTokenManager;

public class BandanaTokenCleanupUpgradeTask
extends AbstractDeferredRunUpgradeTask
implements DatabaseUpgradeTask {
    private final UserVerificationTokenManager userVerificationTokenManager;

    public BandanaTokenCleanupUpgradeTask(UserVerificationTokenManager userVerificationTokenManager) {
        this.userVerificationTokenManager = userVerificationTokenManager;
    }

    public String getBuildNumber() {
        return "9004";
    }

    public boolean runOnSpaceImport() {
        return false;
    }

    public boolean breaksBackwardCompatibility() {
        return false;
    }

    public void doDeferredUpgrade() throws Exception {
        int count = this.userVerificationTokenManager.clearAllExpiredTokens();
        log.info("{} expired UserVerificationToken records cleaned up", (Object)count);
    }
}

