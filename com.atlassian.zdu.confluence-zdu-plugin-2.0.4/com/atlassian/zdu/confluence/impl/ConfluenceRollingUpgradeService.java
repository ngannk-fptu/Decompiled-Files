/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.cluster.ZduManager
 *  com.atlassian.confluence.cluster.ZduStatus$State
 */
package com.atlassian.zdu.confluence.impl;

import com.atlassian.confluence.cluster.ZduManager;
import com.atlassian.confluence.cluster.ZduStatus;
import com.atlassian.zdu.internal.api.RollingUpgradeService;
import java.util.Objects;
import java.util.Optional;

public class ConfluenceRollingUpgradeService
implements RollingUpgradeService {
    private final ZduManager zduManager;

    public ConfluenceRollingUpgradeService(ZduManager zduManager) {
        this.zduManager = Objects.requireNonNull(zduManager);
    }

    @Override
    public boolean isUpgradeModeEnabled() {
        return this.zduManager.getUpgradeStatus().getState() == ZduStatus.State.ENABLED;
    }

    @Override
    public Optional<String> getOriginalVersion() {
        return this.zduManager.getUpgradeStatus().getOriginalClusterVersion();
    }

    @Override
    public void enableUpgradeMode() {
        this.zduManager.startUpgrade();
    }

    @Override
    public void disableUpgradeMode() {
        this.zduManager.endUpgrade();
    }

    @Override
    public void retryFinalization() {
        this.zduManager.retryFinalization();
    }
}

