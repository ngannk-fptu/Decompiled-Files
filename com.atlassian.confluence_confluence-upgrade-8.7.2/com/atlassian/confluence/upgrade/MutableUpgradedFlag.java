/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.upgrade;

import com.atlassian.confluence.upgrade.UpgradedFlag;
import java.util.concurrent.atomic.AtomicBoolean;

final class MutableUpgradedFlag
implements UpgradedFlag {
    private final AtomicBoolean upgraded = new AtomicBoolean(true);

    MutableUpgradedFlag() {
    }

    @Override
    public boolean isUpgraded() {
        return this.upgraded.get();
    }

    void setUpgraded(boolean upgraded) {
        this.upgraded.set(upgraded);
    }
}

