/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.vault.core;

import java.time.Duration;
import org.springframework.lang.Nullable;
import org.springframework.vault.support.VaultResponse;
import org.springframework.vault.support.VaultResponseSupport;
import org.springframework.vault.support.VaultToken;
import org.springframework.vault.support.WrappedMetadata;

public interface VaultWrappingOperations {
    @Nullable
    public WrappedMetadata lookup(VaultToken var1);

    @Nullable
    public VaultResponse read(VaultToken var1);

    @Nullable
    public <T> VaultResponseSupport<T> read(VaultToken var1, Class<T> var2);

    public WrappedMetadata rewrap(VaultToken var1);

    public WrappedMetadata wrap(Object var1, Duration var2);
}

