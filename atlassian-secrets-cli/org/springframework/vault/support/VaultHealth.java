/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.vault.support;

import org.springframework.lang.Nullable;

public interface VaultHealth {
    public boolean isInitialized();

    public boolean isSealed();

    public boolean isStandby();

    public boolean isPerformanceStandby();

    public boolean isRecoveryReplicationSecondary();

    public int getServerTimeUtc();

    @Nullable
    public String getVersion();
}

