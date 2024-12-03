/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.vault.core;

import org.springframework.lang.Nullable;
import org.springframework.vault.support.VaultMetadataRequest;
import org.springframework.vault.support.VaultMetadataResponse;

public interface VaultKeyValueMetadataOperations {
    @Nullable
    public VaultMetadataResponse get(String var1);

    public void put(String var1, VaultMetadataRequest var2);

    public void delete(String var1);
}

