/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.vault.support;

import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.vault.VaultException;

public abstract class AbstractResult<V> {
    @Nullable
    private final VaultException exception;

    protected AbstractResult() {
        this.exception = null;
    }

    protected AbstractResult(VaultException exception) {
        Assert.notNull((Object)exception, "VaultException must not be null");
        this.exception = exception;
    }

    public boolean isSuccessful() {
        return this.exception == null;
    }

    @Nullable
    public Exception getCause() {
        return this.exception;
    }

    @Nullable
    public V get() {
        if (this.isSuccessful()) {
            return this.get0();
        }
        throw new VaultException(this.exception.getMessage(), this.exception);
    }

    @Nullable
    protected abstract V get0();
}

