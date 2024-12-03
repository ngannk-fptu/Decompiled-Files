/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.vault.support;

public interface VaultUnsealStatus {
    public int getSecretShares();

    public int getSecretThreshold();

    public boolean isSealed();

    public int getProgress();
}

