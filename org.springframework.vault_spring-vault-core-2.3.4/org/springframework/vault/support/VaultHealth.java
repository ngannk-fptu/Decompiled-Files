/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
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

