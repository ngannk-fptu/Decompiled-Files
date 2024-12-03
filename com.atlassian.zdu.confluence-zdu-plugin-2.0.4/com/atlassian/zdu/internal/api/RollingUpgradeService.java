/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.zdu.internal.api;

import java.util.Optional;

public interface RollingUpgradeService {
    public boolean isUpgradeModeEnabled();

    public Optional<String> getOriginalVersion();

    public void enableUpgradeMode();

    public void disableUpgradeMode();

    public void retryFinalization();
}

