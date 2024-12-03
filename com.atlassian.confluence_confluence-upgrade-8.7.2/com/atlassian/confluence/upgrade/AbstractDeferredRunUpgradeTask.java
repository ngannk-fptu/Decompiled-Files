/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.upgrade;

import com.atlassian.confluence.upgrade.AbstractUpgradeTask;
import com.atlassian.confluence.upgrade.DeferredUpgradeTask;

public abstract class AbstractDeferredRunUpgradeTask
extends AbstractUpgradeTask
implements DeferredUpgradeTask {
    private volatile boolean upgradeRequired = false;

    @Override
    public boolean isUpgradeRequired() {
        return this.upgradeRequired;
    }

    @Override
    public void setUpgradeRequired(boolean required) {
        this.upgradeRequired = required;
    }

    @Override
    public void doUpgrade() throws Exception {
        this.setUpgradeRequired(true);
    }
}

