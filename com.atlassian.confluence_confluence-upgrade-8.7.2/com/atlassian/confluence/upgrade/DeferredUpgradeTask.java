/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.upgrade;

import com.atlassian.confluence.upgrade.UpgradeTask;

public interface DeferredUpgradeTask
extends UpgradeTask {
    public boolean isUpgradeRequired();

    public void setUpgradeRequired(boolean var1);

    public void doDeferredUpgrade() throws Exception;
}

